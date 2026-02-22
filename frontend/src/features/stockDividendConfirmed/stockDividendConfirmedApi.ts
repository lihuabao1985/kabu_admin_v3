import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type StockDividendConfirmedCreateRequest = components['schemas']['StockDividendConfirmedCreateRequest']
export type StockDividendConfirmedUpdateRequest = components['schemas']['StockDividendConfirmedUpdateRequest']
export type StockDividendConfirmedResponse = components['schemas']['StockDividendConfirmedResponse']
export type StockDividendConfirmedListResponse = components['schemas']['StockDividendConfirmedListResponse']
export type StockDividendConfirmedImportRequest = components['schemas']['StockDividendConfirmedImportRequest']
export type StockDividendConfirmedImportResponse = components['schemas']['StockDividendConfirmedImportResponse']
export type StockDividendConfirmedRightsLastDayStatsResponse =
  components['schemas']['StockDividendConfirmedRightsLastDayStatsResponse']
export type StockDividendConfirmedRightsLastDayStatsListResponse =
  components['schemas']['StockDividendConfirmedRightsLastDayStatsListResponse']
export type ListStockDividendConfirmedQuery = NonNullable<
  operations['listStockDividendConfirmed']['parameters']['query']
>

const toQueryString = (query: ListStockDividendConfirmedQuery | undefined): string => {
  if (!query) {
    return ''
  }
  const params = new URLSearchParams()
  if (query.stockCode) {
    params.set('stockCode', query.stockCode)
  }
  if (query.rightsLastDay) {
    params.set('rightsLastDay', query.rightsLastDay)
  }
  if (query.recordDateFrom) {
    params.set('recordDateFrom', query.recordDateFrom)
  }
  if (query.recordDateTo) {
    params.set('recordDateTo', query.recordDateTo)
  }
  if (query.confirmedFlg) {
    params.set('confirmedFlg', query.confirmedFlg)
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

export const listStockDividendConfirmed = (
  query: ListStockDividendConfirmedQuery | undefined
): Promise<StockDividendConfirmedListResponse> =>
  request<StockDividendConfirmedListResponse>(`/api/stock-dividend-confirmed${toQueryString(query)}`)

export const getStockDividendConfirmedById = (id: number): Promise<StockDividendConfirmedResponse> =>
  request<StockDividendConfirmedResponse>(`/api/stock-dividend-confirmed/${id}`)

export const createStockDividendConfirmed = (
  payload: StockDividendConfirmedCreateRequest
): Promise<StockDividendConfirmedResponse> =>
  request<StockDividendConfirmedResponse>('/api/stock-dividend-confirmed', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updateStockDividendConfirmed = (
  id: number,
  payload: StockDividendConfirmedUpdateRequest
): Promise<StockDividendConfirmedResponse> =>
  request<StockDividendConfirmedResponse>(`/api/stock-dividend-confirmed/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const deleteStockDividendConfirmed = (id: number): Promise<void> =>
  request<void>(`/api/stock-dividend-confirmed/${id}`, {
    method: 'DELETE'
  })

export const confirmStockDividendConfirmed = (id: number): Promise<StockDividendConfirmedResponse> =>
  request<StockDividendConfirmedResponse>(`/api/stock-dividend-confirmed/${id}/confirmed`, {
    method: 'PATCH'
  })

export const unconfirmStockDividendConfirmed = (id: number): Promise<StockDividendConfirmedResponse> =>
  request<StockDividendConfirmedResponse>(`/api/stock-dividend-confirmed/${id}/unconfirmed`, {
    method: 'PATCH'
  })

export const importStockDividendConfirmed = (
  payload: StockDividendConfirmedImportRequest
): Promise<StockDividendConfirmedImportResponse> =>
  request<StockDividendConfirmedImportResponse>('/api/stock-dividend-confirmed:import', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const listStockDividendConfirmedRightsLastDayStats = (): Promise<StockDividendConfirmedRightsLastDayStatsListResponse> =>
  request<StockDividendConfirmedRightsLastDayStatsListResponse>('/api/stock-dividend-confirmed/stats/rights-last-day')
