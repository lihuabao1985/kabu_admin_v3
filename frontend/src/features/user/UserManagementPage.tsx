import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography, message } from 'antd'
import { UserFormModal, type UserFormValues } from './UserFormModal'
import { UserTable } from './UserTable'
import {
  useCreateUserMutation,
  useDeleteUserMutation,
  useUpdateUserMutation,
  useUserListQuery
} from './useUserQueries'
import type { ListUsersQuery, UserResponse } from './userApi'

interface SearchFormValues {
  username: string
  email: string
  status: 'all' | '1' | '0'
}

type ModalState =
  | { open: false }
  | { open: true; mode: 'create' }
  | { open: true; mode: 'edit'; user: UserResponse }

const defaultQuery: ListUsersQuery = {
  page: 1,
  size: 20
}

export function UserManagementPage() {
  const [query, setQuery] = useState<ListUsersQuery>(defaultQuery)
  const [modalState, setModalState] = useState<ModalState>({ open: false })
  const [messageApi, contextHolder] = message.useMessage()

  const { control, handleSubmit, reset } = useForm<SearchFormValues>({
    defaultValues: {
      username: '',
      email: '',
      status: 'all'
    }
  })

  const listQuery = useMemo(() => query, [query])
  const { data, isLoading, isError, error } = useUserListQuery(listQuery)
  const createMutation = useCreateUserMutation()
  const updateMutation = useUpdateUserMutation()
  const deleteMutation = useDeleteUserMutation()

  const openCreateModal = () => setModalState({ open: true, mode: 'create' })
  const openEditModal = (user: UserResponse) => setModalState({ open: true, mode: 'edit', user })
  const closeModal = () => setModalState({ open: false })

  const onSearch = (values: SearchFormValues) => {
    setQuery({
      page: 1,
      size: 20,
      username: values.username.trim() || undefined,
      email: values.email.trim() || undefined,
      status: values.status === 'all' ? undefined : Number(values.status)
    })
  }

  const onReset = () => {
    reset({
      username: '',
      email: '',
      status: 'all'
    })
    setQuery(defaultQuery)
  }

  const onDelete = async (user: UserResponse) => {
    try {
      await deleteMutation.mutateAsync(user.id)
      messageApi.success(`Deleted ${user.username}`)
    } catch (deleteError) {
      const errorMessage = deleteError instanceof Error ? deleteError.message : 'Failed to delete user'
      messageApi.error(errorMessage)
    }
  }

  const onSubmitUser = async (values: UserFormValues) => {
    try {
      if (modalState.open && modalState.mode === 'edit') {
        await updateMutation.mutateAsync({
          id: modalState.user.id,
          payload: values
        })
        messageApi.success(`Updated ${values.username}`)
      } else {
        await createMutation.mutateAsync(values)
        messageApi.success(`Created ${values.username}`)
      }
      closeModal()
    } catch (submitError) {
      const errorMessage = submitError instanceof Error ? submitError.message : 'Failed to save user'
      messageApi.error(errorMessage)
    }
  }

  const modalInitialValues: Partial<UserFormValues> | undefined =
    modalState.open && modalState.mode === 'edit'
      ? {
          username: modalState.user.username,
          displayName: modalState.user.displayName ?? '',
          email: modalState.user.email ?? '',
          status: modalState.user.status
        }
      : undefined

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
      <Card>
        <Form layout="vertical" onFinish={handleSubmit(onSearch)}>
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item label="Username">
                <Controller
                  name="username"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="Search by username" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="Email">
                <Controller
                  name="email"
                  control={control}
                  render={({ field }) => <Input {...field} placeholder="Search by email" />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="Status">
                <Controller
                  name="status"
                  control={control}
                  render={({ field }) => (
                    <Select
                      {...field}
                      options={[
                        { label: 'All', value: 'all' },
                        { label: 'Enabled', value: '1' },
                        { label: 'Disabled', value: '0' }
                      ]}
                    />
                  )}
                />
              </Form.Item>
            </Col>
          </Row>

          <Space>
            <Button type="primary" htmlType="submit">
              Search
            </Button>
            <Button onClick={onReset}>Reset</Button>
            <Button type="dashed" onClick={openCreateModal}>
              Create User
            </Button>
          </Space>
        </Form>
      </Card>

      <Card>
        <Typography.Text style={{ marginBottom: 16, display: 'inline-block' }}>
          Total: {data?.total ?? 0}
        </Typography.Text>

        {isError ? (
          <Alert
            type="error"
            showIcon
            message="Failed to load users"
            description={error instanceof Error ? error.message : 'Unknown error'}
          />
        ) : (
          <UserTable users={data?.items ?? []} loading={isLoading} onEdit={openEditModal} onDelete={onDelete} />
        )}
      </Card>

      <UserFormModal
        open={modalState.open}
        title={modalState.open && modalState.mode === 'edit' ? 'Edit User' : 'Create User'}
        confirmLoading={createMutation.isPending || updateMutation.isPending}
        initialValues={modalInitialValues}
        onCancel={closeModal}
        onSubmit={onSubmitUser}
      />
    </Space>
  )
}
