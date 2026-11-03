"""Structured trip plan: turns the agent's Markdown answer into JSON the Android app can render."""
from typing import Literal

from pydantic import BaseModel, Field

from app.utils.model_loader import load_model


class Activity(BaseModel):
    title: str
    description: str = Field(description="One or two sentences.")
    location: str
    type: Literal["Hotel", "Activity", "Sightseeing", "Food", "Transport"]


class Day(BaseModel):
    dayNumber: int
    date: str = Field(description='Short label such as "Day 1" or "Dec 20, Sat".')
    theme: str = Field(description='Short theme of the day, e.g. "Arrival & Relax".')
    activities: list[Activity]


class Plan(BaseModel):
    title: str
    location: str
    dateRange: str = Field(description='e.g. "Dec 20 - Dec 25"; use "Flexible dates" if none was given.')
    budget: str = Field(description='Total estimated cost with currency, e.g. "₹45,000".')
    duration: str = Field(description='e.g. "5 days".')
    rating: str = Field(description='Traveller rating out of 5 as text, e.g. "4.6"; "-" if unknown.')
    days: list[Day]


def structure_plan(markdown: str) -> Plan:
    llm = load_model().with_structured_output(Plan)
    return llm.invoke(
        "Convert this travel plan into the requested structure. Use only information present in it, "
        "keep it faithful, and keep each description short. "
        'Set rating to "-" unless the plan itself states one.\n\n' + markdown
    )
