using EquipmentTrackerDotNet.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Api.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Equipment> Equipment => Set<Equipment>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Equipment>(entity =>
        {
            entity.Property(e => e.Name).IsRequired().HasMaxLength(200);
            entity.Property(e => e.Category).IsRequired().HasMaxLength(100);
            entity.Property(e => e.SerialNumber).HasMaxLength(100);

            // Fixed IDs are required for HasData seeding.
            entity.HasData(
                new Equipment { Id = 1, Name = "Laptop 14in", Category = "Computer", SerialNumber = "LT-1001" },
                new Equipment { Id = 2, Name = "Projector", Category = "AV", SerialNumber = "PR-2001" },
                new Equipment { Id = 3, Name = "Camera", Category = "Media", SerialNumber = null }
            );
        });
    }
}