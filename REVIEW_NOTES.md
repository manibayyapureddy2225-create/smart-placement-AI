# Project review and fixes

Reviewed 24 October 2026. No source was pushed to GitHub.

| Finding in supplied source | Correction |
| --- | --- |
| Frontend login used localStorage passwords and a hardcoded account | Connected to Spring Boot; removed local password storage and login bypass |
| Backend stored and compared plaintext passwords | BCrypt hashing and verification; normalized emails |
| Duplicate `/api/users` auth bypassed validation | Removed obsolete controller; retained one canonical auth flow |
| All API routes, including admin job creation, were public | Required authentication and added admin restrictions |
| Email/student IDs selected other candidates' records | Derived candidate ownership from authenticated principal |
| Nested user JSON exposed passwords | Excluded password property from JSON |
| Fixed skills, jobs and score 78 presented as real analysis | Added actual PDF text extraction with honest keyword-scanner labels |
| Jobs/applications only existed in browser memory | Connected job listing and application persistence to backend |
| Unsupported major-company partnerships and salary statistics | Removed claims and added a labelled fictional demo vacancy |
| Nonexistent/closed jobs could receive applications | Added job checks and database duplicate constraint |
| Upload filename controlled disk path; no PDF validation | UUID filenames, size/header/parser checks and managed streams |
| Resume replacement left obsolete files | Cleaned previous file after successful save and new file after save failure |
| root/root database defaults | Environment-based MySQL settings; H2 reviewer demo |
| Missing job field checks | Validated names, salary/status and generated IDs |
| Wildcard CORS and public debug endpoints | Removed permissive annotations and debug controller; same-origin proxy |
| Starter styles/page metadata | Simplified styles, project title and focus indicators |
| Frontend audit reported two high-severity packages | Updated lockfile; npm audit subsequently reported zero vulnerabilities |
| Missing reviewer instructions/exclusions | Added README, gitignore and GitHub CI |

## Review scope

The original frontend was a disconnected demonstration. The corrected frontend concentrates on working accounts, PDF scanning, jobs and application history. Decorative training enrollment, unrelated project cards and fake hiring partners were removed. The dark/red theme remains. DOCX was advertised but never parsed; this version supports PDF only. It does not claim an ATS percentage or automated match score.

The original `.git` history, installed dependencies, build output and personal resumes are excluded from the package. Existing remote history has not been edited; clean it separately if old sensitive data was published.

## Verification

The application-context test and two integration test methods pass, covering authentication, access restrictions, candidate isolation, safe password serialization, duplicate/invalid applications, upload validation and extraction from a generated PDF. Frontend production build and lint pass. npm audit reports zero known vulnerabilities after the dependency update. Mockito's subclass mock maker avoids requiring dynamic JVM agent attachment in restricted test environments.

## Remaining limits

MySQL and browser interaction were not exercised; tests use H2 and MockMvc. The supplied Spring Boot 3.2.3 dependency baseline remains; no comprehensive backend dependency CVE audit was performed. This is a portfolio implementation, not production certification. HTTPS is required for hosting. Rate limiting, password recovery, file malware scanning, PDF resource isolation, migrations, OCR and admin UI remain future work. Default demo data resets when the backend stops. See README for setup and deployment details.
