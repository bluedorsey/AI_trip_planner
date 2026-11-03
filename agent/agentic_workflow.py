from langgraph.graph import END, START, MessagesState, StateGraph
from langgraph.prebuilt import ToolNode, tools_condition

from tools.currency_converter_tool import CurrencyConverterTool
from tools.expense_calculator import ExpenseCalculatorTool
from tools.place_search_tool import PlaceSearchTool
from tools.weather_info_tools import WeatherInfoTool
from prompt_builder.prompt import SYSTEM_PROMPT
from utils.model_loader import load_model

class GraphBuilder:
    def __init__(self, model_provider: str = "google"):
        self.model_provider = model_provider
        self.llm = load_model(model_provider=self.model_provider)
        self.system_prompt = SYSTEM_PROMPT
        self.tools = []
        
        # Weather tool 
        self.weather_tool = WeatherInfoTool()

        # PlaceSearch tool
        self.place_search_tool = PlaceSearchTool()

        # CalculateCost tool
        self.calculate_cost_tool = ExpenseCalculatorTool()

        # CurrencyConverter tool
        self.currency_converter_tool = CurrencyConverterTool()

        self.tools.extend([*self.weather_tool.weather_tools_list,
                    *self.place_search_tool.tools_list,
                    *self.calculate_cost_tool.calculator_tools_list,
                    *self.currency_converter_tool.setup_tools(),
        ])
        self.llm_with_tools = self.llm.bind_tools(self.tools)
        self.graph = None






    def agentic_function(self, state: MessagesState):
        messages = [self.system_prompt, *state["messages"]]
        response = self.llm_with_tools.invoke(messages)
        return {"messages": [response]}


    def build_graph(self):
        graph_builder = StateGraph(MessagesState)
        graph_builder.add_node("tools", ToolNode(tools=self.tools))
        graph_builder.add_node("agentic_function", self.agentic_function)
        graph_builder.add_edge(START, "agentic_function")
        graph_builder.add_conditional_edges("agentic_function", tools_condition)
        graph_builder.add_edge("tools", "agentic_function")
        graph_builder.add_edge("agentic_function", END)
        self.graph = graph_builder.compile()
        return self.graph
    def __call__(self):
        return self.build_graph()

  