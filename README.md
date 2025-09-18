# Smart Daily Expense Tracker
**Author:** Arjun Bhatt

## App Overview
A lightweight Smart Expense Tracker module for small business owners to capture daily expenses quickly. Built with Jetpack Compose, MVVM, and modular structure to be AI-assisted and extendable.

## AI Usage Summary
I used ChatGPT and GitHub Copilot to:
- Generate Compose UI component templates and ViewModel scaffolding.
- Iterate on MVVM architecture and StateFlow usage.
- Improve prompt wording and create README and prompt logs.

## Prompt Logs (key prompts)
- "Generate a Jetpack Compose Expense Entry screen with Title, Amount(₹), Category selector, Notes, and optional receipt image upload."
- "Create a ViewModel using Kotlin, StateFlow, and a Repository interface for in-memory storage of Expense data."
- "Provide Navigation graph for Compose with three screens: Entry, List, Report."
- "Write validation logic: title non-empty, amount > 0."

(Full prompt copies available in `prompt_logs.txt`)

## Checklist of Features Implemented
- [x] Jetpack Compose UI skeleton for Expense Entry, Expense List, Expense Report
- [x] MVVM architecture with ViewModel + StateFlow
- [x] In-memory Repository implementation
- [x] Navigation between screens
- [x] Validations (title required, amount > 0)
- [x] Theme switcher (Light/Dark) - skeleton
- [ ] Room persistence (planned)
- [ ] Export to CSV/PDF (planned)
- [ ] Chart rendering (mocked data present)
- [ ] APK (not built in this environment) — see `apk_instructions.txt`

## APK Download Link
This environment cannot build Android APKs. See `apk_instructions.txt` for steps to build locally. If you want, I can produce a CI-ready GitHub Actions YAML to build the APK.

## Screenshots
Placeholder images are in `screenshots/` (replace with real screenshots after running the app).

## Resume
Replace `resume.pdf` with your actual resume file (resume.pdf/.docx/.txt). Current file is a placeholder.

## How to open the project
1. Open Android Studio (Electric Eel or newer recommended).
2. Import the project root as a Gradle project.
3. Build & Run on an emulator or device (minSdkVersion 23 in module-gradle file).
4. To generate an APK: Build -> Build Bundle(s) / APK(s) -> Build APK(s).

## Notes
This package contains AI prompt logs and an AI usage summary as required.
