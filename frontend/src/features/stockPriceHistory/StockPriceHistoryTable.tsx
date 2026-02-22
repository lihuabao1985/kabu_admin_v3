import { Button, Popconfirm, Space, Table } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { StockPriceHistoryResponse } from './stockPriceHistoryApi'

interface StockPriceHistoryTableProps {
  items: StockPriceHistoryResponse[]
  loading: boolean
  onEdit: (item: StockPriceHistoryResponse) => void
  onDelete: (item: StockPriceHistoryResponse) => void
}

export function StockPriceHistoryTable({ items, loading, onEdit, onDelete }: StockPriceHistoryTableProps) {
  const columns: ColumnsType<StockPriceHistoryResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80
    },
    {
      title: '股票代码',
      dataIndex: 'stockCode',
      width: 120
    },
    {
      title: '交易日',
      dataIndex: 'transDate',
      width: 120
    },
    {
      title: '前日价',
      dataIndex: 'beforeDayPrice',
      width: 100
    },
    {
      title: '开/高/低/收',
      width: 220,
      render: (_, record) => `${record.openPrice ?? '-'} / ${record.highPrice ?? '-'} / ${record.lowPrice ?? '-'} / ${
        record.closePrice ?? '-'
      }`
    },
    {
      title: '涨跌额/幅',
      width: 160,
      render: (_, record) => `${record.beforeDayDiff ?? '-'} / ${record.beforeDayDiffPercent ?? '-'}`
    },
    {
      title: '成交量',
      dataIndex: 'volume',
      width: 120
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 180
    },
    {
      title: '操作',
      fixed: 'right',
      width: 160,
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => onEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该历史行情？" onConfirm={() => onDelete(record)}>
            <Button size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={items}
      loading={loading}
      scroll={{ x: 1300 }}
      pagination={false}
    />
  )
}
