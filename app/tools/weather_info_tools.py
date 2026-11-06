import os 
from typing import List,Dict,Any,Optional
from pydantic import BaseModel, Field
from dotenv import load_dotenv
from langchain.tools import tool
from app.utils.weather_info import WeatherInfo

class WeatherInfoTool:

    def __init__(self):
        self.api_key = os.getenv("WEATHERMAP_API_KEY")
        self.weather_service = WeatherInfo(self.api_key)
        self.weather_tools_list = self.setup_tools()

    def setup_tools(self)-> list :
        """Setup the weather information tools."""
        @tool("get_weather_info")
        def get_weather_info(city: str) -> str:
            """
            Get the current weather information for a given city.
            Args:
                city (str): The city for which to retrieve weather information.
            """
            weather_data = self.weather_service.get_current_weather(city)
            if weather_data:
                temp = weather_data.get("main",{}).get("temp",'N/A')
                condition = weather_data.get("weather", [{}])[0].get("description", "N/A")
                return f"The current temperature in {city} is {temp}°C with {condition}."
            else:
                return f"Could not retrieve weather information for {city}."
        
        @tool("get_weather_forecast")
        def get_weather_forecast(city: str) -> str:
            """
            Get the weather forecast for a given city.
            Args:
                city (str): The city for which to retrieve the weather forecast.
            """
            forecast_data = self.weather_service.get_weather_forecast(city)
            if forecast_data:
                forecast_summary = []
                for day in forecast_data.get("list", []):
                    date = day.get("dt_txt", "N/A")
                    if "12:00:00" not in date:  # one reading a day is enough to plan with
                        continue
                    temp = day.get("main", {}).get("temp", "N/A")
                    condition = day.get("weather", [{}])[0].get("description", "N/A")
                    forecast_summary.append(f"{date}: {temp}°C with {condition}")
                return "\n".join(forecast_summary)
            else:
                return f"Could not retrieve weather forecast for {city}."
        return [get_weather_info, get_weather_forecast]