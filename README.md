# Team Equipment Loan Tracker
Web application for tracking team equipment such as laptops, monitors and headsets: what is available, who has borrowed it, and when it is due back.

## Projects

The .NET solution is in [EquipmentTrackerDotNet/](EquipmentTrackerDotNet/):

- `EquipmentTrackerDotNet.Api`: ASP.NET Core Web API, runs at `http://localhost:5209`.
- `EquipmentTrackerDotNet.Web`: Blazor Web App, runs at `http://localhost:5072`.
- `EquipmentTrackerDotNet.Tests`: xUnit tests.

## Prerequisites

- **.NET 10 SDK** (not just the runtime). Check it with `dotnet --info`, and make sure `dotnet` is on your PATH.
- **Git**.
- **NuGet access**, so `dotnet restore` can download packages.
- A modern browser with developer tools.
- An editor, optional: Visual Studio on Windows with the ASP.NET and web development workload, or VS Code with the C# Dev Kit on any OS.

Not required: Node.js, SQL Server, Docker or an Azure subscription. The database is a SQLite file, and the `dotnet-ef` tool is restored from this repository.

## Setup

From the `EquipmentTrackerDotNet` folder, run these once after cloning:

```bash
dotnet restore
dotnet tool restore
dotnet build
```

`dotnet tool restore` installs the `dotnet-ef` version pinned in `dotnet-tools.json`. The database commands need it.

## Database creation and migrations

Create or update the local database. Run this after cloning, and again after pulling new migrations. From the `EquipmentTrackerDotNet.Api` folder:

```bash
dotnet ef database update
```

Equipment data is stored in `equipment.db` in the API folder. The location comes from `ConnectionStrings:Default` in `EquipmentTrackerDotNet.Api/appsettings.json`. The file is git-ignored and survives restarts. The tests use their own temporary database and do not touch it.

On a new database, the seed data includes one overdue loan (the Projector), so you can see the Overdue filter straight away.

If you see a "no such table" error, run `dotnet ef database update` again.

## Configuration

- **API address:** `http://localhost:5209`, set in `EquipmentTrackerDotNet.Api/Properties/launchSettings.json`.
- **Web app's API address:** `ApiBaseUrl` in `EquipmentTrackerDotNet.Web/appsettings.json`. If you change the API port, update this value too.
- **Database location:** `ConnectionStrings:Default` in `EquipmentTrackerDotNet.Api/appsettings.json`.

## Running the applications

Start the API first, then the Web app. Each runs in its own terminal from the `EquipmentTrackerDotNet` folder:

```bash
cd EquipmentTrackerDotNet.Api && dotnet run
cd EquipmentTrackerDotNet.Web && dotnet run
```

Then open `http://localhost:5072/equipment`.

## Running tests

From the `EquipmentTrackerDotNet` folder:

```bash
dotnet test
```

The tests start the API in memory with a temporary SQLite database. They don't need the API to be running, and they don't change `equipment.db`.

## Demo walkthrough

1. Open `http://localhost:5072/equipment` and add a new item with the Add button.
2. Click **Borrow** on an available item, enter a name and a future due date, and submit.
3. Try to borrow the same item again. You should see "This item is already on loan."
4. Click **Return** on the item. It becomes available again.
5. Filter by **Overdue**. The seeded Projector loan appears.
6. Stop both applications, start them again, and check that the data is still there.
7. From `EquipmentTrackerDotNet`, run `dotnet test`.

## Known limitations

- There is no delete for equipment or loans.
- "Today" is the API's UTC date, so the Web's due-date message may differ from your local date near midnight.
