import { ArrowRight, LockKeyhole, UserRound } from 'lucide-react'
import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { BrandLogo } from '../components/BrandLogo'
import { getApiErrorMessage } from '../services/api-error'
import { getHomePath, login } from '../services/auth.service'
import { formatCpf } from '../utils/format'

export function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const [cpf, setCpf] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      const user = await login({ cpf: cpf.replace(/\D/g, ''), password })
      navigate(getHomePath(user))
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Não foi possível entrar. Confira seus dados e tente novamente.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
      <main className="relative grid min-h-screen overflow-hidden bg-ink lg:grid-cols-[1.05fr_0.95fr]">
        <div className="pointer-events-none absolute left-[10%] top-[25%] h-72 w-72 rounded-full bg-brand/[0.07] blur-[110px]" />

        <section className="relative hidden overflow-hidden border-r border-white/[0.06] p-12 lg:flex lg:items-center lg:justify-center">
          <div className="flex flex-col items-center">
            <BrandLogo large logoOnly />

            <div className="mt-8 text-center max-w-sm">
              <h1 className="text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
                Bem-vindo de volta
              </h1>
              <p className="mt-4 text-base text-zinc-400">
                Acesse sua conta para movimentar seu dinheiro com <span className="text-[#ffde00] font-semibold">tranquilidade</span> e segurança.
              </p>
            </div>
          </div>
        </section>

        <section className="relative flex items-center justify-center p-5 sm:p-8 lg:p-12">
          <div className="w-full max-w-md">
            <div className="mb-10 lg:hidden"><BrandLogo /></div>
            <div className="surface p-6 sm:p-8">
              <h2 className="text-3xl font-extrabold tracking-[-0.045em] text-white">Acesse sua conta</h2>
              <form className="mt-8 space-y-5" onSubmit={handleSubmit}>
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-zinc-300">CPF</span>
                  <span className="relative block">
                  <UserRound className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" size={18} />
                  <input
                      required
                      inputMode="numeric"
                      autoComplete="username"
                      value={cpf}
                      onChange={(event) => setCpf(formatCpf(event.target.value))}
                      placeholder="000.000.000-00"
                      className="focus-ring w-full rounded-2xl border border-white/[0.09] bg-black/20 py-3.5 pl-11 pr-4 text-sm text-white placeholder:text-zinc-600 hover:border-white/15"
                  />
                </span>
                </label>
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-zinc-300">Senha</span>
                  <span className="relative block">
                  <LockKeyhole className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-zinc-500" size={18} />
                  <input
                      required
                      type="password"
                      autoComplete="current-password"
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      placeholder="Sua senha"
                      className="focus-ring w-full rounded-2xl border border-white/[0.09] bg-black/20 py-3.5 pl-11 pr-4 text-sm text-white placeholder:text-zinc-600 hover:border-white/15"
                  />
                </span>
                </label>
                {error && <p role="alert" className="rounded-xl border border-red-500/20 bg-red-500/10 px-3 py-2.5 text-sm text-red-300">{error}</p>}
                <button
                    disabled={isSubmitting}
                    type="submit"
                    className="focus-ring flex w-full items-center justify-center gap-2 rounded-2xl bg-brand px-5 py-3.5 text-sm font-bold text-black shadow-brand hover:bg-[#ffe633] disabled:cursor-wait disabled:opacity-70"
                >
                  {isSubmitting ? 'Entrando...' : 'Entrar'} <ArrowRight size={18} />
                </button>
              </form>
              {'message' in (location.state ?? {}) && typeof location.state.message === 'string' && (
                  <p role="status" className="mt-4 rounded-xl border border-brand/20 bg-brand/10 px-3 py-2.5 text-sm text-brand">
                    {location.state.message}
                  </p>
              )}
              <p className="mt-5 text-center text-sm text-zinc-500">
                Ainda não tem conta?{' '}
                <Link to="/cadastro" className="font-semibold text-brand hover:text-[#ffe633]">Abra sua conta</Link>
              </p>
            </div>
          </div>
        </section>
      </main>
  )
}