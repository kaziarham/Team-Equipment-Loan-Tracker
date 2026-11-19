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

The Java + React implementation is in [EquipmentTrackerJavaReact/](EquipmentTrackerJavaReact/):

- `equipment-tracker-java-backend`: Spring Boot API (Java 21), runs at `http://localhost:8080`.
- `equipment-tracker-react-frontend`: React with Vite, runs at `http://localhost:5173`.

The browser talks only to the Vite dev server, which forwards `/api` requests to the backend. The proxy is set in `equipment-tracker-react-frontend/vite.config.js`.

### Prerequisites

- **JDK 21**, not just a runtime. Check it with `java -version` and `javac -version`.
- **Node.js 24 LTS** with npm. Check it with `node --version` and `npm --version`.
- **PostgreSQL** with `psql`. Check it with `psql --version`.
- **Git**.
- A modern browser with developer tools, used to inspect requests and console errors.
- An editor, optional: VS Code with the Extension Pack for Java and the Spring Boot Extension Pack, or IntelliJ IDEA. Its built-in JavaScript support covers the React side.

Not required: Docker, a global Maven install (the Maven Wrapper is included), or a cloud subscription.

### Database setup

Run these once, with PostgreSQL administrative access:

```bash
createuser --pwprompt equipment_tracker
createdb --owner=equipment_tracker equipment_tracker
createdb --owner=equipment_tracker equipment_tracker_test
```

Enter a password at the `--pwprompt` prompt. An empty password won't work with the backend.

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

### Database migrations and inspection

Flyway runs the SQL migrations in `equipment-tracker-java-backend/src/main/resources/db/migration` when the backend starts. To inspect the data:

```bash
psql -h localhost -U equipment_tracker -d equipment_tracker -W
```

The equipment list is currently served from an in-memory list in `EquipmentService`. Database persistence is the next step.

### Running tests and checks

From `equipment-tracker-java-backend`:

```bash
./mvnw test
./mvnw verify
```

From `equipment-tracker-react-frontend`:

```bash
npm run lint
npm run build
```

`npx vitest run` will be added once the frontend tests are configured.
