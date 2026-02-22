import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Modal, Row, Select, Space, Typography, message } from 'antd'
import { StockPriceHistoryFormModal, type StockPriceHistoryFormValues } from './StockPriceHistoryFormModal'
import { StockPriceHistoryTable } from './StockPriceHistoryTable'
import { ListPagination } from '../../lib/ListPagination'
import {
  useCreateStockPriceHistoryMutation,
  useDeleteStockPriceHistoryMutation,
  useImportStockPriceHistoryMutation,
  useStockPriceHistoryListQuery,
  useUpdateStockPriceHistoryMutation
} from './useStockPriceHistoryQueries'
import type {
  ListStockPriceHistoryByStockCodeParams,
  StockPriceHistoryCreateRequest,
  StockPriceHistoryResponse
} from './stockPriceHistoryApi'

interface SearchFormValues {
  stockCode: string
  dateFrom: string
  dateTo: string
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; item: StockPriceHistoryResponse }

const defaultQuery: ListStockPriceHistoryByStockCodeParams = {
  stockCode: '7203',
  page: 1,
  size: 20,
  sort: 'transDate,desc'
}

const defaultImportJson = `[
  {
    "stockCode": "7203",
    "transDate": "2026-02-21",
    "beforeDayPrice": 3540,
    "openPrice": 3550,
    "highPrice": 3570,
    "lowPrice": 3530,
    "closePrice": 3560,
    "adjustedClosePrice": 3560,
    "beforeDayDiff": 20,
    "beforeDayDiffPercent": 0.56,
    "volume": 18000000,
    "remark": "测试导入"
  }
]`

const toNullableInt = (value: string | undefined, fieldLabel: string): number | undefined => {
  const trimmed = value?.trim()
  if (!trimmed) {
    return undefined
  }
  const parsed = Number(trimmed)
  if (!Number.isInteger(parsed)) {
    throw new Error(`${fieldLabel} 必须为整数`)
  }
  return parsed
}

const toNullableFloat = (value: string | undefined, fieldLabel: string): number | undefined => {
  const trimmed = value?.trim()
  if (!trimmed) {
    return undefined
  }
  const parsed = Number(trimmed)
  if (Number.isNaN(parsed)) {
    throw new Error(`${fieldLabel} 必须为数值`)
  }
  return parsed
}

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

export function StockPriceHistoryManagementPage() {
  const [query, setQuery] = useState<ListStockPriceHistoryByStockCodeParams>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [importOpen, setImportOpen] = useState(false)
  const [importMode, setImportMode] = useState<'OVERWRITE' | 'SKIP_DUPLICATE'>('OVERWRITE')
  const [importText, setImportText] = useState(defaultImportJson)
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: '7203',
      dateFrom: '',
      dateTo: ''
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useStockPriceHistoryListQuery(listQuery)
  const createMutation = useCreateStockPriceHistoryMutation()
  const updateMutation = useUpdateStockPriceHistoryMutation()
  const deleteMutation = useDeleteStockPriceHistoryMutation()
  const importMutation = useImportStockPriceHistoryMutation()

  const onSearch = (values: SearchFormValues) => {
    const stockCode = values.stockCode.trim()
    if (!stockCode) {
      messageApi.error('股票代码不能为空')
      return
    }
    setQuery((prev) => ({
      stockCode,
      dateFrom: toNullableText(values.dateFrom),
      dateTo: toNullableText(values.dateTo),
      page: 1,
      size: prev.size ?? 20,
      sort: prev.sort ?? 'transDate,desc'
    }))
  }

  const onReset = () => {
    reset({ stockCode: '7203', dateFrom: '', dateTo: '' })
    setQuery(defaultQuery)
  }

  const toPayload = (values: StockPriceHistoryFormValues): StockPriceHistoryCreateRequest => ({
    stockCode: values.stockCode.trim(),
    transDate: values.transDate.trim(),
    beforeDayPrice: toNullableInt(values.beforeDayPrice, '前日价'),
    openPrice: toNullableInt(values.openPrice, '开盘价'),
    highPrice: toNullableInt(values.highPrice, '最高价'),
    lowPrice: toNullableInt(values.lowPrice, '最低价'),
    closePrice: toNullableInt(values.closePrice, '收盘价'),
    adjustedClosePrice: toNullableInt(values.adjustedClosePrice, '复权收盘价'),
    beforeDayDiff: toNullableInt(values.beforeDayDiff, '涨跌额'),
    beforeDayDiffPercent: toNullableFloat(values.beforeDayDiffPercent, '涨跌幅'),
    volume: toNullableInt(values.volume, '成交量'),
    remark: toNullableText(values.remark)
  })

  const closeModal = () => setModalState({ open: false })

  const onSubmit = async (values: StockPriceHistoryFormValues) => {
    try {
      const payload = toPayload(values)
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({ id: modalState.item.id, payload })
        messageApi.success(`已更新 ${payload.stockCode} ${payload.transDate}`)
      } else {
        await createMutation.mutateAsync(payload)
        messageApi.success(`已创建 ${payload.stockCode} ${payload.transDate}`)
      }
      closeModal()
    } catch (submitError) {
      messageApi.error(submitError instanceof Error ? submitError.message : '保存失败')
    }
  }

  const onDelete = async (item: StockPriceHistoryResponse) => {
    try {
      await deleteMutation.mutateAsync(item.id)
      messageApi.success(`已删除记录 ${item.id}`)
    } catch (deleteError) {
      messageApi.error(deleteError instanceof Error ? deleteError.message : '删除失败')
    }
  }

  const onImport = async () => {
    try {
      const parsed = JSON.parse(importText) as unknown
      if (!Array.isArray(parsed)) {
        messageApi.error('导入数据必须是 JSON 数组')
        return
      }
      const result = await importMutation.mutateAsync({
        mode: importMode,
        items: parsed as StockPriceHistoryCreateRequest[]
      })
      messageApi.success(
        `导入完成，总数${result.total}，成功${result.success}，新增${result.created}，更新${result.updated}，跳过${result.skipped}，失败${result.failed}`
      )
      if (result.failed > 0 && result.failures.length > 0) {
        const first = result.failures[0]
        messageApi.warning(`首条失败：${first.stockCode} ${first.transDate} - ${first.reason}`)
      }
      setImportOpen(false)
    } catch (importError) {
      messageApi.error(importError instanceof Error ? importError.message : '导入失败')
    }
  }

  const modalInitialValues: Partial<StockPriceHistoryFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          stockCode: modalState.item.stockCode,
          transDate: modalState.item.transDate,
          beforeDayPrice: modalState.item.beforeDayPrice?.toString() ?? '',
          openPrice: modalState.item.openPrice?.toString() ?? '',
          highPrice: modalState.item.highPrice?.toString() ?? '',
          lowPrice: modalState.item.lowPrice?.toString() ?? '',
          closePrice: modalState.item.closePrice?.toString() ?? '',
          adjustedClosePrice: modalState.item.adjustedClosePrice?.toString() ?? '',
          beforeDayDiff: modalState.item.beforeDayDiff?.toString() ?? '',
          beforeDayDiffPercent: modalState.item.beforeDayDiffPercent?.toString() ?? '',
          volume: modalState.item.volume?.toString() ?? '',
          remark: modalState.item.remark ?? ''
        }
      : undefined

  const onPageChange = (page: number) => {
    setQuery((prev) => ({
      ...prev,
      page
    }))
  }

  const onPageSizeChange = (size: number) => {
    setQuery((prev) => ({
      ...prev,
      page: 1,
      size
    }))
  }

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item label="股票代码">
                <Controller
                  name="stockCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="例如 7203" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="开始日期">
                <Controller
                  name="dateFrom"
                  control={control}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="结束日期">
                <Controller
                  name="dateTo"
                  control={control}
                  render={({ field }) => <Input {...field} type="date" />}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">
              查询
            </Button>
            <Button onClick={onReset}>重置</Button>
            <Button type="dashed" onClick={() => setModalState({ open: true, mode: 'create' })}>
              新建行情
            </Button>
            <Button onClick={() => setImportOpen(true)}>批量导入</Button>
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
            message="加载历史行情失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <StockPriceHistoryTable
              items={data?.items ?? []}
              loading={isLoading}
              onEdit={(item) => setModalState({ open: true, mode: 'edit', item })}
              onDelete={onDelete}
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

      <StockPriceHistoryFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? '编辑历史行情' : '新建历史行情'}
        confirmLoading={createMutation.isPending || updateMutation.isPending || deleteMutation.isPending}
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmit}
      />

      <Modal
        title="批量导入历史行情"
        open={importOpen}
        onCancel={() => setImportOpen(false)}
        onOk={onImport}
        okText="执行导入"
        confirmLoading={importMutation.isPending}
        width={900}
      >
        <Space direction="vertical" size={12} style={{ width: '100%' }}>
          <Select
            value={importMode}
            onChange={(value) => setImportMode(value)}
            options={[
              { label: '覆盖更新 (OVERWRITE)', value: 'OVERWRITE' },
              { label: '跳过重复 (SKIP_DUPLICATE)', value: 'SKIP_DUPLICATE' }
            ]}
          />
          <Input.TextArea
            rows={14}
            value={importText}
            onChange={(event) => setImportText(event.target.value)}
            placeholder="请输入 JSON 数组"
          />
        </Space>
      </Modal>
    </Space>
  )
}
