import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createUser, deleteUser, listUsers, updateUser, type ListUsersQuery, type UserCreateRequest, type UserUpdateRequest } from './userApi'

const userListKey = (query: ListUsersQuery | undefined) => ['users', query] as const

export const useUserListQuery = (query: ListUsersQuery | undefined) =>
  useQuery({
    queryKey: userListKey(query),
    queryFn: () => listUsers(query)
  })

export const useCreateUserMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: UserCreateRequest) => createUser(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['users'] })
    }
  })
}

export const useUpdateUserMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UserUpdateRequest }) => updateUser(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['users'] })
    }
  })
}

export const useDeleteUserMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteUser(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['users'] })
    }
  })
}
