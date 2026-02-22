import { useEffect, useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography } from 'antd'
import { ListPagination } from '../../lib/ListPagination'
import {
  StockDividendConfirmedTable,
  type StockDividendSortField,
  type StockDividendSortOrder
} from './StockDividendConfirmedTable'
import { useStockDividendConfirmedListQuery } from './useStockDividendConfirmedQueries'
import type { ListStockDividendConfirmedQuery } from './stockDividendConfirmedApi'
import { consumeStockDividendConfirmedDrilldownRightsLastDay } from './stockDividendDrilldown'

interface SearchFormValues {
  stockCode: string
  rightsLastDay: string
  recordDateFrom: string
  recordDateTo: string
  confirmedFlg: '' | '0' | '1'
}

const defaultSortField: StockDividendSortField = 'rightsLastDay'
const defaultSortOrder: StockDividendSortOrder = 'descend'

const defaultQuery: ListStockDividendConfirmedQuery = {
  page: 1,
  size: 20,
  sort: 'rightsLastDay,desc'
}

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

const toSortString = (field: StockDividendSortField, order: StockDividendSortOrder): string =>
  `${field},${order === 'ascend' ? 'asc' : 'desc'}`

export function StockDividendConfirmedManagementPage() {
  const [query, setQuery] = useState<ListStockDividendConfirmedQuery>(defaultQuery)
  const [sortField, setSortField] = useState<StockDividendSortField>(defaultSortField)
  const [sortOrder, setSortOrder] = useState<StockDividendSortOrder>(defaultSortOrder)

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: '',
      rightsLastDay: '',
      recordDateFrom: '',
      recordDateTo: '',
      confirmedFlg: ''
    }
  })

  useEffect(() => {
    const rightsLastDay = consumeStockDividendConfirmedDrilldownRightsLastDay()
    if (!rightsLastDay) {
      return
    }

    reset({
      stockCode: '',
      rightsLastDay,
      recordDateFrom: '',
      recordDateTo: '',
      confirmedFlg: ''
    })

    setQuery((previous) => ({
      page: 1,
      size: previous.size ?? defaultQuery.size,
      sort: previous.sort ?? defaultQuery.sort,
      rightsLastDay
    }))
  }, [reset])

  const onSearch = (values: SearchFormValues) => {
    setQuery((previous) => ({
      stockCode: toNullableText(values.stockCode),
      rightsLastDay: toNullableText(values.rightsLastDay),
      recordDateFrom: toNullableText(values.recordDateFrom),
      recordDateTo: toNullableText(values.recordDateTo),
      confirmedFlg: values.confirmedFlg ? values.confirmedFlg : undefined,
      page: 1,
      size: previous.size ?? 20,
      sort: previous.sort ?? toSortString(sortField, sortOrder)
    }))
  }

  const onReset = () => {
    reset({ stockCode: '', rightsLastDay: '', recordDateFrom: '', recordDateTo: '', confirmedFlg: '' })
    setSortField(defaultSortField)
    setSortOrder(defaultSortOrder)
    setQuery(defaultQuery)
  }

  const onSortChange = (field: StockDividendSortField, order: StockDividendSortOrder) => {
    setSortField(field)
    setSortOrder(order)
    setQuery((previous) => ({
      ...previous,
      page: 1,
      sort: toSortString(field, order)
    }))
  }

  const onPageChange = (page: number) => {
    setQuery((previous) => ({
      ...previous,
      page
    }))
  }

  const onPageSizeChange = (size: number) => {
    setQuery((previous) => ({
      ...previous,
      page: 1,
      size
    }))
  }

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useStockDividendConfirmedListQuery(listQuery)

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
              <Form.Item label="権利付き最終日">
                <Controller
                  name="rightsLastDay"
                  control={control}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="権利確定日(基準日) From">
                <Controller
                  name="recordDateFrom"
                  control={control}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="権利確定日(基準日) To">
                <Controller
                  name="recordDateTo"
                  control={control}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="確定状態">
                <Controller
                  name="confirmedFlg"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '全て', value: '' },
                        { label: '未確定', value: '0' },
                        { label: '確定済み', value: '1' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">
              検索
            </Button>
            <Button onClick={onReset}>リセット</Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>
          合計: {data?.total ?? 0}
        </Typography.Text>
        {isError ? (
          <Alert
            type="error"
            showIcon
            message="株式配当データの取得に失敗しました"
            description={error instanceof Error ? error.message : '不明なエラー'}
          />
        ) : (
          <>
            <StockDividendConfirmedTable
              items={data?.items ?? []}
              loading={isLoading}
              sortField={sortField}
              sortOrder={sortOrder}
              onSortChange={onSortChange}
            />
            <ListPagination
              total={data?.total ?? 0}
              page={data?.page ?? query.page ?? 1}
              size={data?.size ?? query.size ?? 20}
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
