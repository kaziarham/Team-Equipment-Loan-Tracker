import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import EquipmentForm from './EquipmentForm'

/**
 * Component tests for the EquipmentForm component
 */
describe('EquipmentForm', () => {
    it('shows required-field errors and does not submit when fields are empty', () => {
        const onSubmit = vi.fn()
        render(<EquipmentForm submitting={false} onSubmit={onSubmit} onCancel={() => {}} />)

        fireEvent.click(screen.getByRole('button', { name: 'Save' }))

        expect(screen.getByText('Name is required')).toBeTruthy()
        expect(screen.getByText('Category is required')).toBeTruthy()
        expect(onSubmit).not.toHaveBeenCalled()
    })
})