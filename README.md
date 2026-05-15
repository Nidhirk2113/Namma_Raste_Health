# Namma-Raste Health 🛣️

**Namma-Raste Health** is a modern Android application designed to monitor and track the structural health of road infrastructure in Bengaluru. Built with a focus on data-driven maintenance, it allows users and officials to catalog road conditions, report damages with visual evidence, and assess maintenance priorities using a telemetry-based health scoring system.

---

## 🚀 Key Features

- **City Health Dashboard**: Real-time overview of Bengaluru's road network, including average damage levels, traffic flow statistics, and high-priority maintenance alerts.
- **Infrastructure Directory**: A comprehensive list of road assets with advanced search (by name, area, contractor, or condition).
- **Diagnostic Scans (Reporting)**: Capture structural damage reports (potholes, erosion, waterlogging) with:
    - **CameraX Integration**: Attach high-resolution visual evidence.
    - **GPS Telemetry**: Automatic coordinate capture for precise damage location.
- **Detailed Road Metrics**: Deep-dive into specific road segments including:
    - Pothole counts & Traffic flow analysis.
    - Streetlight & Drainage integrity status.
    - Estimated restoration costs.
    - Contractor information and warranty tracking.
- **PDF Health Certificates**: Export professional road health reports for offline sharing and documentation.
- **Live Damage Map**: Visual representation of reported issues across the city (via Google Maps).

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-ViewModel-View)
- **UI Framework**: Material Design 3 (Material Components)
- **Jetpack Libraries**:
    - **ViewBinding**: Type-safe view interaction.
    - **Navigation Component**: Simplified fragment-based navigation with SafeArgs.
    - **Room Database**: Local persistent storage with migration support.
    - **ViewModel & LiveData**: Reactive data handling and lifecycle management.
    - **CameraX**: Camera integration for damage evidence.
- **External Dependencies**:
    - **Google Play Services**: Maps and Fused Location Provider.
    - **MPAndroidChart**: Statistical data visualization on the dashboard.
    - **Coroutines**: Asynchronous programming for database and PDF operations.

---

## 🏗 Project Structure

```text
com.nammaraste.health
├── data
│   ├── local
│   │   ├── dao        # Room DAOs (RoadDao, DamageReportDao)
│   │   ├── entity     # Data classes (Road, DamageReport)
│   │   └── NammaRasteDatabase.kt # Database configuration & migrations
│   └── repository     # Single source of truth for data operations
├── ui
│   ├── dashboard      # Overview charts and stats
│   ├── roads          # Road list and adapters
│   ├── report         # Diagnostic scan / reporting form
│   ├── detail         # Road metrics and incident logs
│   └── addroad        # Asset registration
└── util               # Helpers for PDF generation, Health scoring, etc.
```

---

## 🗄 Database & Migrations

The app utilizes a Room database (`namma_raste_db`) with versioned migrations to support evolving data requirements.
- **Current Version**: 7
- **Key Tables**: `roads`, `damage_reports`, `routes`.
- **Migration 6 -> 7**: Added 12 telemetry fields including `potholeCount`, `avgTrafficPerDay`, `accidentRiskScore`, and `estimatedRepairCostInr`.

---

## ⚙️ Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/NammaRasteHealth.git
   ```
2. **Open in Android Studio**:
   - Use Android Studio Ladybug (2024.2.1) or newer.
   - Gradle JDK: Java 17+.
3. **Configure API Keys**:
   - Add your Google Maps API key in `local.properties` or directly in `AndroidManifest.xml` (for development).
4. **Build and Run**:
   - Sync Gradle and run the `:app` module on an emulator or physical device (Min SDK 26).

---

## 📄 License

Copyright © 2025 NammaRaste. All rights reserved.
Developed for Bengaluru's Urban Infrastructure Monitoring.
