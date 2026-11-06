"""Structured trip plan: turns the agent's Markdown answer into JSON the Android app can render."""
from difflib import get_close_matches
from typing import Literal

from pydantic import BaseModel, Field

from app.utils.model_loader import load_model


class Activity(BaseModel):
    title: str = Field(description='The place\'s exact name, e.g. "Hawa Mahal", not "Visit Hawa Mahal".')
    description: str = Field(description="One or two sentences with concrete details: what to do, timing, entry fee or price.")
    location: str
    type: Literal["Hotel", "Activity", "Sightseeing", "Food", "Transport"]


class Day(BaseModel):
    dayNumber: int
    date: str = Field(description='Short label such as "Day 1" or "Dec 20, Sat".')
    theme: str = Field(description='Short theme of the day, e.g. "Arrival & Relax".')
    activities: list[Activity]


class CostItem(BaseModel):
    label: str = Field(description='e.g. "Hotel (2 nights)".')
    amount: str = Field(description='With currency, e.g. "₹8,000".')


class Plan(BaseModel):
    title: str
    location: str
    dateRange: str = Field(description='e.g. "Dec 20 - Dec 25"; use "Flexible dates" if none was given.')
    budget: str = Field(description='Total estimated cost with currency, e.g. "₹45,000".')
    duration: str = Field(description='e.g. "5 days".')
    rating: str = Field(description='Traveller rating out of 5 as text, e.g. "4.6"; "-" if unknown.')
    perDayBudget: str = Field(description='e.g. "₹12,000 per day"; empty if the plan gives none.')
    costBreakdown: list[CostItem] = Field(description="Each cost line from the plan's breakdown.")
    weather: str = Field(description="The expected weather in one to three sentences; empty if the plan gives none.")
    transport: str = Field(description="How to get there and around, with costs, in one to four sentences; empty if the plan gives none.")
    days: list[Day]


def structure_plan(markdown: str) -> Plan:
    llm = load_model().with_structured_output(Plan)
    return llm.invoke(
        "Convert this travel plan into the requested structure. Use only information present in it "
        "and keep it faithful: keep named places, prices, entry fees and timings. "
        "If it offers several plans (e.g. tourist and off-beat), produce one itinerary covering the "
        "trip's number of days: the main plan with its best off-beat stops woven in, never repeating a day. "
        "Include each day's stops in order, with the named hotel and restaurants as Hotel and Food entries. "
        'Set rating to "-" unless the plan itself states one.\n\n' + markdown
    )


def _find_image(title: str, images: dict[str, str]) -> str | None:
    by_name = {name.casefold(): url for name, url in images.items()}
    title = title.casefold()
    for name, url in by_name.items():
        if name in title or title in name:
            return url
    close = get_close_matches(title, by_name, n=1, cutoff=0.8)  # "Amer Fort" vs "Amber Fort"
    return by_name[close[0]] if close else None


def attach_images(plan: Plan, images: dict[str, str]) -> dict:
    """Plan as JSON with photo URLs from the search results.

    Added after the LLM step on purpose: it would invent URLs. Stops with no photo of their own
    (hotels, "Arrive at airport") get the hero, so every card shows the destination.
    """
    data = plan.model_dump()
    activities = [a for day in data["days"] for a in day["activities"]]
    for a in activities:
        a["imageUrl"] = _find_image(a["title"], images)
    hero = next((a["imageUrl"] for a in activities if a["imageUrl"] and a["type"] == "Sightseeing"), None)
    hero = hero or next(iter(images.values()), None)
    for a in activities:
        a["imageUrl"] = a["imageUrl"] or hero
    data["heroImageUrl"] = hero
    return data
