from pathlib import Path
from typing import List

from setuptools import find_packages, setup


def get_requirements() -> List[str]:
    """
    This function will return list of requirements
    """
    # Relative to this file, not whatever cwd pip builds from.
    lines = (Path(__file__).parent / "requirements.txt").read_text().splitlines()
    return [r for r in (line.strip() for line in lines) if r and r != "-e ."]


setup(
    name="AI-TRAVEL-PLANNER",
    version="0.0.1",
    author="Ashutosh Sahu",
    author_email="sahuashutosh563@gmail.com",
    packages=find_packages(),
    python_requires=">=3.10",
    install_requires=get_requirements(),
)
