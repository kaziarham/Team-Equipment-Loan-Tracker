namespace EquipmentTrackerDotNet.Api.Contracts;

public record EquipmentListItemDto(
    int Id,
    string Name,
    string Category,
    string? SerialNumber,
    LoanDto? ActiveLoan,
    bool IsOverdue
);