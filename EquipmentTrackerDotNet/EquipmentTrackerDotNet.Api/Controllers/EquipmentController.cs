using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace EquipmentTrackerDotNet.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EquipmentController(EquipmentService service, LoanService loanService) : ControllerBase
{
    private static readonly string[] StatusFilters = ["available", "onloan", "overdue"];

    [HttpGet]
    public async Task<ActionResult<List<EquipmentListItemDto>>> GetAll([FromQuery] string? status, CancellationToken cancellationToken)
    {
        if (status is not null && !StatusFilters.Contains(status))
        {
            return BadRequest("status must be available, onloan or overdue.");
        }

        return Ok(await service.GetAllAsync(status, cancellationToken));
    }
    
    [HttpGet("{id:int}")]
    public async Task<ActionResult<EquipmentDto>> GetById(int id, CancellationToken cancellationToken)
    {
        var item = await service.GetByIdAsync(id, cancellationToken);
        return item is null ? NotFound() : Ok(item);
    }

    [HttpPost]
    public async Task<ActionResult<EquipmentDto>> Create(EquipmentRequest request, CancellationToken cancellationToken)
    {
        var created = await service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut("{id:int}")]
    public async Task<IActionResult> Update(int id, EquipmentRequest request, CancellationToken cancellationToken) =>
        await service.UpdateAsync(id, request, cancellationToken) ? NoContent() : NotFound();
    
    [HttpPost("{id:int}/loans")]
    public async Task<IActionResult> Borrow(int id, BorrowRequest request, CancellationToken cancellationToken)
    {
        var result = await loanService.BorrowAsync(id, request, cancellationToken);

        return result.Status switch
        {
            LoanStatus.Created => Created($"/api/equipment/{id}", result.Loan),
            LoanStatus.NotFound => NotFound(),
            LoanStatus.AlreadyOnLoan => Conflict("This item is already on loan."),
            LoanStatus.DueDateInPast => BadRequest("The due date cannot be in the past."),
            _ => throw new InvalidOperationException($"Unexpected loan status {result.Status}.")
        };
    }
}