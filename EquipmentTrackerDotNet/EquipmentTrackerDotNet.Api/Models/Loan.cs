namespace EquipmentTrackerDotNet.Api.Models;

public class Loan
{
    public int Id { get; set; }
    public int EquipmentId { get; set; }
    public Equipment Equipment { get; set; } = null!;
    public string BorrowerName { get; set; } = string.Empty;
    public DateOnly LoanDate { get; set; }
    public DateOnly DueDate { get; set; }
    public DateOnly? ReturnedDate { get; set; }
}