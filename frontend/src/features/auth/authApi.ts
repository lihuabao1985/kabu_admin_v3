import type { components } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export interface AuthLoginForm {
  username: string
  password: string
}

export type AuthLoginResponse = components['schemas']['AuthLoginResponse']

export const login = (form: AuthLoginForm): Promise<AuthLoginResponse> =>
  request<AuthLoginResponse>('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams({
      username: form.username,
      password: form.password
    }).toString()
  })

export const logout = (): Promise<void> =>
  request<void>('/api/auth/logout', {
    method: 'POST'
  })
