export interface UserResponse {
  id: number
  name: string
  email: string
  cpf: string
  role: 'CUSTOMER' | 'MANAGER' | 'ADMINISTRATOR' | string
}

export interface LoginRequest {
  cpf: string
  password: string
}

export interface RegisterRequest {
  name: string
  cpf: string
  password: string
  confirmPassword: string
  transactionPassword: string
  confirmTransactionPassword: string
  email: string
  phoneNumber: string
}

export interface AccountResponse {
  id: number
  accountNumber: string
  balance: number
  active: boolean
  accountType: 'CHECKING' | 'SAVINGS' | string
  name: string
  status: 'ACTIVE' | 'BLOCKED' | 'CLOSED' | string
}

export interface CreditCardResponse {
  id: number
  cardNumber: string
  cvv: string
  creditLimit: number
  currentSpending: number
  availableLimit: number
}

export interface TransactionResponse {
  id: number
  accountNumber: string
  customerName: string
  amount: number
  type: string
  createdAt: string
}

export interface InvoiceResponse {
  id: number
  status: string
  totalAmount: number
  createdAt: string
  purchases: PurchaseResponse[]
}

export interface PurchaseResponse {
  id: number
  amount: number
  description: string
  createdAt: string
}

export interface DashboardResponse {
  totalAccounts: number
  totalCustomers: number
  totalBankBalance: number
  blockedAccounts: number
  pendingRequests: number
}

export interface RequestResponse {
  id: number
  customerId: number | null
  customerName: string | null
  requestType: string
  requestedAccountType: string | null
  requestedLimit: number | null
  status: string
  createdAt: string
  updatedAt: string | null
}

export interface CustomerResponse {
  id: number
  Name: string
  cpf: string
  email: string
  phone: string
  accounts: AccountResponse[]
}

export interface RegisterManagerRequest {
  name: string
  cpf: string
  email: string
  password: string
  phone: string
  registration: string
}

export interface ApiError {
  message?: string
  fieldErrors?: Record<string, string>
}
