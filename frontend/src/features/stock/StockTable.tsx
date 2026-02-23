import { Table, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { FilterValue, SorterResult, TableCurrentDataSource } from 'antd/es/table/interface'
import type { StockResponse } from './stockApi'

export type StockSortField = 'stockCode' | 'typeCode' | 'market' | 'stockPrice'
export type StockSortOrder = 'ascend' | 'descend'

const supportedSortFields: StockSortField[] = ['stockCode', 'typeCode', 'market', 'stockPrice']
const sortableDirections: StockSortOrder[] = ['ascend', 'descend', 'ascend']

interface StockTableProps {
  stocks: StockResponse[]
  loading: boolean
  sortField?: StockSortField
  sortOrder?: StockSortOrder
  onSortChange: (field: StockSortField, order: StockSortOrder) => void
}

const renderText = (value: string | null | undefined): string => {
  const text = value?.trim()
  return text && text.length > 0 ? text : '-'
}

export function StockTable({ stocks, loading, sortField, sortOrder, onSortChange }: StockTableProps) {
  const columns: ColumnsType<StockResponse> = [
    {
      title: '銘柄コード',
      dataIndex: 'stockCode',
      width: 130,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'stockCode' ? sortOrder : null
    },
    {
      title: '銘柄名',
      dataIndex: 'stockName',
      width: 220
    },
    {
      title: '業種コード',
      dataIndex: 'typeCode',
      width: 130,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'typeCode' ? sortOrder : null,
      render: (value) => renderText(value)
    },
    {
      title: '業種名',
      dataIndex: 'typeName',
      width: 180,
      render: (value) => renderText(value)
    },
    {
      title: '市場',
      dataIndex: 'market',
      width: 140,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'market' ? sortOrder : null,
      render: (value) => renderText(value)
    },
    {
      title: '株価',
      dataIndex: 'stockPrice',
      width: 120,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'stockPrice' ? sortOrder : null,
      render: (value) => renderText(value)
    },
    {
      title: '主页',
      dataIndex: 'homepage',
      width: 220,
      render: (value: string | null | undefined) => {
        const homepage = value?.trim()
        if (!homepage) {
          return '-'
        }

        return (
          <Typography.Link href={homepage} target="_blank" rel="noreferrer">
            {homepage}
          </Typography.Link>
        )
      }
    },
    {
      title: '简介',
      dataIndex: 'tekusyoku',
      width: 260,
      ellipsis: true,
      render: (value) => renderText(value)
    }
  ]

  const onChange = (
    _pagination: unknown,
    _filters: Record<string, FilterValue | null>,
    sorter: SorterResult<StockResponse> | SorterResult<StockResponse>[],
    _extra: TableCurrentDataSource<StockResponse>
  ) => {
    const currentSorter = Array.isArray(sorter) ? sorter[0] : sorter
    if (!currentSorter?.field) {
      return
    }

    const field = currentSorter.field as StockSortField
    if (!supportedSortFields.includes(field)) {
      return
    }

    const nextOrder: StockSortOrder =
      currentSorter.order ?? (sortField === field && sortOrder === 'ascend' ? 'descend' : 'ascend')

    onSortChange(field, nextOrder)
  }

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={stocks}
      loading={loading}
      scroll={{ x: 1500 }}
      onChange={onChange}
      pagination={false}
    />
  )
}
