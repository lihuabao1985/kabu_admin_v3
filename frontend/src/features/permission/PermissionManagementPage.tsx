import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography, message } from 'antd'
import { PermissionFormModal, type PermissionFormValues } from './PermissionFormModal'
import { PermissionTable } from './PermissionTable'
import { ListPagination } from '../../lib/ListPagination'
import {
  useCreatePermissionMutation,
  useDeletePermissionMutation,
  usePermissionListQuery,
  useUpdatePermissionMutation,
  useUpdatePermissionStatusMutation
} from './usePermissionQueries'
import type { ListPermissionsQuery, PermissionResponse } from './permissionApi'

interface SearchFormValues {
  permissionCode: string
  permissionName: string
  resourceType: 'all' | 'API' | 'UI' | 'DATA'
  status: 'all' | '1' | '0'
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; permission: PermissionResponse }

type PermissionQueryState = NonNullable<ListPermissionsQuery>

const defaultQuery: PermissionQueryState = {
  page: 1,
  size: 20
}

export function PermissionManagementPage() {
  const [query, setQuery] = useState<PermissionQueryState>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      permissionCode: '',
      permissionName: '',
      resourceType: 'all',
      status: 'all'
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = usePermissionListQuery(listQuery)
  const createMutation = useCreatePermissionMutation()
  const updateMutation = useUpdatePermissionMutation()
  const deleteMutation = useDeletePermissionMutation()
  const statusMutation = useUpdatePermissionStatusMutation()

  const closeModal = () => setModalState({ open: false })

  const onSearch = (values: SearchFormValues) => {
    setQuery((prev) => ({
      page: 1,
      size: prev.size ?? 20,
      permissionCode: values.permissionCode.trim() || undefined,
      permissionName: values.permissionName.trim() || undefined,
      resourceType: values.resourceType === 'all' ? undefined : values.resourceType,
      status: values.status === 'all' ? undefined : Number(values.status)
    }))
  }

  const onReset = () => {
    reset({
      permissionCode: '',
      permissionName: '',
      resourceType: 'all',
      status: 'all'
    })
    setQuery(defaultQuery)
  }

  const onSubmitPermission = async (values: PermissionFormValues) => {
    try {
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({ id: modalState.permission.id, payload: values })
        messageApi.success(`已更新权限 ${values.permissionCode}`)
      } else {
        await createMutation.mutateAsync(values)
        messageApi.success(`已创建权限 ${values.permissionCode}`)
      }
      closeModal()
    } catch (submitError) {
      messageApi.error(submitError instanceof Error ? submitError.message : '保存权限失败')
    }
  }

  const onDeletePermission = async (permission: PermissionResponse) => {
    try {
      await deleteMutation.mutateAsync(permission.id)
      messageApi.success(`已删除权限 ${permission.permissionCode}`)
    } catch (deleteError) {
      messageApi.error(deleteError instanceof Error ? deleteError.message : '删除权限失败')
    }
  }

  const onToggleStatus = async (permission: PermissionResponse, checked: boolean) => {
    try {
      await statusMutation.mutateAsync({ id: permission.id, status: checked ? 1 : 0 })
      messageApi.success(`已更新状态：${permission.permissionCode}`)
    } catch (statusError) {
      messageApi.error(statusError instanceof Error ? statusError.message : '更新状态失败')
    }
  }

  const modalInitialValues: Partial<PermissionFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          permissionCode: modalState.permission.permissionCode,
          permissionName: modalState.permission.permissionName,
          description: modalState.permission.description ?? '',
          status: modalState.permission.status,
          resourceType: modalState.permission.resourceType,
          resource: modalState.permission.resource,
          httpMethod: modalState.permission.httpMethod ?? 'GET',
          action: modalState.permission.action ?? 'READ',
          permissionGroup: modalState.permission.permissionGroup ?? '',
          sortOrder: modalState.permission.sortOrder,
          uiMenuKey: modalState.permission.uiMenuKey ?? '',
          uiRoute: modalState.permission.uiRoute ?? ''
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
            <Col xs={24} md={6}>
              <Form.Item label="权限编码">
                <Controller
                  name="permissionCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按权限编码搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="权限名称">
                <Controller
                  name="permissionName"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按权限名称搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="资源类型">
                <Controller
                  name="resourceType"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '全部', value: 'all' },
                        { label: '接口 (API)', value: 'API' },
                        { label: '界面 (UI)', value: 'UI' },
                        { label: '数据 (DATA)', value: 'DATA' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="状态">
                <Controller
                  name="status"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '全部', value: 'all' },
                        { label: '启用', value: '1' },
                        { label: '停用', value: '0' }
                      ]}
                    />
                  )}
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
              新建权限
            </Button>
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
            message="加载权限列表失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <PermissionTable
              permissions={data?.items ?? []}
              loading={isLoading}
              onEdit={(permission) => setModalState({ open: true, mode: 'edit', permission })}
              onDelete={onDeletePermission}
              onToggleStatus={onToggleStatus}
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

      <PermissionFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? '编辑权限' : '新建权限'}
        confirmLoading={
          createMutation.isPending || updateMutation.isPending || deleteMutation.isPending || statusMutation.isPending
        }
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmitPermission}
      />
    </Space>
  )
}
