# Team Equipment Loan Tracker
Web application for tracking team equipment such as laptops, monitors and headsets: what is available, who has borrowed it, and when it is due back.

## Versions

The repository contains two implementations of the same application. Each has its own section below.

- [.NET version](#net-version): ASP.NET Core Web API and Blazor Web App.
- [Java + React version](#java--react-version): Spring Boot API and React frontend.

## .NET version

The .NET solution is in [EquipmentTrackerDotNet/](EquipmentTrackerDotNet/):

- `EquipmentTrackerDotNet.Api`: ASP.NET Core Web API, runs at `http://localhost:5209`.
- `EquipmentTrackerDotNet.Web`: Blazor Web App, runs at `http://localhost:5072`.
- `EquipmentTrackerDotNet.Tests`: xUnit tests.

### Prerequisites

- **.NET 10 SDK** (not just the runtime). Check it with `dotnet --info`, and make sure `dotnet` is on your PATH.
- **Git**.
- **NuGet access**, so `dotnet restore` can download packages.
- A modern browser with developer tools.
- An editor, optional: Visual Studio on Windows with the ASP.NET and web development workload, or VS Code with the C# Dev Kit on any OS.

Not required: Node.js, SQL Server, Docker or an Azure subscription. The database is a SQLite file, and the `dotnet-ef` tool is restored from this repository.

### Setup

From the `EquipmentTrackerDotNet` folder, run these once after cloning:

```bash
dotnet restore
dotnet tool restore
dotnet build
```

`dotnet tool restore` installs the `dotnet-ef` version pinned in `dotnet-tools.json`. The database commands need it.

### Database creation and migrations

Create or update the local database. Run this after cloning, and again after pulling new migrations. From the `EquipmentTrackerDotNet.Api` folder:

```bash
dotnet ef database update
```

Equipment data is stored in `equipment.db` in the API folder. The location comes from `ConnectionStrings:Default` in `EquipmentTrackerDotNet.Api/appsettings.json`. The file is git-ignored and survives restarts. The tests use their own temporary database and do not touch it.

On a new database, the seed data includes one overdue loan (the Projector), so you can see the Overdue filter straight away.

If you see a "no such table" error, run `dotnet ef database update` again.

### Configuration

- **API address:** `http://localhost:5209`, set in `EquipmentTrackerDotNet.Api/Properties/launchSettings.json`.
- **Web app's API address:** `ApiBaseUrl` in `EquipmentTrackerDotNet.Web/appsettings.json`. If you change the API port, update this value too.
- **Database location:** `ConnectionStrings:Default` in `EquipmentTrackerDotNet.Api/appsettings.json`.

### API endpoints

The Web app calls these endpoints on `http://localhost:5209`.

| Method | Endpoint | Response |
|---|---|---|
| GET | `/api/equipment` | `200 OK` with all equipment. Optional `?status=available`, `onloan` or `overdue`. Any other value returns `400 Bad Request`. |
| GET | `/api/equipment/{id}` | `200 OK`, or `404 Not Found` |
| POST | `/api/equipment` | `201 Created` with the new item, or `400 Bad Request` for invalid input |
| PUT | `/api/equipment/{id}` | `204 No Content`, `400 Bad Request` for invalid input, or `404 Not Found` |
| POST | `/api/equipment/{id}/loans` | `201 Created` with the loan, `400 Bad Request` if the due date is in the past, `404 Not Found`, or `409 Conflict` if the item is already on loan |
| POST | `/api/loans/{id}/return` | `200 OK` with the loan, `404 Not Found`, or `409 Conflict` if the loan was already returned |

Borrow request body: `{ "borrowerName": "...", "dueDate": "YYYY-MM-DD" }`. The name cannot be blank.

Create an item from a terminal:

```bash
curl -i -X POST http://localhost:5209/api/equipment \
  -H 'Content-Type: application/json' \
  -d '{"name":"Canon EOS R6","category":"Camera","serialNumber":"CAM-001"}'
```

You can also send the same request from [Insomnia](https://insomnia.rest/) or [Postman](https://www.postman.com/): use `POST`, set the URL to `http://localhost:5209/api/equipment`, and in the body choose JSON with the same fields.

### Running the applications

Start the API first, then the Web app. Each runs in its own terminal from the `EquipmentTrackerDotNet` folder:

```bash
cd EquipmentTrackerDotNet.Api && dotnet run
cd EquipmentTrackerDotNet.Web && dotnet run
```

Then open `http://localhost:5072/equipment`.

### Running tests

From the `EquipmentTrackerDotNet` folder:

```bash
dotnet test
```

The tests start the API in memory with a temporary SQLite database. They don't need the API to be running, and they don't change `equipment.db`.

### Demo walkthrough

1. Open `http://localhost:5072/equipment` and add a new item with the Add button.
2. Click **Borrow** on an available item, enter a name and a future due date, and submit.
3. Try to borrow the same item again. You should see "This item is already on loan."
4. Click **Return** on the item. It becomes available again.
5. Filter by **Overdue**. The seeded Projector loan appears.
6. Stop both applications, start them again, and check that the data is still there.
7. From `EquipmentTrackerDotNet`, run `dotnet test`.

### Known limitations

- There is no delete for equipment or loans.
- "Today" is the API's UTC date, so the Web's due-date message may differ from your local date near midnight.

## Java + React version

The Java + React solution is in [EquipmentTrackerJavaReact/](EquipmentTrackerJavaReact/):

- `equipment-tracker-java-backend`: Spring Boot API (Java 21), runs at `http://localhost:8080`.
- `equipment-tracker-react-frontend`: React with Vite, runs at `http://localhost:5173`.

The browser talks only to the Vite dev server, which forwards `/api` requests to the backend. The proxy is set in `equipment-tracker-react-frontend/vite.config.js`.

### Prerequisites

- **JDK 21**, not just a runtime. Check it with `java -version` and `javac -version`.
- **Node.js 24 LTS** with npm. Check it with `node --version` and `npm --version`.
- **PostgreSQL 18** (developed and tested with 18.4), with `psql`. Check it with `psql --version`.
- **Git**.
- A modern browser with developer tools, used to inspect requests and console errors.
- An editor, optional: VS Code with the Extension Pack for Java and the Spring Boot Extension Pack, or IntelliJ IDEA. Its built-in JavaScript support covers the React side.
- Optional: [pgAdmin](https://www.pgadmin.org/) or DBeaver to inspect tables and run SQL visually.

Not required: Docker, a global Maven install (the Maven Wrapper is included), or a cloud subscription.

### Database setup

Run these once, with PostgreSQL administrative access:

```bash
createuser --pwprompt equipment_tracker
createdb --owner=equipment_tracker equipment_tracker
createdb --owner=equipment_tracker equipment_tracker_test
```

Enter a password at the `--pwprompt` prompt. An empty password won't work with the backend.

`equipment_tracker` is the application database. `equipment_tracker_test` is used only by the backend tests, which clean their records between runs, so never point the tests at the application database.

### Configuration

The backend reads its database settings from environment variables. Set them in each terminal before you start the backend:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/equipment_tracker
export DB_USERNAME=equipment_tracker
export DB_PASSWORD='<your_password>'
```

Spring Boot doesn't load `.env` files automatically, so the variables must be set in the shell or the IDE run configuration. Never commit passwords.

### Running the applications

Start the backend and the frontend in separate terminals.

```bash
# Terminal 1: from EquipmentTrackerJavaReact/equipment-tracker-java-backend
./mvnw spring-boot:run       # Windows: mvnw.cmd spring-boot:run

# Terminal 2: from EquipmentTrackerJavaReact/equipment-tracker-react-frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. To check the proxy from a terminal:

```bash
curl -i http://localhost:5173/api/equipment
```

A `200 OK` response with a JSON array means the frontend can reach the backend.

### Database migrations and persistence

Flyway runs the SQL migrations in `equipment-tracker-java-backend/src/main/resources/db/migration` when the backend starts. Hibernate only validates the schema (`spring.jpa.hibernate.ddl-auto=validate`); Flyway owns all schema changes.

| Migration | Purpose |
|---|---|
| `V1__create_equipment_and_loan_tables.sql` | Creates the `equipment` and `loan` tables and the partial unique index for active loans. |
| `V2__add_loan_equipment_foreign_key.sql` | Adds the foreign key from `loan.equipment_id` to `equipment.id`. |

Equipment data is stored in PostgreSQL and survives backend restarts. To inspect saved records:

```bash
psql -h localhost -U equipment_tracker -d equipment_tracker -W
```

```sql
SELECT id, name, category, serial_number FROM equipment ORDER BY id;
```

### API endpoints

| Method | Endpoint | Response |
|---|---|---|
| GET | `/api/equipment` | `200 OK` with all equipment, ordered by ID |
| GET | `/api/equipment/{id}` | `200 OK`, or `404 Not Found` |
| POST | `/api/equipment` | `201 Created` with a `Location` header, or `400 Bad Request` for invalid input |
| PUT | `/api/equipment/{id}` | `200 OK`, `400 Bad Request` for invalid input, or `404 Not Found` |

Invalid input returns a `ProblemDetail` body with an `errors` object that maps each field to its message.

Create an item from a terminal:

```bash
curl -i -X POST http://localhost:8080/api/equipment \
  -H 'Content-Type: application/json' \
  -d '{"name":"Canon EOS R6","category":"Camera","serialNumber":"CAM-001"}'
```

You can also send the same request from [Insomnia](https://insomnia.rest/) or [Postman](https://www.postman.com/): use `POST`, set the URL to `http://localhost:8080/api/equipment`, and in the body choose JSON with the same fields.

To see the new row, open the `equipment_tracker` database in [pgAdmin](https://www.pgadmin.org/) or DBeaver and run the `SELECT` query from the section above.

### Running tests and checks

From `equipment-tracker-java-backend`, with the `DB_*` variables set and the `equipment_tracker_test` database created:

```bash
./mvnw test
./mvnw verify
```

The tests use the `test` profile (`src/test/resources/application-test.properties`), which points the datasource at `equipment_tracker_test`. The API integration tests send real HTTP requests to a random-port server and write to that database, so the application database is not touched.

From `equipment-tracker-react-frontend`:

```bash
npm run lint
npm run build
```

`npx vitest run` will be added once the frontend tests are configured.
