import { describe, it, expect, vi } from 'vitest'
import { render, screen, within, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import EquipmentList from './EquipmentList'

/**
 * Unit tests for the EquipmentList component
 */
describe('EquipmentList', () => {
    function renderList(items, props = {}) {
        return render(
            <MemoryRouter>
                <EquipmentList items={items} {...props} />
            </MemoryRouter>
        )
    }

    it('shows an empty message when there is no equipment', () => {
        renderList([])

        expect(screen.getByText('No equipment yet.')).toBeTruthy()
        expect(screen.queryByRole('table')).toBeNull()
    })

    it('renders a row for each item with a dash for a missing serial number', () => {
        renderList([
            { id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: 'CAM-001' },
            { id: 2, name: 'Lenovo ThinkPad X1', category: 'Laptop', serialNumber: null }
        ])

        const rows = screen.getAllByRole('row').slice(1)
        expect(rows).toHaveLength(2)
        expect(within(rows[0]).getByText('Canon EOS R6')).toBeTruthy()
        expect(within(rows[0]).getByText('CAM-001')).toBeTruthy()
        expect(within(rows[1]).getByText('Lenovo ThinkPad X1')).toBeTruthy()
        expect(within(rows[1]).getByText('-')).toBeTruthy()
    })

    it('links each Edit button to that item edit page', () => {
        renderList([{ id: 5, name: 'Projector', category: 'AV', serialNumber: 'PRJ-9' }])

        const editLink = screen.getByRole('link', { name: 'Edit' })
        expect(editLink.getAttribute('href')).toBe('/equipment/5/edit')
    })

    it('offers Borrow for available items and Return for items on loan', () => {
        const onReturn = vi.fn()
        renderList([
            { id: 1, name: 'Canon EOS R6', category: 'Camera', serialNumber: null, availability: 'AVAILABLE' },
            { id: 2, name: 'Projector', category: 'AV', serialNumber: null, availability: 'OVERDUE', activeLoanId: 9 },
        ], { onReturn })

        expect(screen.getByRole('link', { name: 'Borrow' }).getAttribute('href')).toBe('/equipment/1/borrow')
        expect(screen.getByText('Overdue')).toBeTruthy()

        fireEvent.click(screen.getByRole('button', { name: 'Return' }))

        expect(onReturn).toHaveBeenCalledWith(expect.objectContaining({ id: 2, activeLoanId: 9 }))
    })

    it('shows the borrower and due date for items on loan', () => {
        renderList([
            {
                id: 7,
                name: 'Dell XPS 13',
                category: 'Laptop',
                serialNumber: 'LAP-7',
                availability: 'ON_LOAN',
                activeLoanId: 3,
                activeBorrowerName: 'Sam Lee',
                activeDueDate: '2030-01-31'
            }
        ])

        const row = screen.getAllByRole('row')[1]
        expect(within(row).getByText('Borrowed by Sam Lee')).toBeTruthy()
        expect(within(row).getByText('Due 2030-01-31')).toBeTruthy()
    })
})