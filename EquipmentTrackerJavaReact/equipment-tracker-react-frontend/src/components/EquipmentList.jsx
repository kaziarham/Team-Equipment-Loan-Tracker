export default function EquipmentList({ items }) {
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
                </tr>
            </thead>
            <tbody>
                {items.map(item => (
                    <tr key={item.id}>
                        <td>{item.name}</td>
                        <td>{item.category}</td>
                        <td>{item.serialNumber ?? '-'}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}