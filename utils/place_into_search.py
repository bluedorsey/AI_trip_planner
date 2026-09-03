from unittest import result

from langchain_tavily import TavilySearch
from langchain_google_community import GooglePlacesTool , GooglePlacesAPIWrapper

class GooglePlacesSearchTool:
    def __init__(self, api_key):
        self.api_key = api_key
        self.google_places_api_wrapper = GooglePlacesAPIWrapper(api_key=self.api_key)
        self.google_places_tool = GooglePlacesTool(Gplace_api_key=self.google_places_api_wrapper)
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
            api_key=self.api_key, topic="general", include_answer="advanced"
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