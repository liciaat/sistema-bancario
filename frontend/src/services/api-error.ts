import axios from 'axios'
import type { ApiError } from '../types/api'

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (!axios.isAxiosError<ApiError>(error)) return fallback

  if (!error.response) {
    return 'Não foi possível conectar ao servidor. Confirme se a API está em execução na porta 8080.'
  }

  const response = error.response.data
  if (response.message) return response.message

  const validationMessages = Object.values(response.fieldErrors ?? {})
  return validationMessages.length > 0 ? validationMessages.join(' ') : fallback
}
