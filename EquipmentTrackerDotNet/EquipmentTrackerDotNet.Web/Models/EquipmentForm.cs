using System.ComponentModel.DataAnnotations;

namespace EquipmentTrackerDotNet.Web.Models;

public class EquipmentForm
{
    [Required, StringLength(200)]
    public string Name { get; set; } = string.Empty;

    [Required, StringLength(100)]
    public string Category { get; set; } = string.Empty;

    [StringLength(100)]
    public string? SerialNumber { get; set; }
}