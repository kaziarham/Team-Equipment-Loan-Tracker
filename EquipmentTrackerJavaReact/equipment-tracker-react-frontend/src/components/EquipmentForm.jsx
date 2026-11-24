import { useState } from "react";

const fields = [
    { name: 'name', label: 'Name', maxLength: 255, required: true },
    { name: 'category', label: 'Category', maxLength: 100, required: true },
    { name: 'serialNumber', label: 'Serial number', maxLength: 255 },
]

const emptyValues = {
    name: '',
    category: '',
    serialNumber: ''
}

function validateEquipment(values) {
    const errors = {}

    for (const field of fields) {
        const value = values[field.name].trim()

        if (field.required && !value) {
            errors[field.name] = `${field.label} is required`
        } else if (value.length > field.maxLength) {
            errors[field.name] = `${field.label} must be ${field.maxLength} characters or fewer`
        }
    }

    return errors
}

function EquipmentForm({ initialValues = emptyValues, submitting, serverError, onSubmit, onCancel }) {
    const [values, setValues] = useState(initialValues)
    const [errors, setErrors] = useState({})

    function handleChange(event) {
        const { name, value } = event.target
        setValues((current) => ({ ...current, [name]: value }))
    }

    function handleSubmit(event) {
        event.preventDefault()
        const validationErrors = validateEquipment(values)
        setErrors(validationErrors)

        if (Object.keys(validationErrors).length === 0) {
            onSubmit(values)
        }
    }

    return (
        <form onSubmit={handleSubmit} noValidate className="equipment-form">
            {fields.map((field) => (
                <div key={field.name} className="form-field">
                    <label htmlFor={field.name}>{field.label}</label>
                    <input
                        id={field.name}
                        name={field.name}
                        value={values[field.name]}
                        onChange={handleChange}
                        aria-invalid={Boolean(errors[field.name])}
                    />
                    {errors[field.name] && <p role="alert">{errors[field.name]}</p>}
                </div>
            ))}
            {serverError && <p role="alert">{serverError}</p>}
            <div className="form-actions">
                <button type="submit" className="button" disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save'}
                </button>
                <button type="button" className="button button-secondary" onClick={onCancel} disabled={submitting}>
                    Cancel
                </button>
            </div>
        </form>
    )
}

export default EquipmentForm