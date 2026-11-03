
class Calculator:
    @staticmethod
    def multiply(a: int, b: int) -> int:
        """
        Multiplies two integers and returns the result.
        Inputs are two integers a and b, and the output is their product which should also be the integer.
        """
        return a * b

    @staticmethod
    def calculate_total(*x: float) -> float:
        """
        Calculates the total of a list of numbers.
        Inputs are a list of numbers, and the output is their sum which should also be a float.
        """
        return sum(x)

    @staticmethod
    def per_day_expense(total: float, days: int) -> float:
        """
        Calculates the per day expense given a total amount and number of days.
        Inputs are total amount and number of days, and the output is the per day expense which should also be a float.
        """
        if days <= 0:
            raise ValueError("Number of days must be greater than zero.")
        return total / days