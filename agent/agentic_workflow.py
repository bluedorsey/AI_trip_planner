from utils.model_loader import load_model 
from langgraph.graph import StateGraph,MessagesState,END,START
from langgraph.prebuilt import ToolNode, tools_condition
from prompt_builder.prompt import SYSTEM_PROMPT

class GraphBuilder:
    def __init__(self, model_provider: str = "google"):
        self.model_provider = model_provider
        self.model_loader = load_model(model_provider=self.model_provider)
        self.system_prompt = SYSTEM_PROMPT
        self.tools = []
        # Weather tool 
        self.wheather_tool = WeatherInfoTool()
        # PlaceSearch tool
        self.place_search_tool = PlaceSearchTool()

        # CalculateCost tool
        self.calculate_cost_tool = CalculateCostTool()
        # CurrencyConverter tool
        self.currency_converter_tool = CurrencyConverterTool()

        self.tools.extend([*self.wheather_tool.weather_tool_list,
                            *self.place_search_tool.place_search_tool_list,
                            *self.calculate_cost_tool.calculate_cost_tool_list,
                            *self.currency_converter_tool.currency_converter_tool_list])
        self.llm_with_tools = self.llm.bind_tools(self.tools)
        self.graph = None






    def agentic_fuction(self,state: MessagesState):
        user_input =  state['user_input']
        input_question = user_input + [self.system_prompt]
        response = self.llm_with_tools(input_question)
        return {"message": response}


    def build_graph(self):
        Graph_builder = StateGraph(MessagesState)
        Graph_builder.add_node('tools', ToolNode(tools = self.tools))
        Graph_builder.add_node('agentic_function', self.agentic_fuction)
        Graph_builder.add_edge(START,'agentic_function')
        Graph_builder.add_conditional_edges('agentic_function', tools_condition)
        Graph_builder.add_edge('tools','agentic_function')
        Graph_builder.add_edge('agentic_function',END)
        self.graph = Graph_builder.compile()
    def __call__(self):
        return self.build_graph()

  