import { Button, Popconfirm, Space, Table, Tag } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { UserResponse } from './userApi'

interface UserTableProps {
  users: UserResponse[]
  loading: boolean
  onEdit: (user: UserResponse) => void
  onDelete: (user: UserResponse) => void
}

export function UserTable({ users, loading, onEdit, onDelete }: UserTableProps) {
  const columns: ColumnsType<UserResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 100
    },
    {
      title: 'Username',
      dataIndex: 'username'
    },
    {
      title: 'Display Name',
      dataIndex: 'displayName',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: 'Email',
      dataIndex: 'email',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: 'Status',
      dataIndex: 'status',
      width: 120,
      render: (status: number) =>
        status === 1 ? <Tag color="green">Enabled</Tag> : <Tag color="red">Disabled</Tag>
    },
    {
      title: 'Action',
      width: 160,
      render: (_, user) => (
        <Space>
          <Button size="small" onClick={() => onEdit(user)}>
            Edit
          </Button>
          <Popconfirm
            title="Delete this user?"
            okText="Delete"
            cancelText="Cancel"
            onConfirm={() => onDelete(user)}
          >
            <Button danger size="small">
              Delete
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  return <Table rowKey="id" columns={columns} dataSource={users} loading={loading} pagination={false} />
}
