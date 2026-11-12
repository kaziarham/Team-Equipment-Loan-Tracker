using EquipmentTrackerDotNet.Api.Contracts;
using EquipmentTrackerDotNet.Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace EquipmentTrackerDotNet.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class EquipmentController(EquipmentService equipmentService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IReadOnlyList<EquipmentDto>> GetAll() => Ok(equipmentService.GetAll());
}