# Namma-Raste Health 🛣️

<p align="center">
  <b>AI-Powered Road Infrastructure Monitoring System for Bengaluru</b>
</p>

<p align="center">
  Monitor • Analyze • Report • Improve
</p>

---

## 📌 Overview

**Namma-Raste Health** is a smart Android-based infrastructure monitoring application developed to digitally assess, monitor, and manage the structural health of roads across Bengaluru.

The application enables authorities and users to:
- Register road assets
- Capture road damage reports with visual evidence
- Track infrastructure conditions using telemetry data
- Visualize city-wide damage distribution on maps
- Generate analytical insights for maintenance prioritization

The platform combines geospatial intelligence, damage diagnostics, and real-time infrastructure analytics into a unified mobile solution.

---

# 📱 Application Screenshots

## 🏙 City Health Dashboard

<p align="center">
  <img src="screenshots/dashboard.png" width="260"/>
</p>

Provides a real-time overview of road infrastructure statistics including:
- Total roads monitored
- Open maintenance cases
- Damage severity indicators
- Traffic and infrastructure metrics

---

## 🛣 Infrastructure Directory

<p align="center">
  <img src="screenshots/infrastructure_directory.png" width="260"/>
</p>

Enables:
- Road asset registration
- Advanced infrastructure search
- Maintenance classification
- Operational status tracking

---

## 🔍 Diagnostic Scan System

<p align="center">
  <img src="screenshots/diagnostic_scan.png" width="260"/>
</p>

Integrated damage reporting system featuring:
- CameraX image capture
- GPS telemetry
- Damage classification
- Incident documentation
- Structural analysis logging

---

## 🗺 Live Damage Map

<p align="center">
  <img src="screenshots/live_damage_map.png" width="260"/>
</p>

Google Maps powered visualization system for:
- Real-time issue tracking
- Damage hotspot analysis
- Geographical infrastructure monitoring
- Maintenance coordination

---

## 📊 Infrastructure Quality Rankings

<p align="center">
  <img src="screenshots/safety_rankings.png" width="260"/>
</p>

Ranks infrastructure quality based on:
- Damage severity
- Traffic load
- Structural degradation
- Repair urgency metrics

---

# 🚀 Core Features

## ✅ Smart Infrastructure Monitoring
Track and monitor road conditions through a centralized digital platform.

## ✅ Real-Time Damage Reporting
Submit structural damage reports with:
- GPS coordinates
- Camera evidence
- Damage category tagging
- Timestamp logging

## ✅ Infrastructure Analytics
Analyze:
- Traffic intensity
- Pothole density
- Maintenance trends
- Infrastructure deterioration

## ✅ Google Maps Integration
Visualize road assets and incidents geographically using interactive mapping.

## ✅ PDF Report Generation
Generate professional infrastructure health certificates and maintenance summaries.

## ✅ Telemetry-Based Assessment
Evaluate infrastructure using multiple operational metrics and scoring systems.

---

# 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM |
| UI Framework | Material Design 3 |
| Database | Room Database |
| Navigation | Jetpack Navigation Component |
| Async Operations | Kotlin Coroutines |
| Maps | Google Maps SDK |
| Location Services | Fused Location Provider |
| Camera Integration | CameraX |
| Charts & Analytics | MPAndroidChart |
| UI Binding | ViewBinding |

---

# 🏗 Architecture Overview

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern for scalable and maintainable Android development.

```text
com.nammaraste.health
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── database
│   │
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

# 🗄 Database System

The application uses **Room Database** for local persistent storage with migration support.

### Key Tables
- `roads`
- `damage_reports`
- `routes`
- `maintenance_logs`

### Infrastructure Metrics Captured
- Pothole count
- Traffic flow
- Structural crack severity
- Accident risk score
- Drainage condition
- Estimated repair cost
- Maintenance status

---

# ⚙️ Installation Guide

## 1️⃣ Clone Repository

```bash
git clone https://github.com/Nidhirk2113/Namma-Raste-Health.git
```

---

## 2️⃣ Open in Android Studio

Recommended:
- Android Studio Ladybug / Panda or newer
- JDK 17+

---

## 3️⃣ Configure Google Maps API

Add your API key inside:

```properties
local.properties
```

Example:

```properties
MAPS_API_KEY=YOUR_API_KEY
```

---

## 4️⃣ Build & Run

- Sync Gradle
- Connect emulator/device
- Run `app` module

Minimum SDK Supported:
- Android 8.0 (API 26)

---

# 📈 Future Enhancements

- AI-based pothole detection using TensorFlow Lite
- Cloud synchronization with Firebase
- Predictive maintenance analytics
- IoT sensor integration
- Government maintenance dashboard
- Offline-first synchronization
- Live citizen reporting portal

---

# 🎯 Project Objectives

- Improve urban road maintenance efficiency
- Digitize infrastructure monitoring workflows
- Reduce manual inspection overhead
- Enable faster maintenance prioritization
- Provide data-driven infrastructure insights

---

# 🔐 Permissions Used

| Permission | Purpose |
|---|---|
| Camera | Capture damage evidence |
| Location | GPS telemetry |
| Storage | Save reports & PDFs |
| Internet | Maps & cloud services |

---

# 👨‍💻 Developed With

- Kotlin
- Android Jetpack
- Material Design Principles
- Google Maps Platform

---

# 📄 License

Copyright © 2026 Namma-Raste Health

Developed for smart urban infrastructure monitoring and road safety analytics in Bengaluru.

All rights reserved.
