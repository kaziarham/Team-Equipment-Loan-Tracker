# Team-Equipment-Loan-Tracker
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
dotnet build
dotnet test
```

Start the API and the Web app in separate terminals:

```bash
cd EquipmentTrackerDotNet/EquipmentTrackerDotNet.Api && dotnet run
cd EquipmentTrackerDotNet/EquipmentTrackerDotNet.Web && dotnet run
```

Then open `http://localhost:5072/equipment`. The Web app reads the API address from `ApiBaseUrl` in `appsettings.json`.

Equipment data is currently held in memory and resets when the API restarts.
