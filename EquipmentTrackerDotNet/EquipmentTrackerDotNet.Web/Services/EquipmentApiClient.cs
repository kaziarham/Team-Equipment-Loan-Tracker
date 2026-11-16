using System.Net;
using System.Net.Http.Json;
using EquipmentTrackerDotNet.Web.Models;

namespace EquipmentTrackerDotNet.Web.Services;

public class EquipmentApiClient(HttpClient http)
{
    public async Task<List<EquipmentDto>> GetEquipmentAsync(CancellationToken cancellationToken = default) =>
        await http.GetFromJsonAsync<List<EquipmentDto>>("api/equipment", cancellationToken) ?? [];

    public async Task<EquipmentDto?> GetEquipmentByIdAsync(int id, CancellationToken cancellationToken = default)
    {
        var response = await http.GetAsync($"api/equipment/{id}", cancellationToken);
        if (response.StatusCode == HttpStatusCode.NotFound)
        {
            return null;
        }

        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<EquipmentDto>(cancellationToken);
    }

    public async Task CreateEquipmentAsync(EquipmentForm form, CancellationToken cancellationToken = default)
    {
        var response = await http.PostAsJsonAsync("api/equipment", form, cancellationToken);
        response.EnsureSuccessStatusCode();
    }

    public async Task<bool> UpdateEquipmentAsync(int id, EquipmentForm form, CancellationToken cancellationToken = default)
    {
        var response = await http.PutAsJsonAsync($"api/equipment/{id}", form, cancellationToken);
        if (response.StatusCode == HttpStatusCode.NotFound)
        {
            return false;
        }

        response.EnsureSuccessStatusCode();
        return true;
    }
}