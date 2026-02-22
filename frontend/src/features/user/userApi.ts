import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type UserCreateRequest = components['schemas']['UserCreateRequest']
export type UserUpdateRequest = components['schemas']['UserUpdateRequest']
export type UserListResponse = components['schemas']['UserListResponse']
export type UserResponse = components['schemas']['UserResponse']
export type ListUsersQuery = operations['listUsers']['parameters']['query']
export type StatusPatchRequest = components['schemas']['StatusPatchRequest']
export type LockPatchRequest = components['schemas']['LockPatchRequest']

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
  if (query.locked !== undefined) {
    params.set('locked', String(query.locked))
  }
  if (query.tenantId) {
    params.set('tenantId', query.tenantId)
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

export const updateUserStatus = (id: number, payload: StatusPatchRequest): Promise<UserResponse> =>
  request<UserResponse>(`/api/users/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  })

export const updateUserLock = (id: number, payload: LockPatchRequest): Promise<UserResponse> =>
  request<UserResponse>(`/api/users/${id}/lock`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  })
