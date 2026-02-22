import type { components } from '../../types/openapi'
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
export interface ListStockDividendConfirmedQuery {
  stockCode?: string
  industryCode?: string
  rightsLastDay?: string
  page?: number
  size?: number
  sort?: string
}

export interface IndustryCodeOption {
  codeKey: string
  codeValue: string
}

const toQueryString = (query: ListStockDividendConfirmedQuery | undefined): string => {
  if (!query) {
    return ''
  }
  const params = new URLSearchParams()
  if (query.stockCode) {
    params.set('stockCode', query.stockCode)
  }
  if (query.industryCode) {
    params.set('industryCode', query.industryCode)
  }
  if (query.rightsLastDay) {
    params.set('rightsLastDay', query.rightsLastDay)
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


export const listIndustryCodeOptions = (): Promise<IndustryCodeOption[]> =>
  request<IndustryCodeOption[]>('/api/stocks/industry-options')

export const listStockDividendConfirmedRightsLastDayStats = (): Promise<StockDividendConfirmedRightsLastDayStatsListResponse> =>
  request<StockDividendConfirmedRightsLastDayStatsListResponse>('/api/stock-dividend-confirmed/stats/rights-last-day')
