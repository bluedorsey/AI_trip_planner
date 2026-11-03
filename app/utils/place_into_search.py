import html
from functools import cached_property

import serpapi
from langchain_tavily import TavilySearch
from langchain_google_community import GooglePlacesTool , GooglePlacesAPIWrapper

class GooglePlacesSearchTool:
    def __init__(self, api_key):
        self.api_key = api_key

    @cached_property
    def google_places_tool(self):
        # built on first use so a missing key doesn't break app startup
        return GooglePlacesTool(api_wrapper=GooglePlacesAPIWrapper(gplaces_api_key=self.api_key))
        
# atrraction , resturant , street_food , activity , hotel , transport
    def attraction_place(self, Place :str)-> dict:
        """
         Find the best tourist attractions and places to visit near the given
        location using Google Places.

        Prioritize popular, highly rated, and well-reviewed attractions.
        Include a variety of attraction types such as landmarks, historical
        places, sightseeing spots, museums, parks, viewpoints, and other
        notable places that are worth visiting.

        Use this tool when planning sightseeing or deciding what places the
        traveler should visit.

        Args:
            Place: City, neighborhood, landmark, or location where attractions
                should be searched.

        Returns:
            Attraction results including name, rating, address, and other
            available Google Places information.

        """
        return self.google_places_tool.run( Place)

    def restaurant_place(self, Place :str)-> dict:
        """
        Search Google Places for highly rated restaurants in the specified location.

        The results should provide a balanced mix of restaurants across different
        price levels, such as budget, moderate, and expensive/premium options.
        Do not return only the most expensive or only the highest-rated restaurants.
        Prioritize good ratings while maintaining variety in pricing so the user
        can compare restaurants at different budgets.

        Use this tool whenever the user asks for restaurants, places to eat,
        dining recommendations, or restaurant options in a specific location.

        Args:
            place: The city, neighborhood, area, landmark, or location where
                   restaurants should be searched.

        Returns:
            Restaurant search results containing information such as restaurant
            name, rating, price level, address, and other available details.
        """
        return self.google_places_tool.run( Place)

    def street_food_place(self, Place :str)-> dict:
            """
            Find popular and highly recommended local street-food places near the
        given location using Google Places.

        Prioritize authentic local food, popular street-food spots, local
        specialties, affordable prices, good ratings, and places that are
        commonly recommended for visitors. Prefer local and traditional food
        experiences over ordinary restaurants.

        Use this tool when the traveler asks what local street food they should
        try or wants authentic food experiences.

        Args:
            Place: City, neighborhood, landmark, or location where street food
                should be searched.

        Returns:
            Street-food recommendations with available information such as
            name, rating, price level, address, and food-related details.
            """
            return self.google_places_tool.run( Place)
    def activity_place(self, Place :str)-> dict:
            """
             Find interesting activities and experiences near the given location
            using Google Places.

                Include a variety of activities suitable for travelers, such as
                adventure activities, entertainment, shopping, cultural experiences,
                recreational activities, nightlife, family activities, and unique
                local experiences. Prioritize highly rated and popular activities.

                Use this tool when creating an itinerary or when the traveler asks
                what they can do in a location.

                Args:
                    Place: City, neighborhood, landmark, or location where activities
                        should be searched.

                Returns:
                    Activity recommendations with available information such as
                    name, rating, address, and other relevant details.
            """
            return self.google_places_tool.run( Place)
    def hotel_place(self , Place:str)->dict:
          """
          Find well-rated hotels near the given location using Google Places.

            Prioritize hotels that offer good value for money, strong ratings,
            positive reviews, convenient locations, and a range of price levels.
            Include budget, mid-range, and premium options when possible.

            Prefer hotels that are conveniently located near major attractions,
            transportation, and other important areas for travelers.

            Use this tool when the traveler needs accommodation recommendations.

            Args:
                Place: City, neighborhood, landmark, or area where hotels should
                    be searched.

            Returns:
                Hotel results including name, rating, price information, address,
                and other available Google Places details.
          """
          return self.google_places_tool.run(Place)
    def transport(self , Place:str)-> dict:
          """
            Find the nearest and most useful public transportation options near
            the given location using Google Places.

            Look for nearby metro stations, railway stations, bus stations,
            bus stops, airports, taxi stands, and other major transportation
            facilities. Prioritize options that are close, practical, and useful
            for travelers.

            Use this tool when determining how a traveler can get around the
            destination or reach major attractions.

            Args:
                Place: City, neighborhood, landmark, or location from which
                    transportation options should be searched.

            Returns:
                Nearby transportation options with available information such as
                name, type, distance/address, rating, and other relevant details.
          """
          return self.google_places_tool.run(Place)


class tavily_search_tool:
    def __init__(self, api_key):
        self.api_key = api_key
        self.tavily_search = TavilySearch(
            tavily_api_key=self.api_key, topic="general", include_answer="advanced"
        )

    def tavily_attraction_place(self, place: str) -> dict:
        """
        Search for attractions in and around a place using the Tavily Search API.

        Args:
            place: City, neighborhood, landmark, or location to search.

        Returns:
            The advanced answer if Tavily produced one, otherwise the raw results.
        """
        result = self.tavily_search.invoke(
            {"query": f"top attractive places in and around {place}"}
        )
        if isinstance(result, dict) and result.get("answer"):
            return result["answer"]
        return result

    def tavily_restaurant_place(self, place :str)-> dict:
            """
            Search tavily Places for highly rated restaurants in the specified location.

            The results should provide a balanced mix of restaurants across different
            price levels, such as budget, moderate, and expensive/premium options.
            Do not return only the most expensive or only the highest-rated restaurants.
            Prioritize good ratings while maintaining variety in pricing so the user
            can compare restaurants at different budgets.

            Use this tool whenever the user asks for restaurants, places to eat,
            dining recommendations, or restaurant options in a specific location.

            Args:
                place: The city, neighborhood, area, landmark, or location where
                       restaurants should be searched.

            Returns:
                Restaurant search results containing information such as restaurant
                name, rating, price level, address, and other available details.
            """
            result = self.tavily_search.invoke(
                        {"query": f"top attractive places in and around {place}"}
                    )
            if isinstance(result, dict) and result.get("answer"):
                    return result["answer"]
            return result



    def tavily_street_food_place(self, place :str)-> dict:
                """
                Find popular and highly recommended local street-food places near the
            given location using Tavily Search.

            Prioritize authentic local food, popular street-food spots, local
            specialties, affordable prices, good ratings, and places that are
            commonly recommended for visitors. Prefer local and traditional food
            experiences over ordinary restaurants.

            Use this tool when the traveler asks what local street food they should
            try or wants authentic food experiences.

            Args:
                Place: City, neighborhood, landmark, or location where street food
                    should be searched.

            Returns:
                Street-food recommendations with available information such as
                name, rating, price level, address, and food-related details.
                """
                result = self.tavily_search.invoke(
                    {"query": f"top attractive street food place in and around {place}"}
                    )
                if isinstance(result, dict) and result.get("answer"):
                    return result["answer"]
                return result

    def tavily_activity_place(self, place :str)-> dict:
                """
                 Find interesting activities and experiences near the given location
                using Tavily Searc.

                    Include a variety of activities suitable for travelers, such as
                    adventure activities, entertainment, shopping, cultural experiences,
                    recreational activities, nightlife, family activities, and unique
                    local experiences. Prioritize highly rated and popular activities.

                    Use this tool when creating an itinerary or when the traveler asks
                    what they can do in a location.

                    Args:
                        Place: City, neighborhood, landmark, or location where activities
                            should be searched.

                    Returns:
                        Activity recommendations with available information such as
                        name, rating, address, and other relevant details.
                """
                result = self.tavily_search.invoke(
                                    {"query": f"top attractive street food place in and around {place}"}
                                    )
                if isinstance(result, dict) and result.get("answer"):
                                    return result["answer"]
                return result



    def tavily_hotel_place(self , place:str)->dict:
                """
                Find well-rated hotels near the given location using Tavily Search.

                Prioritize hotels that offer good value for money, strong ratings,
                positive reviews, convenient locations, and a range of price levels.
                Include budget, mid-range, and premium options when possible.

                Prefer hotels that are conveniently located near major attractions,
                transportation, and other important areas for travelers.

                Use this tool when the traveler needs accommodation recommendations.

                Args:
                    Place: City, neighborhood, landmark, or area where hotels should
                        be searched.

                Returns:
                    Hotel results including name, rating, price information, address,
                    and other available Tavily Search details.
                """
                result = self.tavily_search.invoke(
                    {"query": f"top attractive street food place in and around {place}"}
                                )
                if isinstance(result, dict) and result.get("answer"):
                            return result["answer"]
                return result


    def tavily_transport(self , place:str)-> dict:
                """
                Find the nearest and most useful public transportation options near
                the given location using Tavily Search.

                Look for nearby metro stations, railway stations, bus stations,
                bus stops, airports, taxi stands, and other major transportation
                facilities. Prioritize options that are close, practical, and useful
                for travelers.

                Use this tool when determining how a traveler can get around the
                destination or reach major attractions.

                Args:
                    Place: City, neighborhood, landmark, or location from which
                        transportation options should be searched.

                Returns:
                    Nearby transportation options with available information such as
                    name, type, distance/address, rating, and other relevant details.
                """
                result = self.tavily_search.invoke(
                    {"query": f"top attractive street food place in and around {place}"}
                                                  )
                if isinstance(result, dict) and result.get("answer"):
                    return result["answer"]
                return result


class SerpAPI_tool:
    """Google search via SerpAPI, parsed down to what an itinerary needs.

    Talks to the official serpapi client directly rather than through
    langchain's SerpAPIWrapper: that wrapper needs the legacy
    google-search-results package, and its .run() collapses the whole page
    into one snippet string — throwing away the local pack, the only block
    carrying ratings, review counts, price bands and coordinates.
    """

    # Rating without volume is noise: 5.0 from 3 reviews is weaker than 4.4 from 1400.
    MIN_REVIEWS = 50

    def __init__(self, api_key, country: str = "in"):
        self.api_key = api_key
        self.country = country  # gl= biases results to the traveler's market
        self.client = serpapi.Client(api_key=api_key)

    #  internals
    def _search(self, query: str) -> str:
        """Run one query and flatten the response into text the LLM can use."""
        raw = self.client.search({
            "engine": "google",
            "q": query,
            "gl": self.country,
            "hl": "en",
        })
        if raw.get("error"):
            return f"No results for '{query}' ({raw['error']})."
        places = self._parse_places(raw)
        return self._format(places) if places else self._fallback(raw, query)

    @classmethod
    def _parse_places(cls, raw: dict) -> list:
        """Pull local_results.places[] — the only block with ratings and prices.

        Everything else in the payload (search_metadata, search_parameters,
        lsig, thumbnails) is bookkeeping and gets dropped.
        """
        places = raw.get("local_results", {}).get("places", [])
        kept = [p for p in places if p.get("reviews", 0) >= cls.MIN_REVIEWS]
        # Small towns rarely clear the threshold; thin data beats no data.
        return kept or places

    @staticmethod
    def _format(places: list) -> str:
        """One line per place. Fields are optional — new listings lack rating/price."""
        lines = []
        for p in places:
            bits = [p.get("title", "Unknown")]
            if p.get("type"):
                bits.append(f"({p['type']})")
            if p.get("rating"):
                bits.append(f"- rated {p['rating']}/5 from {p.get('reviews', '?')} reviews")
            if p.get("price"):
                # A band like "$1-10", not a real price. Do not do arithmetic on it.
                bits.append(f"- price band {p['price']}")
            if p.get("address"):
                bits.append(f"- {p['address']}")
            if p.get("description"):
                bits.append(f"- {p['description']}")
            lines.append("* " + " ".join(bits))
        return "\n".join(lines)

    @staticmethod
    def _sights(raw: dict) -> str:
        """top_sights.sights[] — where attractions actually land.

        Google returns no local pack for broad "things to see in X" queries,
        but it does return this block, with entry fees. Note two quirks:
        `reviews` here is Google's "1.7K" mangled into the float 1.7, so it is
        NOT a usable count and MIN_REVIEWS must not be applied; `description`
        is the open/closed status, which is stale by the time anyone reads the
        itinerary. Both are dropped.
        """
        sights = (raw.get("top_sights") or {}).get("sights", [])
        lines = []
        for s in sights:
            bits = [s.get("title", "Unknown")]
            if s.get("rating"):
                bits.append(f"- rated {s['rating']}/5")
            if s.get("price"):
                bits.append(f"- entry fee {s['price']}")
            lines.append("* " + " ".join(bits))
        return "\n".join(lines)

    @classmethod
    def _fallback(cls, raw: dict, query: str) -> str:
        """No local pack. Happens on broad queries ('best time to visit X')."""
        sights = cls._sights(raw)
        if sights:
            return sights
        box = raw.get("answer_box") or {}
        if box.get("answer") or box.get("snippet"):
            return box.get("answer") or box["snippet"]
        # No knowledge_graph branch on purpose: it resolves an entity from the
        # query string, not from `place`. "best things to do in Jaipur" matched
        # BEST, Mumbai's electricity board, and returned that as the answer.
        organic = raw.get("organic_results") or []
        if organic:
            # Google's snippets carry raw entities ("Hawa Mahal &amp; Jal Mahal").
            return html.unescape("\n".join(
                f"* {r.get('title', '')}: {r.get('snippet', '')}" for r in organic[:5]
            ))
        return f"No results found for '{query}'."

    #  public
    def serp_attraction_place(self, place: str) -> str:
        """
        Find the best tourist attractions and places to visit near the given
        location using SerpAPI (Google).

        Prioritize popular, highly rated, and well-reviewed attractions such as
        landmarks, historical places, museums, parks and viewpoints.

        Args:
            place: City, neighborhood, landmark, or location to search.

        Returns:
            One line per attraction with name, type, rating, review count,
            address and a short description.
        """
        # Exact wording matters: this phrasing returns the top_sights block
        # (names, ratings, entry fees). Adding "and places to visit" loses it.
        return self._search(f"top tourist attractions in {place}")

    def serp_restaurant_place(self, place: str) -> str:
        """
        Search SerpAPI (Google) for highly rated restaurants in the location.

        Results span budget, moderate and premium price bands rather than only
        the most expensive or only the highest rated, so the traveler can
        compare across budgets.

        Args:
            place: The city, neighborhood, area, landmark, or location where
                   restaurants should be searched.

        Returns:
            One line per restaurant with name, rating, review count, price band
            and address.
        """
        return self._search(f"best rated restaurants in {place} budget mid-range and fine dining")

    def serp_street_food_place(self, place: str) -> str:
        """
        Find popular and highly recommended local street-food places near the
        given location using SerpAPI (Google).

        Prefers authentic local specialties and affordable, well-reviewed spots
        over ordinary restaurants.

        Args:
            place: City, neighborhood, landmark, or location where street food
                should be searched.

        Returns:
            One line per street-food spot with name, rating, price band and
            address.
        """
        return self._search(f"famous local street food places and food streets in {place}")

    def serp_activity_place(self, place: str) -> str:
        """
        Find interesting activities and experiences near the given location
        using SerpAPI (Google).

        Covers adventure, entertainment, shopping, cultural experiences,
        nightlife and family activities.

        Args:
            place: City, neighborhood, landmark, or location where activities
                should be searched.

        Returns:
            One line per activity with name, type, rating and address.
        """
        # Never lead these queries with an adjective. "best things to do in
        # Jaipur" resolves BEST (Mumbai's electricity board) as the entity and
        # "fun activities..." resolves the dictionary entry for "fun". Leading
        # with concrete nouns keeps Google on the place. This phrasing also
        # returns a local pack, so activities come back distinct from the
        # top_sights that serp_attraction_place already covers.
        return self._search(f"adventure sports amusement parks and entertainment in {place}")

    def serp_hotel_place(self, place: str) -> str:
        """
        Find well-rated hotels near the given location using SerpAPI (Google).

        Prefers good value for money and convenient locations across budget,
        mid-range and premium options.

        Args:
            place: City, neighborhood, landmark, or area where hotels should
                be searched.

        Returns:
            Hotel names with indicative prices. Google routes hotel queries to
            its booking module rather than a local pack, so this comes back as
            search snippets, not the rated/priced rows the other methods give.
            For real nightly rates use SerpAPI's google_hotels engine, which
            needs check-in and check-out dates this signature does not carry.
        """
        return self._search(f"hotels to stay in {place} with prices and ratings")

    def serp_transport(self, place: str) -> str:
        """
        Find the nearest and most useful public transportation options near the
        given location using SerpAPI (Google).

        Looks for metro stations, railway stations, bus stands, airports and
        taxi stands that are practical for travelers.

        Args:
            place: City, neighborhood, landmark, or location from which
                transportation options should be searched.

        Returns:
            One line per transport option with name, type and address.
        """
        return self._search(f"metro station railway station bus stand and airport near {place}")