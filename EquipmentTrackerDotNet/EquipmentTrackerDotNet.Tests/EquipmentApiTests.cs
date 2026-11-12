using System.Net.Http.Json;
using EquipmentTrackerDotNet.Api.Contracts;
using Microsoft.AspNetCore.Mvc.Testing;

namespace EquipmentTrackerDotNet.Tests;

public class EquipmentApiTests(WebApplicationFactory<Program> factory) : IClassFixture<WebApplicationFactory<Program>>
{
    [Fact]
    public async Task GetEquipment_ReturnsSeededItems()
    {
        using var client = factory.CreateClient();

        var items = await client.GetFromJsonAsync<List<EquipmentDto>>("/api/equipment");

        Assert.NotNull(items);
        Assert.Contains(items, item => item.Name == "Dell XPS 13" && item.Category == "Laptop");
    }
}