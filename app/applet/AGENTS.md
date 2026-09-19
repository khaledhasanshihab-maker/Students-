# Project Memory & Developer Context: Teacher Student Manager

## Project Overview
- **App Name**: Teacher Student Manager
- **Lead Developer**: **Khaled Hasan Shihab** (`khalidhassanshihab4@gmail.com`)
- **App Type**: Modern Android Application (Native Kotlin + Jetpack Compose)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern + Clean Architecture
- **Local Persistence**: Android Room Database (`AppDatabase`) with SQLite and reactive Kotlin StateFlow
- **Session Management**: Secure PIN/Password Teacher Authentication (`SessionManager`)

---

## Developer Credit & Branding Rules (Strictly Preserved)
1. **Startup Splash Screen**:
   - Must prominently display **"Developed by Khaled Hasan Shihab"** with the developer badge and school branding on every startup before navigating to Login or Dashboard.
2. **Login & Settings**:
   - Login screen and Settings About section must keep the developer attribution: **"Developed by Khaled Hasan Shihab"**.
3. **APK Artifacts**:
   - Always retain the standalone installable APK in the project root: `TeacherStudentManager.apk`.

---

## Current Architecture & Modules
- **UI Framework**: Jetpack Compose with Material 3 design system.
  - Theme colors: `PrimaryNavy` (`#1E3A8A`), `SecondaryTeal` (`#0D9488`), `PrimaryLight` (`#3B82F6`), soft slate surfaces.
- **Database (`com.example.data`)**:
  - `Teacher`: Local teacher profiles & credentials
  - `Department`: Academic departments / classes / batches
  - `Student`: Student roll numbers, registration numbers, contact info, and department references
  - `Attendance`: Date-wise student attendance records (Present / Absent / Late)
  - `Marks`: Subject-wise exam marks, grading, and evaluations
- **Screens (`com.example.ui.screens`)**:
  - `SplashScreen`: Animated launch screen with developer credit
  - `LoginScreen`: Teacher PIN/Password login & registration
  - `DashboardScreen`: Quick metrics, active classes, attendance rate, total students
  - `DepartmentsScreen`: Add, edit, and organize classes/departments
  - `StudentsScreen`: Student directory, search, filter, and detail views
  - `AttendanceScreen`: Fast daily roll-call attendance sheet with one-tap toggle
  - `MarksScreen`: Exam evaluation and grading management
  - `ReportsScreen`: Analytical summaries, performance breakdown, and statistics
  - `SettingsScreen`: Database reset, backup/restore preferences, theme, and about section

---

## Completed Features & Enhancements
1. **PDF & Excel Export**:
   - Native Android multi-page vector PDF generation with full tabular attendance and CT/Mid-term marks.
   - Excel (`.xls`) spreadsheet export formatted with styled headers, auto column widths, and color-coded percentages.
   - Standard CSV export.
   - FileProvider setup for sharing files across Gmail, Drive, WhatsApp, etc.
2. **Guardian Absence & Progress Alerts**:
   - Instant SMS alert button when a student is marked Absent during daily attendance.
   - Direct Send-to-Guardian action in Student Report cards with attendance rates and marks breakdown.
3. **Cloud Sync & Multi-Device Backup (Option 2 - Firebase)**:
   - Firebase Firestore integration and `CloudSyncManager` ready.
   - Sync UI added to Settings screen with real-time feedback.
4. **Predefined Semester-wise Curriculum (Diploma CST)**:
   - Full 1st to 7th Semester subject catalog with authentic codes and names.
   - Automatic pre-population, one-tap syllabus assignment, and quick-picker chips.

---

## Future Roadmap & Planned Features
1. **Desktop & Web Integration**:
   - Compose Multiplatform Desktop (`.exe` / `.msi`) or Web Dashboard support.
2. **QR / Barcode Scanner**:
   - Quick attendance check-in via student ID card scanner.
3. **Automated SMS Gateway**:
   - Bulk automatic SMS gateway via Twilio or local GSM modem API.
