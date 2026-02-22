import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type PermissionCreateRequest = components['schemas']['PermissionCreateRequest']
export type PermissionUpdateRequest = components['schemas']['PermissionUpdateRequest']
export type PermissionResponse = components['schemas']['PermissionResponse']
export type PermissionListResponse = components['schemas']['PermissionListResponse']
export type ListPermissionsQuery = operations['listPermissions']['parameters']['query']

const toQueryString = (query: ListPermissionsQuery | undefined): string => {
  if (!query) {
    return ''
  }
  const params = new URLSearchParams()
  if (query.permissionCode) {
    params.set('permissionCode', query.permissionCode)
  }
  if (query.permissionName) {
    params.set('permissionName', query.permissionName)
  }
  if (query.resourceType) {
    params.set('resourceType', query.resourceType)
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

export const listPermissions = (query: ListPermissionsQuery | undefined): Promise<PermissionListResponse> =>
  request<PermissionListResponse>(`/api/permissions${toQueryString(query)}`)

export const createPermission = (payload: PermissionCreateRequest): Promise<PermissionResponse> =>
  request<PermissionResponse>('/api/permissions', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updatePermission = (id: number, payload: PermissionUpdateRequest): Promise<PermissionResponse> =>
  request<PermissionResponse>(`/api/permissions/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const deletePermission = (id: number): Promise<void> =>
  request<void>(`/api/permissions/${id}`, {
    method: 'DELETE'
  })

export const updatePermissionStatus = (id: number, status: number): Promise<PermissionResponse> =>
  request<PermissionResponse>(`/api/permissions/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status })
  })
