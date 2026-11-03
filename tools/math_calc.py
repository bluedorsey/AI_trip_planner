from langchain.tools import tool 
import os 
from dotenv import load_dotenv
from langchain_community.utilities.alpha_vantage import AlphaVantageAPIWrapper

# multiplication tool
@tool
def multiplication(a : int , b:int) ->int:
    """
    Multiply two integers.

    Args:
        a (int): The first int.
        b (int): The second int.

    Returns:
        int: The product of a and b.

    """
    return a*b

# adding tool
@tool
def add(a: int, b: int) -> int:
    """
    Add two ints.

    Args:
        a (int): The first int.
        b (int): The second int.

    Returns:
        int: The sum of a and b.
    """
    return a + b


@tool 
def currency_converter(from_curr : str , to_curr : str , value ):
    os.environ["ALPHAVANTAGE_API_KEY"] = os.getenv('ALPHAVANTAGE_API_KEY')
    alpha_vantage = AlphaVantageAPIWrapper()
    result = alpha_vantage.currency_exchange_rate(from_currency=from_curr, to_currency=to_curr, amount=value)
    return value*float(result['Exchange Rate'])
