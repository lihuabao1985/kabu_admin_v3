import { Button, Space, Table, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { StockResponse } from './stockApi'

interface StockTableProps {
  stocks: StockResponse[]
  loading: boolean
  onShowHistory: (stock: StockResponse) => void
}

const renderText = (value: string | null | undefined): string => {
  const text = value?.trim()
  return text && text.length > 0 ? text : '-'
}

export function StockTable({ stocks, loading, onShowHistory }: StockTableProps) {
  const columns: ColumnsType<StockResponse> = [
    {
      title: '銘柄コード',
      dataIndex: 'stockCode',
      width: 130
    },
    {
      title: '銘柄名',
      dataIndex: 'stockName',
      width: 220
    },
    {
      title: '業務コード',
      dataIndex: 'typeCode',
      width: 130,
      render: (value) => renderText(value)
    },
    {
      title: '業務名',
      dataIndex: 'typeName',
      width: 180,
      render: (value) => renderText(value)
    },
    {
      title: '市場',
      dataIndex: 'market',
      width: 140,
      render: (value) => renderText(value)
    },
    {
      title: '株価',
      dataIndex: 'stockPrice',
      width: 120,
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
    },
    {
      title: '历史股价',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => onShowHistory(record)}>
            K线图
          </Button>
        </Space>
      )
    }
  ]

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={stocks}
      loading={loading}
      scroll={{ x: 1600 }}
      pagination={false}
    />
  )
}
