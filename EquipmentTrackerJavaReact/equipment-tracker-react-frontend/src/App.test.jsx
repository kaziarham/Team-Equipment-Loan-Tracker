import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
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
})