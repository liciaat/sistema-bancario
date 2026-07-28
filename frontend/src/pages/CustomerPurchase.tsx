import { ArrowLeft, CheckCircle2, CreditCard } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { BrandLogo } from '../components/BrandLogo'
import { getApiErrorMessage } from '../services/api-error'
import { getCurrentUser } from '../services/auth.service'
import { getCustomerCreditCard, processCreditPurchase } from '../services/customer.service'
import type { CreditCardResponse } from '../types/api'
import { formatCurrency } from '../utils/format'

export function CustomerPurchase() {
  const user = getCurrentUser()!
  const [card, setCard] = useState<CreditCardResponse | null>(null)
  const [cardNumber, setCardNumber] = useState('')
  const [cvv, setCvv] = useState('')
  const [amount, setAmount] = useState('')
  const [description, setDescription] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  useEffect(() => {
    void getCustomerCreditCard(user.id)
        .then(setCard)
        .catch(() => setError('Cartão não disponível.'))
  }, [user.id])

  async function submit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    if (!card) return
    setError(null)

    const value = Number(amount.replace(',', '.'))
    if (!value || value <= 0) return setError('Informe um valor válido.')

    try {
      const cleanCardNumber = cardNumber.replace(/\s+/g, '')

      await processCreditPurchase({
        cardNumber: cleanCardNumber,
        cvv,
        transactionPassword: password,
        amount: value,
        description
      })

      setSuccess(`Compra de ${formatCurrency(value)} aprovada.`)
      setAmount('')
      setDescription('')
      setPassword('')
    } catch (err) {
      setError(getApiErrorMessage(err, 'Não foi possível processar a compra.'))
    }
  }

  return (
      <main className="min-h-screen bg-ink p-6">
        <div className="mx-auto max-w-md">
          <header className="flex items-center justify-between">
            <BrandLogo compact />
            <Link to="/cartao" className="text-sm text-zinc-400">
              <ArrowLeft size={16} /> Cartão
            </Link>
          </header>

          <section className="surface mt-10 p-6">
            <CreditCard className="text-brand" />
            <h1 className="mt-4 text-2xl font-extrabold">Comprar no crédito</h1>
            <p className="mt-2 text-sm text-zinc-500">
              Limite disponível: {formatCurrency(Number(card?.availableLimit ?? 0))}
            </p>

            <form onSubmit={submit} className="mt-6 space-y-4">
              <input
                  required
                  value={cardNumber}
                  onChange={e => setCardNumber(e.target.value)}
                  placeholder="Número do cartão"
                  className="input"
              />
              <input
                  required
                  inputMode="numeric"
                  maxLength={3}
                  value={cvv}
                  onChange={e => setCvv(e.target.value)}
                  placeholder="CVV"
                  className="input"
              />
              <input
                  required
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Descrição da compra"
                  className="input"
              />
              <input
                  required
                  inputMode="decimal"
                  value={amount}
                  onChange={e => setAmount(e.target.value)}
                  placeholder="Valor"
                  className="input"
              />
              <input
                  required
                  type="password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  placeholder="Senha de transação"
                  className="input"
              />
              <button className="w-full rounded-2xl bg-brand py-3 font-bold text-black">
                Confirmar compra
              </button>
            </form>

            {error && <p className="mt-4 text-sm text-red-300">{error}</p>}

            {success && (
                <p className="mt-4 flex gap-2 text-sm text-brand">
                  <CheckCircle2 size={16} />
                  {success}
                </p>
            )}
          </section>
        </div>
      </main>
  )
}
