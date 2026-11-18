using System.ComponentModel.DataAnnotations;

namespace EquipmentTrackerDotNet.Api.Contracts;

public record BorrowRequest(
    [param: Required, StringLength(200), RegularExpression(@".*\S.*", ErrorMessage = "Borrower name cannot be blank.")] string BorrowerName,
    [param: Required] DateOnly DueDate
);