using EquipmentTrackerDotNet.Api.Models;

namespace EquipmentTrackerDotNet.Api.Contracts;

public record LoanDto(
    int Id,
    int EquipmentId,
    string BorrowerName,
    DateOnly LoanDate,
    DateOnly DueDate,
    DateOnly? ReturnedDate
)
{
    public static LoanDto From(Loan loan) =>
        new LoanDto(
            loan.Id,
            loan.EquipmentId,
            loan.BorrowerName,
            loan.LoanDate,
            loan.DueDate,
            loan.ReturnedDate
        );
}