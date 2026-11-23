import { Link } from 'react-router-dom'

function EquipmentList({ items }) {
    if (items.length === 0) {
        return <p>No equipment yet.</p>
    }

    return (
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Serial number</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {items.map(item => (
                    <tr key={item.id}>
                        <td data-label="Name">{item.name}</td>
                        <td data-label="Category">{item.category}</td>
                        <td data-label="Serial number">{item.serialNumber ?? '-'}</td>
                        <td data-label="Actions">
                            <Link className="button button-secondary" to={`/equipment/${item.id}/edit`}>Edit</Link>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}

export default EquipmentList