import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Popconfirm, Row, Space, Table, Typography, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { useAddStockFavoriteMutation, useRemoveStockFavoriteMutation, useStockFavoritesQuery } from './useStockQueries'
import type { StockFavoriteResponse } from './stockApi'

interface FavoriteFormValues {
  stockCode: string
}

const toNullableText = (value: string | undefined): string | undefined => {
  const trimmed = value?.trim()
  return trimmed ? trimmed : undefined
}

export function StockFavoriteManagementPage() {
  const [messageApi, contextHolder] = message.useMessage()
  const { control, handleSubmit, reset } = useForm<FavoriteFormValues>({
    defaultValues: {
      stockCode: ''
    }
  })

  const { data, isLoading, isError, error } = useStockFavoritesQuery()
  const addMutation = useAddStockFavoriteMutation()
  const removeMutation = useRemoveStockFavoriteMutation()

  const onAdd = async (values: FavoriteFormValues) => {
    const stockCode = toNullableText(values.stockCode)
    if (!stockCode) {
      messageApi.error('请输入銘柄コード')
      return
    }

    try {
      await addMutation.mutateAsync({ stockCode })
      messageApi.success(`已收藏 ${stockCode}`)
      reset({ stockCode: '' })
    } catch (addError) {
      messageApi.error(addError instanceof Error ? addError.message : '收藏失败')
    }
  }

  const onRemove = async (item: StockFavoriteResponse) => {
    try {
      await removeMutation.mutateAsync(item.id)
      messageApi.success(`已取消收藏 ${item.stockCode}`)
    } catch (removeError) {
      messageApi.error(removeError instanceof Error ? removeError.message : '取消收藏失败')
    }
  }

  const columns: ColumnsType<StockFavoriteResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 90
    },
    {
      title: '銘柄コード',
      dataIndex: 'stockCode',
      width: 140
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
      title: '市場',
      dataIndex: 'market',
      width: 120
    },
    {
      title: '株価',
      dataIndex: 'stockPrice',
      width: 120
    },
    {
      title: '收藏时间',
      dataIndex: 'createdAt',
      width: 220
    },
    {
      title: '操作',
      key: 'actions',
      width: 120,
      render: (_value, record) => (
        <Popconfirm
          title="确定取消收藏吗？"
          okText="确定"
          cancelText="取消"
          onConfirm={() => void onRemove(record)}
        >
          <Button danger size="small" loading={removeMutation.isPending}>
            取消收藏
          </Button>
        </Popconfirm>
      )
    }
  ]

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onAdd)}>
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item label="銘柄コード">
                <Controller
                  name="stockCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="例如 7203" />}
                />
              </Form.Item>
            </Col>
          </Row>
          <Space>
            <Button type="primary" htmlType="submit" loading={addMutation.isPending}>
              添加收藏
            </Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>
          收藏总数: {data?.total ?? 0}
        </Typography.Text>
        {isError ? (
          <Alert
            type="error"
            showIcon
            message="加载股票收藏失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <Table
            rowKey={(item) => item.id}
            columns={columns}
            dataSource={data?.items ?? []}
            loading={isLoading}
            pagination={false}
            scroll={{ x: 1200 }}
          />
        )}
      </Card>
    </Space>
  )
}
