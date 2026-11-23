using System.Net.Http.Json;
using EquipmentTrackerDotNet.Api.Contracts;

namespace EquipmentTrackerDotNet.Tests;

/**
 * Integration tests for the Equipment API.
 */
public class EquipmentApiTests(ApiFactory factory) : IClassFixture<ApiFactory>
{
    [Fact]
    public async Task GetEquipment_ReturnsSeededItems()
    {
        using var client = factory.CreateClient();

        var items = await client.GetFromJsonAsync<List<EquipmentDto>>("/api/equipment");

        Assert.NotNull(items);
        Assert.Contains(items, item => item.Name == "Laptop 14in" && item.Category == "Computer");
    }
}