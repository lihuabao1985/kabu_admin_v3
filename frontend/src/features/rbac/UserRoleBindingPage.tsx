import { useCallback, useEffect, useMemo, useState } from 'react'
import { Alert, Button, Card, Space, Table, Tag, Typography, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import type { RoleResponse } from '../role/roleApi'
import { useRoleListQuery } from '../role/useRoleQueries'
import type { UserResponse } from '../user/userApi'
import { useUserListQuery } from '../user/useUserQueries'
import { listUserRoles } from './rbacApi'
import { useAddUserRolesMutation, useRemoveUserRolesMutation, useReplaceUserRolesMutation } from './useRbacQueries'

const normalizeIds = (ids: number[]): number[] => Array.from(new Set(ids)).sort((left, right) => left - right)

export function UserRoleBindingPage() {
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([])
  const [selectedRoleIds, setSelectedRoleIds] = useState<number[]>([])
  const [userRoleMap, setUserRoleMap] = useState<Record<number, number[]>>({})
  const [bindingLoading, setBindingLoading] = useState(false)
  const [bindingError, setBindingError] = useState<string | null>(null)
  const [batchSaving, setBatchSaving] = useState(false)
  const [messageApi, contextHolder] = message.useMessage()

  const userQuery = useMemo(
    () => ({
      page: 1,
      size: 200
    }),
    []
  )
  const roleQuery = useMemo(
    () => ({
      page: 1,
      size: 200
    }),
    []
  )

  const { data: userData, isLoading: userLoading, isError: userError } = useUserListQuery(userQuery)
  const { data: roleData, isLoading: roleLoading, isError: roleError } = useRoleListQuery(roleQuery)
  const replaceUserRolesMutation = useReplaceUserRolesMutation()
  const addUserRolesMutation = useAddUserRolesMutation()
  const removeUserRolesMutation = useRemoveUserRolesMutation()

  const users = userData?.items ?? []
  const roles = roleData?.items ?? []

  const roleNameMap = useMemo(() => {
    const map = new Map<number, string>()
    roles.forEach((role) => {
      map.set(role.id, `${role.roleCode} - ${role.roleName}`)
    })
    return map
  }, [roles])

  const loadUserRoleMap = useCallback(
    async (targetUsers: UserResponse[]) => {
      if (targetUsers.length === 0) {
        setUserRoleMap({})
        setSelectedUserIds([])
        setSelectedRoleIds([])
        setBindingError(null)
        return
      }

      setBindingLoading(true)
      setBindingError(null)

      const results = await Promise.allSettled(
        targetUsers.map(async (user) => {
          const response = await listUserRoles(user.id)
          return { userId: user.id, roleIds: normalizeIds(response.roleIds ?? []) }
        })
      )

      const nextUserRoleMap: Record<number, number[]> = {}
      let failed = 0
      results.forEach((result) => {
        if (result.status === 'fulfilled') {
          nextUserRoleMap[result.value.userId] = result.value.roleIds
        } else {
          failed += 1
        }
      })

      setUserRoleMap(nextUserRoleMap)
      setSelectedUserIds((previous) => {
        const preserved = previous.filter((userId) => nextUserRoleMap[userId] !== undefined)
        if (preserved.length > 0) {
          return normalizeIds(preserved)
        }
        return targetUsers[0] ? [targetUsers[0].id] : []
      })
      setBindingLoading(false)

      if (failed > 0) {
        setBindingError(`部分用户角色加载失败（${failed}/${targetUsers.length}）`)
      }
    },
    []
  )

  useEffect(() => {
    void loadUserRoleMap(users)
  }, [users, loadUserRoleMap])

  useEffect(() => {
    if (selectedUserIds.length !== 1) {
      return
    }
    const [selectedUserId] = selectedUserIds
    setSelectedRoleIds(userRoleMap[selectedUserId] ?? [])
  }, [selectedUserIds, userRoleMap])

  const selectedUsers = useMemo(
    () => users.filter((user) => selectedUserIds.includes(user.id)),
    [selectedUserIds, users]
  )

  const selectedUsersSummary = useMemo(() => {
    if (selectedUsers.length === 0) {
      return '-'
    }
    const preview = selectedUsers
      .slice(0, 4)
      .map((user) => `${user.username}${user.displayName ? ` (${user.displayName})` : ''}`)
      .join('、')
    return selectedUsers.length > 4 ? `${preview} 等${selectedUsers.length}人` : preview
  }, [selectedUsers])

  const selectedUser = useMemo(
    () => (selectedUsers.length === 1 ? selectedUsers[0] : null),
    [selectedUsers]
  )

  const refreshBindings = async () => {
    await loadUserRoleMap(users)
  }

  const runBatch = async (actionName: '替换' | '追加' | '移除', runner: (userId: number) => Promise<void>) => {
    if (selectedUserIds.length === 0) {
      messageApi.error('请先选择至少一个用户')
      return
    }

    setBatchSaving(true)
    let success = 0
    const failedUsers: string[] = []

    for (const userId of selectedUserIds) {
      try {
        await runner(userId)
        success += 1
      } catch {
        const username = users.find((item) => item.id === userId)?.username ?? `用户#${userId}`
        failedUsers.push(username)
      }
    }

    setBatchSaving(false)

    if (success > 0) {
      messageApi.success(`批量${actionName}成功：${success}个用户`)
      await refreshBindings()
    }
    if (failedUsers.length > 0) {
      const failedPreview = failedUsers.slice(0, 4).join('、')
      const failedSuffix = failedUsers.length > 4 ? ` 等${failedUsers.length}个用户` : ''
      messageApi.warning(`批量${actionName}失败：${failedPreview}${failedSuffix}`)
    }
  }

  const onReplace = async () => {
    await runBatch('替换', async (userId) => {
      await replaceUserRolesMutation.mutateAsync({
        userId,
        payload: { ids: selectedRoleIds }
      })
    })
  }

  const onAdd = async () => {
    if (selectedRoleIds.length === 0) {
      messageApi.error('请先选择角色')
      return
    }
    await runBatch('追加', async (userId) => {
      await addUserRolesMutation.mutateAsync({
        userId,
        payload: { ids: selectedRoleIds }
      })
    })
  }

  const onRemove = async () => {
    if (selectedRoleIds.length === 0) {
      messageApi.error('请先选择角色')
      return
    }
    await runBatch('移除', async (userId) => {
      await removeUserRolesMutation.mutateAsync({
        userId,
        payload: { ids: selectedRoleIds }
      })
    })
  }

  const userColumns: ColumnsType<UserResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 180
    },
    {
      title: '显示名',
      dataIndex: 'displayName',
      render: (value: string | null | undefined) => value ?? '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value: number) => (value === 1 ? <Tag color="green">启用</Tag> : <Tag color="default">停用</Tag>)
    },
    {
      title: '已绑定角色',
      render: (_, user) => {
        const roleIds = userRoleMap[user.id] ?? []
        if (roleIds.length === 0) {
          return '-'
        }
        return (
          <Space size={[4, 4]} wrap>
            {roleIds.map((roleId) => (
              <Tag key={`${user.id}-${roleId}`}>{roleNameMap.get(roleId) ?? `角色#${roleId}`}</Tag>
            ))}
          </Space>
        )
      }
    }
  ]

  const roleColumns: ColumnsType<RoleResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80
    },
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      width: 180
    },
    {
      title: '角色名称',
      dataIndex: 'roleName'
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value: number) => (value === 1 ? <Tag color="green">启用</Tag> : <Tag color="default">停用</Tag>)
    }
  ]

  const actionLoading =
    bindingLoading ||
    batchSaving ||
    replaceUserRolesMutation.isPending ||
    addUserRolesMutation.isPending ||
    removeUserRolesMutation.isPending

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
      <Card title="用户角色绑定一览（Table）" loading={userLoading || bindingLoading}>
        {userError ? <Alert type="error" showIcon message="加载用户失败" /> : null}
        {roleError ? <Alert type="error" showIcon message="加载角色失败" style={{ marginTop: 8 }} /> : null}
        {bindingError ? <Alert type="warning" showIcon message={bindingError} style={{ marginTop: 8 }} /> : null}
        <Table
          rowKey="id"
          columns={userColumns}
          dataSource={users}
          loading={userLoading || bindingLoading}
          pagination={false}
          rowSelection={{
            selectedRowKeys: selectedUserIds,
            onChange: (selectedRowKeys) =>
              setSelectedUserIds(normalizeIds(selectedRowKeys.map((selectedRowKey) => Number(selectedRowKey))))
          }}
          style={{ marginTop: 12 }}
        />
      </Card>

      <Card
        title="角色设定（支持多用户批量）"
        loading={roleLoading}
        extra={
          <Space>
            <Button
              onClick={onReplace}
              loading={batchSaving || replaceUserRolesMutation.isPending}
              type="primary"
              disabled={selectedUserIds.length === 0}
            >
              替换保存
            </Button>
            <Button onClick={onAdd} loading={batchSaving || addUserRolesMutation.isPending} disabled={selectedUserIds.length === 0}>
              追加
            </Button>
            <Button
              onClick={onRemove}
              loading={batchSaving || removeUserRolesMutation.isPending}
              disabled={selectedUserIds.length === 0}
              danger
            >
              移除
            </Button>
          </Space>
        }
      >
        <Typography.Paragraph style={{ marginBottom: 8 }}>
          当前选中用户：{selectedUserIds.length === 0 ? '-' : `${selectedUserIds.length}人`}
        </Typography.Paragraph>
        <Typography.Paragraph type="secondary">
          {selectedUser ? `单用户模式：${selectedUsersSummary}` : `多用户模式：${selectedUsersSummary}`}
        </Typography.Paragraph>
        <Table
          rowKey="id"
          columns={roleColumns}
          dataSource={roles}
          loading={roleLoading || actionLoading}
          pagination={false}
          rowSelection={{
            selectedRowKeys: selectedRoleIds,
            onChange: (selectedRowKeys) => setSelectedRoleIds(selectedRowKeys.map((key) => Number(key)))
          }}
        />
      </Card>
    </Space>
  )
}
