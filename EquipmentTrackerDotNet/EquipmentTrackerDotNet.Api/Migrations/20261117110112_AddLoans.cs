using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace EquipmentTrackerDotNet.Api.Migrations
{
    /// <inheritdoc />
    public partial class AddLoans : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Loans",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    EquipmentId = table.Column<int>(type: "INTEGER", nullable: false),
                    BorrowerName = table.Column<string>(type: "TEXT", maxLength: 200, nullable: false),
                    LoanDate = table.Column<DateOnly>(type: "TEXT", nullable: false),
                    DueDate = table.Column<DateOnly>(type: "TEXT", nullable: false),
                    ReturnedDate = table.Column<DateOnly>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Loans", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Loans_Equipment_EquipmentId",
                        column: x => x.EquipmentId,
                        principalTable: "Equipment",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "Loans",
                columns: new[] { "Id", "BorrowerName", "DueDate", "EquipmentId", "LoanDate", "ReturnedDate" },
                values: new object[] { 1, "John Doe", new DateOnly(2026, 11, 8), 2, new DateOnly(2026, 11, 1), null });

            migrationBuilder.CreateIndex(
                name: "IX_Loans_EquipmentId",
                table: "Loans",
                column: "EquipmentId",
                unique: true,
                filter: "ReturnedDate IS NULL");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Loans");
        }
    }
}
