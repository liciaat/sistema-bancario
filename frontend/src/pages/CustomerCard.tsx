import { ArrowLeft, Eye, EyeOff, ShieldCheck, Wifi, X } from 'lucide-react'
import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { BrandLogo } from '../components/BrandLogo'
import { getApiErrorMessage } from '../services/api-error'
import { getCurrentUser } from '../services/auth.service'
import { getCustomerAccounts, getCustomerCreditCard, getCustomerInvoices, payInvoice, requestCreditLimit } from '../services/customer.service'
import type { AccountResponse, CreditCardResponse, InvoiceResponse } from '../types/api'
import { formatCardNumber, formatCurrency } from '../utils/format'

export function CustomerCard() {
  const user = getCurrentUser()!
  const [searchParams, setSearchParams] = useSearchParams()
  const [card, setCard] = useState<CreditCardResponse | null>(null)
  const [invoices, setInvoices] = useState<InvoiceResponse[]>([])
  const [accounts, setAccounts] = useState<AccountResponse[]>([])
  const [isVisible, setIsVisible] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [actionError, setActionError] = useState<string | null>(null)
  const [actionSuccess, setActionSuccess] = useState<string | null>(null)
  const [requestedLimit, setRequestedLimit] = useState('')
  const [paymentPassword, setPaymentPassword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    let mounted = true
    void Promise.all([getCustomerCreditCard(user.id), getCustomerInvoices(user.id), getCustomerAccounts(user.id)])
        .then(([cardData, invoiceData, accountData]) => { if (mounted) { setCard(cardData); setInvoices(invoiceData); setAccounts(accountData) } })
        .catch(() => { if (mounted) setError('Ainda não foi possível consultar seu cartão. Se ele foi solicitado recentemente, aguarde a aprovação.') })
        .finally(() => { if (mounted) setIsLoading(false) })
    return () => { mounted = false }
  }, [user.id])

  const pendingInvoice = useMemo(
      () => invoices.filter((invoice) => invoice.status !== 'PAID').reduce((total, invoice) => total + Number(invoice.totalAmount), 0),
      [invoices],
  )
  const cardNumber = card ? formatCardNumber(card.cardNumber) : '•••• •••• •••• ••••'
  const shownNumber = isVisible ? cardNumber : '•••• •••• •••• ' + (cardNumber.slice(-4) || '••••')
  const action = searchParams.get('acao')
  const pendingInvoices = invoices.filter((invoice) => invoice.status !== 'PAID')

  function closeAction() {
    setActionError(null)
    setActionSuccess(null)
    setSearchParams({})
  }

  useEffect(() => {
    if (!action) return
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !isSubmitting) closeAction()
    }
    window.addEventListener('keydown', closeOnEscape)
    return () => window.removeEventListener('keydown', closeOnEscape)
  }, [action, isSubmitting])

  async function handleLimitRequest(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setActionError(null); setActionSuccess(null)
    const value = Number(requestedLimit.replace(',', '.'))
    if (!Number.isFinite(value) || value <= 0) { setActionError('Informe um limite maior que zero.'); return }
    setIsSubmitting(true)
    try { await requestCreditLimit(user.id, value); setRequestedLimit(''); setActionSuccess('Solicitação enviada para análise.') }
    catch (requestError) { setActionError(getApiErrorMessage(requestError, 'Não foi possível solicitar o limite.')) }
    finally { setIsSubmitting(false) }
  }

  async function handleInvoicePayment(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setActionError(null); setActionSuccess(null)
    const invoice = pendingInvoices[0]
    const account = accounts[0]
    if (!invoice || !account) { setActionError('Não há fatura pendente ou conta disponível para pagamento.'); return }
    if (!paymentPassword) { setActionError('Informe sua senha de transação.'); return }
    setIsSubmitting(true)
    try { await payInvoice(invoice.id, account.accountNumber, paymentPassword); setPaymentPassword(''); setActionSuccess('Fatura paga com sucesso.'); setInvoices(await getCustomerInvoices(user.id)) }
    catch (requestError) { setActionError(getApiErrorMessage(requestError, 'Não foi possível pagar a fatura.')) }
    finally { setIsSubmitting(false) }
  }

  const sortedInvoices = [...invoices].sort((a, b) => {
    if (a.status === 'PENDING' && b.status !== 'PENDING') return -1
    if (a.status !== 'PENDING' && b.status === 'PENDING') return 1
    return b.id - a.id
  })

  return (
      <main className="min-h-screen bg-ink px-5 py-7 sm:px-8 sm:py-10">
        <div className="mx-auto max-w-5xl">
          <header className="flex items-center justify-between"><BrandLogo compact /><Link to="/dashboard" className="focus-ring inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-semibold text-zinc-400 hover:bg-white/[0.05] hover:text-white"><ArrowLeft size={17} /> Início</Link></header>
          <section className="mt-10"><p className="text-sm font-semibold text-brand">Cartão 67 PAY</p><h1 className="mt-1 text-3xl font-extrabold tracking-[-0.05em] text-white sm:text-4xl">Cartão</h1><Link to="/cartao/comprar" className="focus-ring mt-5 inline-flex rounded-2xl bg-brand px-5 py-3 text-sm font-bold text-black shadow-brand hover:bg-[#ffe633]">Comprar no crédito</Link></section>
          {error && <p role="alert" className="mt-7 rounded-2xl border border-amber-400/20 bg-amber-400/10 px-4 py-3 text-sm text-amber-100">{error}</p>}
          {isLoading ? <div className="mt-8 h-72 animate-pulse rounded-[30px] bg-white/[0.04]" /> : <div className="mt-8 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
            <section className="surface p-5 sm:p-7">
              <div className="relative min-h-64 overflow-hidden rounded-[28px] bg-brand p-6 text-black shadow-brand sm:p-7">
                <div className="absolute -right-8 -top-12 h-48 w-48 rounded-full border-[24px] border-black/[0.06]" /><div className="absolute -bottom-12 -left-4 h-36 w-36 rounded-full bg-black/[0.05]" />
                <div className="relative flex min-h-52 flex-col justify-between"><div className="flex items-center justify-between"><p className="font-black tracking-[0.2em]">67 PAY</p><Wifi className="rotate-90" size={26} strokeWidth={2.4} /></div><p className="font-mono text-xl font-semibold tracking-[0.16em] sm:text-2xl">{shownNumber}</p><div className="flex items-end justify-between"><div><p className="text-[10px] font-bold uppercase tracking-[0.14em] text-black/55">Titular</p><p className="mt-1 text-xs font-bold uppercase tracking-[0.08em]">{user.name}</p></div><p className="text-lg font-black italic tracking-tighter">VISA</p></div></div>
              </div>
              <button type="button" onClick={() => setIsVisible((visible) => !visible)} className="focus-ring mt-5 flex w-full items-center justify-center gap-2 rounded-2xl border border-brand/30 bg-brand/10 px-4 py-3 text-sm font-bold text-brand hover:bg-brand hover:text-black">{isVisible ? <EyeOff size={18} /> : <Eye size={18} />}{isVisible ? 'Ocultar dados do cartão' : 'Ver dados do cartão'}</button>
              <div className="mt-4 grid grid-cols-2 gap-3"><div className="rounded-2xl bg-white/[0.04] p-4"><p className="text-[11px] text-zinc-500">CVV</p><p className="mt-1 font-mono text-sm font-bold text-zinc-100">{isVisible ? (card?.cvv ?? '---') : '•••'}</p></div><div className="rounded-2xl bg-white/[0.04] p-4"><p className="text-[11px] text-zinc-500">Limite disponível</p><p className="mt-1 text-sm font-bold text-zinc-100">{formatCurrency(Number(card?.availableLimit ?? 0))}</p></div></div>
            </section>
            <aside className="surface p-6"><div className="flex items-center gap-2 text-brand"><ShieldCheck size={19} /><p className="text-sm font-bold">Resumo do cartão</p></div><div className="mt-7 space-y-5"><div><p className="text-sm text-zinc-500">Fatura atual</p><p className="mt-1 text-2xl font-extrabold tracking-[-0.04em] text-white">{formatCurrency(pendingInvoice || Number(card?.currentSpending ?? 0))}</p></div><div className="h-px bg-white/[0.07]" /><div><p className="text-sm text-zinc-500">Limite total</p><p className="mt-1 text-lg font-bold text-zinc-200">{formatCurrency(Number(card?.creditLimit ?? 0))}</p></div></div><button onClick={() => setSearchParams({ acao: 'fatura' })} className="focus-ring mt-8 w-full rounded-2xl bg-brand px-4 py-3 text-sm font-bold text-black shadow-brand hover:bg-[#ffe633]">Pagar fatura</button><button onClick={() => setSearchParams({ acao: 'limite' })} className="focus-ring mt-3 w-full rounded-2xl border border-white/[0.1] px-4 py-3 text-sm font-semibold text-zinc-300 hover:bg-white/[0.05]">Solicitar crédito</button></aside>
          </div>}
          {!isLoading && <section className="surface mt-6 p-6"><h2 className="text-xl font-bold text-white">Faturas e compras</h2><div className="mt-5 space-y-4">{sortedInvoices.length ? sortedInvoices.map((invoice) => <article key={invoice.id} className="rounded-2xl border border-white/[0.07] p-4"><div className="flex justify-between"><div><p className="font-semibold text-zinc-200">Fatura #{invoice.id}</p><p className="mt-1 text-xs text-zinc-500">{new Date(invoice.createdAt).toLocaleDateString('pt-BR')}</p></div><div className="text-right"><p className="font-bold text-brand">{formatCurrency(Number(invoice.totalAmount))}</p><p className="mt-1 text-xs text-zinc-500">{invoice.status}</p></div></div><div className="mt-4 divide-y divide-white/[0.06]">{invoice.purchases?.map((purchase) => <div key={purchase.id} className="flex justify-between py-2 text-sm"><span className="text-zinc-400">{purchase.description}</span><span className="font-semibold text-zinc-200">{formatCurrency(Number(purchase.amount))}</span></div>)}</div></article>) : <p className="text-sm text-zinc-600">Ainda não há faturas ou compras.</p>}</div></section>}
          {action && !isLoading && <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-5 backdrop-blur-sm" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget && !isSubmitting) closeAction() }}><section role="dialog" aria-modal="true" aria-labelledby="action-modal-title" className="surface w-full max-w-lg p-6 shadow-2xl"><div className="flex items-start justify-between gap-4"><div><p className="text-sm font-semibold text-brand">{action === 'limite' ? 'Mais limite' : 'Pagamento de fatura'}</p><h2 id="action-modal-title" className="mt-1 text-xl font-bold text-white">{action === 'limite' ? 'Solicite uma nova análise' : 'Quite sua fatura atual'}</h2></div><button type="button" onClick={closeAction} disabled={isSubmitting} aria-label="Fechar modal" className="focus-ring rounded-xl p-2 text-zinc-500 hover:bg-white/[0.05] hover:text-white disabled:opacity-50"><X size={20} /></button></div>{action === 'limite' ? <form onSubmit={handleLimitRequest} className="mt-6 space-y-3"><input required inputMode="decimal" value={requestedLimit} onChange={(event) => setRequestedLimit(event.target.value)} placeholder="Limite desejado, ex.: 1500,00" className="input" /><button disabled={isSubmitting} className="focus-ring w-full rounded-2xl bg-brand px-5 py-3 text-sm font-bold text-black hover:bg-[#ffe633] disabled:opacity-60">{isSubmitting ? 'Enviando...' : 'Solicitar crédito'}</button></form> : <form onSubmit={handleInvoicePayment} className="mt-6 space-y-3"><p className="text-sm text-zinc-500">Pagamento da fatura pendente de <span className="font-bold text-zinc-200">{formatCurrency(Number(pendingInvoices[0]?.totalAmount ?? 0))}</span>, usando a conta {accounts[0]?.accountNumber ?? 'indisponível'}.</p><input required type="password" value={paymentPassword} onChange={(event) => setPaymentPassword(event.target.value)} placeholder="Senha de transação" className="input" /><button disabled={isSubmitting || pendingInvoices.length === 0} className="focus-ring w-full rounded-2xl bg-brand px-5 py-3 text-sm font-bold text-black hover:bg-[#ffe633] disabled:opacity-60">{isSubmitting ? 'Pagando...' : 'Confirmar pagamento'}</button></form>}{actionError && <p role="alert" className="mt-4 rounded-xl border border-red-500/20 bg-red-500/10 px-3 py-2 text-sm text-red-300">{actionError}</p>}{actionSuccess && <p role="status" className="mt-4 rounded-xl border border-brand/20 bg-brand/10 px-3 py-2 text-sm text-brand">{actionSuccess}</p>}</section></div>}
        </div>
      </main>
  )
}