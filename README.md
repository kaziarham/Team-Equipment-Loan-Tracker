# Team Equipment Loan Tracker
Web application for tracking team equipment such as laptops, monitors and headsets: what is available, who has borrowed it, and when it is due back.

## Projects

The .NET solution is in [EquipmentTrackerDotNet/](EquipmentTrackerDotNet/):

- `EquipmentTrackerDotNet.Api`: ASP.NET Core Web API, runs at `http://localhost:5209`.
- `EquipmentTrackerDotNet.Web`: Blazor Web App, runs at `http://localhost:5072`.
- `EquipmentTrackerDotNet.Tests`: xUnit tests.

## Running locally

Requires the .NET 10 SDK. From the `EquipmentTrackerDotNet` folder:

```bash
dotnet restore
dotnet tool restore
dotnet build
dotnet test
```

Create the local database once, before the first run. From the `EquipmentTrackerDotNet.Api` folder:

```bash
dotnet ef database update
```

Start the API and the Web app in separate terminals, each from the `EquipmentTrackerDotNet` folder:

```bash
cd EquipmentTrackerDotNet.Api && dotnet run
cd EquipmentTrackerDotNet.Web && dotnet run
```

Then open `http://localhost:5072/equipment`. The Web app reads the API address from `ApiBaseUrl` in `appsettings.json`.

Equipment data is stored in `equipment.db` in the API folder, so it survives restarts. This file is git-ignored. The tests use their own temporary database and do not touch it.
