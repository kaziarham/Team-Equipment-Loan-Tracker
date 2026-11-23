import { useState, useEffect } from 'react'
import { Link, Route, Routes } from 'react-router-dom'
import { fetchEquipment, createEquipment, updateEquipment } from './api/equipmentApi'
import EquipmentList from './components/EquipmentList'
import AddEquipmentPage from './pages/AddEquipmentPage'
import EditEquipmentPage from './pages/EditEquipmentPage'

function App() {
  const [equipment, setEquipment] = useState([])
  const [status, setStatus] = useState('loading')
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    // Ignore results from a previous run (React StrictMode mounts twice in development).
    let ignore = false

    fetchEquipment()
      .then((data) => {
        if (!ignore) {
          setEquipment(data)
          setStatus('ready')
        }
      })
      .catch((error) => {
        if (!ignore) {
          setErrorMessage(error.message)
          setStatus('error')
        }
      })
    
    return () => {
      ignore = true
    }
  }, [])

  async function addItem(values) {
    const created = await createEquipment(values)
    setEquipment((current) => [...current, created])
  }

  async function saveItem(id, values) {
    const updated = await updateEquipment(id, values)
    setEquipment((current) => current.map((item) => (item.id === updated.id ? updated : item)))
  }

  return (
    <main>
      <h1>Equipment Tracker</h1>
      { status === 'loading' && <p>Loading equipment...</p> }
      { status === 'error' && <p role="alert">Could not load equipment: {errorMessage}</p> }
      { status === 'ready' && (
        <Routes>
          <Route
            path="/"
            element={
              <>
                <div className="page-actions">
                  <Link className="button" to="/equipment/new">Add equipment</Link>
                </div>
                <EquipmentList items={equipment} />
              </>
            }
          />
          <Route path="/equipment/new" element={<AddEquipmentPage onSave={addItem} />} />
          <Route
            path="/equipment/:id/edit"
            element={<EditEquipmentPage equipment={equipment} onSave={saveItem} />}
          />
        </Routes>
      )}
    </main>
  )
}

export default App
