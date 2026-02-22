import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type StockPriceHistoryCreateRequest = components['schemas']['StockPriceHistoryCreateRequest']
export type StockPriceHistoryUpdateRequest = components['schemas']['StockPriceHistoryUpdateRequest']
export type StockPriceHistoryResponse = components['schemas']['StockPriceHistoryResponse']
export type StockPriceHistoryListResponse = components['schemas']['StockPriceHistoryListResponse']
export type StockPriceHistoryImportRequest = components['schemas']['StockPriceHistoryImportRequest']
export type StockPriceHistoryImportResponse = components['schemas']['StockPriceHistoryImportResponse']
export type ListStockPriceHistoryByStockCodeQuery = NonNullable<
  operations['listStockPriceHistoryByStockCode']['parameters']['query']
>

export interface ListStockPriceHistoryByStockCodeParams extends ListStockPriceHistoryByStockCodeQuery {
  stockCode: string
}

const toQueryString = (query: ListStockPriceHistoryByStockCodeQuery | undefined): string => {
  if (!query) {
    return ''
  }
  const params = new URLSearchParams()
  if (query.dateFrom) {
    params.set('dateFrom', query.dateFrom)
  }
  if (query.dateTo) {
    params.set('dateTo', query.dateTo)
  }
  if (query.page !== undefined) {
    params.set('page', String(query.page))
  }
  if (query.size !== undefined) {
    params.set('size', String(query.size))
  }
  if (query.sort) {
    params.set('sort', query.sort)
  }
  const queryString = params.toString()
  return queryString ? `?${queryString}` : ''
}

export const listStockPriceHistoryByStockCode = (
  params: ListStockPriceHistoryByStockCodeParams
): Promise<StockPriceHistoryListResponse> =>
  request<StockPriceHistoryListResponse>(
    `/api/stocks/${encodeURIComponent(params.stockCode)}/price-history${toQueryString({
      dateFrom: params.dateFrom,
      dateTo: params.dateTo,
      page: params.page,
      size: params.size,
      sort: params.sort
    })}`
  )

export const getStockPriceHistoryById = (id: number): Promise<StockPriceHistoryResponse> =>
  request<StockPriceHistoryResponse>(`/api/stock-price-history/${id}`)

export const createStockPriceHistory = (
  payload: StockPriceHistoryCreateRequest
): Promise<StockPriceHistoryResponse> =>
  request<StockPriceHistoryResponse>('/api/stock-price-history', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updateStockPriceHistory = (
  id: number,
  payload: StockPriceHistoryUpdateRequest
): Promise<StockPriceHistoryResponse> =>
  request<StockPriceHistoryResponse>(`/api/stock-price-history/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const deleteStockPriceHistory = (id: number): Promise<void> =>
  request<void>(`/api/stock-price-history/${id}`, {
    method: 'DELETE'
  })

export const importStockPriceHistory = (
  payload: StockPriceHistoryImportRequest
): Promise<StockPriceHistoryImportResponse> =>
  request<StockPriceHistoryImportResponse>('/api/stock-price-history:import', {
    method: 'POST',
    body: JSON.stringify(payload)
  })
