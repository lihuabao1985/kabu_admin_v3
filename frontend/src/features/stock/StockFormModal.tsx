import { Controller, useForm } from 'react-hook-form'
import { Form, Input, Modal, Row, Col, Select } from 'antd'
import { useEffect } from 'react'

export interface StockFormValues {
  stockCode: string
  stockName: string
  typeCode?: string
  typeName?: string
  market?: string
  stockPrice?: string
  per?: string
  pbr?: string
  roe?: string
  roa?: string
  preferentialFlg: '0' | '1'
  dividendFlg: '0' | '1'
  delFlg: '0' | '1'
  homepage?: string
  newsUrl?: string
  irUrl?: string
  tekusyoku?: string
  supplier?: string
  salesDestination?: string
}

interface StockFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<StockFormValues>
  onCancel: () => void
  onSubmit: (values: StockFormValues) => Promise<void> | void
}

const flagOptions = [
  { label: '否', value: '0' },
  { label: '是', value: '1' }
]

export function StockFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: StockFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<StockFormValues>({
    defaultValues: {
      stockCode: '',
      stockName: '',
      typeCode: '',
      typeName: '',
      market: '',
      stockPrice: '',
      per: '',
      pbr: '',
      roe: '',
      roa: '',
      preferentialFlg: '0',
      dividendFlg: '0',
      delFlg: '0',
      homepage: '',
      newsUrl: '',
      irUrl: '',
      tekusyoku: '',
      supplier: '',
      salesDestination: ''
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        stockCode: initialValues?.stockCode ?? '',
        stockName: initialValues?.stockName ?? '',
        typeCode: initialValues?.typeCode ?? '',
        typeName: initialValues?.typeName ?? '',
        market: initialValues?.market ?? '',
        stockPrice: initialValues?.stockPrice ?? '',
        per: initialValues?.per ?? '',
        pbr: initialValues?.pbr ?? '',
        roe: initialValues?.roe ?? '',
        roa: initialValues?.roa ?? '',
        preferentialFlg: initialValues?.preferentialFlg ?? '0',
        dividendFlg: initialValues?.dividendFlg ?? '0',
        delFlg: initialValues?.delFlg ?? '0',
        homepage: initialValues?.homepage ?? '',
        newsUrl: initialValues?.newsUrl ?? '',
        irUrl: initialValues?.irUrl ?? '',
        tekusyoku: initialValues?.tekusyoku ?? '',
        supplier: initialValues?.supplier ?? '',
        salesDestination: initialValues?.salesDestination ?? ''
      })
    }
  }, [initialValues, open, reset])

  return (
    <Modal
      title={title}
      open={open}
      onCancel={onCancel}
      confirmLoading={confirmLoading}
      width={900}
      onOk={handleSubmit(async (values) => {
        await onSubmit(values)
      })}
      okText="保存"
      destroyOnClose
    >
      <Form layout="vertical">
        <Row gutter={16}>
          <Col xs={24} md={12}>
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
                render={({ field }) => <Input {...field} maxLength={100} placeholder="例如: 7203" />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={12}>
            <Form.Item
              label="股票名称"
              required
              validateStatus={formState.errors.stockName ? 'error' : ''}
              help={formState.errors.stockName?.message}
            >
              <Controller
                name="stockName"
                control={control}
                rules={{ required: '股票名称不能为空' }}
                render={({ field }) => <Input {...field} maxLength={100} />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="行业代码">
              <Controller name="typeCode" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="行业名称">
              <Controller name="typeName" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="市场">
              <Controller name="market" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={6}>
            <Form.Item label="股价">
              <Controller name="stockPrice" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={6}>
            <Form.Item label="PER">
              <Controller name="per" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={6}>
            <Form.Item label="PBR">
              <Controller name="pbr" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={6}>
            <Form.Item label="ROE/ROA">
              <Input.Group compact>
                <Controller
                  name="roe"
                  control={control}
                  render={({ field }) => <Input {...field} style={{ width: '50%' }} maxLength={100} placeholder="ROE" />}
                />
                <Controller
                  name="roa"
                  control={control}
                  render={({ field }) => <Input {...field} style={{ width: '50%' }} maxLength={100} placeholder="ROA" />}
                />
              </Input.Group>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="优待标记">
              <Controller
                name="preferentialFlg"
                control={control}
                render={({ field }) => <Select {...field} options={flagOptions} />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="配当标记">
              <Controller
                name="dividendFlg"
                control={control}
                render={({ field }) => <Select {...field} options={flagOptions} />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="删除标记">
              <Controller
                name="delFlg"
                control={control}
                render={({ field }) => <Select {...field} options={flagOptions} />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="官网链接">
              <Controller name="homepage" control={control} render={({ field }) => <Input {...field} placeholder="https://..." />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="新闻链接">
              <Controller name="newsUrl" control={control} render={({ field }) => <Input {...field} placeholder="https://..." />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="IR 链接">
              <Controller name="irUrl" control={control} render={({ field }) => <Input {...field} placeholder="https://..." />} />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Form.Item label="供应商">
              <Controller name="supplier" control={control} render={({ field }) => <Input {...field} maxLength={100} />} />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="销售去向">
              <Controller
                name="salesDestination"
                control={control}
                render={({ field }) => <Input {...field} maxLength={100} />}
              />
            </Form.Item>
          </Col>
          <Col xs={24} md={8}>
            <Form.Item label="特色">
              <Controller name="tekusyoku" control={control} render={({ field }) => <Input {...field} />} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
