
# TOEIC Learning App
[🇻🇳 Tiếng Việt](README_VI.md) | [🇬🇧 English](README.md)

An Android application designed to help users improve their English skills and prepare for the TOEIC (Test of English for International Communication) exam.

## 1. Project Overview

- **Project Name:** Unnamed
- **Platform:** Android
- **Programming Language:** Java
- **UI Design:** XML / Figma / Canva
- **IDE:** Android Studio
- **Team Size:** 4 members

## 2. Team Members

| No. | Full Name        | Student ID  | Role        |
|---|------------------|-------------|-------------|
| 1 | [Trần Đông Bình] | [745105011] | Team Leader |
| 2 | [Name]           | [ID]        | Developer   |
| 3 | [Name]           | [ID]        | Developer   |
| 4 | [Trần Văn Sơn]           | [ 745105082 ]        | Developer   |

## 3. Main Features

### 3.1. User Authentication
- User registration
- User login and logout
- Personal profile management

### 3.2. Vocabulary Learning
- **Topic-based Learning:** Browse vocabulary topics and view words belonging to each topic.

- **Flashcards:** Learn vocabulary through randomly displayed flashcards.

- **Learning Assessment:** Rate vocabulary retention using three options:
    - **Easy:** 3 days

    - **Hard:** 10–20 minutes
    - **Can't Remember:** 1–5 minutes
  
- **Vocabulary Review:**  Practice learned words through interactive quizzes, including fill-in-the-blank and multiple-choice questions.


### 3.3. Grammar Learning

- **Grammar Topics:** Browse grammar lessons categorized by topics.
- **Grammar Lessons:** View grammar rules, explanations, structures, and examples.
- **Grammar Practice:** Practice grammar knowledge through interactive exercises.
- **Quiz:** Test understanding with different question formats, including multiple-choice and fill-in-the-blank questions.
- **Learning Progress:** Track completed lessons and quiz results.


### 3.4. TOEIC Practice

- **Listening Practice:** Practice TOEIC Listening skills through Parts 1–4.
- **Reading Practice:** Practice TOEIC Reading skills through Parts 5–7.
- **Part-based Practice:** Select individual TOEIC Parts to practice specific skills.
- **Instant Feedback:** View correct answers, explanations, and practice results after completing exercises.

### 3.5. Mock Tests

- **Full-length TOEIC Tests:** Take complete TOEIC Listening and Reading practice tests.
- **Countdown Timer:** Manage test duration with a countdown timer.
- **Automatic Scoring:** Automatically calculate test scores after submission.
- **Detailed Results:** Review test scores, correct answers, and incorrect answers.

### 3.6. Learning Statistics

- **Test History:** View previous test attempts and results.
- **Score Tracking:** Monitor TOEIC scores over time.
- **Learning Progress:** Track learning activities and overall progress.

## 4. Technologies

- Java
- XML
- Android Studio
- Android SDK
- Git & GitHub
- [SQLite / Room / Firebase]

## 5. Project Structure

```text
TOEIC_App/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           └── res/
├── gradle/
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 6. Development Progress

- [ ] Requirement Analysis
- [ ] UI/UX Design
- [ ] Database Design
- [ ] User Authentication
- [ ] Vocabulary Module
- [ ] Grammar Module 
- [ ] TOEIC Practice Module
- [ ] Mock Test Module
- [ ] Statistics Module
- [ ] Testing and Debugging
- [ ] Final Release

## Installation

Follow these steps to set up and run the project locally.

1. **Clone the repository**

   ```bash
   git clone https://github.com/MonkeyFlash/Final_Project_TOEIC_app.git
   ```

2. **Open the project**

    - Open Android Studio.
    - Select **Open** and choose the cloned project folder.
    - Wait for Gradle Sync to finish.

3. **Configure the Android SDK**

    - Make sure the required Android SDK is installed.
    - Install any missing SDK components if Android Studio prompts you.

4. **Run the application**

    - Connect an Android device with USB debugging enabled, or start an Android Emulator.
    - Click the **Run** button in Android Studio to build and launch the application.
## 8. Team Workflow

To ensure smooth collaboration and maintain code quality, all team members should follow these rules:

### 8.1. Task Management
- Each team member is assigned specific tasks and responsibilities.
- Complete assigned tasks within the agreed deadlines.
- Update task progress regularly so that everyone knows what has been completed and what is still in progress.
- Inform the team leader when facing difficulties or delays.

## 8.2. Git & GitHub
- All source code must be uploaded and managed through the team's GitHub repository.
- The `main` branch is the official version of the project. Do not modify it directly.
- Before working on a feature, create a separate branch from the latest `main` branch.
- Use the following branch naming format: `feature/feature-name`.
- Commit and push your changes regularly using clear and meaningful commit messages.
- Before starting new work, pull the latest changes to avoid working with outdated code.
- When a feature is completed, create a Pull Request (PR) to request merging it into `main`.
- All Pull Requests must be reviewed and approved by the team leader before merging.

### 8.3. Coding Rules
- Use Java for application logic and XML for user interfaces.
- Follow consistent naming conventions for classes, methods, variables, and layout files.
- Organize files into the agreed project structure.
- Do not modify another member's code without discussing it first.
- Avoid duplicating existing code or creating unnecessary files.

### 8.4. Code Review & Testing
- Test your feature before creating a Pull Request.
- Review the code and check for errors before merging.
- Make sure the application still runs correctly after integrating new features.
- Fix reported bugs before considering a task completed.

### 8.5. Team Communication
- Discuss important changes with the team before implementation.
- Ask for help when encountering problems instead of leaving tasks unfinished.
- Keep team members informed about progress and potential conflicts.
- Update project documentation whenever major features or structures change.

## 9. License

This project is developed for educational purposes as part of an Android Programming course.
