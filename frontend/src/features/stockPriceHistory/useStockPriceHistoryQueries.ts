import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createStockPriceHistory,
  deleteStockPriceHistory,
  importStockPriceHistory,
  listStockPriceHistoryByStockCode,
  updateStockPriceHistory,
  type ListStockPriceHistoryByStockCodeParams,
  type StockPriceHistoryCreateRequest,
  type StockPriceHistoryImportRequest,
  type StockPriceHistoryUpdateRequest
} from './stockPriceHistoryApi'

const historyListKey = (params: ListStockPriceHistoryByStockCodeParams | undefined) =>
  ['stockPriceHistory', params] as const

export const useStockPriceHistoryListQuery = (params: ListStockPriceHistoryByStockCodeParams | undefined) =>
  useQuery({
    queryKey: historyListKey(params),
    queryFn: () => listStockPriceHistoryByStockCode(params as ListStockPriceHistoryByStockCodeParams),
    enabled: !!params?.stockCode
  })

export const useCreateStockPriceHistoryMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockPriceHistoryCreateRequest) => createStockPriceHistory(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockPriceHistory'] })
    }
  })
}

export const useUpdateStockPriceHistoryMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: StockPriceHistoryUpdateRequest }) =>
      updateStockPriceHistory(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockPriceHistory'] })
    }
  })
}

export const useDeleteStockPriceHistoryMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteStockPriceHistory(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockPriceHistory'] })
    }
  })
}

export const useImportStockPriceHistoryMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockPriceHistoryImportRequest) => importStockPriceHistory(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stockPriceHistory'] })
    }
  })
}
