import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import BorrowEquipmentPage from './BorrowEquipmentPage'

/**
 * Unit tests for BorrowEquipmentPage form validation
 */
describe('BorrowEquipmentPage', () => {
    const item = { id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: null, availability: 'AVAILABLE' }

    function renderPage(onBorrow) {
        return render(
            <MemoryRouter initialEntries={[`/equipment/1/borrow`]}>
                <Routes>
                    <Route path="/equipment/:id/borrow" element={<BorrowEquipmentPage equipment={[item]} onBorrow={onBorrow} />} />
                </Routes>
            </MemoryRouter>
        )
    }

    it('shows required-field errors and does not borrow when fields are empty', () => {
        const onBorrow = vi.fn()
        renderPage(onBorrow)

        fireEvent.click(screen.getByRole('button', { name: 'Borrow' }))

        expect(screen.getByText('Borrower name is required')).toBeTruthy()
        expect(screen.getByText('Due date is required')).toBeTruthy()
        expect(onBorrow).not.toHaveBeenCalled()
    })

    it('rejects a due date in the past and does not borrow', () => {
        const onBorrow = vi.fn()
        renderPage(onBorrow)

        fireEvent.change(screen.getByLabelText('Borrower name'), { target: { value: 'Ana Pereira' } })
        fireEvent.change(screen.getByLabelText('Due date'), { target: { value: '2000-01-01' } })
        fireEvent.click(screen.getByRole('button', { name: 'Borrow' }))

        expect(screen.getByText('Due date cannot be in the past')).toBeTruthy()
        expect(onBorrow).not.toHaveBeenCalled()
    })

    it('shows the server message when borrowing fails', async () => {
        const onBorrow = vi.fn().mockRejectedValue(new Error('Equipment 1 is already on loan'))
        renderPage(onBorrow)

        fireEvent.change(screen.getByLabelText('Borrower name'), { target: { value: 'Ben Okafor' } })
        fireEvent.change(screen.getByLabelText('Due date'), { target: { value: '2099-12-01' } })
        fireEvent.click(screen.getByRole('button', { name: 'Borrow' }))

        expect(await screen.findByText('Could not borrow equipment: Equipment 1 is already on loan')).toBeTruthy()
    })
})