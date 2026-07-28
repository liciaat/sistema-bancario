import type { LoginRequest, RegisterRequest, UserResponse } from '../types/api'
import { http } from './http'

const sessionKey = '67pay:user'

export async function login(credentials: LoginRequest): Promise<UserResponse> {
  const { data } = await http.post<UserResponse>('/auth/login', credentials)
  sessionStorage.setItem(sessionKey, JSON.stringify(data))
  return data
}

export async function register(payload: RegisterRequest): Promise<UserResponse> {
  const { data } = await http.post<UserResponse>('/auth/register', payload)
  return data
}

export async function updatePassword(cpf: string, password: string, newPassword: string): Promise<void> { await http.patch('/auth/password', { cpf, password, newPassword }) }
export async function closeAccount(userId: number, password: string): Promise<void> { await http.post('/auth/close-acount', { userId, password }) }

export function getCurrentUser(): UserResponse | null {
  const serializedUser = sessionStorage.getItem(sessionKey)
  if (!serializedUser) return null

  try {
    return JSON.parse(serializedUser) as UserResponse
  } catch {
    sessionStorage.removeItem(sessionKey)
    return null
  }
}

export function logout(): void {
  sessionStorage.removeItem(sessionKey)
}

export function getHomePath(user: UserResponse): string {
  if (user.role === 'ADMINISTRATOR') return '/admin'
  if (user.role === 'MANAGER') return '/gerencia'
  return '/dashboard'
}
