import requests

class CurrencyConverter:
    def __init__(self,api_key:str):
        self.base_url=f"https://v6.exchangerate-api.com/v6/{api_key}/latest/"

    def convert(self,amount:float,from_currency:str,to_currency:str):
        """Convertion the currency amount from one to another """
        url = f"{self.base_url}/{from_currency}"
        response = requests.get(url)
        if response.status_code != 200:
            raise Exception('API call failed ', response.json())
        rate = response.json()['conversion_rates']
        if to_currency not in rate :
            raise ValueError(f'{to_currency} not found in exchange rate')
        return amount *rate[to_currency]

















