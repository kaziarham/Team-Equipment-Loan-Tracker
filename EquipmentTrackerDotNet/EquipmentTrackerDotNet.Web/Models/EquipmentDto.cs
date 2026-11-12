namespace EquipmentTrackerDotNet.Web.Models;

public record EquipmentDto(
    int Id,
    string Name,
    string Category,
    string? SerialNumber
);