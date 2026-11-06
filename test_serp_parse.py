"""Self-check for SerpAPI_tool's response parsing. Run: python test_serp_parse.py

No network. Fixtures below are trimmed from real SerpAPI responses for Jaipur.
"""
from app.plan import Activity, Day, _json_lines, _with_images
from app.utils.place_into_search import SerpAPI_tool


def test_local_pack():
    raw = {"local_results": {"places": [
        {"title": "Handi Restaurant", "type": "Indian restaurant", "rating": 4.1,
         "reviews": 13000, "price": "Rs400-1,400", "reviews_original": "(13K)",
         "description": "Vintage address for non-veg specialties",
         "address": "MI Road", "thumbnail": "http://x.jpg", "position": 1,
         "place_id": "14433092704581672109"},
        {"title": "Brand New Cafe", "rating": 5.0, "reviews": 3},
    ]}}
    out = SerpAPI_tool._format(SerpAPI_tool._parse_places(raw))
    assert "Handi Restaurant" in out
    assert "Brand New Cafe" not in out, "MIN_REVIEWS did not drop the 3-review place"
    assert "x.jpg" not in out and "(13K)" not in out, "junk fields leaked into the prompt"


def test_thin_data_survives():
    """Small towns rarely clear MIN_REVIEWS; returning nothing is worse."""
    thin = {"local_results": {"places": [{"title": "Village Dhaba", "reviews": 4}]}}
    assert SerpAPI_tool._parse_places(thin), "thin-data fallback dropped everything"


def test_top_sights():
    """Broad attraction queries return top_sights, never a local pack.

    `reviews: 1.7` is Google's "1.7K" mangled by SerpAPI. If MIN_REVIEWS ever
    gets applied to this block, Hawa Mahal disappears from every Jaipur trip.
    """
    raw = {"top_sights": {"sights": [
        {"title": "Hawa Mahal", "rating": 4.5, "reviews": 1.7,
         "price": "Rs600.00", "description": "Closed"},
    ]}}
    out = SerpAPI_tool._fallback(raw, "q")
    assert "Hawa Mahal" in out, "top_sights not reached"
    assert "entry fee Rs600.00" in out
    assert "Closed" not in out, "stale open/closed status leaked into the itinerary"


def test_organic_fallback_unescapes():
    raw = {"organic_results": [{"title": "T", "snippet": "Hawa Mahal &amp; Jal Mahal"}]}
    assert SerpAPI_tool._fallback(raw, "q") == "* T: Hawa Mahal & Jal Mahal"


def test_empty_response():
    assert SerpAPI_tool._parse_places({}) == [], "missing local_results must not raise"
    assert "No results" in SerpAPI_tool._fallback({}, "q")


def test_images_collected_and_upsized():
    raw = {"top_sights": {"sights": [
        {"title": "Hawa Mahal", "thumbnail": "https://lh3.googleusercontent.com/grass-cs/abc=s148-w148-h148-n-k-no"},
        {"title": "No Photo"},
    ]}, "local_results": {"places": [
        {"title": "Handi Restaurant", "thumbnail": "https://serpapi.com/searches/1/images/x.jpeg"},
    ]}}
    assert SerpAPI_tool._images(raw) == {
        "Hawa Mahal": "https://lh3.googleusercontent.com/grass-cs/abc=w800",
        "Handi Restaurant": "https://serpapi.com/searches/1/images/x.jpeg",
    }


def test_day_images():
    """Real matches win, and stops with no photo get the hero rather than an empty card."""
    def stop(title, type="Sightseeing"):
        return Activity(title=title, description="d", location="l", type=type)
    day = Day(dayNumber=1, date="Day 1", theme="t", activities=[
        stop("Lunch at Handi Restaurant", "Food"), stop("Amer Fort"), stop("Arrive at airport", "Transport"),
    ])
    out = _with_images(day, {"Handi Restaurant": "food.jpg", "Amber Fort": "fort.jpg"}, "hero.jpg")
    assert [a["imageUrl"] for a in out["activities"]] == ["food.jpg", "fort.jpg", "hero.jpg"]
    assert _with_images(day, {}, None)["activities"][0]["imageUrl"] is None, "no search results must not raise"


def test_json_lines():
    """Chunks split mid-line; fences and chatter are skipped; a trailing line without newline counts."""
    class Chunk:
        def __init__(self, content): self.content = content
    nl = chr(10)
    chunks = [Chunk('```json' + nl + '{"title": "T"'), Chunk('}' + nl + 'not json' + nl + '{"dayNu'),
              Chunk([{"type": "text", "text": 'mber": 1}'}])]
    assert list(_json_lines(chunks)) == [{"title": "T"}, {"dayNumber": 1}]


if __name__ == "__main__":
    for name, fn in sorted(globals().items()):
        if name.startswith("test_"):
            fn()
            print("ok", name)
