using EquipmentTrackerDotNet.Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace EquipmentTrackerDotNet.Api.Controllers;

[ApiController]
[Route("api/loans")]
public class LoanController(LoanService loanService) : ControllerBase
{
    [HttpPost("{id:int}/return")]
    public async Task<IActionResult> Return(int id, CancellationToken cancellationToken)
    {
        var result = await loanService.ReturnAsync(id, cancellationToken);

        return result.Status switch
        {
            LoanStatus.Returned => Ok(result.Loan),
            LoanStatus.NotFound => NotFound(),
            LoanStatus.AlreadyReturned => Conflict("This loan has already been returned."),
            _ => throw new InvalidOperationException($"Unexpected loan status {result.Status}.")
        };
    }
}