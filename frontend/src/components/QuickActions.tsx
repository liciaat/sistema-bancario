import { ArrowDownToLine, ArrowLeftRight, BadgePlus, ReceiptText } from 'lucide-react'
import { Link } from 'react-router-dom'

const actions = [
  { label: 'Depositar', icon: ArrowDownToLine, to: '/conta?acao=deposito' },
  { label: 'Transferir', icon: ArrowLeftRight, to: '/conta?acao=transferir' },
  { label: 'Pagar fatura', icon: ReceiptText, to: '/cartao?acao=fatura' },
  { label: 'Solicitar limite', icon: BadgePlus, to: '/cartao?acao=limite' },
  { label: 'Comprar no crédito', icon: ReceiptText, to: '/cartao/comprar' },
]

export function QuickActions() {
  return (
    <section>
      <div className="mb-4 flex items-end justify-between">
        <div>
          <h2 className="text-lg font-bold text-white">Ações rápidas</h2>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        {actions.map(({ label, icon: Icon, to }) => (
          <Link
            key={label}
            to={to}
            className="focus-ring group rounded-[22px] border border-white/[0.07] bg-panel p-4 text-left shadow-card hover:-translate-y-0.5 hover:border-brand/30 hover:bg-panel-soft"
          >
            <span className="grid h-10 w-10 place-items-center rounded-2xl bg-brand/10 text-brand transition group-hover:bg-brand group-hover:text-black">
              <Icon size={19} />
            </span>
            <span className="mt-4 block text-sm font-semibold text-zinc-200">{label}</span>
          </Link>
        ))}
      </div>
    </section>
  )
}
