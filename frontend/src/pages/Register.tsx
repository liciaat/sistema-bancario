import { ArrowLeft, ArrowRight, LockKeyhole, UserRound } from 'lucide-react'
import { useState } from 'react'
import type { FormEvent, ReactNode } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { BrandLogo } from '../components/BrandLogo'
import { getApiErrorMessage } from '../services/api-error'
import { register } from '../services/auth.service'
import type { RegisterRequest } from '../types/api'
import { formatCpf, formatPhone } from '../utils/format'

const initialForm: RegisterRequest = {
  name: '',
  cpf: '',
  password: '',
  confirmPassword: '',
  transactionPassword: '',
  confirmTransactionPassword: '',
  email: '',
  phoneNumber: '',
}

export function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState<RegisterRequest>(initialForm)
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  function updateField<Key extends keyof RegisterRequest>(field: Key, value: RegisterRequest[Key]) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError(null)

    if (form.password !== form.confirmPassword) {
      setError('A confirmação da senha de acesso não confere.')
      return
    }
    if (form.transactionPassword !== form.confirmTransactionPassword) {
      setError('A confirmação da senha de transação não confere.')
      return
    }

    setIsSubmitting(true)
    try {
      await register({
        ...form,
        cpf: form.cpf.replace(/\D/g, ''),
        phoneNumber: form.phoneNumber.replace(/\D/g, ''),
      })
      navigate('/login', {
        replace: true,
        state: { message: 'Conta criada! Agora entre com seu CPF e senha.' },
      })
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Não foi possível criar sua conta. Revise os dados e tente novamente.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="min-h-screen bg-ink px-5 py-8 sm:px-8 sm:py-10">
      <div className="mx-auto max-w-5xl">
        <header className="flex items-center justify-between">
          <BrandLogo compact />
          <Link to="/login" className="focus-ring inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-semibold text-zinc-400 hover:bg-white/[0.05] hover:text-white"><ArrowLeft size={17} /> Entrar</Link>
        </header>
        <div className="mx-auto mt-10 max-w-2xl overflow-hidden rounded-[32px] border border-white/[0.07] bg-panel shadow-card">
          <section className="p-6 sm:p-8 lg:p-10">
            <p className="text-sm font-semibold text-brand">Abra sua conta</p>
            <h2 className="mt-2 text-3xl font-extrabold tracking-[-0.045em] text-white">Seus dados iniciais</h2>
            <p className="mt-2 text-sm text-zinc-500">Todos os campos são obrigatórios.</p>
            <form onSubmit={handleSubmit} className="mt-7 space-y-5">
              <div className="grid gap-5 sm:grid-cols-2">
                <Field label="Nome completo" className="sm:col-span-2"><span className="relative block"><UserRound className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" size={18} /><input required value={form.name} onChange={(event) => updateField('name', event.target.value)} placeholder="Como no seu documento" className="input pl-11" /></span></Field>
                <Field label="CPF"><input required inputMode="numeric" value={form.cpf} onChange={(event) => updateField('cpf', formatCpf(event.target.value))} placeholder="000.000.000-00" className="input" /></Field>
                <Field label="Celular"><input required inputMode="tel" value={form.phoneNumber} onChange={(event) => updateField('phoneNumber', formatPhone(event.target.value))} placeholder="(00) 00000-0000" className="input" /></Field>
                <Field label="E-mail" className="sm:col-span-2"><input required type="email" autoComplete="email" value={form.email} onChange={(event) => updateField('email', event.target.value)} placeholder="voce@exemplo.com" className="input" /></Field>
              </div>
              <div className="border-t border-white/[0.07] pt-5"><p className="mb-4 text-sm font-semibold text-zinc-200">Defina suas senhas</p><div className="grid gap-5 sm:grid-cols-2">
                <Field label="Senha de acesso"><span className="relative block"><LockKeyhole className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" size={17} /><input required type="password" minLength={6} autoComplete="new-password" value={form.password} onChange={(event) => updateField('password', event.target.value)} placeholder="Mínimo 6 caracteres" className="input pl-11" /></span></Field>
                <Field label="Confirmar senha"><input required type="password" minLength={6} autoComplete="new-password" value={form.confirmPassword} onChange={(event) => updateField('confirmPassword', event.target.value)} placeholder="Repita sua senha" className="input" /></Field>
                <Field label="Senha de transação"><input required type="password" inputMode="numeric" value={form.transactionPassword} onChange={(event) => updateField('transactionPassword', event.target.value)} placeholder="Para movimentações" className="input" /></Field>
                <Field label="Confirmar senha de transação"><input required type="password" inputMode="numeric" value={form.confirmTransactionPassword} onChange={(event) => updateField('confirmTransactionPassword', event.target.value)} placeholder="Repita a senha" className="input" /></Field>
              </div></div>
              {error && <p role="alert" className="rounded-xl border border-red-500/20 bg-red-500/10 px-3 py-2.5 text-sm text-red-300">{error}</p>}
              <button disabled={isSubmitting} type="submit" className="focus-ring flex w-full items-center justify-center gap-2 rounded-2xl bg-brand px-5 py-3.5 text-sm font-bold text-black shadow-brand hover:bg-[#ffe633] disabled:cursor-wait disabled:opacity-70">{isSubmitting ? 'Criando conta...' : 'Criar minha conta'} <ArrowRight size={18} /></button>
            </form>
          </section>
        </div>
      </div>
    </main>
  )
}

function Field({ label, children, className = '' }: { label: string; children: ReactNode; className?: string }) {
  return <label className={`block ${className}`}><span className="mb-2 block text-sm font-medium text-zinc-300">{label}</span>{children}</label>
}
