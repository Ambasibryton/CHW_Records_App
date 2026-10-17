# CHW_Records_App
# CHW Connect
### An Offline-First Community Health Worker Records Management System

CHW Connect is an Android-based application designed to help Community Health Workers (CHWs) capture, manage, and synchronize patient visit records, including in environments where internet connectivity is unreliable.

The system follows an **offline-first architecture**, allowing records to be stored locally on the device and synchronized with a backend API when network connectivity is available.

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running the Backend](#running-the-backend)
- [Running the Android App](#running-the-android-app)
- [Database Design](#database-design)
- [Synchronization Workflow](#synchronization-workflow)
- [API Overview](#api-overview)
- [Security and Privacy](#security-and-privacy)
- [Current Development Status](#current-development-status)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

Community Health Workers play an important role in delivering primary healthcare services at the community level. However, recording and managing patient visits can be challenging when paper-based processes or unreliable internet connectivity are involved.

CHW Connect aims to provide a digital solution that supports:

- Local recording of patient visits.
- Persistent storage of records on Android devices.
- Management of visit information and clinical observations.
- Communication between the Android application and a FastAPI backend.
- Synchronization of locally stored records with a central server.

The project is being developed as an offline-first system, with reliable synchronization and protection of sensitive health information as key design considerations.

## Key Features

### Android Application

- **Visit recording:** Capture patient and visit information.
- **Offline storage:** Store records locally using Room, built on SQLite.
- **Visit history:** Display saved visit records.
- **Visit details:** View patient and visit information.
- **Synchronization status:** Track whether a record is pending synchronization.
- **Backend integration:** Communicate with the REST API when the server is reachable.

### Backend API

- RESTful API built with FastAPI.
- Request data validation using Pydantic.
- SQLite database support for persistent backend storage.
- API endpoints for receiving and retrieving visit records.
- Health-check endpoint for testing server availability.

## System Architecture

The system has three main components:

1. **Android client:** The user interface and visit-recording functionality.
2. **Local database:** Room/SQLite stores records on the phone.
3. **Backend server:** FastAPI exposes endpoints and stores records in a separate SQLite database.

```text
+-----------------------------+
|       CHW Android App       |
|     Kotlin + Jetpack        |
|          Compose            |
+--------------+--------------+
               |
               v
+-----------------------------+
|       Room Database         |
|  Local visit_records table  |
|     Offline persistence     |
+--------------+--------------+
               |
               | HTTP REST API
               | When connected
               v
+-----------------------------+
|       FastAPI Backend       |
|     Request validation      |
|         Pydantic            |
+--------------+--------------+
               |
               v
+-----------------------------+
|      SQLite Database        |
|       Server-side data      |
+-----------------------------+
```

**Important:** The local Room database and backend SQLite database are separate. A record saved on the phone is not automatically guaranteed to exist on the server; successful synchronization must be verified.

## Technology Stack

| Component | Technology |
|---|---|
| Mobile platform | Android |
| Programming language | Kotlin |
| User interface | Jetpack Compose |
| Local persistence | Room / SQLite |
| Backend framework | FastAPI |
| Backend data validation | Pydantic |
| API server | Uvicorn |
| Backend database | SQLite |
| Version control | Git and GitHub |
| Development environment | Android Studio and Python |

## Project Structure

The repository is organized to keep the Android application and backend code together.

```text
CHW_Records_App/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
├── backend/
│   ├── main.py
│   └── requirements.txt
├── gradle/
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

The exact Android source package structure may vary as development continues.

## Getting Started

### Prerequisites

Install the following tools:

- Android Studio.
- Android SDK and Kotlin support.
- Python 3.
- Git.
- A physical Android device or emulator for testing.
- An internet connection for backend synchronization tests.

### Clone the Repository

```bash
git clone git@github.com:Ambasibryton/CHW_Records_App.git
cd CHW_Records_App
```

Alternatively, clone using the HTTPS repository URL.

## Running the Backend

### 1. Navigate to the backend directory

```bash
cd backend
```

### 2. Create a Python virtual environment

On Ubuntu:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

### 3. Install dependencies

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 4. Start the API server

```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

For local testing, the API will normally be available at:

- API base URL: `http://127.0.0.1:8000`
- Interactive API documentation: `http://127.0.0.1:8000/docs`
- Health-check endpoint: use the endpoint defined in `main.py`.

The `--reload` option is intended for development, not production.

### 5. Test the backend

Open a browser on the computer running the server and visit:

```text
http://127.0.0.1:8000/docs
```

For a physical phone to connect, configure the Android application to use the computer's reachable LAN IP address or a deployed HTTPS URL. Do not use `127.0.0.1` as the server address on the phone because it refers to the phone itself.

## Running the Android App

1. Open the repository in Android Studio.
2. Allow Gradle to synchronize.
3. Connect an Android phone with USB debugging enabled, or start an emulator.
4. Select the app run configuration.
5. Run the application.
6. Create a visit record and verify that it is stored locally.

The app should be tested in both connected and disconnected conditions to confirm that locally stored records remain available offline.

## Database Design

### Local database

The Android app uses a Room entity named `VisitRecord`, stored in the `visit_records` table.

The record model includes fields such as:

- `localId` — local primary key.
- `chwId` — Community Health Worker identifier.
- `patientName` — patient name.
- `villageName` — village or community.
- `visitType` — type of visit.
- `vitalsBp` — blood pressure.
- `vitalsTemp` — temperature.
- `urgencyLevel` — visit urgency classification.
- `visitNotes` — optional visit notes.
- `createdAtLocal` — record creation timestamp.
- `syncStatus` — synchronization state, such as `Pending`.

### Backend database

The backend uses SQLite for server-side persistence. Its database schema is defined by the backend implementation.

The local and backend databases should be treated as separate data stores. Synchronization logic is responsible for transferring records between them.

## Synchronization Workflow

The intended offline-first workflow is:

1. The CHW creates a visit record.
2. The Android application saves the record to Room.
3. The record is initially marked as `Pending`.
4. The application sends the record to the backend when connectivity is available.
5. The backend validates the request and stores the visit.
6. The Android application marks the record as synchronized only after receiving and handling a successful server response.

If a request fails, the record should remain pending so it can be retried.

**Development note:** Reliable synchronization requires more than an internet connection. The client and backend must agree on the API endpoint, HTTP method, request schema, response handling, and duplicate-record prevention strategy.

## API Overview

The backend exposes REST endpoints defined in `backend/main.py`.

Typical responsibilities include:

| Operation | Purpose |
|---|---|
| Health check | Confirm that the backend is reachable |
| Submit visit | Send a visit record to the backend |
| Retrieve visits | Read records stored on the backend |

Consult the interactive API documentation at `/docs` to confirm the actual endpoint paths, methods, request bodies, and responses implemented in the current version.

## Security and Privacy

Patient health information is sensitive. CHW Connect should be developed and deployed with appropriate safeguards.

- Never commit real patient records or database files to GitHub.
- Never commit passwords, access tokens, signing keys, or secret environment files.
- Use HTTPS for production API communication.
- Implement authentication and authorization before using real patient data.
- Validate all incoming API requests.
- Restrict access to records according to user roles.
- Consider encryption, backups, retention policies, and audit logging.
- Use synthetic patient data for development and demonstrations.
- Follow applicable Kenyan data protection requirements, including the Data Protection Act, 2019.

The current implementation should not be assumed to be production-ready until the necessary security controls have been implemented and tested.

## Current Development Status

CHW Connect is an active development project.

| Component | Status |
|---|---|
| Android application | Under development |
| Room local persistence | Implemented and being tested |
| Visit-record form and listing | Under development |
| FastAPI backend | Implemented and being tested |
| Backend SQLite storage | Available |
| Phone-to-backend connectivity | Being tested |
| End-to-end visit synchronization | Requires verification |
| Production security and deployment | Future work |

## Future Improvements

Planned improvements may include:

- Automatic synchronization when connectivity returns.
- Robust retry handling and duplicate prevention.
- User authentication and role-based access.
- Secure transport and protection of stored health data.
- Improved reporting and visit-history filtering.
- Server deployment with HTTPS.
- Automated tests for API endpoints and database operations.
- Backup and recovery mechanisms.
- Synchronization conflict resolution.
- Monitoring and audit logging.

## Contributing

Contributions and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make and test your changes.
4. Commit with a clear message.
5. Submit a pull request describing the changes.

Do not include real patient information, credentials, or private database files in contributions.

## License

No license has been specified yet. Until a license is added to the repository, reuse and redistribution permissions should not be assumed.

---

**Project:** CHW Connect  
**Repository:** [Ambasibryton/CHW_Records_App](https://github.com/Ambasibryton/CHW_Records_App)  
**Platform:** Android + FastAPI  
**Development approach:** Offline-first

