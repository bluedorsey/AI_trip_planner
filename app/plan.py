"""Trip plan for the Android app: research with the tool agent, then stream the plan as JSON lines
(summary first, then one line per day) so the app can show it while it is still being written."""
import json
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import date
from difflib import get_close_matches
from typing import Iterator, Literal

from langchain_core.messages import HumanMessage, SystemMessage
from pydantic import BaseModel, ConfigDict, ValidationError

from app.prompt_builder.prompt import WRITER_PROMPT
from app.tools.place_search_tool import PlaceSearchTool
from app.tools.weather_info_tools import WeatherInfoTool
from app.utils.model_loader import load_model


class _Model(BaseModel):
    model_config = ConfigDict(coerce_numbers_to_str=True)  # models sometimes write 35000 instead of "35000"


class Activity(_Model):
    title: str
    description: str
    location: str
    type: Literal["Hotel", "Activity", "Sightseeing", "Food", "Transport"]


class Day(_Model):
    dayNumber: int
    date: str
    theme: str
    activities: list[Activity]


class CostItem(_Model):
    label: str
    amount: str


class Meta(_Model):
    title: str
    location: str
    dateRange: str = "Flexible dates"
    budget: str = "-"
    duration: str = ""
    rating: str = "-"
    perDayBudget: str = ""
    costBreakdown: list[CostItem] = []
    weather: str = ""
    transport: str = ""


# Research tools to run, and what the app shows when each one finishes.
STATUS = {
    "get_weather_forecast": "Checked the weather",
    "attraction_places_tool": "Found top attractions",
    "resturant_place_tool": "Found restaurants",
    "street_food_tool": "Found street food",
    "Hotel_place_tool": "Compared hotels",
    "activity_place_tool": "Found activities",
    "transport_tool": "Checked transport",
}


def message_text(content) -> str:
    """Gemini returns a list of content blocks, Groq a string."""
    if isinstance(content, str):
        return content
    return "".join(b.get("text", "") if isinstance(b, dict) else str(b) for b in content)


def _loads(line: str) -> dict | None:
    try:
        obj = json.loads(line.strip())
    except ValueError:  # blank lines, code fences, chatter
        return None
    return obj if isinstance(obj, dict) else None


def _json_lines(chunks) -> Iterator[dict]:
    """Streamed text chunks -> one dict per complete JSON line."""
    buf = ""
    for chunk in chunks:
        buf += message_text(chunk.content)
        *lines, buf = buf.split("\n")
        yield from filter(None, map(_loads, lines))
    if obj := _loads(buf):
        yield obj


def _find_image(title: str, images: dict[str, str]) -> str | None:
    by_name = {name.casefold(): url for name, url in images.items()}
    title = title.casefold()
    for name, url in by_name.items():
        if name in title or title in name:
            return url
    close = get_close_matches(title, by_name, n=1, cutoff=0.8)  # "Amer Fort" vs "Amber Fort"
    return by_name[close[0]] if close else None


def _with_images(day: Day, images: dict[str, str], hero: str | None) -> dict:
    """Day as JSON with photo URLs from the search results. Added here, never by the LLM, which
    would invent URLs. Stops with no photo of their own (hotels, "Arrive at airport") get the hero."""
    data = day.model_dump()
    for a in data["activities"]:
        a["imageUrl"] = _find_image(a["title"], images) or hero
    return data


def _destination(query: str) -> str:
    reply = load_model().invoke(
        "Reply with only the destination (a place name, no other words) of this trip request:\n" + query
    )
    lines = message_text(reply.content).strip().strip('."\'').splitlines()
    return lines[0] if lines else query


def stream_plan(query: str) -> Iterator[dict]:
    """Events: status*, meta, day*, then done (or error). Every search runs in parallel, then one
    streamed model call writes the plan from the notes. No agent loop: the searches are always the
    same, and a model deciding to call them one per turn cost a model round trip each."""
    yield {"type": "status", "text": "Understanding your request"}
    place = _destination(query)
    yield {"type": "status", "text": f"Researching {place}"}

    places = PlaceSearchTool()
    search = places.serpapi_search
    tools = {t.name: t for t in [*places.tools_list, *WeatherInfoTool().weather_tools_list] if t.name in STATUS}
    notes = []
    with ThreadPoolExecutor(len(tools)) as pool:
        # each tool takes one argument (place or city), whatever it is called
        futures = {pool.submit(t.invoke, {next(iter(t.args)): place}): name for name, t in tools.items()}
        for done in as_completed(futures):
            try:
                notes.append(str(done.result()))
            except Exception:
                continue  # one failed search should not sink the trip
            yield {"type": "status", "text": STATUS[futures[done]]}

    yield {"type": "status", "text": "Writing your itinerary"}
    hero = search.hero or next(iter(search.images.values()), None)
    prompt = [
        SystemMessage(f"{WRITER_PROMPT}\nToday's date: {date.today():%B %d, %Y}."),
        HumanMessage(f"Request: {query}\n\nResearch notes:\n" + "\n\n".join(notes)),
    ]
    for _ in range(2):  # a model can ramble instead of writing the lines; one retry, then a clean error
        days = 0
        for obj in _json_lines(load_model().stream(prompt)):
            try:
                if "dayNumber" in obj:
                    yield {"type": "day", **_with_images(Day(**obj), search.images, hero)}
                    days += 1
                elif "title" in obj:
                    yield {"type": "meta", **Meta(**obj).model_dump(), "heroImageUrl": hero}
            except ValidationError:
                continue  # drop one bad line rather than the whole trip
        if days:
            yield {"type": "done"}
            return
    yield {"type": "error", "message": "The model returned no itinerary"}
