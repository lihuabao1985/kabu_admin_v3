import { Controller, useForm } from 'react-hook-form'
import { useEffect } from 'react'
import { Col, Form, Input, Modal, Row, Select } from 'antd'

export interface StockDividendConfirmedFormValues {
  stockCode: string
  dividendAmount: string
  dividendYield?: string
  rightsLastDay?: string
  exDividendDate?: string
  recordDate: string
  confirmedFlg: '0' | '1'
}

interface StockDividendConfirmedFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<StockDividendConfirmedFormValues>
  onCancel: () => void
  onSubmit: (values: StockDividendConfirmedFormValues) => Promise<void> | void
}

export function StockDividendConfirmedFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: StockDividendConfirmedFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<StockDividendConfirmedFormValues>({
    defaultValues: {
      stockCode: '',
      dividendAmount: '',
      dividendYield: '',
      rightsLastDay: '',
      exDividendDate: '',
      recordDate: '',
      confirmedFlg: '0'
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        stockCode: initialValues?.stockCode ?? '',
        dividendAmount: initialValues?.dividendAmount ?? '',
        dividendYield: initialValues?.dividendYield ?? '',
        rightsLastDay: initialValues?.rightsLastDay ?? '',
        exDividendDate: initialValues?.exDividendDate ?? '',
        recordDate: initialValues?.recordDate ?? '',
        confirmedFlg: initialValues?.confirmedFlg ?? '0'
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
                render={({ field }) => <Input {...field} maxLength={10} placeholder="例如 7203" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item
              label="每股配当(円)"
              required
              validateStatus={formState.errors.dividendAmount ? 'error' : ''}
              help={formState.errors.dividendAmount?.message}
            >
              <Controller
                name="dividendAmount"
                control={control}
                rules={{ required: '配当金额不能为空' }}
                render={({ field }) => <Input {...field} placeholder="例如 90.00" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="配当利回り(%)">
              <Controller
                name="dividendYield"
                control={control}
                render={({ field }) => <Input {...field} placeholder="例如 2.56" />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="含权最后日">
              <Controller
                name="rightsLastDay"
                control={control}
                render={({ field }) => <Input {...field} type="date" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="除权日">
              <Controller
                name="exDividendDate"
                control={control}
                render={({ field }) => <Input {...field} type="date" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item
              label="基准日"
              required
              validateStatus={formState.errors.recordDate ? 'error' : ''}
              help={formState.errors.recordDate?.message}
            >
              <Controller
                name="recordDate"
                control={control}
                rules={{ required: '基准日不能为空' }}
                render={({ field }) => <Input {...field} type="date" />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="确权状态">
              <Controller
                name="confirmedFlg"
                control={control}
                render={({ field }) => (
                  <Select
                    {...field}
                    options={[
                      { label: '未确权', value: '0' },
                      { label: '已确权', value: '1' }
                    ]}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
