
import os
from typing import Literal

from dotenv import load_dotenv
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_groq import ChatGroq

from utils.config_loader import load_config

load_dotenv()


class ModelLoader:
    def __init__(self, model_provider: Literal["google", "groq"]):
        self.model_provider = model_provider
        self.config = load_config()

    def load_llm(self):
        if self.model_provider == "google":
            gemini_api_key = os.getenv("GEMINI_API_KEY")
            if not gemini_api_key:
                raise RuntimeError("GEMINI_API_KEY is required for the Google model")
            model_name = self.config["llm"]["gemini"]["model"]
            return ChatGoogleGenerativeAI(model=model_name, api_key=gemini_api_key)

        if self.model_provider == "groq":
            groq_api_key = os.getenv("GROQ_API_KEY")
            if not groq_api_key:
                raise RuntimeError("GROQ_API_KEY is required for the Groq model")
            model_name = self.config["llm"]["groq"]["model"]
            return ChatGroq(api_key=groq_api_key, model=model_name)

        raise ValueError(f"Unsupported model provider: {self.model_provider}")


def load_model(model_provider: Literal["google", "groq"] = "google"):
    return ModelLoader(model_provider).load_llm()