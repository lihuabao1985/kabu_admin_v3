import { Button, Popconfirm, Space, Switch, Table, Tag } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { PermissionResponse } from './permissionApi'

interface PermissionTableProps {
  permissions: PermissionResponse[]
  loading: boolean
  onEdit: (permission: PermissionResponse) => void
  onDelete: (permission: PermissionResponse) => void
  onToggleStatus: (permission: PermissionResponse, checked: boolean) => void
}

export function PermissionTable({
  permissions,
  loading,
  onEdit,
  onDelete,
  onToggleStatus
}: PermissionTableProps) {
  const columns: ColumnsType<PermissionResponse> = [
    { title: 'ID', dataIndex: 'id', width: 90 },
    { title: '权限编码', dataIndex: 'permissionCode' },
    { title: '权限名称', dataIndex: 'permissionName' },
    { title: '资源类型', dataIndex: 'resourceType', width: 120 },
    {
      title: '资源标识',
      dataIndex: 'resource',
      render: (value: string) => <Tag>{value}</Tag>
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 130,
      render: (_: number, permission) => (
        <Switch
          checked={permission.status === 1}
          onChange={(checked) => onToggleStatus(permission, checked)}
          checkedChildren="开"
          unCheckedChildren="关"
        />
      )
    },
    {
      title: '操作',
      width: 160,
      render: (_, permission) => (
        <Space>
          <Button size="small" onClick={() => onEdit(permission)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该权限？" onConfirm={() => onDelete(permission)} okText="删除" cancelText="取消">
            <Button size="small" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return <Table rowKey="id" columns={columns} dataSource={permissions} loading={loading} pagination={false} />
}
