import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createPermission,
  deletePermission,
  listPermissions,
  updatePermission,
  updatePermissionStatus,
  type ListPermissionsQuery,
  type PermissionCreateRequest,
  type PermissionUpdateRequest
} from './permissionApi'

export const usePermissionListQuery = (query: ListPermissionsQuery | undefined) =>
  useQuery({
    queryKey: ['permissions', query],
    queryFn: () => listPermissions(query)
  })

export const useCreatePermissionMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: PermissionCreateRequest) => createPermission(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['permissions'] })
    }
  })
}

export const useUpdatePermissionMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: PermissionUpdateRequest }) => updatePermission(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['permissions'] })
    }
  })
}

export const useDeletePermissionMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deletePermission(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['permissions'] })
    }
  })
}

export const useUpdatePermissionStatusMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: number }) => updatePermissionStatus(id, status),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['permissions'] })
    }
  })
}
