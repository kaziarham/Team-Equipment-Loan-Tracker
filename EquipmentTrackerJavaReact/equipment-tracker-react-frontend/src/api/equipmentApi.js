export async function fetchEquipment() {
    const response = await fetch('/api/equipment')

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`)
    }

    return response.json()
}