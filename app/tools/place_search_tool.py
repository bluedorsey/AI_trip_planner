from typing import List
from langchain.tools import tool
from app.utils.place_into_search import tavily_search_tool, GooglePlacesSearchTool , SerpAPI_tool
import os
from dotenv import load_dotenv

class PlaceSearchTool:
    def __init__(self):
        self.api_key = os.getenv("GPLACE_KEY_API")
        self.tavily_api_key = os.getenv("TVAILAY_API_KEY")
        self.serp_api_key = os.getenv("SERP_API_KEY")
        self.tavily_search = tavily_search_tool(self.tavily_api_key)
        self.google_places_tool = GooglePlacesSearchTool(self.api_key)
        self.serpapi_search = SerpAPI_tool(self.serp_api_key)
        self.tools_list = self._set_up_tools()

    def _set_up_tools(self)->List:
        """Setup the place search tools."""
        @tool
        def attraction_places_tool(place:str)->List:
            """
            Find the top attractive places in and around the given location .
            Args:
                place (str): The location for which to find attractive places.
            Returns:
                dict: A dictionary containing information about the attractive places.
            """
            try:
                attraction_result = self.serpapi_search.serp_attraction_place(place)
                if attraction_result:# google place search 
                    return f"Following are the attractions of {place} as suggested by google: {attraction_result}"
            except :#fallback to tavily search if google places fails
                attraction_result = self.tavily_search.tavily_attraction_place(place)
                return f"Following are the attractions of {place} as suggested by tavily: {attraction_result}"

        @tool
        def resturant_place_tool(place:str)->List:
            """
            Find the top resturant with higher rating 3.8+ in and around the given location .
            Args:
                place (str): The location for which to find High rated Resturants .
            Returns:
                dict: A dictionary containing information about the Resturant. 
            """
            try:
                resturant_result = self.serpapi_search.serp_restaurant_place(place)
                if resturant_result:
                    return f"Following are the best resturant with higher ratings 3.8+ of {place} suggested by google: {resturant_result}"
            except:
                resturant_result = self.tavily_search.tavily_restaurant_place(place)
                return f"Following are the best resturant with higher ratings 3.8+ of {place} suggested by tavily: {resturant_result}"

        @tool
        def street_food_tool(place:str)->List:
            """
            Find the top street food options in and around the given location .
            Args:
                place (str): The location for which to find street food options .
            Returns:
                dict: A dictionary containing information about the street food options .
            """
            try:
                street_food_result = self.serpapi_search.serp_street_food_place(place)
                if street_food_result:
                    return f"Following are the best street food options in {place} suggested by google: {street_food_result}"
            except:
                street_food_result = self.tavily_search.tavily_street_food_place(place)
                return f"Following are the best street food options in {place} suggested by tavily: {street_food_result}"

        @tool
        def Hotel_place_tool(place:str)->List:
            """
            Find the top hotel options in and around the given location .
            Args:
                place (str): The location for which to find hotel options .
            Returns:
                dict: A dictionary containing information about the hotel options .
            """
            try:
                hotel_result = self.serpapi_search.serp_hotel_place(place)
                if hotel_result:
                    return f"Following are the best hotel options in {place} suggested by google: {hotel_result}"
            except:
                hotel_result = self.tavily_search.tavily_hotel_place(place)
                return f"Following are the best hotel options in {place} suggested by tavily: {hotel_result}"

        @tool
        def activity_place_tool(place:str)->List:
                """
                Find the top activity options in and around the given location .
                Args:
                    place (str): The location for which to find activity options .
                Returns:
                    dict: A dictionary containing information about the activity options .
                """
                try:
                    activity_result = self.serpapi_search.serp_activity_place(place)
                    if activity_result:
                        return f"Following are the best activity options in {place} suggested by google: {activity_result}"
                except:
                    activity_result = self.tavily_search.tavily_activity_place(place)
                    return f"Following are the best activity options in {place} suggested by tavily: {activity_result}"

        @tool
        def transport_tool(place:str)->str:
            """
            Find metro stations, railway stations, bus stands and airports in and around the given location.
            Args:
                place (str): The location for which to find transport options.
            Returns:
                str: The transport options.
            """
            try:
                return f"Following are the transport options in {place} suggested by google: {self.serpapi_search.serp_transport(place)}"
            except Exception:
                return f"Following are the transport options in {place} suggested by tavily: {self.tavily_search.tavily_transport(place)}"

        return [attraction_places_tool, resturant_place_tool, street_food_tool, Hotel_place_tool, activity_place_tool, transport_tool]
        
