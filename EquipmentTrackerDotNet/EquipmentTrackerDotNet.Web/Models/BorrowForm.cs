using System.ComponentModel.DataAnnotations;

namespace EquipmentTrackerDotNet.Web.Models;

public class BorrowForm
{
    [Required, StringLength(200)]
    public string BorrowerName { get; set; } = string.Empty;

    [Required]
    public DateOnly? DueDate { get; set; }
}