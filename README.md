# Smart Placement AI

A student placement portal built with **Java 17, Spring Boot, Maven, React and Vite**. Candidates can register, upload a PDF resume, scan its skill keywords, browse jobs and persist applications.

The name is project branding: the implemented scanner uses deterministic keyword matching, not a trained AI model or commercial ATS scoring. Demo jobs are fictional and do not represent hiring partnerships.

## Run locally

Requirements: JDK 17, Node.js 22.12+ (Node 24 tested), and internet access for first-time dependency downloads. Maven is downloaded automatically by the included wrapper.

Terminal 1:

```bash
cd backend
# macOS/Linux: chmod +x mvnw
./mvnw spring-boot:run
# Windows PowerShell: .\mvnw.cmd spring-boot:run
```

Terminal 2:

```bash
cd frontend
npm ci
npm run dev
```

Open http://localhost:5173. Create your own account with a password of 8–72 characters (maximum 72 UTF-8 bytes). There is no hardcoded demo login. The default `demo` profile uses an in-memory H2 database and seeds one clearly labelled sample vacancy. Accounts and applications reset when the backend stops. Uploaded files are local to `backend/uploads/` and are never committed; remove that folder after a demo if you no longer need its contents.

Try: register → open Resume → upload a text-based PDF → inspect detected skills → open Jobs → apply → open Applications. Log out and log back in to check persistence while the backend remains running.

## MySQL mode

Create an empty `smartplacement` database and a dedicated database user. Configure these environment variables in the backend process (do not commit their real values):

```text
SPRING_PROFILES_ACTIVE=mysql
DB_URL=jdbc:mysql://localhost:3306/smartplacement
DB_USERNAME=<your database user>
DB_PASSWORD=<your database password>
```

Then run the same backend command. Spring Boot reads process environment variables; it does **not** automatically load `.env` files. The MySQL profile retains data and uses Hibernate `update` for local development. Use versioned schema migrations before production deployment. Start with an empty database: old plaintext-password accounts are incompatible and must be recreated. Existing databases also need duplicate application rows removed before adding the new unique constraint.

Public registration always creates a `STUDENT`. To review admin APIs, use a local MySQL database, register a separate account, and deliberately assign its role using an administrator database connection:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-admin-email';
```

There is no public role-assignment endpoint or admin UI. Admins can POST `/api/admin/jobs` with JSON fields `companyName`, `role`, `description`, `location`, `requiredSkills` and optional nonnegative `salary`.

## API and security

Authenticated calls use HTTP Basic credentials in an explicit Authorization header. Credentials stay in React memory, never in localStorage. Refreshing the page logs the user out. This is a local portfolio implementation; use HTTPS and a same-origin reverse proxy for any hosted deployment. Vite proxies `/api` to `http://localhost:8080` during development and local preview. A static-only host such as GitHub Pages cannot run the Java backend.

| Route | Access / behavior |
| --- | --- |
| POST `/api/auth/register` | Public registration; BCrypt password hashes |
| POST `/api/auth/login` | Public credential check; returns safe account metadata |
| GET `/api/health` | Public health message |
| GET/POST `/api/student/profile` | Current authenticated candidate only |
| POST `/api/resumes/upload` | Current account; multipart `file`, PDF up to 10 MB |
| GET `/api/resumes` | Current account's upload metadata |
| GET `/api/ai/resume-analysis/latest` | Scans current uploaded PDF on each request |
| GET `/api/jobs` | Authenticated active vacancies |
| POST `/api/applications/apply` | JSON `jobId`; candidate ID comes from authentication |
| GET `/api/applications/mine` | Current account's applications |
| POST `/api/admin/jobs` | Admin only |
| GET `/api/applications/job/{jobId}` | Admin only |

Passwords and local resume paths are excluded from JSON. Arbitrary candidate IDs/emails cannot select another account. Resume filenames are replaced with UUIDs on disk; non-PDF headers and oversized uploads are rejected. A database uniqueness constraint prevents duplicate applications, including concurrent requests.

## Verification

```bash
cd backend
./mvnw verify
# Windows: .\mvnw.cmd verify
```

```bash
cd frontend
npm ci
npm run build
npm run lint
npm audit
```

Integration tests exercise authentication, authorization, account ownership, password hashing and serialization, job applications, duplicate rejection, file validation and extraction from a real generated PDF. GitHub Actions repeats backend verification and frontend checks on pushes and pull requests.

## Scope and limitations

- PDF keyword scanning supports text-based PDFs. DOCX, OCR, semantic matching and ATS percentages are not implemented.
- Candidate profile APIs are available; there is no profile-editing UI or admin dashboard.
- No real recruitment partnerships, email notifications, training enrollment or placement guarantees are claimed.
- MySQL integration and browser interaction were not exercised in this review environment; H2 integration tests and frontend build/lint were run.
- Production hardening still needs dependency monitoring, rate limiting, password reset, email verification, durable managed file storage, upload malware scanning, PDF parsing resource limits and schema migrations. The backend retains the supplied Spring Boot 3.2.3 baseline; this is not a complete backend dependency vulnerability audit.

## Publish to GitHub

Use the contents of this cleaned folder for a **new repository**. The uploaded archive's old `.git` directory, installed dependencies, build output and personal resume files are intentionally omitted. See `REVIEW_NOTES.md` for findings and verification.

From the extracted project folder:

```bash
git init
git add .
git commit -m "Prepare Smart Placement project for review"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/smart-placement-ai.git
git push -u origin main
```

Create the empty GitHub repository first and replace `YOUR_USERNAME`. If you use an existing repository, review and clean its history separately: replacing working files does not remove earlier credentials or uploaded resumes from Git history. Rotate any real credentials previously published. Keep source files and the lockfile; do not upload `node_modules`, `target`, `dist`, `.env`, or `uploads`.
