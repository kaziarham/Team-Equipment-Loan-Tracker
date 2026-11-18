using EquipmentTrackerDotNet.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Api.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Equipment> Equipment => Set<Equipment>();
    public DbSet<Loan> Loans => Set<Loan>();

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

        modelBuilder.Entity<Loan>(entity =>
        {
            entity.Property(l => l.BorrowerName).IsRequired().HasMaxLength(200);

            entity.HasOne(l => l.Equipment)
                  .WithMany(e => e.Loans)
                  .HasForeignKey(l => l.EquipmentId);

            // At most one active (not returned) loan per item.
            entity.HasIndex(l => l.EquipmentId)
                  .IsUnique()
                  .HasFilter("ReturnedDate IS NULL");

            // Seeded overdue loan for the demonstration: Projector due well before today.
            entity.HasData(new Loan
            {
                Id = 1,
                EquipmentId = 2,
                BorrowerName = "John Doe",
                LoanDate = new DateOnly(2026, 11, 1),
                DueDate = new DateOnly(2026, 11, 8)
            });
        });
    }
}