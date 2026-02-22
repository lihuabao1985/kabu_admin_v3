import { Table } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { StockPriceHistoryResponse } from './stockPriceHistoryApi'

interface StockPriceHistoryTableProps {
  items: StockPriceHistoryResponse[]
  loading: boolean
}

export function StockPriceHistoryTable({ items, loading }: StockPriceHistoryTableProps) {
  const columns: ColumnsType<StockPriceHistoryResponse> = [
    { title: '銘柄コード', dataIndex: 'stockCode', width: 110 },
    { title: '銘柄名', dataIndex: 'stockName', width: 160 },
    { title: '業種名', dataIndex: 'typeName', width: 150 },
    { title: '取引日', dataIndex: 'transDate', width: 120 },
    { title: '前日終値', dataIndex: 'beforeDayPrice', width: 100 },
    { title: '始値', dataIndex: 'openPrice', width: 100 },
    { title: '高値', dataIndex: 'highPrice', width: 100 },
    { title: '安値', dataIndex: 'lowPrice', width: 100 },
    { title: '終値', dataIndex: 'closePrice', width: 100 },
    { title: '前日比', dataIndex: 'beforeDayDiff', width: 100 },
    { title: '前日比率', dataIndex: 'beforeDayDiffPercent', width: 110 },
    { title: '出来高', dataIndex: 'volume', width: 120 }
  ]

  return <Table rowKey="id" columns={columns} dataSource={items} loading={loading} scroll={{ x: 1370 }} pagination={false} />
}
