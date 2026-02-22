import { Controller, useForm } from 'react-hook-form'
import { Form, Input, Modal, Select } from 'antd'
import { useEffect } from 'react'
import type { UserCreateRequest } from './userApi'

export type UserFormValues = UserCreateRequest

interface UserFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<UserFormValues>
  onCancel: () => void
  onSubmit: (values: UserFormValues) => Promise<void> | void
}

export function UserFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: UserFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<UserFormValues>({
    defaultValues: {
      username: '',
      displayName: '',
      email: '',
      status: 1
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        username: initialValues?.username ?? '',
        displayName: initialValues?.displayName ?? '',
        email: initialValues?.email ?? '',
        status: initialValues?.status ?? 1
      })
    }
  }, [initialValues, open, reset])

  return (
    <Modal
      title={title}
      open={open}
      onCancel={onCancel}
      confirmLoading={confirmLoading}
      onOk={handleSubmit(async (values) => {
        await onSubmit(values)
      })}
      okText="Save"
      destroyOnClose
    >
      <Form layout="vertical">
        <Form.Item
          label="Username"
          validateStatus={formState.errors.username ? 'error' : ''}
          help={formState.errors.username?.message}
          required
        >
          <Controller
            name="username"
            control={control}
            rules={{ required: 'Username is required' }}
            render={({ field }) => <Input {...field} maxLength={50} />}
          />
        </Form.Item>

        <Form.Item label="Display Name">
          <Controller
            name="displayName"
            control={control}
            render={({ field }) => <Input {...field} maxLength={100} />}
          />
        </Form.Item>

        <Form.Item
          label="Email"
          validateStatus={formState.errors.email ? 'error' : ''}
          help={formState.errors.email?.message}
        >
          <Controller
            name="email"
            control={control}
            rules={{
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: 'Invalid email format'
              }
            }}
            render={({ field }) => <Input {...field} maxLength={100} />}
          />
        </Form.Item>

        <Form.Item label="Status" required>
          <Controller
            name="status"
            control={control}
            render={({ field }) => (
              <Select
                {...field}
                options={[
                  { label: 'Enabled', value: 1 },
                  { label: 'Disabled', value: 0 }
                ]}
              />
            )}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
