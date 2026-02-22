import { Table } from 'antd'
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table'
import type { FilterValue, SorterResult, TableCurrentDataSource } from 'antd/es/table/interface'
import type { StockDividendConfirmedResponse } from './stockDividendConfirmedApi'

export type StockDividendSortField = 'stockCode' | 'typeName' | 'dividendYield' | 'rightsLastDay'
export type StockDividendSortOrder = 'ascend' | 'descend'

const supportedSortFields: StockDividendSortField[] = ['stockCode', 'typeName', 'dividendYield', 'rightsLastDay']
const sortableDirections: StockDividendSortOrder[] = ['ascend', 'descend', 'ascend']

interface StockDividendConfirmedTableProps {
  items: StockDividendConfirmedResponse[]
  loading: boolean
  sortField?: StockDividendSortField
  sortOrder?: StockDividendSortOrder
  onSortChange: (field: StockDividendSortField, order: StockDividendSortOrder) => void
}

export function StockDividendConfirmedTable({
  items,
  loading,
  sortField,
  sortOrder,
  onSortChange
}: StockDividendConfirmedTableProps) {
  const columns: ColumnsType<StockDividendConfirmedResponse> = [
    {
      title: '銘柄コード',
      dataIndex: 'stockCode',
      width: 140,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'stockCode' ? sortOrder : null
    },
    {
      title: '銘柄名',
      dataIndex: 'stockName',
      width: 200,
      render: (value) => value ?? '-'
    },
    {
      title: '業種名',
      dataIndex: 'typeName',
      width: 180,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'typeName' ? sortOrder : null,
      render: (value) => value ?? '-'
    },
    {
      title: '株価',
      dataIndex: 'stockPrice',
      width: 120,
      render: (value) => value ?? '-'
    },
    {
      title: '配当金',
      dataIndex: 'dividendAmount',
      width: 120
    },
    {
      title: '配当利回り(%)',
      dataIndex: 'dividendYield',
      width: 140,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'dividendYield' ? sortOrder : null,
      render: (value) => (value === null || value === undefined ? '-' : value)
    },
    {
      title: '権利付き最終日',
      dataIndex: 'rightsLastDay',
      width: 140,
      sorter: true,
      sortDirections: sortableDirections,
      sortOrder: sortField === 'rightsLastDay' ? sortOrder : null,
      render: (value) => value ?? '-'
    },
    {
      title: '配当落ち日',
      dataIndex: 'exDividendDate',
      width: 140,
      render: (value) => value ?? '-'
    },
    {
      title: '権利確定日(基準日)',
      dataIndex: 'recordDate',
      width: 160
    }
  ]

  const onChange = (
    _pagination: TablePaginationConfig,
    _filters: Record<string, FilterValue | null>,
    sorter: SorterResult<StockDividendConfirmedResponse> | SorterResult<StockDividendConfirmedResponse>[],
    _extra: TableCurrentDataSource<StockDividendConfirmedResponse>
  ) => {
    const currentSorter = Array.isArray(sorter) ? sorter[0] : sorter
    if (!currentSorter?.field) {
      return
    }

    const field = currentSorter.field as StockDividendSortField
    if (!supportedSortFields.includes(field)) {
      return
    }

    const nextOrder: StockDividendSortOrder =
      currentSorter.order ?? (sortField === field && sortOrder === 'ascend' ? 'descend' : 'ascend')

    onSortChange(field, nextOrder)
  }

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={items}
      loading={loading}
      onChange={onChange}
      scroll={{ x: 1500 }}
      pagination={false}
    />
  )
}
