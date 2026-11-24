const baseUrl = '/api/equipment'

async function request(url, options) {
    const response = await fetch(url, options)

    if (!response.ok) {
        const problem = await response.json().catch(() => ({}))
        throw new Error(problem?.detail ?? `Request failed with status ${response.status}`)
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

export function borrowEquipment(equipmentId, loan) {
    return request(`${baseUrl}/${equipmentId}/loans`, sendJson('POST', loan))
}

export function returnLoan(loanId) {
    return request(`/api/loans/${loanId}/return`, { method: 'POST' })
}