import type { AccountResponse, CreditCardResponse, InvoiceResponse, TransactionResponse } from '../types/api'
import { http } from './http'

export async function getCustomerAccounts(customerId: number): Promise<AccountResponse[]> {
  const { data } = await http.get<AccountResponse[]>(`/customers/${customerId}/accounts`)
  return data
}

export async function getCustomerCreditCard(customerId: number): Promise<CreditCardResponse> {
  const { data } = await http.get<CreditCardResponse>(`/credit-cards/customer/${customerId}`)
  return data
}

export async function getCustomerInvoices(customerId: number): Promise<InvoiceResponse[]> {
  const { data } = await http.get<InvoiceResponse[]>(`/credit-cards/customer/${customerId}/invoices`)
  return data
}

export async function getTransactionHistory(accountId: number): Promise<TransactionResponse[]> {
  const { data } = await http.get<TransactionResponse[]>(`/accounts/${accountId}/transactionHistory`)
  return data
}

export async function deposit(accountId: number, amount: number): Promise<void> {
  await http.post(`/accounts/${accountId}/deposit`, { amount })
}

export async function withdraw(accountId: number, amount: number, transactionPassword: string): Promise<void> {
  await http.post(`/accounts/${accountId}/withdraw`, { amount, transactionPassword })
}

export async function transfer(accountId: number, targetAccountNumber: string, amount: number, transactionPassword: string): Promise<void> {
  await http.post(`/accounts/${accountId}/transfer`, { targetAccountNumber, amount, transactionPassword })
}

export async function transferBetweenOwnAccounts(accountId: number, targetAccountNumber: string, amount: number, transactionPassword: string): Promise<void> {
  await http.post(`/accounts/${accountId}/transferBetweenOwnAccount`, { targetAccountNumber, amount, transactionPassword })
}

export async function requestCreditLimit(customerId: number, requestedLimit: number): Promise<void> {
  await http.post('/requests/credit', { customerId, requestedLimit })
}

export async function payInvoice(invoiceId: number, accountNumber: string, transactionPassword: string): Promise<void> {
  await http.patch(`/credit-cards/invoices/invoice/${invoiceId}/pay`, { accountNumber, transactionPassword })
}

export async function processCreditPurchase(payload: { cardNumber: string; cvv: string; transactionPassword: string; amount: number; description: string }): Promise<void> {
  await http.post('/credit-cards/purchase', payload)
}

export async function getCustomerRequests(customerId: number) { const { data } = await http.get(`/requests/customer/${customerId}`); return data }
export async function requestSavingsAccount(customerId: number): Promise<void> { await http.post('/requests/savings', { customerId }) }
export async function updateCustomer(customerId: number, payload: { name?: string; email?: string; phone?: string }) { const { data } = await http.patch(`/customers/${customerId}`, payload); return data }
