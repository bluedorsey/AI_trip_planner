import os

from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_groq import ChatGroq

from app.utils.config_loader import load_config

load_dotenv()

# provider -> (API-key env var, chat model class). max_retries=0 below: fail fast so the router
# moves on instead of the client sleeping through its own backoff on a 429.
_PROVIDERS = {
    "google": ("GEMINI_API_KEY", ChatGoogleGenerativeAI),
    "groq": ("GROQ_API_KEY", ChatGroq),
}


def load_model():
    """First model in config.yaml with fallbacks to the rest. Works with bind_tools/with_structured_output."""
    llms = []
    for entry in load_config()["llm"]["models"]:
        env, cls = _PROVIDERS[entry["provider"]]
        if key := os.getenv(env):  # skip providers without a key instead of crashing
            llms.append(cls(model=entry["model"], api_key=key, max_retries=0))
    if not llms:
        raise RuntimeError("No LLM API key set for any model in config.yaml")
    return llms[0].with_fallbacks(llms[1:]) if len(llms) > 1 else llms[0]
