namespace EquipmentTrackerDotNet.Web.Models;

public record LoanInfo(
    int Id,
    int EquipmentId,
    string BorrowerName,
    DateOnly LoanDate,
    DateOnly DueDate,
    DateOnly? ReturnedDate
);