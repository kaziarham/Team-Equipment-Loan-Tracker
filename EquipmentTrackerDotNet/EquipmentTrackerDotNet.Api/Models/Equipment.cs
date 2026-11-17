namespace EquipmentTrackerDotNet.Api.Models;

public class Equipment
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public string? SerialNumber { get; set; }
    public List<Loan> Loans { get; set; } = [];
}