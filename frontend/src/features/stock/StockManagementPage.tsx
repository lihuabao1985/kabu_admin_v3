import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Space, Typography } from 'antd'
import { StockTable, type StockSortField, type StockSortOrder } from './StockTable'
import { ListPagination } from '../../lib/ListPagination'
import { useStockListQuery } from './useStockQueries'
import type { ListStocksQuery } from './stockApi'

interface SearchFormValues {
  stockCode: string
  stockName: string
  typeName: string
  market: string
  stockPriceFrom: string
  stockPriceTo: string
  freeWord: string
}

type StockQueryState = NonNullable<ListStocksQuery>

const defaultQuery: StockQueryState = {
  page: 1,
  size: 20,
  sort: 'stockCode,asc'
}

const toNullable = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

const toSortString = (field: StockSortField, order: StockSortOrder): string =>
  `${field},${order === 'ascend' ? 'asc' : 'desc'}`

const parseSort = (sort?: string): { field: StockSortField; order: StockSortOrder } => {
  if (!sort) {
    return { field: 'stockCode', order: 'ascend' }
  }

  const [rawField, rawOrder] = sort.split(',')
  const fieldMap: Record<string, StockSortField> = {
    stockcode: 'stockCode',
    stock_code: 'stockCode',
    typecode: 'typeCode',
    type_code: 'typeCode',
    market: 'market',
    stockprice: 'stockPrice',
    stock_price: 'stockPrice'
  }

  const field = fieldMap[rawField?.trim().toLowerCase() ?? ''] ?? 'stockCode'
  const order = rawOrder?.trim().toLowerCase() === 'asc' ? 'ascend' : 'descend'
  return { field, order }
}

export function StockManagementPage() {
  const [query, setQuery] = useState<StockQueryState>(defaultQuery)

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: '',
      stockName: '',
      typeName: '',
      market: '',
      stockPriceFrom: '',
      stockPriceTo: '',
      freeWord: ''
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useStockListQuery(listQuery)
  const currentSort = parseSort(query.sort)

  const onSearch = (values: SearchFormValues) => {
    setQuery((previous) => ({
      page: 1,
      size: previous.size ?? 20,
      sort: previous.sort ?? defaultQuery.sort,
      stockCode: toNullable(values.stockCode),
      stockName: toNullable(values.stockName),
      typeName: toNullable(values.typeName),
      market: toNullable(values.market),
      stockPriceFrom: toNullable(values.stockPriceFrom),
      stockPriceTo: toNullable(values.stockPriceTo),
      freeWord: toNullable(values.freeWord)
    }))
  }

  const onReset = () => {
    reset({
      stockCode: '',
      stockName: '',
      typeName: '',
      market: '',
      stockPriceFrom: '',
      stockPriceTo: '',
      freeWord: ''
    })
    setQuery(defaultQuery)
  }

  const onSortChange = (field: StockSortField, order: StockSortOrder) => {
    setQuery((previous) => ({
      ...previous,
      page: 1,
      sort: toSortString(field, order)
    }))
  }

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={6}>
              <Form.Item label="銘柄コード">
                <Controller
                  name="stockCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="例: 7203" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="銘柄名">
                <Controller
                  name="stockName"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="銘柄名" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="業種名">
                <Controller
                  name="typeName"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="業種名" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="市場">
                <Controller
                  name="market"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="市場" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="株価範囲(From)">
                <Controller
                  name="stockPriceFrom"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="1000" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="株価範囲(To)">
                <Controller
                  name="stockPriceTo"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="3000" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item label="フリー">
                <Controller
                  name="freeWord"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="会社简介・特色など" />}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">
              検索
            </Button>
            <Button onClick={onReset}>重置</Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>
          总数：{data?.total ?? 0}
        </Typography.Text>
        {isError ? (
          <Alert
            type="error"
            showIcon
            message="加载股票列表失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <StockTable
              stocks={data?.items ?? []}
              loading={isLoading}
              sortField={currentSort.field}
              sortOrder={currentSort.order}
              onSortChange={onSortChange}
            />
            <ListPagination
              total={data?.total ?? 0}
              page={data?.page ?? query.page ?? 1}
              size={data?.size ?? query.size ?? 20}
              disabled={isLoading}
              onPageChange={(page) => setQuery((previous) => ({ ...previous, page }))}
              onSizeChange={(size) => setQuery((previous) => ({ ...previous, page: 1, size }))}
            />
          </>
        )}
      </Card>
    </Space>
  )
}
