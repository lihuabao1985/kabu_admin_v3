import { Empty, Segmented, Spin, Typography } from 'antd'
import { useMemo, useState } from 'react'
import type { StockPriceHistoryResponse } from '../stockPriceHistory/stockPriceHistoryApi'

interface StockHistoryCandlestickChartProps {
  items: StockPriceHistoryResponse[]
  loading: boolean
}

interface CandleData {
  date: string
  open: number
  high: number
  low: number
  close: number
  volume: number
  ma5?: number
  ma25?: number
  ma50?: number
}

const chartWidth = 980
const chartHeight = 520
const plotLeft = 72
const plotTop = 24
const plotRight = 24
const plotBottom = 36
const volumeGap = 28
const volumeHeight = 110
const priceTickCount = 7
const volumeTickCount = 4

const periodOptions = [
  { label: '30日', value: 30 },
  { label: '60日', value: 60 },
  { label: '100日', value: 100 },
  { label: '半年', value: 120 },
  { label: '一年', value: 250 },
  { label: '5年', value: 1250 },
  { label: '10年', value: 2500 }
] as const

const priceFormatter = new Intl.NumberFormat('ja-JP', {
  maximumFractionDigits: 2
})

const calculateMA = (values: number[], endIndex: number, period: number): number | undefined => {
  if (endIndex + 1 < period) {
    return undefined
  }

  let sum = 0
  for (let index = endIndex - period + 1; index <= endIndex; index += 1) {
    sum += values[index]
  }
  return sum / period
}

const toCandleData = (items: StockPriceHistoryResponse[]): CandleData[] => {
  const candles = [...items]
    .sort((a, b) => b.transDate.localeCompare(a.transDate))
    .map((item) => {
      const open = item.openPrice ?? item.beforeDayPrice ?? item.closePrice ?? item.adjustedClosePrice
      const close = item.closePrice ?? item.openPrice ?? item.beforeDayPrice ?? item.adjustedClosePrice

      if (open === null || open === undefined || close === null || close === undefined) {
        return null
      }

      const rawHigh = item.highPrice ?? Math.max(open, close)
      const rawLow = item.lowPrice ?? Math.min(open, close)
      const high = Math.max(rawHigh, open, close)
      const low = Math.min(rawLow, open, close)

      return {
        date: item.transDate,
        open,
        high,
        low,
        close,
        volume: Number(item.volume ?? 0)
      }
    })
    .filter((item): item is CandleData => item !== null)

  const closePrices = candles.map((item) => item.close)

  return candles.map((item, index) => ({
    ...item,
    ma5: calculateMA(closePrices, index, 5),
    ma25: calculateMA(closePrices, index, 25),
    ma50: calculateMA(closePrices, index, 50)
  }))
}

export function StockHistoryCandlestickChart({ items, loading }: StockHistoryCandlestickChartProps) {
  const [periodDays, setPeriodDays] = useState<number>(100)
  const allCandles = useMemo(() => toCandleData(items), [items])
  const candles = useMemo(() => allCandles.slice(0, periodDays), [allCandles, periodDays])

  if (loading) {
    return <Spin />
  }

  if (candles.length === 0) {
    return <Empty description="暂无可用于K线图的历史股价数据" />
  }

  const rawMinPrice = Math.min(...candles.map((item) => item.low))
  const rawMaxPrice = Math.max(...candles.map((item) => item.high))
  const priceRange = rawMaxPrice - rawMinPrice
  const padding = priceRange === 0 ? Math.max(rawMaxPrice * 0.02, 1) : priceRange * 0.06
  const minPrice = Math.max(0, rawMinPrice - padding)
  const maxPrice = rawMaxPrice + padding
  const safeRange = maxPrice - minPrice || 1

  const plotWidth = chartWidth - plotLeft - plotRight
  const pricePlotHeight = chartHeight - plotTop - plotBottom - volumeGap - volumeHeight
  const volumeTop = plotTop + pricePlotHeight + volumeGap

  const toY = (price: number): number => plotTop + ((maxPrice - price) / safeRange) * pricePlotHeight

  const step = plotWidth / candles.length
  const bodyWidth = Math.max(4, Math.min(14, step * 0.58))

  const latest = candles[0]
  const previous = candles.length > 1 ? candles[1] : null
  const diff = previous ? latest.close - previous.close : 0

  const xLabelIndexes = [0, Math.floor((candles.length - 1) / 2), candles.length - 1]
  const maxVolume = Math.max(...candles.map((item) => item.volume), 1)
  const maColors = {
    ma5: '#fa8c16',
    ma25: '#722ed1',
    ma50: '#13c2c2'
  }

  const buildMALinePath = (period: 'ma5' | 'ma25' | 'ma50') => {
    const points = candles
      .map((item, index) => {
        const value = item[period]
        if (value === undefined) {
          return null
        }
        const x = plotLeft + step * (index + 0.5)
        const y = toY(value)
        return `${x},${y}`
      })
      .filter((point): point is string => point !== null)

    return points.join(' ')
  }

  return (
    <div>
      <Segmented
        style={{ marginBottom: 12 }}
        options={periodOptions.map((option) => ({ label: option.label, value: option.value }))}
        value={periodDays}
        onChange={(value) => setPeriodDays(Number(value))}
      />
      <Typography.Text strong>
        最新：{latest.date} 终値 {priceFormatter.format(latest.close)}
      </Typography.Text>
      <Typography.Text
        style={{
          marginLeft: 16,
          color: diff >= 0 ? '#cf1322' : '#1d39c4'
        }}
      >
        前日比 {diff >= 0 ? '+' : ''}
        {priceFormatter.format(diff)}
      </Typography.Text>
      <Typography.Text style={{ marginLeft: 16 }}>
        MA5 <span style={{ color: maColors.ma5 }}>●</span> / MA25 <span style={{ color: maColors.ma25 }}>●</span> / MA50{' '}
        <span style={{ color: maColors.ma50 }}>●</span>
      </Typography.Text>

      <svg viewBox={`0 0 ${chartWidth} ${chartHeight}`} width="100%" height={chartHeight} role="img">
        <rect x={0} y={0} width={chartWidth} height={chartHeight} fill="#fafafa" />

        {Array.from({ length: priceTickCount }).map((_, index) => {
          const ratio = index / (priceTickCount - 1)
          const y = plotTop + pricePlotHeight * ratio
          const tickPrice = maxPrice - safeRange * ratio
          return (
            <g key={`grid-${index}`}>
              <line x1={plotLeft} y1={y} x2={chartWidth - plotRight} y2={y} stroke="#f0f0f0" strokeWidth={1} />
              <text x={plotLeft - 8} y={y + 4} textAnchor="end" fontSize={11} fill="#8c8c8c">
                {priceFormatter.format(tickPrice)}
              </text>
            </g>
          )
        })}

        <line
          x1={plotLeft}
          y1={plotTop + pricePlotHeight}
          x2={chartWidth - plotRight}
          y2={plotTop + pricePlotHeight}
          stroke="#d9d9d9"
          strokeWidth={1}
        />

        {candles.map((item, index) => {
          const x = plotLeft + step * (index + 0.5)
          const yOpen = toY(item.open)
          const yClose = toY(item.close)
          const yHigh = toY(item.high)
          const yLow = toY(item.low)
          const rise = item.close >= item.open
          const color = rise ? '#cf1322' : '#1d39c4'
          const bodyY = Math.min(yOpen, yClose)
          const bodyHeight = Math.max(1, Math.abs(yClose - yOpen))

          return (
            <g key={`${item.date}-${index}`}>
              <line x1={x} y1={yHigh} x2={x} y2={yLow} stroke={color} strokeWidth={1.2} />
              <rect
                x={x - bodyWidth / 2}
                y={bodyY}
                width={bodyWidth}
                height={bodyHeight}
                fill={color}
                opacity={0.92}
              />
              <title>
                {item.date} O:{item.open} H:{item.high} L:{item.low} C:{item.close}
              </title>
            </g>
          )
        })}

        {(['ma5', 'ma25', 'ma50'] as const).map((period) => {
          const path = buildMALinePath(period)
          if (!path) {
            return null
          }

          return (
            <polyline
              key={period}
              points={path}
              fill="none"
              stroke={maColors[period]}
              strokeWidth={1.8}
              strokeLinejoin="round"
              strokeLinecap="round"
            />
          )
        })}

        <line x1={plotLeft} y1={volumeTop} x2={chartWidth - plotRight} y2={volumeTop} stroke="#d9d9d9" strokeWidth={1} />
        <line
          x1={plotLeft}
          y1={volumeTop + volumeHeight}
          x2={chartWidth - plotRight}
          y2={volumeTop + volumeHeight}
          stroke="#d9d9d9"
          strokeWidth={1}
        />

        {Array.from({ length: volumeTickCount }).map((_, index) => {
          const ratio = index / (volumeTickCount - 1)
          const y = volumeTop + volumeHeight * ratio
          const tickValue = Math.round(maxVolume * (1 - ratio))
          return (
            <g key={`volume-grid-${index}`}>
              <line x1={plotLeft} y1={y} x2={chartWidth - plotRight} y2={y} stroke="#f0f0f0" strokeWidth={1} />
              <text x={plotLeft - 8} y={y + 4} textAnchor="end" fontSize={11} fill="#8c8c8c">
                {tickValue.toLocaleString('ja-JP')}
              </text>
            </g>
          )
        })}

        {candles.map((item, index) => {
          const x = plotLeft + step * (index + 0.5)
          const rise = item.close >= item.open
          const color = rise ? '#cf1322' : '#1d39c4'
          const barHeight = (item.volume / maxVolume) * volumeHeight
          return (
            <rect
              key={`volume-${item.date}-${index}`}
              x={x - bodyWidth / 2}
              y={volumeTop + volumeHeight - barHeight}
              width={bodyWidth}
              height={Math.max(1, barHeight)}
              fill={color}
              opacity={0.6}
            />
          )
        })}

        {xLabelIndexes.map((labelIndex) => {
          const candle = candles[labelIndex]
          if (!candle) {
            return null
          }

          const x = plotLeft + step * (labelIndex + 0.5)
          return (
            <text
              key={`x-label-${labelIndex}`}
              x={x}
              y={chartHeight - 16}
              textAnchor="middle"
              fontSize={11}
              fill="#8c8c8c"
            >
              {candle.date}
            </text>
          )
        })}
      </svg>
    </div>
  )
}
