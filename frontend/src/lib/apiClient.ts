import type { components } from '../types/openapi'

type ErrorResponse = components['schemas']['ErrorResponse']

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

export const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
  const headers = new Headers(init?.headers)
  const hasBody = init?.body !== undefined && init?.body !== null
  if (hasBody && !(init?.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    credentials: 'include',
    headers
  })

  if (!response.ok) {
    let message = `请求失败，状态码 ${response.status}`
    try {
      const errorBody = (await response.json()) as ErrorResponse
      if (errorBody.message) {
        message = errorBody.message
      }
    } catch {
      // 响应体解析失败时保留默认错误提示。
    }
    throw new ApiError(message, response.status)
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}
