using System.Net;
using System.Net.Http.Json;
using EquipmentTrackerDotNet.Web.Models;
using EquipmentTrackerDotNet.Web.Services;

namespace EquipmentTrackerDotNet.Web.Tests;

/**
 * Unit tests for the EquipmentApiClient class.
 */
public class EquipmentApiClientTests
{
    [Fact]
    public async Task BorrowAsync_WhenApiReturnsConflict_ReturnsConflict()
    {
        var client = CreateClient(HttpStatusCode.Conflict);

        var status = await client.BorrowAsync(1, new BorrowForm { BorrowerName = "Alex", DueDate = new DateOnly(2026, 12, 1) });

        Assert.Equal(HttpStatusCode.Conflict, status);
    }

    [Fact]
    public async Task ReturnAsync_WhenLoanNotFound_ReturnsNotFound()
    {
        var client = CreateClient(HttpStatusCode.NotFound);

        var status = await client.ReturnAsync(99);

        Assert.Equal(HttpStatusCode.NotFound, status);
    }

    [Fact]
    public async Task GetEquipmentByIdAsync_WhenNotFound_ReturnsNull()
    {
        var client = CreateClient(HttpStatusCode.NotFound);

        var item = await client.GetEquipmentByIdAsync(99);

        Assert.Null(item);
    }

    [Fact]
    public async Task UpdateEquipmentAsync_WhenNotFound_ReturnsFalse()
    {
        var client = CreateClient(HttpStatusCode.NotFound);

        var updated = await client.UpdateEquipmentAsync(99, new EquipmentForm { Name = "Monitor", Category = "Displays" });

        Assert.False(updated);
    }

    [Fact]
    public async Task GetEquipmentAsync_WhenApiReturnsItems_ReturnsThem()
    {
        var items = new List<EquipmentListItem> { new(1, "Laptop 14in", "Computer", null, null, false) };
        var client = CreateClient(HttpStatusCode.OK, items);

        var result = await client.GetEquipmentAsync();

        var item = Assert.Single(result);
        Assert.Equal("Laptop 14in", item.Name);
    }

    [Fact]
    public async Task CreateEquipmentAsync_WhenApiReturnsCreated_DoesNotThrow()
    {
        var client = CreateClient(HttpStatusCode.Created);

        var exception = await Record.ExceptionAsync(() =>
            client.CreateEquipmentAsync(new EquipmentForm { Name = "Monitor", Category = "Displays" }));

        Assert.Null(exception);
    }

    [Fact]
    public async Task CreateEquipmentAsync_WhenApiReturnsBadRequest_Throws()
    {
        var client = CreateClient(HttpStatusCode.BadRequest);

        await Assert.ThrowsAsync<HttpRequestException>(() =>
            client.CreateEquipmentAsync(new EquipmentForm { Name = "Monitor", Category = "Displays" }));
    }

    [Fact]
    public async Task UpdateEquipmentAsync_WhenApiReturnsNoContent_ReturnsTrue()
    {
        var client = CreateClient(HttpStatusCode.NoContent);

        var updated = await client.UpdateEquipmentAsync(1, new EquipmentForm { Name = "Monitor", Category = "Displays" });

        Assert.True(updated);
    }

    [Fact]
    public async Task BorrowAsync_WhenApiReturnsCreated_ReturnsCreated()
    {
        var client = CreateClient(HttpStatusCode.Created);

        var status = await client.BorrowAsync(1, new BorrowForm { BorrowerName = "Alex", DueDate = new DateOnly(2026, 12, 1) });

        Assert.Equal(HttpStatusCode.Created, status);
    }

    [Fact]
    public async Task GetEquipmentByIdAsync_WhenFound_ReturnsItem()
    {
        var equipment = new EquipmentDto(1, "Laptop 14in", "Computer", null);
        var client = CreateClient(HttpStatusCode.OK, equipment);

        var result = await client.GetEquipmentByIdAsync(1);

        Assert.NotNull(result);
        Assert.Equal("Laptop 14in", result.Name);
    }

    private static EquipmentApiClient CreateClient(HttpStatusCode statusCode, object? body = null) =>
        new(new HttpClient(new StubHandler(statusCode, body)) { BaseAddress = new Uri("http://localhost:5209") });
    
    private sealed class StubHandler(HttpStatusCode statusCode, object? body) : HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken) =>
            Task.FromResult(new HttpResponseMessage(statusCode)
            {
                Content = body is null ? null : JsonContent.Create(body)
            });
    }
}