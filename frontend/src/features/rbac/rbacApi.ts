import type { components } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type IdBatchRequest = components['schemas']['IdBatchRequest']
export type UserRoleListResponse = components['schemas']['UserRoleListResponse']
export type RolePermissionListResponse = components['schemas']['RolePermissionListResponse']
export type AuthPermissionResponse = components['schemas']['AuthPermissionResponse']

export const getCurrentPermissions = (): Promise<AuthPermissionResponse> =>
  request<AuthPermissionResponse>('/api/auth/me/permissions')

export const listUserRoles = (userId: number): Promise<UserRoleListResponse> =>
  request<UserRoleListResponse>(`/api/users/${userId}/roles`)

export const replaceUserRoles = (userId: number, payload: IdBatchRequest): Promise<UserRoleListResponse> =>
  request<UserRoleListResponse>(`/api/users/${userId}/roles`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const addUserRoles = (userId: number, payload: IdBatchRequest): Promise<UserRoleListResponse> =>
  request<UserRoleListResponse>(`/api/users/${userId}/roles`, {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const removeUserRoles = (userId: number, payload: IdBatchRequest): Promise<UserRoleListResponse> =>
  request<UserRoleListResponse>(`/api/users/${userId}/roles`, {
    method: 'DELETE',
    body: JSON.stringify(payload)
  })

export const listRolePermissions = (roleId: number): Promise<RolePermissionListResponse> =>
  request<RolePermissionListResponse>(`/api/roles/${roleId}/permissions`)

export const replaceRolePermissions = (roleId: number, payload: IdBatchRequest): Promise<RolePermissionListResponse> =>
  request<RolePermissionListResponse>(`/api/roles/${roleId}/permissions`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const addRolePermissions = (roleId: number, payload: IdBatchRequest): Promise<RolePermissionListResponse> =>
  request<RolePermissionListResponse>(`/api/roles/${roleId}/permissions`, {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const removeRolePermissions = (roleId: number, payload: IdBatchRequest): Promise<RolePermissionListResponse> =>
  request<RolePermissionListResponse>(`/api/roles/${roleId}/permissions`, {
    method: 'DELETE',
    body: JSON.stringify(payload)
  })
