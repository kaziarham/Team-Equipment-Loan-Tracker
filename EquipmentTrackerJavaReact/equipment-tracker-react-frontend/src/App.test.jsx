import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, fireEvent, within } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import App from './App'

/**
 * Integration tests for the App component
 */
describe('App equipment list', () => {
    afterEach(() => {
        vi.unstubAllGlobals()
    })

    it('shows equipment returned by the API', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
            ok: true,
            json: async () => [{ id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: 'CAM-001' }],
        }))

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        expect(await screen.findByText('Canon EOS R6')).toBeTruthy()
    })

    it('shows a loading message while equipment is being fetched', () => {
        vi.stubGlobal('fetch', vi.fn(() => new Promise(() => {})))

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )
        expect(screen.getByText('Loading equipment...')).toBeInTheDocument()
    })

    it('shows a visible error when the API fails', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
            ok: false,
            status: 500,
            json: async () => ({}),
        }))

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        expect(await screen.findByText(/Request failed with status 500/)).toBeTruthy()
    })

    it('shows a visible error when saving new equipment fails', async () => {
        vi.stubGlobal('fetch', vi.fn((url, options) => {
            if (options?.method === 'POST') {
                return Promise.resolve({ ok: false, status: 500, json: async () => ({}) })
            }
            return Promise.resolve({ ok: true, json: async () => [] })
        }))

        render(
            <MemoryRouter initialEntries={['/equipment/new']}>
                <App />
            </MemoryRouter>
        )

        fireEvent.change(await screen.findByLabelText('Name'), { target: { value: 'Laptop' } })
        fireEvent.change(screen.getByLabelText('Category'), { target: { value: 'Laptop' } })
        fireEvent.click(screen.getByRole('button', { name: 'Save' }))

        expect(await screen.findByText(/Could not save equipment: Request failed with status 500/)).toBeTruthy()
    })

    it('filters the list by availability', async () => {
        vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
            ok: true,
            json: async () => [
                { id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: null, availability: 'AVAILABLE' },
                { id: 2, name: 'Projector', category: 'AV', serialNumber: null, availability: 'OVERDUE', activeLoanId: 9 },
            ],
        }))

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        await screen.findByText('Canon EOS R6')
        fireEvent.click(screen.getByRole('button', { name: 'Overdue' }))

        expect(screen.queryByText('Canon EOS R6')).toBeNull()
        expect(screen.getByText('Projector')).toBeTruthy()
    })

    it('borrows an available item and then shows it as on loan', async () => {
        let borrowed = false
        const fetchMock = vi.fn((url, options) => {
            if (options?.method === 'POST') {
                borrowed = true
                return Promise.resolve({ ok: true, json: async () => ({ id: 10, status: 'ACTIVE' }) })
            }
            return Promise.resolve({
                ok: true,
                json: async () => [{
                    id: 1,
                    name: 'Canon EOS R6',
                    category: 'Camera',
                    serialNumber: null,
                    availability: borrowed ? 'ON_LOAN' : 'AVAILABLE',
                    activeLoanId: borrowed ? 10 : null,
                }],
            })
        })
        vi.stubGlobal('fetch', fetchMock)

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        fireEvent.click(await screen.findByRole('link', { name: 'Borrow' }))
        fireEvent.change(screen.getByLabelText('Borrower name'), { target: { value: 'Ana Pereira' } })
        fireEvent.change(screen.getByLabelText('Due date'), { target: { value: '2099-12-01' } })
        fireEvent.click(screen.getByRole('button', { name: 'Borrow' }))

        expect(await within(await screen.findByRole('table')).findByText('On loan')).toBeTruthy()
        expect(fetchMock).toHaveBeenCalledWith('/api/equipment/1/loans', expect.objectContaining({ method: 'POST' }))
    })

    it('returns an on-loan item and then shows it as available', async () => {
        let returned = false
        const fetchMock = vi.fn((url, options) => {
            if (options?.method === 'POST') {
                returned = true
                return Promise.resolve({ ok: true, json: async () => ({ id: 7, status: 'RETURNED' }) })
            }
            return Promise.resolve({
                ok: true,
                json: async () => [{
                    id: 1,
                    name: 'Canon EOS R6',
                    category: 'Camera',
                    serialNumber: null,
                    availability: returned ? 'AVAILABLE' : 'ON_LOAN',
                    activeLoanId: returned ? null : 7,
                }],
            })
        })
        vi.stubGlobal('fetch', fetchMock)

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        fireEvent.click(await screen.findByRole('button', { name: 'Return' }))

        expect(await within(await screen.findByRole('table')).findByText('Available')).toBeTruthy()
        expect(fetchMock).toHaveBeenCalledWith('/api/loans/7/return', expect.objectContaining({ method: 'POST' }))
    })

    it('shows the server message when returning an item fails', async () => {
        vi.stubGlobal('fetch', vi.fn((url, options) => {
            if (options?.method === 'POST') {
                return Promise.resolve({ ok: false, status: 409, json: async () => ({ detail: 'Loan 7 was already returned' }) })
            }
            return Promise.resolve({
                ok: true,
                json: async () => [{ id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: null, availability: 'ON_LOAN', activeLoanId: 7 }],
            })
        }))

        render(
            <MemoryRouter>
                <App />
            </MemoryRouter>
        )

        fireEvent.click(await screen.findByRole('button', { name: 'Return' }))

        expect(await screen.findByText('Could not return Canon EOS R6: Loan 7 was already returned')).toBeTruthy()
    })
})