import { useEffect } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { Form, Input, InputNumber, Modal, Select } from 'antd'
import type { PermissionCreateRequest } from './permissionApi'

export type PermissionFormValues = PermissionCreateRequest

interface PermissionFormModalProps {
  open: boolean
  title: string
  confirmLoading: boolean
  initialValues?: Partial<PermissionFormValues>
  onCancel: () => void
  onSubmit: (values: PermissionFormValues) => Promise<void> | void
}

export function PermissionFormModal({
  open,
  title,
  confirmLoading,
  initialValues,
  onCancel,
  onSubmit
}: PermissionFormModalProps) {
  const { control, handleSubmit, reset, formState } = useForm<PermissionFormValues>({
    defaultValues: {
      permissionCode: '',
      permissionName: '',
      description: '',
      status: 1,
      resourceType: 'API',
      resource: '',
      httpMethod: 'GET',
      action: 'READ',
      permissionGroup: '',
      sortOrder: 0,
      uiMenuKey: '',
      uiRoute: ''
    }
  })

  useEffect(() => {
    if (open) {
      reset({
        permissionCode: initialValues?.permissionCode ?? '',
        permissionName: initialValues?.permissionName ?? '',
        description: initialValues?.description ?? '',
        status: initialValues?.status ?? 1,
        resourceType: initialValues?.resourceType ?? 'API',
        resource: initialValues?.resource ?? '',
        httpMethod: initialValues?.httpMethod ?? 'GET',
        action: initialValues?.action ?? 'READ',
        permissionGroup: initialValues?.permissionGroup ?? '',
        sortOrder: initialValues?.sortOrder ?? 0,
        uiMenuKey: initialValues?.uiMenuKey ?? '',
        uiRoute: initialValues?.uiRoute ?? ''
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
      width={720}
      destroyOnClose
    >
      <Form layout="vertical">
        <Form.Item
          label="权限编码"
          validateStatus={formState.errors.permissionCode ? 'error' : ''}
          help={formState.errors.permissionCode?.message}
          required
        >
          <Controller
            name="permissionCode"
            control={control}
            rules={{ required: '权限编码不能为空' }}
            render={({ field }) => <Input {...field} maxLength={120} />}
          />
        </Form.Item>
        <Form.Item
          label="权限名称"
          validateStatus={formState.errors.permissionName ? 'error' : ''}
          help={formState.errors.permissionName?.message}
          required
        >
          <Controller
            name="permissionName"
            control={control}
            rules={{ required: '权限名称不能为空' }}
            render={({ field }) => <Input {...field} maxLength={120} />}
          />
        </Form.Item>
        <Form.Item label="描述">
          <Controller
            name="description"
            control={control}
            render={({ field }) => <Input {...field} maxLength={255} />}
          />
        </Form.Item>
        <Form.Item label="资源类型" required>
          <Controller
            name="resourceType"
            control={control}
            render={({ field }) => (
              <Select
                {...field}
                options={[
                  { label: '接口 (API)', value: 'API' },
                  { label: '界面 (UI)', value: 'UI' },
                  { label: '数据 (DATA)', value: 'DATA' }
                ]}
              />
            )}
          />
        </Form.Item>
        <Form.Item
          label="资源标识"
          validateStatus={formState.errors.resource ? 'error' : ''}
          help={formState.errors.resource?.message}
          required
        >
          <Controller
            name="resource"
            control={control}
            rules={{ required: '资源标识不能为空' }}
            render={({ field }) => <Input {...field} maxLength={200} />}
          />
        </Form.Item>
        <Form.Item label="HTTP 方法">
          <Controller
            name="httpMethod"
            control={control}
            render={({ field }) => (
              <Select
                {...field}
                options={[
                  { label: 'GET', value: 'GET' },
                  { label: 'POST', value: 'POST' },
                  { label: 'PUT', value: 'PUT' },
                  { label: 'PATCH', value: 'PATCH' },
                  { label: 'DELETE', value: 'DELETE' }
                ]}
              />
            )}
          />
        </Form.Item>
        <Form.Item label="动作">
          <Controller
            name="action"
            control={control}
            render={({ field }) => <Input {...field} value={field.value ?? ''} maxLength={50} />}
          />
        </Form.Item>
        <Form.Item label="权限分组">
          <Controller
            name="permissionGroup"
            control={control}
            render={({ field }) => <Input {...field} value={field.value ?? ''} maxLength={60} />}
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
        <Form.Item label="排序值">
          <Controller
            name="sortOrder"
            control={control}
            render={({ field }) => <InputNumber {...field} min={0} style={{ width: '100%' }} />}
          />
        </Form.Item>
        <Form.Item label="前端菜单键">
          <Controller
            name="uiMenuKey"
            control={control}
            render={({ field }) => <Input {...field} value={field.value ?? ''} maxLength={120} />}
          />
        </Form.Item>
        <Form.Item label="前端路由">
          <Controller
            name="uiRoute"
            control={control}
            render={({ field }) => <Input {...field} value={field.value ?? ''} maxLength={200} />}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
