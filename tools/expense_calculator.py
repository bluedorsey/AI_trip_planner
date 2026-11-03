



from typing import List
from langchain.tools import tool
from utils.calculator import Calculator as CalculatorService

class ExpenseCalculatorTool:
    def __init__(self):
        self.calculator = CalculatorService()
        self.calculator_tools_list = self._setup_tools()

    def _setup_tools(self)-> List:

        @tool
        def Estimate_hotel_cost(price_per_night: float, total_number_days: int):
            """
            calculate the total cost of a hotel stay given the price per night and the total number of days.
            Args:
                price_per_night (float): The cost per night of the hotel.
                total_number_days (int): The total number of days of the stay.
            Returns:
                float: The total cost of the hotel stay.
            """
            return self.calculator.multiply(price_per_night, total_number_days)

        @tool
        def total_expense(expenses: List[float]):
            """
            Calculate the total expense given a list of expenses.
            Args:
                expenses (List[float]): A list of expenses.
            Returns:
                float: The total expense.
            """
            return self.calculator.calculate_total(*expenses)

        @tool
        def per_day_expense(total: float, days: int):
            """
            Calculate the per day expense given a total amount and number of days.
            Args:
                total (float): The total amount.
                days (int): The number of days.
            Returns:
                float: The per day expense.
            """
            return self.calculator.per_day_expense(total, days)
        return [Estimate_hotel_cost, total_expense, per_day_expense]
