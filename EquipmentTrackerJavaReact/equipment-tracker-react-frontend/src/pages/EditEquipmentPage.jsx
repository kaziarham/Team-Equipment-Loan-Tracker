import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import EquipmentForm from '../components/EquipmentForm'

function EditEquipmentPage({ equipment, onSave }) {
    const { id } = useParams()
    const navigate = useNavigate()
    const [saving, setSaving] = useState(false)
    const [saveError, setSaveError] = useState('')
    const item = equipment.find((entry) => String(entry.id) === id)

    if (!item) {
        return (
            <section>
                <p role="alert">Equipment not found.</p>
                <Link className="button button-secondary" to="/">Back to equipment</Link>
            </section>
        )
    }

    async function handleSubmit(values) {
        setSaving(true)
        setSaveError('')

        try {
            await onSave(item.id, values)
            navigate('/')
        } catch (error) {
            setSaveError(`Could not save equipment: ${error.message}`)
            setSaving(false)
        }
    }

    return (
        <section className="form-page">
            <h2>Edit Equipment</h2>
            <EquipmentForm
                key={item.id}
                initialValues={{ name: item.name, category: item.category, serialNumber: item.serialNumber ?? '' }}
                submitting={saving}
                serverError={saveError}
                onSubmit={handleSubmit}
                onCancel={() => navigate('/')}
            />
        </section>
    )
}

export default EditEquipmentPage