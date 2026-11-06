import os

from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_groq import ChatGroq

from app.utils.config_loader import load_config

load_dotenv()

# provider -> (API-key env var, chat model class, client options).
_PROVIDERS = {
    # Gemini's 503 "high demand" clears in seconds; retrying (~7s at most) keeps the best model answering.
    "google": ("GEMINI_API_KEY", ChatGoogleGenerativeAI, {"max_retries": 4}),
    # Fail fast: Groq's backoff sleeps through a long 429. Its default output cap (3072) truncates plans.
    "groq": ("GROQ_API_KEY", ChatGroq, {"max_retries": 0, "max_tokens": 8000}),
}


def load_model():
    """First model in config.yaml with fallbacks to the rest. Works with bind_tools/with_structured_output."""
    llms = []
    for entry in load_config()["llm"]["models"]:
        env, cls, options = _PROVIDERS[entry["provider"]]
        if key := os.getenv(env):  # skip providers without a key instead of crashing
            llms.append(cls(model=entry["model"], api_key=key, **options))
    if not llms:
        raise RuntimeError("No LLM API key set for any model in config.yaml")
    return llms[0].with_fallbacks(llms[1:]) if len(llms) > 1 else llms[0]
