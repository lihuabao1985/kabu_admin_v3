import type { components, operations } from '../../types/openapi'

type ErrorResponse = components['schemas']['ErrorResponse']
export type UserCreateRequest = components['schemas']['UserCreateRequest']
export type UserUpdateRequest = components['schemas']['UserUpdateRequest']
export type UserListResponse = components['schemas']['UserListResponse']
export type UserResponse = components['schemas']['UserResponse']
export type ListUsersQuery = operations['listUsers']['parameters']['query']

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

const toQueryString = (query: ListUsersQuery | undefined): string => {
  if (!query) {
    return ''
  }

  const params = new URLSearchParams()
  if (query.username) {
    params.set('username', query.username)
  }
  if (query.email) {
    params.set('email', query.email)
  }
  if (query.status !== undefined) {
    params.set('status', String(query.status))
  }
  if (query.page !== undefined) {
    params.set('page', String(query.page))
  }
  if (query.size !== undefined) {
    params.set('size', String(query.size))
  }
  const queryString = params.toString()
  return queryString ? `?${queryString}` : ''
}

const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {})
    }
  })

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`
    try {
      const errorBody = (await response.json()) as ErrorResponse
      if (errorBody.message) {
        message = errorBody.message
      }
    } catch {
      // Keep the generic message when body cannot be parsed.
    }
    throw new ApiError(message, response.status)
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

export const listUsers = (query: ListUsersQuery | undefined): Promise<UserListResponse> =>
  request<UserListResponse>(`/api/users${toQueryString(query)}`)

export const createUser = (payload: UserCreateRequest): Promise<UserResponse> =>
  request<UserResponse>('/api/users', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updateUser = (id: number, payload: UserUpdateRequest): Promise<UserResponse> =>
  request<UserResponse>(`/api/users/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const deleteUser = (id: number): Promise<void> =>
  request<void>(`/api/users/${id}`, {
    method: 'DELETE'
  })
