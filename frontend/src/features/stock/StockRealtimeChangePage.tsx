import { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Space, Statistic } from 'antd'
import { useStockRealtimeChangeQuery } from './useStockQueries'

interface SearchFormValues {
  stockCode: string
}

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

const formatNumber = (value: number | null | undefined): string => {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '--'
  }
  return value.toFixed(2)
}

export function StockRealtimeChangePage() {
  const [stockCode, setStockCode] = useState<string>()
  const { control, handleSubmit } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: ''
    }
  })

  const { data, isLoading, isError, error } = useStockRealtimeChangeQuery(stockCode)

  const onSearch = (values: SearchFormValues) => {
    setStockCode(toNullableText(values.stockCode))
  }

  const changeAmount = data?.changeAmount ?? null
  const changePercent = data?.changePercent ?? null

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item label="銘柄コード">
                <Controller
                  name="stockCode"
                  control={control}
                  rules={{ required: true }}
                  render={({ field }) => <Input {...field} placeholder="例如 7203" />}
                />
              </Form.Item>
            </Col>
          </Row>
          <Button type="primary" htmlType="submit" loading={isLoading}>
            查询实时涨跌幅
          </Button>
        </Form>
      </Card>

      {isError ? (
        <Alert
          type="error"
          showIcon
          message="查询实时涨跌幅失败"
          description={error instanceof Error ? error.message : '未知错误'}
        />
      ) : data ? (
        <Card title={`${data.stockCode}${data.stockName ? ` - ${data.stockName}` : ''}`}>
          <Row gutter={[16, 16]}>
            <Col xs={24} md={8}>
              <Statistic title="当前价" value={formatNumber(data.currentPrice)} />
            </Col>
            <Col xs={24} md={8}>
              <Statistic title="参考收盘价" value={formatNumber(data.referenceClosePrice)} />
            </Col>
            <Col xs={24} md={8}>
              <Statistic title="参考日期" value={data.referenceDate ?? '--'} />
            </Col>
            <Col xs={24} md={12}>
              <Statistic
                title="涨跌额"
                value={formatNumber(changeAmount)}
                valueStyle={{
                  color: changeAmount !== null && changeAmount > 0 ? '#cf1322' : changeAmount !== null && changeAmount < 0 ? '#1677ff' : undefined
                }}
              />
            </Col>
            <Col xs={24} md={12}>
              <Statistic
                title="涨跌幅(%)"
                value={formatNumber(changePercent)}
                valueStyle={{
                  color:
                    changePercent !== null && changePercent > 0
                      ? '#cf1322'
                      : changePercent !== null && changePercent < 0
                        ? '#1677ff'
                        : undefined
                }}
              />
            </Col>
          </Row>
        </Card>
      ) : null}
    </Space>
  )
}
