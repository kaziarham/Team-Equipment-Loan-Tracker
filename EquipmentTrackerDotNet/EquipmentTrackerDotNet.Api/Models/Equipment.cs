namespace EquipmentTrackerDotNet.Api.Models;

public record Equipment(
    int Id,
    string Name,
    string Category,
    string? SerialNumber
);