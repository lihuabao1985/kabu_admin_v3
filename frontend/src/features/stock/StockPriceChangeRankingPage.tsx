import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Table, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { ListPagination } from '../../lib/ListPagination'
import {
  useStockPriceChangeRankingQuery
} from './useStockQueries'
import type {
  ListStockPriceChangeRankingQuery,
  PriceChangeType,
  StockPriceChangeRankingResponse
} from './stockApi'

interface SearchFormValues {
  startDate: string
  endDate: string
  changeType: PriceChangeType
  changePercent: string
}

const toDateString = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const defaultEndDate = new Date()
const defaultStartDate = new Date(defaultEndDate)
defaultStartDate.setDate(defaultStartDate.getDate() - 30)

const createDefaultQuery = (): ListStockPriceChangeRankingQuery => ({
  startDate: toDateString(defaultStartDate),
  endDate: toDateString(defaultEndDate),
  changeType: 'RISE',
  page: 1,
  size: 20
})

const defaultQuery = createDefaultQuery()

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

export function StockPriceChangeRankingPage() {
  const [query, setQuery] = useState<ListStockPriceChangeRankingQuery | undefined>(undefined)
  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      startDate: defaultQuery.startDate,
      endDate: defaultQuery.endDate,
      changeType: defaultQuery.changeType,
      changePercent: ''
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useStockPriceChangeRankingQuery(listQuery)

  const onSearch = (values: SearchFormValues) => {
    setQuery((previous) => ({
      startDate: values.startDate,
      endDate: values.endDate,
      changeType: values.changeType,
      changePercent: toNullableText(values.changePercent),
      page: 1,
      size: previous?.size ?? 20
    }))
  }

  const onReset = () => {
    reset({
      startDate: defaultQuery.startDate,
      endDate: defaultQuery.endDate,
      changeType: defaultQuery.changeType,
      changePercent: ''
    })
    setQuery(undefined)
  }

  const onPageChange = (page: number) => {
    setQuery((previous) => {
      if (!previous) {
        return previous
      }
      return {
        ...previous,
        page
      }
    })
  }

  const onPageSizeChange = (size: number) => {
    setQuery((previous) => {
      if (!previous) {
        return previous
      }
      return {
        ...previous,
        page: 1,
        size
      }
    })
  }

  const page = data?.page ?? query?.page ?? 1
  const size = data?.size ?? query?.size ?? 20
  const startIndex = (page - 1) * size

  const columns: ColumnsType<StockPriceChangeRankingResponse> = [
    {
      title: '排名',
      key: 'rank',
      width: 80,
      render: (_value, _record, index) => startIndex + index + 1
    },
    {
      title: '銘柄コード',
      dataIndex: 'stockCode',
      width: 120
    },
    {
      title: '銘柄名',
      dataIndex: 'stockName',
      width: 220
    },
    {
      title: '業種名',
      dataIndex: 'typeName',
      width: 180
    },
    {
      title: '起始日期',
      dataIndex: 'startDate',
      width: 140
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      width: 140
    },
    {
      title: '起始收盘价',
      dataIndex: 'startClosePrice',
      width: 150
    },
    {
      title: '结束收盘价',
      dataIndex: 'endClosePrice',
      width: 150
    },
    {
      title: '涨跌额',
      dataIndex: 'changeAmount',
      width: 120
    },
    {
      title: '涨跌幅(%)',
      dataIndex: 'changePercent',
      width: 130,
      render: (value: number) => {
        const numeric = Number(value)
        const color = numeric > 0 ? '#cf1322' : numeric < 0 ? '#1677ff' : undefined
        return <span style={{ color }}>{numeric.toFixed(2)}</span>
      }
    }
  ]

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={6}>
              <Form.Item label="开始日期">
                <Controller
                  name="startDate"
                  control={control}
                  rules={{ required: true }}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="结束日期">
                <Controller
                  name="endDate"
                  control={control}
                  rules={{ required: true }}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="涨跌类型">
                <Controller
                  name="changeType"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '涨幅', value: 'RISE' },
                        { label: '跌幅', value: 'FALL' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="涨跌幅度(%)">
                <Controller
                  name="changePercent"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="例如 5" />}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">
              检索
            </Button>
            <Button onClick={onReset}>重置</Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>
          总数: {data?.total ?? 0}
        </Typography.Text>
        {!query ? (
          <Alert
            type="info"
            showIcon
            message="请输入检索条件后点击“检索”加载排行榜"
          />
        ) : isError ? (
          <Alert
            type="error"
            showIcon
            message="加载涨跌幅排行榜失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <Table
              rowKey={(item) => `${item.stockCode}-${item.startDate}-${item.endDate}`}
              columns={columns}
              dataSource={data?.items ?? []}
              loading={isLoading}
              pagination={false}
              scroll={{ x: 1200 }}
            />
            <ListPagination
              total={data?.total ?? 0}
              page={page}
              size={size}
              disabled={isLoading}
              onPageChange={onPageChange}
              onSizeChange={onPageSizeChange}
            />
          </>
        )}
      </Card>
    </Space>
  )
}
