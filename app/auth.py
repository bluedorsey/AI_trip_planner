"""Firebase ID-token auth. Use `Depends(current_user)` on any route that needs a signed-in user.

The Android app sends `Authorization: Bearer <Firebase ID token>`. Verifying that token only
needs the project ID (Google's public certs do the rest), so no service-account credentials.
"""
import os

from dotenv import load_dotenv
from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from google.auth.transport import requests as google_requests
from google.oauth2 import id_token

load_dotenv()

PROJECT_ID = os.getenv("FIREBASE_PROJECT_ID", "ai-trip-planner-e9ee8")
_bearer = HTTPBearer(auto_error=False)
_request = google_requests.Request()


def current_user(creds: HTTPAuthorizationCredentials | None = Depends(_bearer)) -> dict:
    """Returns the decoded token claims (sub/user_id, email, ...) or raises 401."""
    if creds is None:
        raise HTTPException(401, "Missing bearer token", headers={"WWW-Authenticate": "Bearer"})
    try:
        return id_token.verify_firebase_token(creds.credentials, _request, audience=PROJECT_ID)
    except Exception as e:
        print(f"verify_firebase_token failed: {e!r}")  # server log only; response stays generic
        raise HTTPException(401, "Invalid or expired token", headers={"WWW-Authenticate": "Bearer"})
