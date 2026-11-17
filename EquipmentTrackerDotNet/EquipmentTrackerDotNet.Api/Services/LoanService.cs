using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Data;
using EquipmentTrackerDotNet.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EquipmentTrackerDotNet.Api.Services;

public enum LoanStatus
{
    Created,
    Returned,
    NotFound,
    AlreadyOnLoan,
    AlreadyReturned,
    DueDateInPast
}

public record LoanResult(
    LoanStatus Status,
    LoanDto? Loan = null
);

public class LoanService(AppDbContext db, TimeProvider timeProvider)
{
    public async Task<LoanResult> BorrowAsync(int equipmentId, BorrowRequest request, CancellationToken cancellationToken = default)
    {
        var today = Today();
        if (request.DueDate < today)
        {
            return new LoanResult(LoanStatus.DueDateInPast);
        }

        if (!await db.Equipment.AnyAsync(e => e.Id == equipmentId, cancellationToken))
        {
            return new LoanResult(LoanStatus.NotFound);
        }

        if (await db.Loans.AnyAsync(l => l.EquipmentId == equipmentId && l.ReturnedDate == null, cancellationToken))
        {
            return new LoanResult(LoanStatus.AlreadyOnLoan);
        }

        var loan = new Loan
        {
            EquipmentId = equipmentId,
            BorrowerName = request.BorrowerName,
            LoanDate = today,
            DueDate = request.DueDate
        };

        db.Loans.Add(loan);
        
        try
        {
            await db.SaveChangesAsync(cancellationToken);
        }
        catch (DbUpdateException)
        {
            // The filtered unique index rejects a second active loan if two requests race.
            return new LoanResult(LoanStatus.AlreadyOnLoan);
        }

        return new LoanResult(LoanStatus.Created, LoanDto.From(loan));
    }

    public async Task<LoanResult> ReturnAsync(int loanId, CancellationToken cancellationToken = default)
    {
        var loan = await db.Loans.FindAsync([loanId], cancellationToken);
        if (loan == null)
        {
            return new LoanResult(LoanStatus.NotFound);
        }

        if (loan.ReturnedDate is not null)
        {
            return new LoanResult(LoanStatus.AlreadyReturned);
        }

        loan.ReturnedDate = Today();
        await db.SaveChangesAsync(cancellationToken);

        return new LoanResult(LoanStatus.Returned, LoanDto.From(loan));
    }

    private DateOnly Today() => DateOnly.FromDateTime(timeProvider.GetUtcNow().UtcDateTime);
}