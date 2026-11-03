"""Firebase ID-token auth. Use `Depends(current_user)` on any route that needs a signed-in user.

The Android app sends `Authorization: Bearer <Firebase ID token>`. Verifying that token only
needs the project ID (Google's public certs do the rest). SERVICE_ACCOUNT_KEY is used as
credentials only when it holds the full service-account JSON.
"""
import json
import os

import firebase_admin
from dotenv import load_dotenv
from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from firebase_admin import auth, credentials

load_dotenv()

PROJECT_ID = os.getenv("FIREBASE_PROJECT_ID", "ai-trip-planner-e9ee8")
_bearer = HTTPBearer(auto_error=False)


def _init() -> None:
    if firebase_admin._apps:
        return
    key = os.getenv("SERVICE_ACCOUNT_KEY", "").strip()
    cred = credentials.Certificate(json.loads(key)) if key.startswith("{") else None
    firebase_admin.initialize_app(cred, {"projectId": PROJECT_ID})


def current_user(creds: HTTPAuthorizationCredentials | None = Depends(_bearer)) -> dict:
    """Returns the decoded token (uid, email, ...) or raises 401."""
    if creds is None:
        raise HTTPException(401, "Missing bearer token", headers={"WWW-Authenticate": "Bearer"})
    _init()
    try:
        return auth.verify_id_token(creds.credentials)
    except Exception:
        raise HTTPException(401, "Invalid or expired token", headers={"WWW-Authenticate": "Bearer"})
