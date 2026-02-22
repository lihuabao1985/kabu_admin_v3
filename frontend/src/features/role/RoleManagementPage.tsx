import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography, message } from 'antd'
import { RoleFormModal, type RoleFormValues } from './RoleFormModal'
import { RoleTable } from './RoleTable'
import { ListPagination } from '../../lib/ListPagination'
import {
  useCreateRoleMutation,
  useDeleteRoleMutation,
  useRoleListQuery,
  useUpdateRoleMutation,
  useUpdateRoleStatusMutation
} from './useRoleQueries'
import type { ListRolesQuery, RoleResponse } from './roleApi'

interface SearchFormValues {
  roleCode: string
  roleName: string
  status: 'all' | '1' | '0'
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; role: RoleResponse }

type RoleQueryState = NonNullable<ListRolesQuery>

const defaultQuery: RoleQueryState = {
  page: 1,
  size: 20
}

export function RoleManagementPage() {
  const [query, setQuery] = useState<RoleQueryState>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: { roleCode: '', roleName: '', status: 'all' }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useRoleListQuery(listQuery)
  const createMutation = useCreateRoleMutation()
  const updateMutation = useUpdateRoleMutation()
  const deleteMutation = useDeleteRoleMutation()
  const statusMutation = useUpdateRoleStatusMutation()

  const closeModal = () => setModalState({ open: false })

  const onSearch = (values: SearchFormValues) => {
    setQuery((prev) => ({
      page: 1,
      size: prev.size ?? 20,
      roleCode: values.roleCode.trim() || undefined,
      roleName: values.roleName.trim() || undefined,
      status: values.status === 'all' ? undefined : Number(values.status)
    }))
  }

  const onReset = () => {
    reset({ roleCode: '', roleName: '', status: 'all' })
    setQuery(defaultQuery)
  }

  const onSubmitRole = async (values: RoleFormValues) => {
    try {
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({ id: modalState.role.id, payload: values })
        messageApi.success(`已更新角色 ${values.roleCode}`)
      } else {
        await createMutation.mutateAsync(values)
        messageApi.success(`已创建角色 ${values.roleCode}`)
      }
      closeModal()
    } catch (submitError) {
      messageApi.error(submitError instanceof Error ? submitError.message : '保存角色失败')
    }
  }

  const onDeleteRole = async (role: RoleResponse) => {
    try {
      await deleteMutation.mutateAsync(role.id)
      messageApi.success(`已删除角色 ${role.roleCode}`)
    } catch (deleteError) {
      messageApi.error(deleteError instanceof Error ? deleteError.message : '删除角色失败')
    }
  }

  const onToggleStatus = async (role: RoleResponse, checked: boolean) => {
    try {
      await statusMutation.mutateAsync({ id: role.id, status: checked ? 1 : 0 })
      messageApi.success(`已更新状态：${role.roleCode}`)
    } catch (statusError) {
      messageApi.error(statusError instanceof Error ? statusError.message : '更新状态失败')
    }
  }

  const modalInitialValues: Partial<RoleFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          roleCode: modalState.role.roleCode,
          roleName: modalState.role.roleName,
          description: modalState.role.description ?? '',
          status: modalState.role.status,
          isSystem: modalState.role.isSystem,
          sortOrder: modalState.role.sortOrder
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
              <Form.Item label="角色编码">
                <Controller
                  name="roleCode"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按角色编码搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="角色名称">
                <Controller
                  name="roleName"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按角色名称搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
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
              新建角色
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
            message="加载角色列表失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <RoleTable
              roles={data?.items ?? []}
              loading={isLoading}
              onEdit={(role) => setModalState({ open: true, mode: 'edit', role })}
              onDelete={onDeleteRole}
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

      <RoleFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? '编辑角色' : '新建角色'}
        confirmLoading={
          createMutation.isPending || updateMutation.isPending || deleteMutation.isPending || statusMutation.isPending
        }
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmitRole}
      />
    </Space>
  )
}
