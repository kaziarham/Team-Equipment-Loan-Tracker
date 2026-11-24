import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { fetchEquipment, createEquipment, updateEquipment, borrowEquipment, returnLoan } from './equipmentApi'

/**
 * Unit tests for the Equipment API helpers (fetch is mocked)
 */
describe('equipmentApi', () => {
    beforeEach(() => {
        vi.stubGlobal('fetch', vi.fn())
    })

    afterEach(() => {
        vi.unstubAllGlobals()
    })

    function mockResponse(body, { ok = true, status = 200 } = {}) {
        fetch.mockResolvedValue({ ok, status, json: async () => body })
    }

    it('fetchEquipment calls GET /api/equipment and returns the parsed list', async () => {
        const items = [{ id: 1, name: 'Camera' }]
        mockResponse(items)

        const result = await fetchEquipment()

        expect(fetch).toHaveBeenCalledWith('/api/equipment', undefined)
        expect(result).toEqual(items)
    })

    it('createEquipment sends a POST with the JSON body', async () => {
        const values = { name: 'Laptop', category: 'Laptop', serialNumber: '' }
        mockResponse({ id: 2, ...values })

        await createEquipment(values)

        expect(fetch).toHaveBeenCalledWith('/api/equipment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
        })
    })

    it('updateEquipment sends a PUT to the item URL with the JSON body', async () => {
        const values = { name: 'Camera', category: 'Camera', serialNumber: 'CAM-001' }
        mockResponse({ id: 7, ...values })

        await updateEquipment(7, values)

        expect(fetch).toHaveBeenCalledWith('/api/equipment/7', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
        })
    })

    it('borrowEquipment sends a POST to the equipment loans URL', async () => {
        const loan = { borrowerName: 'Ana Pereira', dueDate: '2099-12-01' }
        mockResponse({ id: 10, ...loan })

        await borrowEquipment(7, loan)

        expect(fetch).toHaveBeenCalledWith('/api/equipment/7/loans', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loan),
        })
    })

    it('returnLoan sends a POST to the loan return URL', async () => {
        mockResponse({ id: 10, returnedDate: '2026-11-24' })

        await returnLoan(10)

        expect(fetch).toHaveBeenCalledWith('/api/loans/10/return', { method: 'POST' })
    })

    it('throws an error containing the status when the response is not ok', async () => {
        mockResponse(null, { ok: false, status: 404 })

        await expect(fetchEquipment()).rejects.toThrow('Request failed with status 404')
    })

    it('uses the server problem detail as the error message when one is returned', async () => {
        mockResponse({ detail: 'Equipment 7 is already on loan' }, { ok: false, status: 409 })

        await expect(borrowEquipment(7, { borrowerName: 'Ben', dueDate: '2099-12-01' }))
            .rejects.toThrow('Equipment 7 is already on loan')
    })
})