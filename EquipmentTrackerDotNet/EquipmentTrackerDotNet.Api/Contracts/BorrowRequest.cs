using System.ComponentModel.DataAnnotations;

namespace EquipmentTrackerDotNet.Api.Contracts;

public record BorrowRequest(
    [param: Required, StringLength(200)] string BorrowerName,
    [param: Required] DateOnly DueDate
);