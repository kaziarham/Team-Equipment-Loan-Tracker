namespace EquipmentTrackerDotNet.Web.Models;

public record EquipmentListItem(
    int Id,
    string Name,
    string Category,
    string? SerialNumber,
    LoanInfo? ActiveLoan,
    bool IsOverdue
);