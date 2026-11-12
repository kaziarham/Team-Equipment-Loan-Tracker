using System.Net.Http.Json;
using EquipmentTrackerDotNet.Web.Models;

namespace EquipmentTrackerDotNet.Web.Services;

public class EquipmentApiClient(HttpClient http)
{
    public async Task<List<EquipmentDto>> GetEquipmentAsync(CancellationToken cancellationToken = default) =>
        await http.GetFromJsonAsync<List<EquipmentDto>>("api/equipment", cancellationToken) ?? [];
}