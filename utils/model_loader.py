
from langchain_groq import ChatGroq
from langchain_google_genai import ChatGoogleGenerativeAI
from pydantic import BaseModel, Field
from typing import Any, Optional,Literal
from utils.config_loader import load_config
from dotenv import load_dotenv
import os


class Config_loader:
    def __init__(self):
        self.config = load_config()
    def __getitem__(self, key):
        return self.config[key]

class ModelLoader(BaseModel):
    model_provider : Literal["google","groq"]
    config: Optional[Config_loader] = Field(default=None, exclude=True)
    def model_post_init(self,__context:Any) -> None:
        self.model = Config_loader()

    class Config:
        arbitrary_types_allowed = True

    def load_llm(self):
        print('LLm loading')
        if self.model_provider == "google":
            print('Loading google gemini model')
            gemini_api_key = os.getenv("GEMINI_API_KEY")
            model_name = self.config["llm"]["gemini"]["model"]
            llm = ChatGoogleGenerativeAI(model=model_name, api_key=gemini_api_key)
        elif self.model_provider == "groq":
            print('Loading groq model')
            groq_api_key = os.getenv("GROQ_API_KEY")
            model_name = self.config["llm"]["groq"]["model"]
            llm = ChatGroq(groq_api_key=groq_api_key, model_name=model_name)
        return llm