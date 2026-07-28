import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './components/ProtectedRoute'
import { HomeRedirect } from './components/HomeRedirect'
import DashboardLayout from './components/DashboardLayout'
import { AdminDashboard } from './pages/AdminDashboard'
import { CustomerDashboard } from './pages/CustomerDashboard'
import { CustomerCard } from './pages/CustomerCard'
import { CustomerAccount } from './pages/CustomerAccount'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { ManagerDashboard } from './pages/ManagerDashboard'
import { CustomerProfile } from './pages/CustomerProfile'
import { CustomerPurchase } from './pages/CustomerPurchase'

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/cadastro" element={<Register />} />
            <Route path="/" element={<HomeRedirect />} />

            <Route element={<ProtectedRoute roles={['CUSTOMER']} />}>
                <Route element={<DashboardLayout />}>
                    <Route path="/dashboard" element={<CustomerDashboard />} />
                    <Route path="/conta" element={<CustomerAccount />} />
                    <Route path="/cartao" element={<CustomerCard />} />
                    <Route path="/perfil" element={<CustomerProfile />} />
                    <Route path="/cartao/comprar" element={<CustomerPurchase />} />
                </Route>
            </Route>

            <Route element={<ProtectedRoute roles={['MANAGER']} />}>
                <Route path="/gerencia" element={<ManagerDashboard />} />
            </Route>

            <Route element={<ProtectedRoute roles={['ADMINISTRATOR']} />}>
                <Route path="/admin" element={<AdminDashboard />} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}