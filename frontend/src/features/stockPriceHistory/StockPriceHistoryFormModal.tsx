import { Controller, useForm } from 'react-hook-form'
import { useEffect } from 'react'
import { Col, Form, Input, Modal, Row } from 'antd'

export interface StockPriceHistoryFormValues {
  stockCode: string
  transDate: string
  beforeDayPrice?: string
  openPrice?: string
  highPrice?: string
  lowPrice?: string
  closePrice?: string
  adjustedClosePrice?: string
  beforeDayDiff?: string
  beforeDayDiffPercent?: string
  volume?: string
  remark?: string
}

interface StockPriceHistoryFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<StockPriceHistoryFormValues>
  onCancel: () => void
  onSubmit: (values: StockPriceHistoryFormValues) => Promise<void> | void
}

export function StockPriceHistoryFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: StockPriceHistoryFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<StockPriceHistoryFormValues>({
    defaultValues: {
      stockCode: '',
      transDate: '',
      beforeDayPrice: '',
      openPrice: '',
      highPrice: '',
      lowPrice: '',
      closePrice: '',
      adjustedClosePrice: '',
      beforeDayDiff: '',
      beforeDayDiffPercent: '',
      volume: '',
      remark: ''
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        stockCode: initialValues?.stockCode ?? '',
        transDate: initialValues?.transDate ?? '',
        beforeDayPrice: initialValues?.beforeDayPrice ?? '',
        openPrice: initialValues?.openPrice ?? '',
        highPrice: initialValues?.highPrice ?? '',
        lowPrice: initialValues?.lowPrice ?? '',
        closePrice: initialValues?.closePrice ?? '',
        adjustedClosePrice: initialValues?.adjustedClosePrice ?? '',
        beforeDayDiff: initialValues?.beforeDayDiff ?? '',
        beforeDayDiffPercent: initialValues?.beforeDayDiffPercent ?? '',
        volume: initialValues?.volume ?? '',
        remark: initialValues?.remark ?? ''
      })
    }
  }, [initialValues, open, reset])

  return (
    <Modal
      title={title}
      open={open}
      onCancel={onCancel}
      onOk={handleSubmit(async (values) => {
        await onSubmit(values)
      })}
      confirmLoading={confirmLoading}
      okText="保存"
      destroyOnClose
      width={900}
    >
      <Form layout="vertical">
        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item
              label="股票代码"
              required
              validateStatus={formState.errors.stockCode ? 'error' : ''}
              help={formState.errors.stockCode?.message}
            >
              <Controller
                name="stockCode"
                control={control}
                rules={{ required: '股票代码不能为空' }}
                render={({ field }) => <Input {...field} maxLength={100} placeholder="例如 7203" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item
              label="交易日期"
              required
              validateStatus={formState.errors.transDate ? 'error' : ''}
              help={formState.errors.transDate?.message}
            >
              <Controller
                name="transDate"
                control={control}
                rules={{ required: '交易日期不能为空' }}
                render={({ field }) => <Input {...field} type="date" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="成交量">
              <Controller
                name="volume"
                control={control}
                render={({ field }) => <Input {...field} placeholder="非负整数" />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={4}>
            <Form.Item label="前日价">
              <Controller name="beforeDayPrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={4}>
            <Form.Item label="开盘价">
              <Controller name="openPrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={4}>
            <Form.Item label="最高价">
              <Controller name="highPrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={4}>
            <Form.Item label="最低价">
              <Controller name="lowPrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={4}>
            <Form.Item label="收盘价">
              <Controller name="closePrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={4}>
            <Form.Item label="复权收盘">
              <Controller name="adjustedClosePrice" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="涨跌额">
              <Controller name="beforeDayDiff" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="涨跌幅(%)">
              <Controller name="beforeDayDiffPercent" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="备注">
              <Controller name="remark" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
