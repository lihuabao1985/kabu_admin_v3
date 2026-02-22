import { Button, Popconfirm, Space, Switch, Table, Tag } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { RoleResponse } from './roleApi'

interface RoleTableProps {
  roles: RoleResponse[]
  loading: boolean
  onEdit: (role: RoleResponse) => void
  onDelete: (role: RoleResponse) => void
  onToggleStatus: (role: RoleResponse, checked: boolean) => void
}

export function RoleTable({ roles, loading, onEdit, onDelete, onToggleStatus }: RoleTableProps) {
  const columns: ColumnsType<RoleResponse> = [
    { title: 'ID', dataIndex: 'id', width: 100 },
    { title: '角色编码', dataIndex: 'roleCode' },
    { title: '角色名称', dataIndex: 'roleName' },
    {
      title: '系统角色',
      dataIndex: 'isSystem',
      width: 120,
      render: (value: number) => (value === 1 ? <Tag color="gold">系统</Tag> : <Tag>自定义</Tag>)
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 140,
      render: (_: number, role) => (
        <Switch
          checked={role.status === 1}
          onChange={(checked) => onToggleStatus(role, checked)}
          checkedChildren="开"
          unCheckedChildren="关"
        />
      )
    },
    {
      title: '操作',
      width: 180,
      render: (_, role) => (
        <Space>
          <Button size="small" onClick={() => onEdit(role)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该角色？" onConfirm={() => onDelete(role)} okText="删除" cancelText="取消">
            <Button size="small" danger disabled={role.isSystem === 1}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return <Table rowKey="id" columns={columns} dataSource={roles} loading={loading} pagination={false} />
}
