import type { components } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type StockPriceHistoryResponse =
  components['schemas']['StockPriceHistoryResponse'] & { stockName?: string | null; typeName?: string | null }
export type StockPriceHistoryListResponse = components['schemas']['StockPriceHistoryListResponse']

export interface ListStockPriceHistoryParams {
  stockCode?: string
  typeName?: string
  dateFrom?: string
  dateTo?: string
  page?: number
  size?: number
  sort?: string
}

const toQueryString = (query: ListStockPriceHistoryParams | undefined): string => {
  if (!query) return ''
  const params = new URLSearchParams()
  if (query.stockCode) params.set('stockCode', query.stockCode)
  if (query.typeName) params.set('typeName', query.typeName)
  if (query.dateFrom) params.set('dateFrom', query.dateFrom)
  if (query.dateTo) params.set('dateTo', query.dateTo)
  if (query.page !== undefined) params.set('page', String(query.page))
  if (query.size !== undefined) params.set('size', String(query.size))
  if (query.sort) params.set('sort', query.sort)
  const queryString = params.toString()
  return queryString ? `?${queryString}` : ''
}

export const listStockPriceHistory = (params: ListStockPriceHistoryParams): Promise<StockPriceHistoryListResponse> =>
  request<StockPriceHistoryListResponse>(`/api/stock-price-history${toQueryString(params)}`)

export interface ListStockPriceHistoryByStockCodeParams extends ListStockPriceHistoryParams {
  stockCode: string
}

export const listStockPriceHistoryByStockCode = (
  params: ListStockPriceHistoryByStockCodeParams
): Promise<StockPriceHistoryListResponse> => listStockPriceHistory(params)
