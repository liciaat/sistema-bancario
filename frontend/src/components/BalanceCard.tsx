import { Eye, EyeOff } from 'lucide-react'
import { formatCurrency } from '../utils/format'

interface BalanceCardProps {
  balance: number
  isVisible: boolean
  onToggleVisibility: () => void
  accountNumber?: string
  accountLabel?: string
}

export function BalanceCard({ balance, isVisible, onToggleVisibility, accountNumber, accountLabel = 'Conta' }: BalanceCardProps) {
  return (
    <section className="relative overflow-hidden rounded-[30px] bg-brand px-6 py-7 text-black shadow-brand sm:px-8">
      <div className="absolute -right-10 -top-16 h-52 w-52 rounded-full border-[24px] border-black/[0.05]" />
      <div className="absolute -bottom-20 right-20 h-40 w-40 rounded-full bg-black/[0.04]" />
      <div className="relative">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-sm font-semibold text-black/65">Saldo disponível</p>
            <p className="mt-3 text-3xl font-extrabold tracking-[-0.05em] sm:text-4xl">
              {isVisible ? formatCurrency(balance) : '••••••'}
            </p>
          </div>
          <button
            type="button"
            onClick={onToggleVisibility}
            aria-label={isVisible ? 'Ocultar saldo' : 'Mostrar saldo'}
            className="focus-ring grid h-10 w-10 place-items-center rounded-2xl bg-black/[0.08] text-black hover:bg-black/[0.14]"
          >
            {isVisible ? <EyeOff size={19} /> : <Eye size={19} />}
          </button>
        </div>
        <div className="mt-8 flex items-center gap-2 text-xs font-semibold text-black/60">
          <span className="h-2 w-2 rounded-full bg-black/70" />
          {accountLabel} · {accountNumber ?? 'em análise'}
        </div>
      </div>
    </section>
  )
}
