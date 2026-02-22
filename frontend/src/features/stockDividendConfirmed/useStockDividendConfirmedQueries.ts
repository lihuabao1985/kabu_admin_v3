import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  confirmStockDividendConfirmed,
  createStockDividendConfirmed,
  deleteStockDividendConfirmed,
  importStockDividendConfirmed,
  listStockDividendConfirmed,
  listStockDividendConfirmedRightsLastDayStats,
  unconfirmStockDividendConfirmed,
  updateStockDividendConfirmed,
  type ListStockDividendConfirmedQuery,
  type StockDividendConfirmedCreateRequest,
  type StockDividendConfirmedImportRequest,
  type StockDividendConfirmedUpdateRequest
} from './stockDividendConfirmedApi'

const listKey = (query: ListStockDividendConfirmedQuery | undefined) => ['stockDividendConfirmed', query] as const
const rightsLastDayStatsKey = () => ['stockDividendConfirmed', 'rightsLastDayStats'] as const

export const useStockDividendConfirmedListQuery = (query: ListStockDividendConfirmedQuery | undefined) =>
  useQuery({
    queryKey: listKey(query),
    queryFn: () => listStockDividendConfirmed(query)
  })

export const useStockDividendConfirmedRightsLastDayStatsQuery = () =>
  useQuery({
    queryKey: rightsLastDayStatsKey(),
    queryFn: () => listStockDividendConfirmedRightsLastDayStats()
  })

export const useCreateStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockDividendConfirmedCreateRequest) => createStockDividendConfirmed(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}

export const useUpdateStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: StockDividendConfirmedUpdateRequest }) =>
      updateStockDividendConfirmed(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}

export const useDeleteStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteStockDividendConfirmed(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}

export const useConfirmStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => confirmStockDividendConfirmed(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}

export const useUnconfirmStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => unconfirmStockDividendConfirmed(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}

export const useImportStockDividendConfirmedMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockDividendConfirmedImportRequest) => importStockDividendConfirmed(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockDividendConfirmed'] })
    }
  })
}
