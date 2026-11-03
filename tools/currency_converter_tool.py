from dotenv import load_dotenv
import os
from langchain.tools import tool
from utils.currency_converter import CurrencyConverter

class CurrencyConverterTool:
    def __init__(self):
        self.api_key = os.getenv("EXCHANGE_RATE_KEY")
    def setup_tools(self):

        @tool("currency_converter")
        def currency_converter(from_curr: str, to_curr: str, value: float) -> str:
            """
            Convert a given amount from one currency to another.

            Args:
                from_curr (str): The currency code to convert from (e.g., 'USD').
                to_curr (str): The currency code to convert to (e.g., 'EUR').
                value (float): The amount of money to convert."""
            converter = CurrencyConverter(self.api_key)
            result = converter.convert(value, from_curr, to_curr)
            return f"{value} {from_curr} = {result} {to_curr}"
        return [currency_converter]
