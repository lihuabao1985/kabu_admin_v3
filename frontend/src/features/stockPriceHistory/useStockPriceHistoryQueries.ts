import { useQuery } from '@tanstack/react-query'
import { listStockPriceHistory, type ListStockPriceHistoryParams } from './stockPriceHistoryApi'

const historyListKey = (params: ListStockPriceHistoryParams | undefined) => ['stockPriceHistory', params] as const

export const useStockPriceHistoryListQuery = (params: ListStockPriceHistoryParams | undefined) =>
  useQuery({
    queryKey: historyListKey(params),
    queryFn: () => listStockPriceHistory(params as ListStockPriceHistoryParams),
    enabled: !!params
  })
