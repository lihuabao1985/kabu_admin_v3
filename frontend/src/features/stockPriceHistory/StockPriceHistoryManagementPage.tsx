import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography } from 'antd'
import { StockPriceHistoryTable } from './StockPriceHistoryTable'
import { ListPagination } from '../../lib/ListPagination'
import { useStockPriceHistoryListQuery } from './useStockPriceHistoryQueries'
import type { IndustryCodeOption, ListStockPriceHistoryParams } from './stockPriceHistoryApi'
import { listIndustryCodeOptions } from './stockPriceHistoryApi'
import { useQuery } from '@tanstack/react-query'

interface SearchFormValues {
  stockCode: string
  typeCode: string
  dateFrom: string
  dateTo: string
}

const defaultQuery: ListStockPriceHistoryParams = {
  page: 1,
  size: 20,
  sort: 'stockCode,asc'
}

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

export function StockPriceHistoryManagementPage() {
  const [query, setQuery] = useState<ListStockPriceHistoryParams>(defaultQuery)

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: '',
      typeCode: '',
      dateFrom: '',
      dateTo: ''
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useStockPriceHistoryListQuery(listQuery)
  const { data: industryOptions = [] } = useQuery<IndustryCodeOption[]>({
    queryKey: ['stock-industry-code-options'],
    queryFn: listIndustryCodeOptions
  })

  const onSearch = (values: SearchFormValues) => {
    setQuery((prev) => ({
      stockCode: toNullableText(values.stockCode),
      typeCode: toNullableText(values.typeCode),
      dateFrom: toNullableText(values.dateFrom),
      dateTo: toNullableText(values.dateTo),
      page: 1,
      size: prev.size ?? 20,
      sort: prev.sort ?? 'stockCode,asc'
    }))
  }

  const onReset = () => {
    reset({ stockCode: '', typeCode: '', dateFrom: '', dateTo: '' })
    setQuery(defaultQuery)
  }

  const onPageChange = (page: number) => {
    setQuery((prev) => ({ ...prev, page }))
  }

  const onPageSizeChange = (size: number) => {
    setQuery((prev) => ({ ...prev, page: 1, size }))
  }

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={6}>
              <Form.Item label="銘柄コード">
                <Controller name="stockCode" control={control} render={({ field }) => <Input {...field} placeholder="例如 7203" />} />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="業種名">
                <Controller
                  name="typeCode"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      allowClear
                      placeholder="请选择業種"
                      options={industryOptions.map((item) => ({ label: item.codeValue, value: item.codeKey }))}
                    />
                  )}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="开始日期">
                <Controller name="dateFrom" control={control} render={({ field }) => <Input {...field} type="date" />} />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="结束日期">
                <Controller name="dateTo" control={control} render={({ field }) => <Input {...field} type="date" />} />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">查询</Button>
            <Button onClick={onReset}>重置</Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>总数：{data?.total ?? 0}</Typography.Text>
        {isError ? (
          <Alert type="error" showIcon message="加载历史行情失败" description={error instanceof Error ? error.message : '未知错误'} />
        ) : (
          <>
            <StockPriceHistoryTable items={data?.items ?? []} loading={isLoading} />
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
