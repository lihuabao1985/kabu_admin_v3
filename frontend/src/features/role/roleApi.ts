import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type RoleCreateRequest = components['schemas']['RoleCreateRequest']
export type RoleUpdateRequest = components['schemas']['RoleUpdateRequest']
export type RoleResponse = components['schemas']['RoleResponse']
export type RoleListResponse = components['schemas']['RoleListResponse']
export type ListRolesQuery = operations['listRoles']['parameters']['query']

const toQueryString = (query: ListRolesQuery | undefined): string => {
  if (!query) {
    return ''
  }
  const params = new URLSearchParams()
  if (query.roleCode) {
    params.set('roleCode', query.roleCode)
  }
  if (query.roleName) {
    params.set('roleName', query.roleName)
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

export const listRoles = (query: ListRolesQuery | undefined): Promise<RoleListResponse> =>
  request<RoleListResponse>(`/api/roles${toQueryString(query)}`)

export const createRole = (payload: RoleCreateRequest): Promise<RoleResponse> =>
  request<RoleResponse>('/api/roles', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updateRole = (id: number, payload: RoleUpdateRequest): Promise<RoleResponse> =>
  request<RoleResponse>(`/api/roles/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const deleteRole = (id: number): Promise<void> =>
  request<void>(`/api/roles/${id}`, {
    method: 'DELETE'
  })

export const updateRoleStatus = (id: number, status: number): Promise<RoleResponse> =>
  request<RoleResponse>(`/api/roles/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status })
  })
