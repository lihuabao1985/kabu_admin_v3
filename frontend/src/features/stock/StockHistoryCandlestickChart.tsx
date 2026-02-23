import { Empty, Spin, Typography } from 'antd'
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
}

const chartWidth = 980
const chartHeight = 420
const plotLeft = 72
const plotTop = 24
const plotRight = 24
const plotBottom = 48

const priceFormatter = new Intl.NumberFormat('ja-JP', {
  maximumFractionDigits: 2
})

const toCandleData = (items: StockPriceHistoryResponse[]): CandleData[] =>
  [...items]
    .sort((a, b) => a.transDate.localeCompare(b.transDate))
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
        close
      }
    })
    .filter((item): item is CandleData => item !== null)

export function StockHistoryCandlestickChart({ items, loading }: StockHistoryCandlestickChartProps) {
  if (loading) {
    return <Spin />
  }

  const candles = toCandleData(items)
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
  const plotHeight = chartHeight - plotTop - plotBottom

  const toY = (price: number): number => plotTop + ((maxPrice - price) / safeRange) * plotHeight

  const step = plotWidth / candles.length
  const bodyWidth = Math.max(4, Math.min(14, step * 0.58))

  const latest = candles[candles.length - 1]
  const previous = candles.length > 1 ? candles[candles.length - 2] : null
  const diff = previous ? latest.close - previous.close : 0

  const xLabelIndexes = [0, Math.floor((candles.length - 1) / 2), candles.length - 1]

  return (
    <div>
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

      <svg viewBox={`0 0 ${chartWidth} ${chartHeight}`} width="100%" height={chartHeight} role="img">
        <rect x={0} y={0} width={chartWidth} height={chartHeight} fill="#fafafa" />

        {Array.from({ length: 5 }).map((_, index) => {
          const y = plotTop + (plotHeight / 4) * index
          const tickPrice = maxPrice - (safeRange / 4) * index
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
          y1={plotTop + plotHeight}
          x2={chartWidth - plotRight}
          y2={plotTop + plotHeight}
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
