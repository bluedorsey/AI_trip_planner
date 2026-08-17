from fastapi import FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from agent.agentic_workflow import GraphBuilder
import os


app = FastAPI()

class QueryRequest(BaseModel):
    query: str

@app.post("/query")
async def query_endpoint(query: QueryRequest):
    try : 
        print("Received query:", query)
        graph = GraphBuilder(model_provider="google")
        react_app = graph()

        png_graph = react_app.get_graph().draw_meramaid_png()
        with open("graph.png", "wb") as f:
            f.write(png_graph)

            print('Graph saved as graph.png')

        messages = {'user_input': [query.query]}
        output = react_app.invoke(messages)

    #if result is dict with message 
        if isinstance(output, dict) and 'message' in output:
            final_response = output['message'][-1].content
        else: #if not just send back the user input
            final_response = str(output)

    except Exception as e:
        return JSONResponse(content={"error": str(e)},status_code=500)
    