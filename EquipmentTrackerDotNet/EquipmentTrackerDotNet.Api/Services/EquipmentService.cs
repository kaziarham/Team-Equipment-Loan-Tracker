using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Data;
using EquipmentTrackerDotNet.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Api.Services;

public class EquipmentService(AppDbContext db)
{
    public async Task<List<EquipmentDto>> GetAllAsync(CancellationToken cancellationToken = default) =>
        await db.Equipment
            .AsNoTracking()
            .OrderBy(equipment => equipment.Id)
            .Select(equipment => new EquipmentDto(equipment.Id, equipment.Name, equipment.Category, equipment.SerialNumber))
            .ToListAsync(cancellationToken);
    
    public async Task<EquipmentDto?> GetByIdAsync(int id, CancellationToken cancellationToken = default) =>
        await db.Equipment
            .AsNoTracking()
            .Where(equipment => equipment.Id == id)
            .Select(equipment => new EquipmentDto(equipment.Id, equipment.Name, equipment.Category, equipment.SerialNumber))
            .FirstOrDefaultAsync(cancellationToken);
    
    public async Task<EquipmentDto> CreateAsync(EquipmentRequest request, CancellationToken cancellationToken = default)
    {
        var entity = new Equipment
        {
            Name = request.Name,
            Category = request.Category,
            SerialNumber = request.SerialNumber
        };

        db.Equipment.Add(entity);
        await db.SaveChangesAsync(cancellationToken);

        return new EquipmentDto(entity.Id, entity.Name, entity.Category, entity.SerialNumber);
    }

    public async Task<bool> UpdateAsync(int id, EquipmentRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await db.Equipment.FindAsync([id], cancellationToken);
        if (entity == null)
        {
            return false;
        }

        entity.Name = request.Name;
        entity.Category = request.Category;
        entity.SerialNumber = request.SerialNumber;

        await db.SaveChangesAsync(cancellationToken);
        return true;
    }
}