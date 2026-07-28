import { Navigate, Outlet } from 'react-router-dom'
import { getCurrentUser, getHomePath } from '../services/auth.service'

interface ProtectedRouteProps {
  roles?: string[]
}

export function ProtectedRoute({ roles }: ProtectedRouteProps) {
  const user = getCurrentUser()
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) return <Navigate to={getHomePath(user)} replace />
  return <Outlet />
}
