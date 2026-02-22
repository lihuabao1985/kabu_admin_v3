import { Controller, useForm } from 'react-hook-form'
import { Form, Input, Modal, Select } from 'antd'
import { useEffect } from 'react'

export interface UserFormValues {
  username: string
  displayName?: string
  email?: string
  phone?: string
  password?: string
  status: number
}

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
      phone: '',
      password: '',
      status: 1
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        username: initialValues?.username ?? '',
        displayName: initialValues?.displayName ?? '',
        email: initialValues?.email ?? '',
        phone: initialValues?.phone ?? '',
        password: initialValues?.password ?? '',
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
      okText="保存"
      destroyOnClose
    >
      <Form layout="vertical">
        <Form.Item
          label="用户名"
          validateStatus={formState.errors.username ? 'error' : ''}
          help={formState.errors.username?.message}
          required
        >
          <Controller
            name="username"
            control={control}
            rules={{ required: '用户名不能为空' }}
            render={({ field }) => <Input {...field} maxLength={50} />}
          />
        </Form.Item>

        <Form.Item label="显示名称">
          <Controller
            name="displayName"
            control={control}
            render={({ field }) => <Input {...field} maxLength={100} />}
          />
        </Form.Item>

        <Form.Item
          label="邮箱"
          validateStatus={formState.errors.email ? 'error' : ''}
          help={formState.errors.email?.message}
        >
          <Controller
            name="email"
            control={control}
            rules={{
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: '邮箱格式不正确'
              }
            }}
            render={({ field }) => <Input {...field} maxLength={100} />}
          />
        </Form.Item>

        <Form.Item label="手机号">
          <Controller
            name="phone"
            control={control}
            render={({ field }) => <Input {...field} maxLength={30} />}
          />
        </Form.Item>

        <Form.Item label="密码">
          <Controller
            name="password"
            control={control}
            render={({ field }) => <Input.Password {...field} maxLength={100} />}
          />
        </Form.Item>

        <Form.Item label="状态" required>
          <Controller
            name="status"
            control={control}
            render={({ field }) => (
              <Select
                {...field}
                options={[
                  { label: '启用', value: 1 },
                  { label: '停用', value: 0 }
                ]}
              />
            )}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
