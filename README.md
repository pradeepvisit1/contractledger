# ContractLedger — Spring Boot + MySQL
# Complete Setup Guide

## PROJECT STRUCTURE
contractledger/
├── pom.xml
├── src/main/java/com/contractledger/
│   ├── ContractLedgerApplication.java   ← Main entry point
│   ├── model/Models.java                ← User, Project, Investor, Expense, Worker, WageLog
│   ├── repository/Repositories.java     ← DB queries
│   ├── security/Security.java           ← JWT + Spring Security
│   ├── dto/DTOs.java                    ← Request/Response objects
│   └── controller/
│       ├── AuthController.java          ← POST /api/auth/login, /register
│       └── Controllers.java             ← All other REST APIs
├── src/main/resources/
│   ├── application.properties           ← DB config (edit this!)
│   └── static/index.html                ← Frontend (served by Spring Boot)
└── database/init.sql                    ← Default users

═══════════════════════════════════════
STEP 1 — Requirements
═══════════════════════════════════════
- Java 17+     → https://adoptium.net
- Maven 3.8+   → https://maven.apache.org
- MySQL 8.0+   → https://dev.mysql.com/downloads/

Check versions:
  java -version
  mvn -version
  mysql --version

═══════════════════════════════════════
STEP 2 — Create MySQL Database
═══════════════════════════════════════
mysql -u root -p
CREATE DATABASE contractledger;
exit;

═══════════════════════════════════════
STEP 3 — Configure application.properties
═══════════════════════════════════════
Edit: src/main/resources/application.properties

Change:
  spring.datasource.password=YOUR_MYSQL_PASSWORD  ← set your MySQL root password
  app.cors.origins=http://localhost:8080,...       ← add your domain if needed

═══════════════════════════════════════
STEP 4 — Build & Run
═══════════════════════════════════════
cd contractledger
mvn spring-boot:run

Open browser: http://localhost:8080
You should see the login screen!

═══════════════════════════════════════
STEP 5 — Create Users (2 ways)
═══════════════════════════════════════

WAY A — Run SQL (after Spring Boot started once):
  mysql -u root -p contractledger < database/init.sql
  Default password for all: Admin@2024

WAY B — Register via API (use Postman or curl):
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"pradeep","password":"Pass@1234","fullName":"Pradeep Kumar"}'

  # To create ADMIN:
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"Admin@2024","fullName":"Admin","role":"ADMIN","adminSecret":"CLAdmin@2024"}'

═══════════════════════════════════════
STEP 6 — Share with 5 Users
═══════════════════════════════════════
All 5 users open: http://YOUR-PC-IP:8080

Find your PC IP:
  Windows: ipconfig → IPv4 Address
  Mac/Linux: ifconfig | grep inet

Example: http://192.168.1.10:8080

All users see same data — MySQL is the single source of truth.
No sync needed. Every save goes directly to database.

═══════════════════════════════════════
STEP 7 — Deploy to Production (Optional)
═══════════════════════════════════════
Build JAR:
  mvn clean package -DskipTests
  # Creates: target/contractledger-1.0.0.jar

Run on server:
  java -jar target/contractledger-1.0.0.jar

Free cloud options:
  - Railway.app (easiest, MySQL included free)
  - Render.com
  - AWS EC2 free tier

For Railway:
  1. Push to GitHub
  2. railway.app → New → Deploy from GitHub
  3. Add MySQL service
  4. Set env vars: SPRING_DATASOURCE_PASSWORD, APP_JWT_SECRET

═══════════════════════════════════════
API REFERENCE
═══════════════════════════════════════
POST   /api/auth/login                          → Get JWT token
POST   /api/auth/register                       → Register user

GET    /api/projects                            → All projects
POST   /api/projects                            → Create project
GET    /api/projects/{id}                       → Project with totals
PUT    /api/projects/{id}                       → Update
DELETE /api/projects/{id}                       → Delete

GET    /api/projects/{id}/investors             → List investors
POST   /api/projects/{id}/investors             → Add investor
DELETE /api/projects/{id}/investors/{invId}     → Delete

GET    /api/projects/{id}/expenses              → List (add ?category=MATERIAL to filter)
POST   /api/projects/{id}/expenses              → Add expense
DELETE /api/projects/{id}/expenses/{expId}      → Delete

GET    /api/projects/{id}/workers               → List workers
POST   /api/projects/{id}/workers               → Add worker
DELETE /api/projects/{id}/workers/{wId}         → Delete

GET    /api/projects/{id}/wagelogs              → List wage logs
POST   /api/projects/{id}/wagelogs              → Log daily wages
DELETE /api/projects/{id}/wagelogs/{wlId}       → Delete

GET    /api/projects/{id}/report                → Full report

GET    /api/admin/users                         → All users (admin only)
PUT    /api/admin/users/{id}/toggle             → Enable/disable user
PUT    /api/admin/users/{id}/password           → Reset password

═══════════════════════════════════════
DEFAULT USERS (after running init.sql)
═══════════════════════════════════════
admin    / Admin@2024  (ADMIN role)
pradeep  / Admin@2024
user2    / Admin@2024
user3    / Admin@2024
user4    / Admin@2024

⚠️  Change all passwords after first login!
