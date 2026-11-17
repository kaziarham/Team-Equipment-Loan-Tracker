using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Data;
using EquipmentTrackerDotNet.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Api.Services;

public class EquipmentService(AppDbContext db, TimeProvider timeProvider)
{
    public async Task<List<EquipmentListItemDto>> GetAllAsync(string? status = null, CancellationToken cancellationToken = default)
    {
        var today = DateOnly.FromDateTime(timeProvider.GetUtcNow().UtcDateTime);

        // The include only loads active loans, so Loans contains at most one item.
        IQueryable<Equipment> query = db.Equipment
            .AsNoTracking()
            .Include(equipment => equipment.Loans.Where(loan => loan.ReturnedDate == null));
        
        query = status switch
        {
            "available" => query.Where(e => !e.Loans.Any(l => l.ReturnedDate == null)),
            "onloan" => query.Where(e => e.Loans.Any(l => l.ReturnedDate == null)),
            "overdue" => query.Where(e => e.Loans.Any(l => l.ReturnedDate == null && l.DueDate < today)),
            _ => query
        };

        var items = await query.OrderBy(e => e.Id).ToListAsync(cancellationToken);
        return items.Select(e => ToListItem(e, today)).ToList();
    }
    
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

    private static EquipmentListItemDto ToListItem(Equipment equipment, DateOnly today)
    {
        var activeLoan = equipment.Loans.FirstOrDefault();

        return new EquipmentListItemDto(
            equipment.Id,
            equipment.Name,
            equipment.Category,
            equipment.SerialNumber,
            activeLoan is null ? null : LoanDto.From(activeLoan),
            activeLoan is not null && activeLoan.DueDate < today
        );
    }
}