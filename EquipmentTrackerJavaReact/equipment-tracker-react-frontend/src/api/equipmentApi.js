const baseUrl = '/api/equipment'

async function request(url, options) {
    const response = await fetch(url, options)

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`)
    }

    return response.json()
}

function sendJson(method, body) {
    return {
        method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body),
    }
}

export function fetchEquipment() {
    return request(baseUrl)
}

export function createEquipment(equipment) {
    return request(baseUrl, sendJson('POST', equipment))
}

export function updateEquipment(id, equipment) {
    return request(`${baseUrl}/${id}`, sendJson('PUT', equipment))
}