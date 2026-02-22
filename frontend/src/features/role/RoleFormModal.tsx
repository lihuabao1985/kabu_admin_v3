import { useEffect } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Form, Input, InputNumber, Modal, Select } from 'antd'
import type { RoleCreateRequest } from './roleApi'

export type RoleFormValues = RoleCreateRequest

interface RoleFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<RoleFormValues>
  onCancel: () => void
  onSubmit: (values: RoleFormValues) => Promise<void> | void
}

export function RoleFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: RoleFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<RoleFormValues>({
    defaultValues: {
      roleCode: '',
      roleName: '',
      description: '',
      status: 1,
      isSystem: 0,
      sortOrder: 0
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        roleCode: initialValues?.roleCode ?? '',
        roleName: initialValues?.roleName ?? '',
        description: initialValues?.description ?? '',
        status: initialValues?.status ?? 1,
        isSystem: initialValues?.isSystem ?? 0,
        sortOrder: initialValues?.sortOrder ?? 0
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
      cancelText="取消"
      destroyOnClose
    >
      <Form layout="vertical">
        <Form.Item
          label="角色编码"
          validateStatus={formState.errors.roleCode ? 'error' : ''}
          help={formState.errors.roleCode?.message}
          required
        >
          <Controller
            name="roleCode"
            control={control}
            rules={{ required: '角色编码不能为空' }}
            render={({ field }) => <Input {...field} maxLength={50} />}
          />
        </Form.Item>
        <Form.Item
          label="角色名称"
          validateStatus={formState.errors.roleName ? 'error' : ''}
          help={formState.errors.roleName?.message}
          required
        >
          <Controller
            name="roleName"
            control={control}
            rules={{ required: '角色名称不能为空' }}
            render={({ field }) => <Input {...field} maxLength={100} />}
          />
        </Form.Item>
        <Form.Item label="描述">
          <Controller
            name="description"
            control={control}
            render={({ field }) => <Input {...field} maxLength={255} />}
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
        <Form.Item label="系统角色" required>
          <Controller
            name="isSystem"
            control={control}
            render={({ field }) => (
              <Select
                {...field}
                options={[
                  { label: '否', value: 0 },
                  { label: '是', value: 1 }
                ]}
              />
            )}
          />
        </Form.Item>
        <Form.Item label="排序值" required>
          <Controller
            name="sortOrder"
            control={control}
            render={({ field }) => <InputNumber {...field} style={{ width: '100%' }} min={0} />}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
