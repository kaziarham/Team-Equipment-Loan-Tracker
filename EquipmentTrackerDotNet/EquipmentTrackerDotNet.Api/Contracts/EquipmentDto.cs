namespace EquipmentTrackerDotNet.Api.Contracts;

public record EquipmentDto(
    int Id,
    string Name,
    string Category,
    string? SerialNumber
);