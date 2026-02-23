import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  addStockFavorite,
  createStock,
  deleteStock,
  getStockRealtimeChange,
  importStocks,
  listStockFavorites,
  listStockOptions,
  listStocks,
  removeStockFavorite,
  updateStock,
  updateStockDeleteFlag,
  type ListStockOptionsQuery,
  type ListStocksQuery,
  type StockFavoriteCreateRequest,
  type StockCreateRequest,
  type StockImportRequest,
  type StockUpdateRequest
} from './stockApi'

const stockListKey = (query: ListStocksQuery | undefined) => ['stocks', query] as const
const stockOptionsKey = (query: ListStockOptionsQuery | undefined) => ['stocks', 'options', query] as const
const stockRealtimeChangeKey = (stockCode: string | undefined) => ['stocks', 'realtimeChange', stockCode] as const
const stockFavoritesKey = ['stocks', 'favorites'] as const

export const useStockListQuery = (query: ListStocksQuery | undefined) =>
  useQuery({
    queryKey: stockListKey(query),
    queryFn: () => listStocks(query)
  })

export const useStockOptionsQuery = (query: ListStockOptionsQuery | undefined) =>
  useQuery({
    queryKey: stockOptionsKey(query),
    queryFn: () => listStockOptions(query)
  })
export const useStockRealtimeChangeQuery = (stockCode: string | undefined) =>
  useQuery({
    queryKey: stockRealtimeChangeKey(stockCode),
    queryFn: () => getStockRealtimeChange((stockCode ?? '').trim()),
    enabled: !!stockCode?.trim()
  })

export const useStockFavoritesQuery = () =>
  useQuery({
    queryKey: stockFavoritesKey,
    queryFn: listStockFavorites
  })

export const useCreateStockMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockCreateRequest) => createStock(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stocks'] })
    }
  })
}

export const useUpdateStockMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: StockUpdateRequest }) => updateStock(id, payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stocks'] })
    }
  })
}

export const useDeleteStockMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteStock(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stocks'] })
    }
  })
}

export const useUpdateStockDeleteFlagMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, delFlg }: { id: number; delFlg: '0' | '1' }) => updateStockDeleteFlag(id, { delFlg }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stocks'] })
    }
  })
}

export const useImportStocksMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockImportRequest) => importStocks(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['stocks'] })
    }
  })
}

export const useAddStockFavoriteMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: StockFavoriteCreateRequest) => addStockFavorite(payload),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: stockFavoritesKey })
    }
  })
}

export const useRemoveStockFavoriteMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => removeStockFavorite(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: stockFavoritesKey })
    }
  })
}
