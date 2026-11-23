import { describe, it, expect } from 'vitest'
import { render, screen, within } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import EquipmentList from './EquipmentList'

/**
 * Unit tests for the EquipmentList component
 */
describe('EquipmentList', () => {
    function renderList(items) {
        return render(
            <MemoryRouter>
                <EquipmentList items={items} />
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
})