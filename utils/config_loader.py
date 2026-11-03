import yaml
from pathlib import Path

def load_config(config_path: str = "config/config.yaml") -> dict:
    path = Path(config_path)
    if not path.is_absolute():
        path = Path(__file__).resolve().parent.parent / path
    with path.open("r", encoding="utf-8") as f:
        config = yaml.safe_load(f)

    return config or {}
