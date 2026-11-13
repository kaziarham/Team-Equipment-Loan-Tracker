using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace EquipmentTrackerDotNet.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EquipmentController(EquipmentService service) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<List<EquipmentDto>>> GetAll(CancellationToken cancellationToken) =>
        Ok(await service.GetAllAsync(cancellationToken));
    
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
}