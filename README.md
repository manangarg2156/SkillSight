# SkillSight — standalone Java edition

SkillSight is a local desktop application written in Java. It uses Java Swing and APIs included with the JDK. It does not use Maven, JavaFX, a SQL server, or downloaded libraries.

## Code layout

- `SkillSight.java` is the application entry point and contains the Swing screens, navigation, and UI event handling.
- `LocalDataStore.java` handles local save and restore operations.
- `PasswordHasher.java` handles password hashing and verification.
- `RoadmapCatalog.java` contains the skill-specific learning stages and learning resources.
- `ReportExporter.java` creates CSV and PDF reports.
- `AssessmentQuestionBank.java` provides the skill-specific question sets.
- `AssessmentScorer.java` calculates marks, percentage, accuracy, and topic/difficulty breakdowns.

Java methods must live inside a class, so related UI methods stay together in `SkillSight.java`; independent responsibilities are in their own files. The nested account and skill data classes stay there to keep compatibility with data files already created by earlier versions.

## Run it

Install JDK 21 or newer, open PowerShell in this folder, and run:

```powershell
java SkillSight.java
```

The first launch shows account creation. Create a student profile, then sign in. No database server or environment variables are needed.

## Included features

- Student registration and login. Passwords are stored as PBKDF2-HMAC-SHA256 hashes with per-account random salts.
- Local profile editing, skill self-assessment, skill targets, and immutable score attempts.
- Dashboard metrics and custom-drawn score charts; an assessment history chart appears as attempts accumulate.
- Filterable skills table, explainable career guidance, and saved learning-roadmap progress.
- A separate five-stage learning path for every catalog skill, with skill-specific topics and practice activities.
- A timed, 10-question knowledge assessment for every catalog skill, with 3 Easy, 4 Medium, and 3 Difficult questions. Each item has its own scenario, four distinct skill-specific choices, a shuffled answer position, and an explanation; repeated prompts and option sets are rejected.
- Results include score, accuracy, unanswered items, time, difficulty breakdown, topic strengths, and next-practice suggestions. Assessment attempts are saved locally and can be opened from Assessment History.
- Analytics include skill comparison, separate skill trend lines, a skill-strength radar, proficiency distribution, and attempt-to-attempt comparison. Filters can narrow results by skill, date range, and assessment difficulty.
- Student report preview and CSV/PDF export, plus local data backup and restore.
- Data is stored under `%USERPROFILE%\.skillsight\skillsight.data` on Windows. It remains on this computer and is not shared or synchronized by the app.

All proficiency ratings are self-reported, not verified expertise. Career matches are transparent guidance estimates, not job or suitability guarantees. The data file contains password hashes and personal information; keep Windows account access private and use the backup controls carefully.

## Troubleshooting

- **`java` is not recognized:** install a JDK (not only a JRE), then open a new PowerShell window and check `java --version`.
- **Unsupported source version / source file mode:** use JDK 21 or newer and run the command from the project folder.
- **Forgot password:** this local edition has no email-based password reset. The app has no server account recovery.
- **Move to another computer:** use Settings → Back up my data, copy the backup securely, then choose Restore from backup on the other installation.

## Viva notes

- **Why no SQL?** The single-user local edition stores Java-serialized domain objects in a file so it can run without configuring a database server.
- **How are passwords protected?** PBKDF2-HMAC-SHA256 with 210,000 iterations, a random 16-byte salt, and constant-time hash comparison.
- **How are career scores calculated?** Each sample career has transparent skill weights. Match percentage is the weighted sum of current score fractions; missing skill scores contribute zero.
- **How do assessments work?** The local Java question bank supplies 10 individually authored multiple-choice items per supported skill (3 Easy, 4 Medium, 3 Difficult), with unique choices and explanations. Scores are calculated from saved responses; student code is never executed.
- **What question formats are supported?** The current bank supports multiple-choice questions. It does not execute student code or include code-output questions; those need a separate safe evaluator and curated question content.
- **What is a limitation?** The app is a desktop local profile, not a multi-user hosted system. The file is not encrypted at rest, though passwords are hashed.
