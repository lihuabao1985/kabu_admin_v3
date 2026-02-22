import { Button, Popconfirm, Space, Switch, Table } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { UserResponse } from './userApi'

interface UserTableProps {
  users: UserResponse[]
  loading: boolean
  onEdit: (user: UserResponse) => void
  onDelete: (user: UserResponse) => void
  onToggleStatus: (user: UserResponse, checked: boolean) => void
  onToggleLock: (user: UserResponse, checked: boolean) => void
}

export function UserTable({ users, loading, onEdit, onDelete, onToggleStatus, onToggleLock }: UserTableProps) {
  const columns: ColumnsType<UserResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 100
    },
    {
      title: '用户名',
      dataIndex: 'username'
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (_: number, user) => (
        <Switch
          checked={user.status === 1}
          onChange={(checked) => onToggleStatus(user, checked)}
          checkedChildren="开"
          unCheckedChildren="关"
        />
      )
    },
    {
      title: '锁定',
      dataIndex: 'accountLocked',
      width: 120,
      render: (_: number | undefined, user) => (
        <Switch
          checked={user.accountLocked === 1}
          onChange={(checked) => onToggleLock(user, checked)}
          checkedChildren="已锁"
          unCheckedChildren="未锁"
        />
      )
    },
    {
      title: '操作',
      width: 160,
      render: (_, user) => (
        <Space>
          <Button size="small" onClick={() => onEdit(user)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该用户？"
            okText="删除"
            cancelText="取消"
            onConfirm={() => onDelete(user)}
          >
            <Button danger size="small">
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return <Table rowKey="id" columns={columns} dataSource={users} loading={loading} pagination={false} />
}
