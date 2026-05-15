# Namma-Raste Health 🛣️

**Namma-Raste Health** is an AI-assisted Android application developed to modernize the monitoring, assessment, and maintenance of urban road infrastructure in Bengaluru. The platform is designed to digitally transform conventional road inspection workflows by combining infrastructure analytics, geospatial intelligence, and telemetry-based monitoring into a centralized mobile solution.

Urban road maintenance often relies on manual inspections, delayed reporting systems, and fragmented infrastructure records, leading to inefficient maintenance planning and slower response times. Namma-Raste Health addresses these challenges by enabling authorities and infrastructure teams to perform real-time road condition assessment, damage reporting, and maintenance prioritization directly from mobile devices.

The application provides a comprehensive ecosystem for monitoring road health across multiple operational parameters such as pothole density, traffic exposure, drainage condition, accident risk, structural degradation, and maintenance urgency. Using integrated GPS telemetry and CameraX-based image capture, users can report road damage incidents with precise location data and visual evidence, ensuring accurate infrastructure documentation and faster issue resolution.

The platform also incorporates a live infrastructure visualization system powered by Google Maps, allowing users to geographically monitor road conditions, identify damage hotspots, and analyze city-wide infrastructure trends. Through analytical dashboards and telemetry-driven scoring mechanisms, authorities can evaluate infrastructure quality and optimize maintenance resource allocation using data-driven insights.

Built using modern Android development practices including MVVM architecture, Room Database, Kotlin Coroutines, and Material Design 3, the application emphasizes scalability, maintainability, and operational efficiency. Namma-Raste Health demonstrates how mobile technology and intelligent infrastructure analytics can contribute toward smarter urban governance, improved road safety, and more sustainable city infrastructure management.

---

# 📱 Application Screenshots

## 🏙 City Health Dashboard

<p align="center">
  <img src="https://raw.githubusercontent.com/Nidhirk2113/Namma_Raste_Health/main/Screenshots/City_health_overview_screen.png" width="260"/>
</p>

Provides a real-time overview of infrastructure health including road statistics, active reports, operational metrics, and maintenance indicators.

---

## 🛣 Infrastructure Directory

<p align="center">
  <img src="https://raw.githubusercontent.com/Nidhirk2113/Namma_Raste_Health/main/Screenshots/Infrastructure_directory_screen.png" width="260"/>
</p>

A centralized infrastructure management module with road registration, smart search, operational filtering, and maintenance categorization.

---

## 🔍 Diagnostic Scan System

<p align="center">
  <img src="https://raw.githubusercontent.com/Nidhirk2113/Namma_Raste_Health/main/Screenshots/Diagnostic_scan_screen.png" width="260"/>
</p>

Capture and submit structural damage reports with:
- CameraX image evidence
- GPS telemetry
- Damage classification
- Timestamp logging
- Diagnostic analysis notes

---

## 🗺 Live Damage Map

<p align="center">
  <img src="https://raw.githubusercontent.com/Nidhirk2113/Namma_Raste_Health/main/Screenshots/Live_Damage_maps_screen.png" width="260"/>
</p>

Google Maps powered visualization system for monitoring reported infrastructure damage and identifying maintenance hotspots across the city.

---

## 📊 Infrastructure Quality Rankings

<p align="center">
  <img src="https://raw.githubusercontent.com/Nidhirk2113/Namma_Raste_Health/main/Screenshots/safety_rankings_screen.png" width="260"/>
</p>

Ranks infrastructure quality based on telemetry metrics, traffic exposure, structural degradation, and maintenance urgency.

---

## 🚀 Key Features

- **City Health Dashboard**: Real-time overview of Bengaluru's road network, including average damage levels, traffic flow statistics, and high-priority maintenance alerts.

- **Infrastructure Directory**: A comprehensive list of road assets with advanced search and operational filtering.

- **Diagnostic Scans (Reporting)**: Capture structural damage reports including:
    - **CameraX Integration** for visual evidence
    - **GPS Telemetry** for accurate location tracking
    - Damage classification and timestamp logging

- **Detailed Road Metrics**:
    - Pothole counts
    - Traffic flow analysis
    - Drainage and streetlight integrity
    - Estimated repair costs
    - Contractor and maintenance tracking

- **PDF Health Certificates**: Export infrastructure health reports for inspections and documentation.

- **Live Damage Map**: Real-time geographical visualization of reported infrastructure damage using Google Maps.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Material Design 3
- **Jetpack Libraries**:
    - ViewBinding
    - Navigation Component
    - Room Database
    - ViewModel & LiveData
    - CameraX
- **External Dependencies**:
    - Google Maps SDK
    - Fused Location Provider
    - MPAndroidChart
    - Kotlin Coroutines

---

## 🏗 Project Structure

```text
com.nammaraste.health
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── database
│   └── repository
│
├── ui
│   ├── dashboard
│   ├── roads
│   ├── report
│   ├── detail
│   ├── rankings
│   └── map
│
├── viewmodel
│
└── util
```

---

## 🗄 Database & Migrations

The app utilizes a Room database (`namma_raste_db`) with migration support for scalable infrastructure monitoring.

### Key Tables
- `roads`
- `damage_reports`
- `routes`
- `maintenance_logs`

### Infrastructure Metrics Captured
- Pothole count
- Traffic density
- Structural crack severity
- Drainage condition
- Accident risk score
- Estimated repair cost
- Maintenance status

---

## ⚙️ Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Nidhirk2113/Namma_Raste_Health.git
```

### 2. Open in Android Studio

Recommended:
- Android Studio Panda / Ladybug or newer
- Java 17+

### 3. Configure Google Maps API Key

Add your API key inside:

```properties
local.properties
```

Example:

```properties
MAPS_API_KEY=YOUR_API_KEY
```

### 4. Build & Run

- Sync Gradle
- Connect emulator or Android device
- Run the `app` module

Minimum SDK:
- Android 8.0 (API 26)

---

## 📈 Future Enhancements

- AI-based pothole detection using TensorFlow Lite
- Firebase cloud synchronization
- Predictive maintenance analytics
- IoT sensor integration
- Government monitoring dashboard
- Offline-first infrastructure synchronization

---

## 🎯 Project Objectives

- Improve urban road maintenance efficiency
- Digitize infrastructure monitoring workflows
- Reduce manual inspection overhead
- Enable data-driven maintenance prioritization
- Improve infrastructure safety analytics

---

## 🔐 Permissions Used

| Permission | Purpose |
|---|---|
| Camera | Capture structural damage evidence |
| Location | GPS telemetry |
| Storage | Save reports and PDFs |
| Internet | Maps and network operations |

---

## 👨‍💻 Developed Using

- Kotlin
- Android Jetpack
- Material Design 3
- Google Maps Platform
- Room Persistence Library

---

## 📄 License

Copyright © 2026 Namma-Raste Health

Developed for Bengaluru's urban infrastructure monitoring and road safety analytics.
