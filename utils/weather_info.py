import requests 





class WeatherInfo:
    def __init__(self, api_key):
        self.api_key = api_key
        self.base_url = "https://api.openweathermap.org/data/2.5/"

    def get_current_weather(self, city):
        """Get the current weather information for a given city."""
        try:
            url= f"{self.base_url}weather?q={city}&appid={self.api_key}&units=metric"
            params = {
                "q": city,
                "appid": self.api_key}
            response = requests.get(url, params=params)
            return response.json() if response.status_code == 200 else None

        except Exception as e:
            print(f"Error constructing URL: {e}")
            return None

    def get_weather_forecast(self, city):
        """Get the weather forecast for a given city."""
        try:
            url = f"{self.base_url}forecast?q={city}&appid={self.api_key}&units=metric"
            params = {
                "q": city,
                "appid": self.api_key,
                "units": "metric",
                "cnt": 5  # Get forecast for the next 5 days
                }
            response = requests.get(url, params=params)
            return response.json() if response.status_code == 200 else None

        except Exception as e:
            print(f"Error constructing URL: {e}")
            return None