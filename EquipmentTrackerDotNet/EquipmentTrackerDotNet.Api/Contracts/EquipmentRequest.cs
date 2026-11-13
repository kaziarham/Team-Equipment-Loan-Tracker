using System.ComponentModel.DataAnnotations;

namespace EquipmentTrackerDotNet.Api.Contracts;

public record EquipmentRequest(
    [param: Required, StringLength(200)] string Name,
    [param: Required, StringLength(100)] string Category,
    [param: StringLength(100)] string? SerialNumber
);