import { useMutation, useQueryClient } from '@tanstack/react-query'
import { login, logout, type AuthLoginForm } from './authApi'

export const useLoginMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (form: AuthLoginForm) => login(form),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['auth', 'permissions'] })
    }
  })
}

export const useLogoutMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => logout(),
    onSettled: async () => {
      await queryClient.invalidateQueries({ queryKey: ['auth', 'permissions'] })
    }
  })
}
