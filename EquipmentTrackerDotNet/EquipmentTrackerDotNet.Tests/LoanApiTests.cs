using System.Net;
using System.Net.Http.Json;
using EquipmentTrackerDotNet.Api.Contracts;

namespace EquipmentTrackerDotNet.Tests;

public class LoanApiTests(ApiFactory factory) : IClassFixture<ApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    [Fact]
    public async Task CreateEquipment_WithValidInput_Returns201()
    {
        var response = await _client.PostAsJsonAsync("/api/equipment", new EquipmentRequest("Monitor 27in", "Displays", null));

        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }

    [Fact]
    public async Task CreateEquipment_WithMissingName_Returns400()
    {
        var response = await _client.PostAsJsonAsync("/api/equipment", new EquipmentRequest("", "Displays", null));

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task Borrow_WhenAlreadyOnLoan_Returns409()
    {
        var id = await CreateEquipmentAsync();
        Assert.Equal(HttpStatusCode.Created, (await BorrowAsync(id)).StatusCode);

        var second = await BorrowAsync(id);
        Assert.Equal(HttpStatusCode.Conflict, second.StatusCode);
    }

    [Fact]
    public async Task Borrow_WithDueDateInPast_Returns400()
    {
        var id = await CreateEquipmentAsync();
        var yesterday = DateOnly.FromDateTime(DateTime.UtcNow).AddDays(-1);

        var response = await _client.PostAsJsonAsync($"/api/equipment/{id}/loans", new BorrowRequest("John Doe", yesterday));

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task Return_MakesItemAvailableAgain()
    {
        var id = await CreateEquipmentAsync();
        var loan = await (await BorrowAsync(id)).Content.ReadFromJsonAsync<LoanDto>();

        var returned = await _client.PostAsync($"/api/loans/{loan!.Id}/return", null);
        var available = await _client.GetFromJsonAsync<List<EquipmentListItemDto>>("/api/equipment?status=available");

        Assert.Equal(HttpStatusCode.OK, returned.StatusCode);
        Assert.Contains(available!, item => item.Id == id);
    }

    [Fact]
    public async Task OverdueFilter_IncludesSeededOverdueLoan()
    {
        var overdue = await _client.GetFromJsonAsync<List<EquipmentListItemDto>>("/api/equipment?status=overdue");

        var projector = Assert.Single(overdue!, item => item.Name == "Projector");
        Assert.True(projector.IsOverdue);
    }

    [Fact]
    public async Task Borrow_WithWhiteSpaceBorrowerName_Returns400()
    {
        var id = await CreateEquipmentAsync();

        var response = await _client.PostAsJsonAsync($"/api/equipment/{id}/loans", new BorrowRequest("   ", DateOnly.FromDateTime(DateTime.UtcNow).AddDays(7)));

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task Borrow_AfterReturn_Returns201()
    {
        var id = await CreateEquipmentAsync();
        var loan = await (await BorrowAsync(id)).Content.ReadFromJsonAsync<LoanDto>();
        await _client.PostAsync($"/api/loans/{loan!.Id}/return", null);

        var second = await BorrowAsync(id);

        Assert.Equal(HttpStatusCode.Created, second.StatusCode);
    }

    private async Task<int> CreateEquipmentAsync()
    {
        var response = await _client.PostAsJsonAsync("/api/equipment", new EquipmentRequest($"Item {Guid.NewGuid():N}", "Tools", null));
        var created = await response.Content.ReadFromJsonAsync<EquipmentDto>();
        return created!.Id;
    }

    private Task<HttpResponseMessage> BorrowAsync(int equipmentId) =>
        _client.PostAsJsonAsync($"/api/equipment/{equipmentId}/loans", new BorrowRequest("John Doe", DateOnly.FromDateTime(DateTime.UtcNow).AddDays(7)));
}