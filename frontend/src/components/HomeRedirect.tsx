import { Navigate } from 'react-router-dom'
import { getCurrentUser, getHomePath } from '../services/auth.service'

export function HomeRedirect() {
  const user = getCurrentUser()
  return <Navigate to={user ? getHomePath(user) : '/login'} replace />
}
