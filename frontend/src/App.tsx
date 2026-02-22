import { useEffect, useMemo, useState } from 'react'
import { Alert, Button, Layout, Menu, Space, Spin, Typography, message } from 'antd'
import type { MenuProps } from 'antd'
import { LoginPage } from './features/auth/LoginPage'
import { useLogoutMutation } from './features/auth/useAuthQueries'
import { flattenVisiblePages, getVisibleMenuModules, menuModules, type PageKey } from './features/navigation/menuRegistry'
import { subscribePageNavigation } from './features/navigation/pageNavigation'
import { useCurrentPermissionsQuery } from './features/rbac/useRbacQueries'
import { ApiError } from './lib/apiClient'

const defaultPageKey: PageKey = menuModules[0]?.pages[0]?.key ?? 'users'

export function App() {
  const [selectedKey, setSelectedKey] = useState<PageKey>(defaultPageKey)
  const [messageApi, contextHolder] = message.useMessage()
  const { data, isLoading, isError, error, refetch } = useCurrentPermissionsQuery()
  const logoutMutation = useLogoutMutation()

  const authorities = data?.permissions ?? []
  const isUnauthorized = error instanceof ApiError && error.status === 401
  const visibleModules = useMemo(() => getVisibleMenuModules(authorities), [authorities])
  const visiblePages = useMemo(() => flattenVisiblePages(visibleModules), [visibleModules])
  const visiblePageMap = useMemo(
    () => new Map(visiblePages.map((page) => [page.key, page])),
    [visiblePages]
  )
  const menuItems: MenuProps['items'] = useMemo(
    () =>
      visibleModules.map((module) => ({
        key: module.key,
        label: module.label,
        children: module.pages.map((page) => ({
          key: page.key,
          label: page.label
        }))
      })),
    [visibleModules]
  )

  useEffect(() => {
    if (visiblePages.length === 0) {
      return
    }
    const hasSelectedKey = visiblePages.some((page) => page.key === selectedKey)
    if (!hasSelectedKey) {
      setSelectedKey(visiblePages[0].key)
    }
  }, [selectedKey, visiblePages])

  useEffect(
    () =>
      subscribePageNavigation((pageKey) => {
        if (visiblePageMap.has(pageKey)) {
          setSelectedKey(pageKey)
        }
      }),
    [visiblePageMap]
  )

  if (isError && isUnauthorized) {
    return (
      <>
        {contextHolder}
        <LoginPage onLoginSuccess={() => void refetch()} />
      </>
    )
  }

  const onLogout = async () => {
    try {
      await logoutMutation.mutateAsync()
      messageApi.success('已退出登录')
      await refetch()
    } catch (logoutError) {
      messageApi.error(logoutError instanceof Error ? logoutError.message : '退出登录失败')
    }
  }

  const SelectedPage = visiblePageMap.get(selectedKey)?.component ?? null
  const isVisiblePageKey = (key: string): key is PageKey => visiblePageMap.has(key as PageKey)

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {contextHolder}
      <Layout.Sider width={260} style={{ background: '#001529', paddingTop: 16 }}>
        <Typography.Title level={4} style={{ color: '#fff', textAlign: 'center', marginBottom: 24 }}>
          Kabu 管理后台
        </Typography.Title>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          defaultOpenKeys={visibleModules.map((module) => module.key)}
          onClick={(event) => {
            if (isVisiblePageKey(event.key)) {
              setSelectedKey(event.key)
            }
          }}
        />
      </Layout.Sider>
      <Layout.Content style={{ padding: 24, background: '#f5f7fa' }}>
        <Space style={{ width: '100%', justifyContent: 'flex-end', marginBottom: 12 }}>
          <Button onClick={onLogout} loading={logoutMutation.isPending}>
            退出登录
          </Button>
        </Space>
        {isLoading ? (
          <Spin />
        ) : isError ? (
          <Alert
            type="error"
            showIcon
            message="加载当前权限失败"
            description={error instanceof Error ? error.message : '未知错误'}
          />
        ) : visiblePages.length === 0 ? (
          <Alert
            type="warning"
            showIcon
            message="无可访问菜单"
            description="当前账号未配置管理模块权限。"
          />
        ) : (
          SelectedPage ? <SelectedPage /> : null
        )}
      </Layout.Content>
    </Layout>
  )
}
