import { useState, useEffect } from 'react'
import { fetchEquipment } from './api/equipmentApi'
import EquipmentList from './components/EquipmentList'

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

  return (
    <main>
      <h1>Equipment Tracker</h1>
      { status === 'loading' && <p>Loading equipment...</p> }
      { status === 'error' && <p role="alert">Could not load equipment: {errorMessage}</p> }
      { status === 'ready' && <EquipmentList items={equipment} /> }
    </main>
  )
}

export default App
