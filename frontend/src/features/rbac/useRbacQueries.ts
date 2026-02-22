import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  addRolePermissions,
  addUserRoles,
  getCurrentPermissions,
  listRolePermissions,
  listUserRoles,
  removeRolePermissions,
  removeUserRoles,
  replaceRolePermissions,
  replaceUserRoles,
  type IdBatchRequest
} from './rbacApi'

export const useCurrentPermissionsQuery = () =>
  useQuery({
    queryKey: ['auth', 'permissions'],
    queryFn: () => getCurrentPermissions(),
    retry: false
  })

export const useUserRolesQuery = (userId: number | null) =>
  useQuery({
    queryKey: ['rbac', 'userRoles', userId],
    queryFn: () => listUserRoles(userId ?? 0),
    enabled: userId !== null
  })

export const useReplaceUserRolesMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: IdBatchRequest }) => replaceUserRoles(userId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'userRoles', variables.userId] })
    }
  })
}

export const useAddUserRolesMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: IdBatchRequest }) => addUserRoles(userId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'userRoles', variables.userId] })
    }
  })
}

export const useRemoveUserRolesMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ userId, payload }: { userId: number; payload: IdBatchRequest }) => removeUserRoles(userId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'userRoles', variables.userId] })
    }
  })
}

export const useRolePermissionsQuery = (roleId: number | null) =>
  useQuery({
    queryKey: ['rbac', 'rolePermissions', roleId],
    queryFn: () => listRolePermissions(roleId ?? 0),
    enabled: roleId !== null
  })

export const useReplaceRolePermissionsMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ roleId, payload }: { roleId: number; payload: IdBatchRequest }) =>
      replaceRolePermissions(roleId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'rolePermissions', variables.roleId] })
    }
  })
}

export const useAddRolePermissionsMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ roleId, payload }: { roleId: number; payload: IdBatchRequest }) =>
      addRolePermissions(roleId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'rolePermissions', variables.roleId] })
    }
  })
}

export const useRemoveRolePermissionsMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ roleId, payload }: { roleId: number; payload: IdBatchRequest }) =>
      removeRolePermissions(roleId, payload),
    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({ queryKey: ['rbac', 'rolePermissions', variables.roleId] })
    }
  })
}
