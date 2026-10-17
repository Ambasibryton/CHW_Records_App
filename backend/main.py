
from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional
import sqlite3
import json

app = FastAPI(
    title="CHW Connect API",
    version="1.0.0"
)

DB_NAME = "visits.db"


class VisitRecord(BaseModel):
    localId: str
    patientName: str
    villageName: str
    chwId: str
    visitType: str
    vitalsBp: str
    vitalsTemp: float
    urgencyLevel: str
    visitNotes: Optional[str] = None
    createdAtLocal: str


def get_connection():
    connection = sqlite3.connect(DB_NAME)
    connection.row_factory = sqlite3.Row
    return connection


def initialize_database():
    with get_connection() as connection:
        connection.execute("""
            CREATE TABLE IF NOT EXISTS visits (
                local_id TEXT PRIMARY KEY,
                payload TEXT NOT NULL
            )
        """)


initialize_database()


@app.get("/")
def home():
    return {"message": "CHW Connect API is running"}


@app.get("/health")
def health_check():
    return {"status": "healthy"}


@app.post("/api/visits")
def upload_visit(visit: VisitRecord):
    visit_data = visit.model_dump()
    visit_json = json.dumps(visit_data)

    with get_connection() as connection:
        connection.execute(
            """
            INSERT INTO visits (local_id, payload)
            VALUES (?, ?)
            ON CONFLICT(local_id) DO NOTHING
            """,
            (visit.localId, visit_json)
        )

    return {
        "success": True,
        "message": "Visit received successfully",
        "localId": visit.localId
    }


@app.get("/api/visits")
def get_visits():
    with get_connection() as connection:
        rows = connection.execute(
            "SELECT payload FROM visits ORDER BY rowid DESC"
        ).fetchall()

    return [json.loads(row["payload"]) for row in rows]

