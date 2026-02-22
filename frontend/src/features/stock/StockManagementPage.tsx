import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Modal, Row, Select, Space, Typography, message } from 'antd'
import { StockFormModal, type StockFormValues } from './StockFormModal'
import { StockHistoryCandlestickChart } from './StockHistoryCandlestickChart'
import { StockTable } from './StockTable'
import { ListPagination } from '../../lib/ListPagination'
import {
  useCreateStockMutation,
  useImportStocksMutation,
  useStockListQuery,
  useUpdateStockMutation
} from './useStockQueries'
import type { ListStocksQuery, StockCreateRequest, StockResponse } from './stockApi'
import type { ListStockPriceHistoryByStockCodeParams } from '../stockPriceHistory/stockPriceHistoryApi'
import { useStockPriceHistoryListQuery } from '../stockPriceHistory/useStockPriceHistoryQueries'

interface SearchFormValues {
  stockCode: string
  stockName: string
  typeCode: string
  market: string
  delFlg: '0' | '1' | 'ALL'
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; stock: StockResponse }

type PriceHistoryModalState =
  | { open: false }
  | { open: true; stock: StockResponse }

type StockQueryState = NonNullable<ListStocksQuery>

const defaultQuery: StockQueryState = {
  page: 1,
  size: 20,
  sort: 'id,desc'
}

const defaultImportJson = `[
  {
    "stockCode": "7203",
    "stockName": "トヨタ自動車",
    "market": "東証プライム",
    "stockPrice": "3520"
  },
  {
    "stockCode": "6758",
    "stockName": "ソニーグループ",
    "market": "東証プライム",
    "stockPrice": "13980"
  }
]`

const toNullable = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

export function StockManagementPage() {
  const [query, setQuery] = useState<StockQueryState>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [priceHistoryModalState, setPriceHistoryModalState] = useState<PriceHistoryModalState>({ open: false })
  const [importOpen, setImportOpen] = useState(false)
  const [importMode, setImportMode] = useState<'INCREMENTAL' | 'FULL'>('INCREMENTAL')
  const [importText, setImportText] = useState(defaultImportJson)
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      stockCode: '',
      stockName: '',
      typeCode: '',
      market: '',
      delFlg: '0'
    }
  })

  const listQuery = useMemo(() => query, [query])
  const historyListQuery = useMemo<ListStockPriceHistoryByStockCodeParams | undefined>(() => {
    if (!priceHistoryModalState.open) {
      return undefined
    }

    return {
      stockCode: priceHistoryModalState.stock.stockCode,
      page: 1,
      size: 100,
      sort: 'transDate,desc'
    }
  }, [priceHistoryModalState])

  const { data, isLoading, isError, error } = useStockListQuery(listQuery)
  const {
    data: historyData,
    isLoading: historyLoading,
    isError: historyIsError,
    error: historyError
  } = useStockPriceHistoryListQuery(historyListQuery)
  const createMutation = useCreateStockMutation()
  const updateMutation = useUpdateStockMutation()
  const importMutation = useImportStocksMutation()

  const closeModal = () => setModalState({ open: false })
  const closePriceHistoryModal = () => setPriceHistoryModalState({ open: false })
  const onShowHistory = (stock: StockResponse) => setPriceHistoryModalState({ open: true, stock })

  const onSearch = (values: SearchFormValues) => {
    setQuery((previous) => ({
      page: 1,
      size: previous.size ?? 20,
      sort: previous.sort ?? 'id,desc',
      stockCode: toNullable(values.stockCode),
      stockName: toNullable(values.stockName),
      typeCode: toNullable(values.typeCode),
      market: toNullable(values.market),
      delFlg: values.delFlg
    }))
  }

  const onReset = () => {
    reset({
      stockCode: '',
      stockName: '',
      typeCode: '',
      market: '',
      delFlg: '0'
    })
    setQuery(defaultQuery)
  }

  const toPayload = (values: StockFormValues): StockCreateRequest => ({
    stockCode: values.stockCode.trim(),
    stockName: values.stockName.trim(),
    typeCode: toNullable(values.typeCode),
    typeName: toNullable(values.typeName),
    market: toNullable(values.market),
    stockPrice: toNullable(values.stockPrice),
    per: toNullable(values.per),
    pbr: toNullable(values.pbr),
    roe: toNullable(values.roe),
    roa: toNullable(values.roa),
    preferentialFlg: values.preferentialFlg,
    dividendFlg: values.dividendFlg,
    delFlg: values.delFlg,
    homepage: toNullable(values.homepage),
    newsUrl: toNullable(values.newsUrl),
    irUrl: toNullable(values.irUrl),
    tekusyoku: toNullable(values.tekusyoku),
    supplier: toNullable(values.supplier),
    salesDestination: toNullable(values.salesDestination)
  })

  const onSubmitStock = async (values: StockFormValues) => {
    try {
      const payload = toPayload(values)
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({ id: modalState.stock.id, payload })
        messageApi.success(`已更新股票 ${payload.stockCode}`)
      } else {
        await createMutation.mutateAsync(payload)
        messageApi.success(`已新增股票 ${payload.stockCode}`)
      }
      closeModal()
    } catch (submitError) {
      messageApi.error(submitError instanceof Error ? submitError.message : '保存股票失败')
    }
  }

  const onImport = async () => {
    try {
      const parsed = JSON.parse(importText) as unknown
      if (!Array.isArray(parsed)) {
        messageApi.error('导入内容必须是 JSON 数组')
        return
      }

      const payload: StockCreateRequest[] = parsed as StockCreateRequest[]
      const result = await importMutation.mutateAsync({
        mode: importMode,
        items: payload
      })

      messageApi.success(
        `导入完成，总数 ${result.total}，成功 ${result.success}，新增 ${result.created}，更新 ${result.updated}，失败 ${result.failed}`
      )

      if (result.failed > 0 && result.failures.length > 0) {
        const firstFailure = result.failures[0]
        messageApi.warning(`首条失败：${firstFailure.stockCode} - ${firstFailure.reason}`)
      }

      setImportOpen(false)
    } catch (importError) {
      messageApi.error(importError instanceof Error ? importError.message : '导入失败')
    }
  }

  const modalInitialValues: Partial<StockFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          stockCode: modalState.stock.stockCode,
          stockName: modalState.stock.stockName,
          typeCode: modalState.stock.typeCode ?? '',
          typeName: modalState.stock.typeName ?? '',
          market: modalState.stock.market ?? '',
          stockPrice: modalState.stock.stockPrice ?? '',
          per: modalState.stock.per ?? '',
          pbr: modalState.stock.pbr ?? '',
          roe: modalState.stock.roe ?? '',
          roa: modalState.stock.roa ?? '',
          preferentialFlg: modalState.stock.preferentialFlg === '1' ? '1' : '0',
          dividendFlg: modalState.stock.dividendFlg === '1' ? '1' : '0',
          delFlg: modalState.stock.delFlg === '1' ? '1' : '0',
          homepage: modalState.stock.homepage ?? '',
          newsUrl: modalState.stock.newsUrl ?? '',
          irUrl: modalState.stock.irUrl ?? '',
          tekusyoku: modalState.stock.tekusyoku ?? '',
          supplier: modalState.stock.supplier ?? '',
          salesDestination: modalState.stock.salesDestination ?? ''
        }
      : undefined

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

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
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
                  render={({ field }) => <Input {...field} placeholder="请输入銘柄名" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={4}>
              <Form.Item label="業種コード">
                <Controller
                  name="typeCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="業種コード" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={4}>
              <Form.Item label="市場">
                <Controller
                  name="market"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="市场" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={4}>
              <Form.Item label="删除标记">
                <Controller
                  name="delFlg"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '未删除', value: '0' },
                        { label: '已删除', value: '1' },
                        { label: '全部', value: 'ALL' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit">
              检索
            </Button>
            <Button onClick={onReset}>重置</Button>
            <Button type="dashed" onClick={() => setModalState({ open: true, mode: 'create' })}>
              新增股票
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
            message="加载股票列表失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <StockTable stocks={data?.items ?? []} loading={isLoading} onShowHistory={onShowHistory} />
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

      <StockFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? '编辑股票' : '新增股票'}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmitStock}
      />

      <Modal
        title="批量导入股票"
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
              { label: '增量更新 (INCREMENTAL)', value: 'INCREMENTAL' },
              { label: '全量覆盖 (FULL)', value: 'FULL' }
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

      <Modal
        title={
          priceHistoryModalState.open
            ? `${priceHistoryModalState.stock.stockCode} ${priceHistoryModalState.stock.stockName} 历史股价`
            : '历史股价'
        }
        open={priceHistoryModalState.open}
        onCancel={closePriceHistoryModal}
        footer={null}
        width={1120}
        destroyOnClose
      >
        {historyIsError ? (
          <Alert
            type="error"
            showIcon
            message="加载历史股价失败"
            description={historyError instanceof Error ? historyError.message : '未知错误'}
          />
        ) : (
          <Space direction="vertical" size={12} style={{ width: '100%' }}>
            <Typography.Text type="secondary">最多显示最近 100 个交易日，以 K 线方式展示。</Typography.Text>
            <StockHistoryCandlestickChart items={historyData?.items ?? []} loading={historyLoading} />
          </Space>
        )}
      </Modal>
    </Space>
  )
}
