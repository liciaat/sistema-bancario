import type { DashboardResponse, RegisterManagerRequest, UserResponse } from '../types/api'
import { http } from './http'

export async function getAdminDashboard(): Promise<DashboardResponse> { const { data } = await http.get('/admin/dashboard'); return data }
export async function getManagers(): Promise<UserResponse[]> { const { data } = await http.get('/admin/managers'); return data }
export async function getInactiveManagers(): Promise<UserResponse[]> { const { data } = await http.get('/admin/managers/inactive'); return data }
export async function createManager(payload: RegisterManagerRequest): Promise<void> { await http.post('/admin/managers', payload) }
export async function updateManager(id: number, payload: Partial<Pick<RegisterManagerRequest, 'name' | 'email' | 'phone'>>): Promise<void> { await http.patch(`/admin/managers/${id}`, payload) }
export async function removeManager(id: number): Promise<void> { await http.delete(`/admin/managers/${id}`) }
export async function updateInterestRate(rate: number): Promise<void> { await http.post('/admin/interest-rate', undefined, { params: { rate } }) }
