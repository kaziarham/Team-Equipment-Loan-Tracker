import { Link } from 'react-router-dom'

const availabilityLabels = {
    AVAILABLE: 'Available',
    ON_LOAN: 'On loan',
    OVERDUE: 'Overdue',
}

const statusClasses = {
    AVAILABLE: 'status-available',
    ON_LOAN: 'status-on-loan',
    OVERDUE: 'status-overdue',
}

const isOnLoan = (item) => item.availability === 'ON_LOAN' || item.availability === 'OVERDUE'

function EquipmentList({ items, onReturn, emptyMessage = 'No equipment yet.' }) {
    if (items.length === 0) {
        return <p>{emptyMessage}</p>
    }

    return (
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Serial number</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {items.map(item => (
                    <tr key={item.id}>
                        <td data-label="Name">{item.name}</td>
                        <td data-label="Category">{item.category}</td>
                        <td data-label="Serial number">{item.serialNumber ?? '-'}</td>
                        <td data-label="Status">
                            <div className="status-value">
                                <span className={`status-badge ${statusClasses[item.availability] ?? ''}`}>
                                    {availabilityLabels[item.availability] ?? ''}
                                </span>
                                {isOnLoan(item) && (
                                    <div className="loan-details">
                                        <span>Borrowed by {item.activeBorrowerName}</span>
                                        <span>Due {item.activeDueDate}</span>
                                    </div>
                                )}
                            </div>
                        </td>
                        <td data-label="Actions">
                            <div className="row-actions">
                                <Link className="button button-secondary" to={`/equipment/${item.id}/edit`}>Edit</Link>
                                {item.availability === 'AVAILABLE' && (
                                    <Link className="button" to={`/equipment/${item.id}/borrow`}>Borrow</Link>
                                )}
                                {isOnLoan(item) && (
                                    <button type="button" className="button" onClick={() => onReturn(item)}>Return</button>
                                )}
                            </div>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}

export default EquipmentList