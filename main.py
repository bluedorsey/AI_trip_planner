from fastapi import FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from agent.agentic_workflow import GraphBuilder


app = FastAPI()

class QueryRequest(BaseModel):
    query: str

@app.post("/query")
async def query_endpoint(query: QueryRequest):
    try : 
        graph = GraphBuilder(model_provider="google")
        output = graph().invoke({"messages": [("user", query.query)]})
        messages = output.get("messages", [])
        final_message = messages[-1] if messages else None
        final_response = getattr(final_message, "content", str(final_message))
        if isinstance(final_response, list):  # Gemini returns content blocks
            final_response = "".join(b.get("text", "") if isinstance(b, dict) else str(b) for b in final_response)
        return {"response": final_response}

    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
    