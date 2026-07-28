import { ArrowDownToLine, ArrowLeft, ArrowLeftRight, ArrowUpFromLine, CheckCircle2, Landmark } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { BrandLogo } from '../components/BrandLogo'
import { getApiErrorMessage } from '../services/api-error'
import { getCurrentUser } from '../services/auth.service'
import { deposit, getCustomerAccounts, getTransactionHistory, transfer, transferBetweenOwnAccounts, withdraw } from '../services/customer.service'
import type { AccountResponse, TransactionResponse } from '../types/api'
import { formatCurrency } from '../utils/format'

type Operation = 'deposito' | 'transferir' | 'saque'
const operations = [
  { id: 'deposito' as const, label: 'Depositar', icon: ArrowDownToLine },
  { id: 'transferir' as const, label: 'Transferir', icon: ArrowLeftRight },
  { id: 'saque' as const, label: 'Sacar', icon: ArrowUpFromLine },
]

export function CustomerAccount() {
  const user = getCurrentUser()!
  const [searchParams] = useSearchParams()
  const [operation, setOperation] = useState<Operation>(searchParams.get('acao') === 'transferir' ? 'transferir' : 'deposito')
  const [accounts, setAccounts] = useState<AccountResponse[]>([])
  const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null)
  const [transactions, setTransactions] = useState<TransactionResponse[]>([])

  const [amount, setAmount] = useState('')
  const [targetAccountNumber, setTargetAccountNumber] = useState('')
  const [isOwnTransfer, setIsOwnTransfer] = useState(false)
  const [transactionPassword, setTransactionPassword] = useState('')

  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const selectedAccount = accounts.find((account) => account.id === selectedAccountId) ?? accounts[0]

  async function loadAccounts() {
    const data = await getCustomerAccounts(user.id)
    setAccounts(data)
    setSelectedAccountId((current) => current ?? data[0]?.id ?? null)
    return data
  }

  useEffect(() => {
    void loadAccounts()
        .catch(() => setError('Não foi possível carregar suas contas.'))
        .finally(() => setIsLoading(false))
  }, [user.id])

  useEffect(() => {
    if (selectedAccount?.id) {
      void getTransactionHistory(selectedAccount.id)
          .then(setTransactions)
          .catch(() => setTransactions([]))
    }
  }, [selectedAccount?.id])

  // Melhoria de UX: Limpa os formulários ao trocar de aba
  function handleOperationChange(newOperation: Operation) {
    setOperation(newOperation)
    setError(null)
    setSuccess(null)
    setAmount('')
    setTransactionPassword('')
    setTargetAccountNumber('')
    setIsOwnTransfer(false)
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!selectedAccount) return
    setError(null)
    setSuccess(null)

    const numericAmount = Number(amount.replace(',', '.'))
    if (!Number.isFinite(numericAmount) || numericAmount <= 0) {
      return setError('Informe um valor maior que zero.')
    }

    if (operation !== 'deposito' && !transactionPassword) {
      return setError('Informe sua senha de transação.')
    }

    if (operation === 'transferir' && !targetAccountNumber.trim()) {
      return setError('Informe o número da conta de destino.')
    }

    setIsSubmitting(true)
    try {
      if (operation === 'deposito') {
        await deposit(selectedAccount.id, numericAmount)
        setSuccess('Depósito realizado com sucesso.')
      } else if (operation === 'saque') {
        await withdraw(selectedAccount.id, numericAmount, transactionPassword)
        setSuccess('Saque realizado com sucesso.')
      } else if (operation === 'transferir') {
        if (isOwnTransfer) {
          await transferBetweenOwnAccounts(selectedAccount.id, targetAccountNumber, numericAmount, transactionPassword)
        } else {
          await transfer(selectedAccount.id, targetAccountNumber, numericAmount, transactionPassword)
        }
        setSuccess('Transferência realizada com sucesso.')
      }

      setAmount('')
      setTargetAccountNumber('')
      setTransactionPassword('')

      const data = await loadAccounts()
      const updated = data.find((account) => account.id === selectedAccount.id)
      if (updated) {
        setTransactions(await getTransactionHistory(updated.id))
      }
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Não foi possível concluir a operação.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
      <main className="min-h-screen bg-ink px-5 py-7 sm:px-8 sm:py-10">
        <div className="mx-auto max-w-5xl">
          <header className="flex items-center justify-between">
            <BrandLogo compact />
            <Link to="/dashboard" className="focus-ring inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-semibold text-zinc-400 hover:bg-white/[0.05] hover:text-white">
              <ArrowLeft size={17} /> Início
            </Link>
          </header>

          <section className="mt-10">
            <p className="text-sm font-semibold text-brand">Sua conta</p>
            <h1 className="mt-1 text-3xl font-extrabold tracking-[-0.05em] text-white sm:text-4xl">Movimente com tranquilidade.</h1>
          </section>

          {isLoading ? (
              <div className="mt-8 h-72 animate-pulse rounded-[30px] bg-white/[0.04]" />
          ) : (
              <div className="mt-8 grid gap-6 lg:grid-cols-[390px_minmax(0,1fr)]">
                <section className="surface p-6">
                  <div className="flex items-center gap-2 text-brand">
                    <Landmark size={19} />
                    <p className="text-sm font-bold">Conta selecionada</p>
                  </div>

                  <select
                      value={selectedAccount?.id ?? ''}
                      onChange={(event) => setSelectedAccountId(Number(event.target.value))}
                      className="focus-ring mt-5 w-full rounded-2xl border border-white/[0.09] bg-black/20 px-4 py-3 text-sm text-white"
                  >
                    {accounts.map((account) => (
                        <option key={account.id} value={account.id}>
                          {account.accountType === 'CHECKING' ? 'Conta corrente' : 'Conta poupança'} • {account.accountNumber}
                        </option>
                    ))}
                  </select>

                  <p className="mt-7 text-sm text-zinc-500">Saldo disponível</p>
                  <p className="mt-1 text-3xl font-extrabold tracking-[-0.05em] text-white">
                    {formatCurrency(Number(selectedAccount?.balance ?? 0))}
                  </p>

                  <div className="mt-8 grid grid-cols-3 gap-2">
                    {operations.map(({ id, label, icon: Icon }) => (
                        <button
                            key={id}
                            type="button"
                            onClick={() => handleOperationChange(id)}
                            className={`focus-ring rounded-2xl p-3 text-center text-xs font-semibold transition-colors ${
                                operation === id ? 'bg-brand text-black' : 'bg-white/[0.04] text-zinc-400 hover:bg-white/[0.08]'
                            }`}
                        >
                          <Icon className="mx-auto mb-2" size={17} />
                          {label}
                        </button>
                    ))}
                  </div>

                  <form onSubmit={handleSubmit} className="mt-6 space-y-4">
                    <label className="block">
                      <span className="mb-2 block text-xs font-medium text-zinc-400">Valor</span>
                      <input
                          required
                          inputMode="decimal"
                          value={amount}
                          onChange={(event) => setAmount(event.target.value)}
                          placeholder="0,00"
                          className="input"
                      />
                    </label>

                    {operation === 'transferir' && (
                        <>
                          <label className="flex items-center gap-2 text-xs text-zinc-400">
                            <input
                                type="checkbox"
                                checked={isOwnTransfer}
                                onChange={(event) => {
                                  const own = event.target.checked
                                  setIsOwnTransfer(own)
                                  if (own) {
                                    setTargetAccountNumber(accounts.find((account) => account.id !== selectedAccount?.id)?.accountNumber ?? '')
                                  } else {
                                    setTargetAccountNumber('') // Limpa ao desmarcar a caixa
                                  }
                                }}
                                className="h-4 w-4 accent-[#ffde00]"
                            />
                            Transferir entre minhas contas
                          </label>

                          {isOwnTransfer ? (
                              <label className="block">
                                <span className="mb-2 block text-xs font-medium text-zinc-400">Conta de destino</span>
                                <select
                                    required
                                    value={targetAccountNumber}
                                    onChange={(event) => setTargetAccountNumber(event.target.value)}
                                    className="input"
                                >
                                  {accounts.filter((account) => account.id !== selectedAccount?.id).map((account) => (
                                      <option key={account.id} value={account.accountNumber}>
                                        {account.accountType === 'CHECKING' ? 'Conta corrente' : 'Conta poupança'} · {account.accountNumber}
                                      </option>
                                  ))}
                                </select>
                              </label>
                          ) : (
                              <label className="block">
                                <span className="mb-2 block text-xs font-medium text-zinc-400">Número da conta de destino</span>
                                <input
                                    required
                                    value={targetAccountNumber}
                                    onChange={(event) => setTargetAccountNumber(event.target.value)}
                                    placeholder="Ex.: 123456789"
                                    className="input"
                                />
                              </label>
                          )}
                        </>
                    )}

                    {operation !== 'deposito' && (
                        <label className="block">
                          <span className="mb-2 block text-xs font-medium text-zinc-400">Senha de transação</span>
                          <input
                              required
                              type="password"
                              value={transactionPassword}
                              onChange={(event) => setTransactionPassword(event.target.value)}
                              className="input"
                          />
                        </label>
                    )}

                    {error && (
                        <p role="alert" className="rounded-xl border border-red-500/20 bg-red-500/10 px-3 py-2 text-sm text-red-300">
                          {error}
                        </p>
                    )}
                    {success && (
                        <p role="status" className="flex items-center gap-2 rounded-xl border border-brand/20 bg-brand/10 px-3 py-2 text-sm text-brand">
                          <CheckCircle2 size={16} />
                          {success}
                        </p>
                    )}

                    <button
                        disabled={isSubmitting || !selectedAccount}
                        className="focus-ring w-full rounded-2xl bg-brand px-4 py-3 text-sm font-bold text-black shadow-brand hover:bg-[#ffe633] disabled:opacity-60"
                    >
                      {isSubmitting ? 'Processando...' : operations.find((item) => item.id === operation)?.label}
                    </button>
                  </form>
                </section>

                <section className="surface p-6">
                  <h2 className="text-lg font-bold text-white">Últimas movimentações</h2>
                  <p className="mt-1 text-sm text-zinc-500">Seu extrato recente nesta conta.</p>

                  <div className="mt-6 divide-y divide-white/[0.06]">
                    {transactions.length === 0 ? (
                        <p className="py-8 text-center text-sm text-zinc-600">Ainda não há movimentações para exibir.</p>
                    ) : (
                        transactions.slice(0, 10).map((transaction) => (
                            <div key={transaction.id} className="flex items-center justify-between gap-4 py-4">
                              <div>
                                <p className="text-sm font-semibold text-zinc-200">
                                  {transaction.type === 'DEPOSIT' ? 'Depósito' : transaction.type === 'WITHDRAW' ? 'Saque' : 'Transferência'}
                                </p>
                                <p className="mt-1 text-xs text-zinc-600">
                                  {new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(transaction.createdAt))}
                                </p>
                              </div>
                              <p className={`text-sm font-bold ${Number(transaction.amount) >= 0 ? 'text-brand' : 'text-zinc-200'}`}>
                                {Number(transaction.amount) >= 0 ? '+' : '-'} {formatCurrency(Math.abs(Number(transaction.amount)))}
                              </p>
                            </div>
                        ))
                    )}
                  </div>
                </section>
              </div>
          )}
        </div>
      </main>
  )
}