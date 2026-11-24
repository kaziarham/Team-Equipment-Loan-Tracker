import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'

function todayIso() {
    const now = new Date()
    const localMidnightOffset = now.getTimezoneOffset() * 60000
    return new Date(now - localMidnightOffset).toISOString().slice(0, 10)
}

function validateLoan(values) {
    const errors = {}
    const borrowerName = values.borrowerName.trim()

    if (!borrowerName) {
        errors.borrowerName = 'Borrower name is required'
    } else if (borrowerName.length > 255) {
        errors.borrowerName = 'Borrower name must be 255 characters or fewer'
    }

    if (!values.dueDate) {
        errors.dueDate = 'Due date is required'
    } else if (values.dueDate < todayIso()) {
        errors.dueDate = 'Due date cannot be in the past'
    }

    return errors
}

function BorrowEquipmentPage({ equipment, onBorrow }) {
    const { id } = useParams()
    const navigate = useNavigate()
    const [values, setValues] = useState({ borrowerName: '', dueDate: '' })
    const [errors, setErrors] = useState({})
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

    if (item.availability !== 'AVAILABLE') {
        return (
            <section>
                <p role="alert">{item.name} is already on loan.</p>
                <Link className="button button-secondary" to="/">Back to equipment</Link>
            </section>
        )
    }

    function handleChange(event) {
        const { name, value } = event.target
        setValues((current) => ({ ...current, [name]: value }))
    }

    async function handleSubmit(event) {
        event.preventDefault()
        const validationErrors = validateLoan(values)
        setErrors(validationErrors)

        if (Object.keys(validationErrors).length > 0) {
            return
        }

        setSaving(true)
        setSaveError('')

        try {
            await onBorrow(item.id, { borrowerName: values.borrowerName.trim(), dueDate: values.dueDate })
            navigate('/')
        } catch (error) {
            setSaveError(`Could not borrow equipment: ${error.message}`)
            setSaving(false)
        }
    }

    return (
        <section className="form-page">
            <h2>Borrow {item.name}</h2>
            <form onSubmit={handleSubmit} noValidate className="equipment-form">
                <div className="form-field">
                    <label htmlFor="borrowerName">Borrower name</label>
                    <input
                        id="borrowerName"
                        name="borrowerName"
                        value={values.borrowerName}
                        onChange={handleChange}
                        aria-invalid={Boolean(errors.borrowerName)}
                    />
                    {errors.borrowerName && <p role="alert">{errors.borrowerName}</p>}
                </div>

                <div className="form-field">
                    <label htmlFor="dueDate">Due date</label>
                    <input
                        id="dueDate"
                        name="dueDate"
                        type="date"
                        min={todayIso()}
                        value={values.dueDate}
                        onChange={handleChange}
                        aria-invalid={Boolean(errors.dueDate)}
                    />
                    {errors.dueDate && <p role="alert">{errors.dueDate}</p>}
                </div>
                {saveError && <p role="alert">{saveError}</p>}

                <div className="form-actions">
                    <button type="submit" className="button" disabled={saving}>
                        {saving ? 'Saving...' : 'Borrow'}
                    </button>
                    <button type="button" className="button button-secondary" onClick={() => navigate('/')} disabled={saving}>
                        Cancel
                    </button>
                </div>
            </form>
        </section>
    )
}

export default BorrowEquipmentPage