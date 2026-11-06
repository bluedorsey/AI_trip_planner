import json

from fastapi import Depends, FastAPI
from fastapi.responses import JSONResponse, StreamingResponse
from pydantic import BaseModel
from app.agent.agentic_workflow import GraphBuilder
from app.auth import current_user
from app.plan import message_text, stream_plan


app = FastAPI()

class QueryRequest(BaseModel):
    query: str


def ask_agent(query: str) -> str:
    output = GraphBuilder()().invoke({"messages": [("user", query)]})
    messages = output.get("messages", [])
    final_message = messages[-1] if messages else None
    return message_text(getattr(final_message, "content", str(final_message)))


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
    """Streams the trip for the app as JSON lines: status*, meta, day*, then done (or error)."""
    def lines():
        try:
            for event in stream_plan(query.query):
                yield json.dumps(event, ensure_ascii=False) + "\n"
        except Exception as e:  # headers are already sent by now, so report in-band
            yield json.dumps({"type": "error", "message": str(e)}) + "\n"
    return StreamingResponse(lines(), media_type="application/x-ndjson", headers={"Cache-Control": "no-cache"})
