using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Data;
using EquipmentTrackerDotNet.Api.Services;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Tests;

/**
 * Unit tests for the LoanService class.
 */
public class LoanServiceTests
{
    [Fact]
    public async Task Borrow_WithDueDateBeforeToday_ReturnsDueDateInPast()
    {
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseSqlite("DataSource=:memory:")
            .Options;
        var service = new LoanService(new AppDbContext(options), new FixedTimeProvider(new DateTimeOffset(2026, 11, 20, 12, 0, 0, TimeSpan.Zero)));

        // The date check returns before any database call, so the context is never opened.
        var result = await service.BorrowAsync(1, new BorrowRequest("Alex", new DateOnly(2026, 11, 19)));

        Assert.Equal(LoanStatus.DueDateInPast, result.Status);
    }

    private sealed class FixedTimeProvider(DateTimeOffset now) : TimeProvider
    {
        public override DateTimeOffset GetUtcNow() => now;
    }
}