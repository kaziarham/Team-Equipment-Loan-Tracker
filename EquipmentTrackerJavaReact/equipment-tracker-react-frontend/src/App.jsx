import { useState, useEffect } from 'react'
import { Route, Routes } from 'react-router-dom'
import { fetchEquipment, createEquipment, updateEquipment, borrowEquipment, returnLoan } from './api/equipmentApi'
import EquipmentList from './components/EquipmentList'
import AddEquipmentPage from './pages/AddEquipmentPage'
import BorrowEquipmentPage from './pages/BorrowEquipmentPage'
import EditEquipmentPage from './pages/EditEquipmentPage'

function App() {
  const [equipment, setEquipment] = useState([])
  const [status, setStatus] = useState('loading')
  const [errorMessage, setErrorMessage] = useState('')
  const [filter, setFilter] = useState('ALL')
  const [actionError, setActionError] = useState('')

  const filters = [
  { value: 'ALL', label: 'All' },
  { value: 'AVAILABLE', label: 'Available' },
  { value: 'ON_LOAN', label: 'On loan' },
  { value: 'OVERDUE', label: 'Overdue' },
]

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

  const visibleEquipment = filter === 'ALL'
    ? equipment
    : equipment.filter((item) => item.availability === filter)
  
  async function refreshEquipment() {
    setEquipment(await fetchEquipment())
  }

  async function addItem(values) {
    const created = await createEquipment(values)
    setEquipment((current) => [...current, created])
  }

  async function saveItem(id, values) {
    const updated = await updateEquipment(id, values)
    setEquipment((current) => current.map((item) => (item.id === updated.id ? updated : item)))
  }

  async function borrowItem(id, values) {
    await borrowEquipment(id, values)
    await refreshEquipment()
  }

  async function returnItem(item) {
    setActionError('')

    try {
      await returnLoan(item.activeLoanId)
      await refreshEquipment()
    } catch (error) {
      setActionError(`Could not return ${item.name}: ${error.message}`)
    }
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
                  <div className="filter-group" role="group" aria-label="Filter by availability">
                    {filters.map((option) => (
                      <button
                        key={option.value}
                        type="button"
                        className={`filter-button ${filter === option.value ? 'filter-button-active' : ''}`}
                        aria-pressed={filter === option.value}
                        onClick={() => setFilter(option.value)}
                      >
                        {option.label}
                      </button>
                    ))}
                  </div>
                </div>
                {actionError && <p role="alert">{actionError}</p>}
                <EquipmentList 
                  items={visibleEquipment}
                  onReturn={returnItem}
                  emptyMessage={filter === 'ALL' ? 'No equipment yet.' : 'No equipment matches this filter.'}
                />
              </>
            }
          />
          <Route path="/equipment/new" element={<AddEquipmentPage onSave={addItem} />} />
          <Route
            path="/equipment/:id/edit"
            element={<EditEquipmentPage equipment={equipment} onSave={saveItem} />}
          />
          <Route
            path="/equipment/:id/borrow"
            element={<BorrowEquipmentPage equipment={equipment} onBorrow={borrowItem} />}
          />
        </Routes>
      )}
    </main>
  )
}

export default App
