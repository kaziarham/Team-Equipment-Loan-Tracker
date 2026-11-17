using System.Net;
using System.Net.Http.Json;
using EquipmentTrackerDotNet.Web.Models;

namespace EquipmentTrackerDotNet.Web.Services;

public class EquipmentApiClient(HttpClient http)
{
    public async Task<List<EquipmentListItem>> GetEquipmentAsync(string? status = null, CancellationToken cancellationToken = default)
    {
        var url = string.IsNullOrEmpty(status) ? "/api/equipment" : $"api/equipment?status={Uri.EscapeDataString(status)}";
        return await http.GetFromJsonAsync<List<EquipmentListItem>>(url, cancellationToken) ?? [];
    }

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

    public async Task<HttpStatusCode> BorrowAsync(int equipmentId, BorrowForm form, CancellationToken cancellationToken = default)
    {
        var response = await http.PostAsJsonAsync($"api/equipment/{equipmentId}/loans", form, cancellationToken);
        return response.StatusCode;
    }

    public async Task<HttpStatusCode> ReturnAsync(int loanId, CancellationToken cancellationToken = default)
    {
        var response = await http.PostAsync($"api/loans/{loanId}/return", null, cancellationToken);
        return response.StatusCode;
    }
}