using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Models;

namespace EquipmentTrackerDotNet.Api.Services;

public class EquipmentService
{
    private readonly List<Equipment> _items =
    [
        new(1, "Dell XPS 13", "Laptop", "SN-1001"),
        new(2, "Dell U2723QE 27in", "Monitor", "SN-2001"),
        new(3, "Jabra Evolve2 65", "Headset", null)
    ];

    public IReadOnlyList<EquipmentDto> GetAll() =>
        _items.Select(item => new EquipmentDto(item.Id, item.Name, item.Category, item.SerialNumber)).ToList();
}