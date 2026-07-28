import axios from 'axios'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { BalanceCard } from '../components/BalanceCard'
import { QuickActions } from '../components/QuickActions'
import { getCurrentUser } from '../services/auth.service'
import { getCustomerAccounts } from '../services/customer.service'
import type { AccountResponse } from '../types/api'

type DashboardData = { accounts: AccountResponse[] }

export function CustomerDashboard() {
  const navigate = useNavigate()
  const user = getCurrentUser()!
  const [isBalanceVisible, setIsBalanceVisible] = useState(true)
  const [data, setData] = useState<DashboardData>({ accounts: [] })
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let isMounted = true

    async function loadDashboard() {
      try {
        const accounts = await getCustomerAccounts(user.id)
        if (isMounted) setData({ accounts })
      } catch (requestError) {
        if (isMounted) {
          setError(axios.isAxiosError(requestError) ? 'Não foi possível carregar os dados da sua conta.' : 'Ocorreu um erro inesperado.')
        }
      } finally {
        if (isMounted) setIsLoading(false)
      }
    }

    void loadDashboard()
    return () => { isMounted = false }
  }, [user.id])

  const mainAccount = data.accounts.find((account) => account.active) ?? data.accounts[0]

  return (
      <div className="w-full">
        <header className="flex items-center justify-between pb-8 mb-8 border-b border-white/[0.06]">
          <div>
            <p className="text-sm font-medium text-zinc-500">Visão geral</p>
          </div>
          <div className="flex items-center gap-5">
            <button onClick={() => navigate('/perfil')} className="flex items-center gap-3 text-left transition-colors rounded-2xl hover:bg-white/[0.05] p-2">
            <span className="grid h-10 w-10 place-items-center rounded-2xl bg-[#ffde00] text-sm font-extrabold text-black">
              {user.name.charAt(0).toUpperCase()}
            </span>
              <div className="hidden sm:block">
                <p className="max-w-36 truncate text-sm font-semibold text-zinc-200">{user.name}</p>
                <p className="text-xs text-zinc-500">Ver perfil</p>
              </div>
            </button>
          </div>
        </header>

        <div className="mb-8">
          <h1 className="text-3xl font-extrabold tracking-[-0.05em] text-white sm:text-4xl">Início</h1>
        </div>

        {error && (
            <div role="alert" className="mb-6 rounded-2xl border border-amber-400/20 bg-amber-400/10 px-4 py-3 text-sm text-amber-100">
              {error}
            </div>
        )}

        {isLoading ? (
            <DashboardSkeleton />
        ) : (
            <div className="space-y-8">
              <div className="grid gap-5 lg:grid-cols-2">
                {data.accounts.filter((account) => account.active).map((account) => (
                    <BalanceCard
                        key={account.id}
                        balance={Number(account.balance)}
                        accountNumber={account.accountNumber}
                        accountLabel={account.accountType === 'SAVINGS' ? 'Conta poupança' : 'Conta corrente'}
                        isVisible={isBalanceVisible}
                        onToggleVisibility={() => setIsBalanceVisible((visible) => !visible)}
                    />
                ))}
                {!data.accounts.length && (
                    <BalanceCard
                        balance={Number(mainAccount?.balance ?? 0)}
                        accountNumber={mainAccount?.accountNumber}
                        isVisible={isBalanceVisible}
                        onToggleVisibility={() => setIsBalanceVisible((visible) => !visible)}
                    />
                )}
              </div>
              <QuickActions />
            </div>
        )}
      </div>
  )
}

function DashboardSkeleton() {
  return <div className="h-52 animate-pulse rounded-[30px] bg-white/5" />
}