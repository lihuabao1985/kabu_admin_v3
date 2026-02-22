import { useCallback, useEffect, useMemo, useState } from 'react'
import { Alert, Button, Card, Select, Space, Table, Tag, Typography, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { usePermissionListQuery } from '../permission/usePermissionQueries'
import type { RoleResponse } from '../role/roleApi'
import { useRoleListQuery } from '../role/useRoleQueries'
import { listRolePermissions } from './rbacApi'
import { useReplaceRolePermissionsMutation } from './useRbacQueries'

const normalizeIds = (ids: number[]): number[] => Array.from(new Set(ids)).sort((left, right) => left - right)

const isSameIds = (left: number[], right: number[]): boolean =>
  left.length === right.length && left.every((value, index) => value === right[index])

export function RolePermissionBindingPage() {
  const [basePermissionMap, setBasePermissionMap] = useState<Record<number, number[]>>({})
  const [draftPermissionMap, setDraftPermissionMap] = useState<Record<number, number[]>>({})
  const [bindingLoading, setBindingLoading] = useState(false)
  const [bindingError, setBindingError] = useState<string | null>(null)
  const [batchSaving, setBatchSaving] = useState(false)
  const [messageApi, contextHolder] = message.useMessage()

  const roleQuery = useMemo(
    () => ({
      page: 1,
      size: 200
    }),
    []
  )
  const permissionQuery = useMemo(
    () => ({
      page: 1,
      size: 500
    }),
    []
  )

  const { data: roleData, isLoading: roleLoading, isError: roleError } = useRoleListQuery(roleQuery)
  const { data: permissionData, isLoading: permissionLoading, isError: permissionError } =
    usePermissionListQuery(permissionQuery)
  const replaceRolePermissionsMutation = useReplaceRolePermissionsMutation()

  const roles = roleData?.items ?? []
  const permissions = permissionData?.items ?? []

  const permissionNameMap = useMemo(() => {
    const map = new Map<number, string>()
    permissions.forEach((permission) => {
      map.set(permission.id, `${permission.permissionCode} - ${permission.permissionName}`)
    })
    return map
  }, [permissions])

  const permissionOptions = useMemo(
    () =>
      permissions.map((permission) => ({
        label: `${permission.permissionCode} - ${permission.permissionName}`,
        value: permission.id
      })),
    [permissions]
  )

  const loadAllRolePermissions = useCallback(async (targetRoles: RoleResponse[]) => {
    if (targetRoles.length === 0) {
      setBasePermissionMap({})
      setDraftPermissionMap({})
      setBindingError(null)
      return
    }

    setBindingLoading(true)
    setBindingError(null)

    const results = await Promise.allSettled(
      targetRoles.map(async (role) => {
        const response = await listRolePermissions(role.id)
        return { roleId: role.id, permissionIds: normalizeIds(response.permissionIds ?? []) }
      })
    )

    const nextMap: Record<number, number[]> = {}
    let failed = 0
    results.forEach((result) => {
      if (result.status === 'fulfilled') {
        nextMap[result.value.roleId] = result.value.permissionIds
      } else {
        failed += 1
      }
    })

    setBasePermissionMap(nextMap)
    setDraftPermissionMap(nextMap)
    setBindingLoading(false)

    if (failed > 0) {
      setBindingError(`部分角色权限加载失败（${failed}/${targetRoles.length}）`)
    }
  }, [])

  useEffect(() => {
    void loadAllRolePermissions(roles)
  }, [roles, loadAllRolePermissions])

  const changedRoleIds = useMemo(
    () =>
      roles
        .map((role) => role.id)
        .filter((roleId) => {
          const baseIds = basePermissionMap[roleId] ?? []
          const draftIds = draftPermissionMap[roleId] ?? []
          return !isSameIds(baseIds, draftIds)
        }),
    [basePermissionMap, draftPermissionMap, roles]
  )

  const onPermissionChange = (roleId: number, permissionIds: number[]) => {
    const normalized = normalizeIds(permissionIds)
    setDraftPermissionMap((previous) => ({
      ...previous,
      [roleId]: normalized
    }))
  }

  const onBatchSave = async () => {
    if (changedRoleIds.length === 0) {
      messageApi.warning('没有待保存的变更')
      return
    }

    setBatchSaving(true)
    let success = 0
    let failed = 0

    for (const roleId of changedRoleIds) {
      try {
        await replaceRolePermissionsMutation.mutateAsync({
          roleId,
          payload: { ids: draftPermissionMap[roleId] ?? [] }
        })
        success += 1
      } catch (saveError) {
        failed += 1
        const roleName = roles.find((role) => role.id === roleId)?.roleCode ?? `${roleId}`
        messageApi.error(
          saveError instanceof Error ? `${roleName} 保存失败: ${saveError.message}` : `${roleName} 保存失败`
        )
      }
    }

    setBatchSaving(false)

    if (success > 0) {
      messageApi.success(`批量保存完成：成功 ${success} 条`)
      await loadAllRolePermissions(roles)
    }
    if (failed > 0) {
      messageApi.warning(`批量保存有失败：失败 ${failed} 条`)
    }
  }

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
      dataIndex: 'roleName',
      width: 220
    },
    {
      title: '当前权限',
      width: 320,
      render: (_, role) => {
        const currentIds = basePermissionMap[role.id] ?? []
        if (currentIds.length === 0) {
          return '-'
        }
        return (
          <Space size={[4, 4]} wrap>
            {currentIds.map((permissionId) => (
              <Tag key={`current-${role.id}-${permissionId}`}>
                {permissionNameMap.get(permissionId) ?? `权限#${permissionId}`}
              </Tag>
            ))}
          </Space>
        )
      }
    },
    {
      title: '权限设定（支持批处理）',
      width: 420,
      render: (_, role) => (
        <Select
          mode="multiple"
          style={{ width: '100%' }}
          allowClear
          maxTagCount={3}
          placeholder="选择权限"
          options={permissionOptions}
          value={draftPermissionMap[role.id] ?? []}
          onChange={(value) => onPermissionChange(role.id, value.map((item) => Number(item)))}
        />
      )
    },
    {
      title: '变更状态',
      width: 120,
      render: (_, role) => {
        const baseIds = basePermissionMap[role.id] ?? []
        const draftIds = draftPermissionMap[role.id] ?? []
        return isSameIds(baseIds, draftIds) ? <Tag color="default">未变更</Tag> : <Tag color="orange">待保存</Tag>
      }
    }
  ]

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      {contextHolder}
      <Card
        title="角色权限绑定一览（Table）"
        extra={
          <Space>
            <Typography.Text>待保存：{changedRoleIds.length}</Typography.Text>
            <Button onClick={() => void loadAllRolePermissions(roles)} disabled={bindingLoading || batchSaving}>
              重新加载
            </Button>
            <Button
              type="primary"
              onClick={onBatchSave}
              loading={batchSaving || replaceRolePermissionsMutation.isPending}
              disabled={bindingLoading || changedRoleIds.length === 0}
            >
              批量保存变更
            </Button>
          </Space>
        }
      >
        {roleError ? <Alert type="error" showIcon message="加载角色失败" /> : null}
        {permissionError ? <Alert type="error" showIcon message="加载权限失败" style={{ marginTop: 8 }} /> : null}
        {bindingError ? <Alert type="warning" showIcon message={bindingError} style={{ marginTop: 8 }} /> : null}
        <Table
          rowKey="id"
          columns={roleColumns}
          dataSource={roles}
          loading={roleLoading || permissionLoading || bindingLoading}
          pagination={false}
          scroll={{ x: 1280 }}
          style={{ marginTop: 12 }}
        />
      </Card>
    </Space>
  )
}
