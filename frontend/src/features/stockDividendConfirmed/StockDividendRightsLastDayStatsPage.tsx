import { Alert, Button, Card, Space, Table } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { useStockDividendConfirmedRightsLastDayStatsQuery } from './useStockDividendConfirmedQueries'
import type { StockDividendConfirmedRightsLastDayStatsResponse } from './stockDividendConfirmedApi'
import { drilldownToStockDividendConfirmed } from './stockDividendDrilldown'

export function StockDividendRightsLastDayStatsPage() {
  const { data, isLoading, isError, error } = useStockDividendConfirmedRightsLastDayStatsQuery()

  const onDrilldown = (rightsLastDay: string) => {
    drilldownToStockDividendConfirmed(rightsLastDay)
  }

  const columns: ColumnsType<StockDividendConfirmedRightsLastDayStatsResponse> = [
    {
      title: '含权最后日',
      dataIndex: 'rightsLastDay',
      width: 220,
      render: (value: string) => (
        <Button type="link" onClick={() => onDrilldown(value)}>
          {value}
        </Button>
      )
    },
    {
      title: '统计数',
      dataIndex: 'totalCount',
      width: 160,
      render: (value: number, record) => (
        <Button type="link" onClick={() => onDrilldown(record.rightsLastDay)}>
          {value}
        </Button>
      )
    }
  ]

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card>
        {isError ? (
          <Alert
            type="error"
            showIcon
            message="加载股票配当统计失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <Table
            rowKey={(item) => item.rightsLastDay}
            columns={columns}
            dataSource={data?.items ?? []}
            loading={isLoading}
            pagination={false}
            scroll={{ x: 480 }}
          />
        )}
      </Card>
    </Space>
  )
}
