import type { AccountResponse, CustomerResponse, RequestResponse, TransactionResponse } from '../types/api'
import { http } from './http'

export async function getPendingRequests(): Promise<RequestResponse[]> { const { data } = await http.get('/managers/requests/pending'); return data }
export async function processRequest(id: number, approved: boolean, managerId: number, managerPassword: string): Promise<void> { await http.patch(`/requests/${id}/${approved ? 'approve' : 'reject'}`, { managerId, managerPassword }) }
export async function getManagerCustomers(): Promise<CustomerResponse[]> { const { data } = await http.get('/managers/reports/customers'); return data }
export async function getManagerTransactions(): Promise<TransactionResponse[]> { const { data } = await http.get('/managers/reports/transactions'); return data }
export async function getNegativeAccounts(): Promise<AccountResponse[]> { const { data } = await http.get('/managers/reports/negative-accounts'); return data }
export async function toggleAccountStatus(id: number): Promise<void> { await http.patch(`/managers/accounts/${id}/toggle-status`) }
