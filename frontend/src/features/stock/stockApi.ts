import type { components, operations } from '../../types/openapi'
import { request } from '../../lib/apiClient'

export type StockCreateRequest = components['schemas']['StockCreateRequest']
export type StockUpdateRequest = components['schemas']['StockUpdateRequest']
export type StockDeleteFlagPatchRequest = components['schemas']['StockDeleteFlagPatchRequest']
export type StockImportRequest = components['schemas']['StockImportRequest']
export type StockImportResponse = components['schemas']['StockImportResponse']
export type StockResponse = components['schemas']['StockResponse']
export type StockListResponse = components['schemas']['StockListResponse']
export type StockOptionResponse = components['schemas']['StockOptionResponse']
export type ListStocksQuery = operations['listStocks']['parameters']['query'] & {
  typeName?: string
  stockPriceFrom?: string
  stockPriceTo?: string
  freeWord?: string
}
export type ListStockOptionsQuery = operations['listStockOptions']['parameters']['query']
export type PriceChangeType = 'RISE' | 'FALL'

export interface ListStockPriceChangeRankingQuery {
  startDate: string
  endDate: string
  changeType: PriceChangeType
  changePercent?: string
  page?: number
  size?: number
}

export interface StockPriceChangeRankingResponse {
  stockCode: string
  stockName?: string | null
  startDate: string
  endDate: string
  startClosePrice: number
  endClosePrice: number
  changeAmount: number
  changePercent: number
}

export interface StockPriceChangeRankingListResponse {
  items: StockPriceChangeRankingResponse[]
  total: number
  page: number
  size: number
}

export interface StockRealtimeChangeResponse {
  stockCode: string
  stockName?: string | null
  currentPrice?: number | null
  referenceDate?: string | null
  referenceClosePrice?: number | null
  changeAmount?: number | null
  changePercent?: number | null
}

export interface StockFavoriteCreateRequest {
  stockCode: string
}

export interface StockFavoriteResponse {
  id: number
  stockCode: string
  stockName?: string | null
  typeName?: string | null
  market?: string | null
  stockPrice?: string | null
  createdAt: string
}

export interface StockFavoriteListResponse {
  items: StockFavoriteResponse[]
  total: number
}

const toStockQueryString = (query: ListStocksQuery | undefined): string => {
  if (!query) {
    return ''
  }

  const params = new URLSearchParams()
  if (query.stockCode) {
    params.set('stockCode', query.stockCode)
  }
  if (query.stockName) {
    params.set('stockName', query.stockName)
  }
  if (query.typeName) {
    params.set('typeName', query.typeName)
  }
  if (query.market) {
    params.set('market', query.market)
  }
  if (query.stockPriceFrom) {
    params.set('stockPriceFrom', query.stockPriceFrom)
  }
  if (query.stockPriceTo) {
    params.set('stockPriceTo', query.stockPriceTo)
  }
  if (query.freeWord) {
    params.set('freeWord', query.freeWord)
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

const toOptionQueryString = (query: ListStockOptionsQuery | undefined): string => {
  if (!query) {
    return ''
  }

  const params = new URLSearchParams()
  if (query.keyword) {
    params.set('keyword', query.keyword)
  }
  if (query.market) {
    params.set('market', query.market)
  }
  if (query.limit !== undefined) {
    params.set('limit', String(query.limit))
  }

  const queryString = params.toString()
  return queryString ? `?${queryString}` : ''
}

const toPriceChangeRankingQueryString = (query: ListStockPriceChangeRankingQuery): string => {
  const params = new URLSearchParams()
  params.set('startDate', query.startDate)
  params.set('endDate', query.endDate)
  params.set('changeType', query.changeType)
  if (query.changePercent) {
    params.set('changePercent', query.changePercent)
  }
  if (query.page !== undefined) {
    params.set('page', String(query.page))
  }
  if (query.size !== undefined) {
    params.set('size', String(query.size))
  }
  const queryString = params.toString()
  return queryString ? `?${queryString}` : ''
}

export const listStocks = (query: ListStocksQuery | undefined): Promise<StockListResponse> =>
  request<StockListResponse>(`/api/stocks${toStockQueryString(query)}`)

export const getStockById = (id: number): Promise<StockResponse> =>
  request<StockResponse>(`/api/stocks/${id}`)

export const createStock = (payload: StockCreateRequest): Promise<StockResponse> =>
  request<StockResponse>('/api/stocks', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const updateStock = (id: number, payload: StockUpdateRequest): Promise<StockResponse> =>
  request<StockResponse>(`/api/stocks/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })

export const updateStockDeleteFlag = (
  id: number,
  payload: StockDeleteFlagPatchRequest
): Promise<StockResponse> =>
  request<StockResponse>(`/api/stocks/${id}/delete-flag`, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  })

export const deleteStock = (id: number): Promise<void> =>
  request<void>(`/api/stocks/${id}`, {
    method: 'DELETE'
  })

export const listStockOptions = (
  query: ListStockOptionsQuery | undefined
): Promise<StockOptionResponse[]> => request<StockOptionResponse[]>(`/api/stocks/options${toOptionQueryString(query)}`)

export const importStocks = (payload: StockImportRequest): Promise<StockImportResponse> =>
  request<StockImportResponse>('/api/stocks:import', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const listStockPriceChangeRanking = (
  query: ListStockPriceChangeRankingQuery
): Promise<StockPriceChangeRankingListResponse> =>
  request<StockPriceChangeRankingListResponse>(`/api/stocks/price-change-ranking${toPriceChangeRankingQueryString(query)}`)

export const getStockRealtimeChange = (stockCode: string): Promise<StockRealtimeChangeResponse> =>
  request<StockRealtimeChangeResponse>(`/api/stocks/realtime-change?stockCode=${encodeURIComponent(stockCode)}`)

export const listStockFavorites = (): Promise<StockFavoriteListResponse> =>
  request<StockFavoriteListResponse>('/api/stocks/favorites')

export const addStockFavorite = (payload: StockFavoriteCreateRequest): Promise<StockFavoriteResponse> =>
  request<StockFavoriteResponse>('/api/stocks/favorites', {
    method: 'POST',
    body: JSON.stringify(payload)
  })

export const removeStockFavorite = (id: number): Promise<void> =>
  request<void>(`/api/stocks/favorites/${id}`, {
    method: 'DELETE'
  })
