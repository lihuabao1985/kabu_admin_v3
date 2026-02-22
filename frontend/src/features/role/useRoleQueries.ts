import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createRole,
  deleteRole,
  listRoles,
  updateRole,
  updateRoleStatus,
  type ListRolesQuery,
  type RoleCreateRequest,
  type RoleUpdateRequest
} from './roleApi'

export const useRoleListQuery = (query: ListRolesQuery | undefined) =>
  useQuery({
    queryKey: ['roles', query],
    queryFn: () => listRoles(query)
  })

export const useCreateRoleMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: RoleCreateRequest) => createRole(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['roles'] })
    }
  })
}

export const useUpdateRoleMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: RoleUpdateRequest }) => updateRole(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['roles'] })
    }
  })
}

export const useDeleteRoleMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteRole(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['roles'] })
    }
  })
}

export const useUpdateRoleStatusMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: number }) => updateRoleStatus(id, status),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['roles'] })
    }
  })
}
