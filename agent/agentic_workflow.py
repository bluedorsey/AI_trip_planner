from utils.model_loader import load_model 
from langgraph.graph import StateGraph,MessagesState,END,START
from langgraph.prebuilt import ToolNode, tools_condition
from prompt_builder.prompt import SYSTEM_PROMPT

class AgenticWorkflow:
    def __init__(self):
        self.system_prompt = SYSTEM_PROMPT

        
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

  