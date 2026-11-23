import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import EquipmentForm from '../components/EquipmentForm'

function AddEquipmentPage({ onSave }) {
    const navigate = useNavigate()
    const [saving, setSaving] = useState(false)
    const [saveError, setSaveError] = useState('')

    async function handleSubmit(values) {
        setSaving(true)
        setSaveError('')

        try {
            await onSave(values)
            navigate('/')
        } catch (error) {
            setSaveError(`Could not save equipment: ${error.message}`)
            setSaving(false)
        }
    }

    return (
        <section className="form-page">
            <h2>Add equipment</h2>
            <EquipmentForm
                submitting={saving}
                serverError={saveError}
                onSubmit={handleSubmit}
                onCancel={() => navigate('/')}
            />
        </section>
    )
}

export default AddEquipmentPage