import { Wifi } from 'lucide-react'
import type { CreditCardResponse } from '../types/api'
import { formatCardNumber, formatCurrency } from '../utils/format'

interface CreditCardProps {
  card: CreditCardResponse | null
  invoiceTotal: number
  customerName: string
}

export function CreditCard({ card, invoiceTotal, customerName }: CreditCardProps) {
  const limit = card?.availableLimit ?? 0
  const currentInvoice = invoiceTotal || card?.currentSpending || 0
  const cardNumber = card ? formatCardNumber(card.cardNumber) : '•••• •••• •••• ••••'

  return (
    <section className="surface p-5 sm:p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-semibold text-zinc-200">Seu cartão</p>
          <p className="mt-1 text-xs text-zinc-500">Controle sem complicação.</p>
        </div>
        <span className="rounded-full border border-brand/25 bg-brand/10 px-3 py-1 text-[11px] font-bold text-brand">CRÉDITO</span>
      </div>
      <div className="relative mt-5 min-h-52 overflow-hidden rounded-[25px] bg-brand p-5 text-black shadow-brand">
        <div className="absolute -right-5 -top-8 h-36 w-36 rounded-full border-[18px] border-black/[0.06]" />
        <div className="absolute -bottom-10 -left-3 h-28 w-28 rounded-full bg-black/[0.05]" />
        <div className="relative flex h-full min-h-44 flex-col justify-between">
          <div className="flex items-center justify-between">
            <p className="text-sm font-black tracking-[0.2em]">67 PAY</p>
            <Wifi className="rotate-90" size={23} strokeWidth={2.4} />
          </div>
          <p className="font-mono text-lg font-semibold tracking-[0.18em]">{cardNumber}</p>
          <div className="flex items-end justify-between gap-3">
            <div className="min-w-0">
              <p className="truncate text-[9px] font-bold uppercase tracking-[0.15em] text-black/55">Titular</p>
              <p className="truncate text-xs font-bold uppercase tracking-[0.08em]">{customerName}</p>
            </div>
            <p className="text-sm font-black italic tracking-tighter">VISA</p>
          </div>
        </div>
      </div>
      <div className="mt-5 grid grid-cols-2 gap-3">
        <div className="rounded-2xl bg-white/[0.04] p-3.5">
          <p className="text-[11px] font-medium text-zinc-500">Limite disponível</p>
          <p className="mt-1 text-sm font-bold text-zinc-100">{formatCurrency(limit)}</p>
        </div>
        <div className="rounded-2xl bg-white/[0.04] p-3.5">
          <p className="text-[11px] font-medium text-zinc-500">Fatura atual</p>
          <p className="mt-1 text-sm font-bold text-zinc-100">{formatCurrency(currentInvoice)}</p>
        </div>
      </div>
    </section>
  )
}
