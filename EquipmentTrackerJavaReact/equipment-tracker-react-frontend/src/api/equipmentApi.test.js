import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { fetchEquipment, createEquipment, updateEquipment } from './equipmentApi'

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

    it('throws an error containing the status when the response is not ok', async () => {
        mockResponse(null, { ok: false, status: 404 })

        await expect(fetchEquipment()).rejects.toThrow('Request failed with status 404')
    })
})