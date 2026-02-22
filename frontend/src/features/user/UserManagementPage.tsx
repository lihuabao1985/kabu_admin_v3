import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography, message } from 'antd'
import { UserFormModal, type UserFormValues } from './UserFormModal'
import { UserTable } from './UserTable'
import { ListPagination } from '../../lib/ListPagination'
import {
  useCreateUserMutation,
  useDeleteUserMutation,
  useUpdateUserLockMutation,
  useUpdateUserStatusMutation,
  useUpdateUserMutation,
  useUserListQuery
} from './useUserQueries'
import type { ListUsersQuery, UserResponse } from './userApi'

interface SearchFormValues {
  username: string
  email: string
  tenantId: string
  locked: 'all' | '1' | '0'
  status: 'all' | '1' | '0'
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; user: UserResponse }

type UserQueryState = NonNullable<ListUsersQuery>

const defaultQuery: UserQueryState = {
  page: 1,
  size: 20
}

export function UserManagementPage() {
  const [query, setQuery] = useState<UserQueryState>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      username: '',
      email: '',
      tenantId: '',
      locked: 'all',
      status: 'all'
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useUserListQuery(listQuery)
  const createMutation = useCreateUserMutation()
  const updateMutation = useUpdateUserMutation()
  const deleteMutation = useDeleteUserMutation()
  const statusMutation = useUpdateUserStatusMutation()
  const lockMutation = useUpdateUserLockMutation()

  const openCreateModal = () => setModalState({ open: true, mode: 'create' })
  const openEditModal = (user: UserResponse) => setModalState({ open: true, mode: 'edit', user })
  const closeModal = () => setModalState({ open: false })

  const onSearch = (values: SearchFormValues) => {
    setQuery((prev) => ({
      page: 1,
      size: prev.size ?? 20,
      username: values.username.trim() || undefined,
      email: values.email.trim() || undefined,
      tenantId: values.tenantId.trim() || undefined,
      locked: values.locked === 'all' ? undefined : Number(values.locked),
      status: values.status === 'all' ? undefined : Number(values.status)
    }))
  }

  const onReset = () => {
    reset({
      username: '',
      email: '',
      tenantId: '',
      locked: 'all',
      status: 'all'
    })
    setQuery(defaultQuery)
  }

  const onDelete = async (user: UserResponse) => {
    try {
      await deleteMutation.mutateAsync(user.id)
      messageApi.success(`已删除用户 ${user.username}`)
    } catch (deleteError) {
      const errorMessage = deleteError instanceof Error ? deleteError.message : '删除用户失败'
      messageApi.error(errorMessage)
    }
  }

  const onSubmitUser = async (values: UserFormValues) => {
    const payload = {
      username: values.username,
      displayName: values.displayName?.trim() || undefined,
      email: values.email?.trim() || undefined,
      phone: values.phone?.trim() || undefined,
      password: values.password?.trim() || undefined,
      status: values.status
    }
    try {
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({
          id: modalState.user.id,
          payload
        })
        messageApi.success(`已更新用户 ${values.username}`)
      } else {
        if (!payload.password) {
          messageApi.error('新建用户时密码不能为空')
          return
        }
        const createPayload = {
          ...payload,
          password: payload.password
        }
        await createMutation.mutateAsync(createPayload)
        messageApi.success(`已创建用户 ${values.username}`)
      }
      closeModal()
    } catch (submitError) {
      const errorMessage = submitError instanceof Error ? submitError.message : '保存用户失败'
      messageApi.error(errorMessage)
    }
  }

  const modalInitialValues: Partial<UserFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          username: modalState.user.username,
          displayName: modalState.user.displayName ?? '',
          email: modalState.user.email ?? '',
          phone: modalState.user.phone ?? '',
          password: '',
          status: modalState.user.status
        }
      : undefined

  const onToggleStatus = async (user: UserResponse, checked: boolean) => {
    try {
      await statusMutation.mutateAsync({ id: user.id, status: checked ? 1 : 0 })
      messageApi.success(`已更新状态：${user.username}`)
    } catch (statusError) {
      messageApi.error(statusError instanceof Error ? statusError.message : '更新状态失败')
    }
  }

  const onToggleLock = async (user: UserResponse, checked: boolean) => {
    try {
      await lockMutation.mutateAsync({ id: user.id, locked: checked ? 1 : 0 })
      messageApi.success(`已更新锁定状态：${user.username}`)
    } catch (lockError) {
      messageApi.error(lockError instanceof Error ? lockError.message : '更新锁定状态失败')
    }
  }

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
              <Form.Item label="用户名">
                <Controller
                  name="username"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按用户名搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="邮箱">
                <Controller
                  name="email"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按邮箱搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="租户ID">
                <Controller
                  name="tenantId"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="按租户ID搜索" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={3}>
              <Form.Item label="锁定状态">
                <Controller
                  name="locked"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: '全部', value: 'all' },
                        { label: '已锁定', value: '1' },
                        { label: '未锁定', value: '0' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={3}>
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
            <Button type="dashed" onClick={openCreateModal}>
              新建用户
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
            message="加载用户列表失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : (
          <>
            <UserTable
              users={data?.items ?? []}
              loading={isLoading}
              onEdit={openEditModal}
              onDelete={onDelete}
              onToggleStatus={onToggleStatus}
              onToggleLock={onToggleLock}
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

      <UserFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? '编辑用户' : '新建用户'}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmitUser}
      />
    </Space>
  )
}
