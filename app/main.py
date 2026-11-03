from fastapi import Depends, FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from app.agent.agentic_workflow import GraphBuilder
from app.auth import current_user
from app.plan import structure_plan


app = FastAPI()

class QueryRequest(BaseModel):
    query: str


def ask_agent(query: str) -> str:
    graph = GraphBuilder()
    output = graph().invoke({"messages": [("user", query)]})
    messages = output.get("messages", [])
    final_message = messages[-1] if messages else None
    final_response = getattr(final_message, "content", str(final_message))
    if isinstance(final_response, list):  # Gemini returns content blocks
        final_response = "".join(b.get("text", "") if isinstance(b, dict) else str(b) for b in final_response)
    return final_response


@app.get("/health")
async def health():
    return {"status": "ok"}

@app.post("/query")
async def query_endpoint(query: QueryRequest, user: dict = Depends(current_user)):
    try:
        return {"response": ask_agent(query.query)}
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)

@app.post("/plan")
def plan_endpoint(query: QueryRequest, user: dict = Depends(current_user)):
    """Structured version of /query for the app: agent answer -> Plan JSON."""
    try:
        return structure_plan(ask_agent(query.query)).model_dump()
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
