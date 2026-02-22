import { Layout, Typography } from 'antd'
import { UserManagementPage } from './features/user/UserManagementPage'

export function App() {
  return (
    <Layout style={{ minHeight: '100vh', padding: 24, background: '#f5f7fa' }}>
      <Layout.Content style={{ maxWidth: 1200, width: '100%', margin: '0 auto' }}>
        <Typography.Title level={3}>User Management</Typography.Title>
        <UserManagementPage />
      </Layout.Content>
    </Layout>
  )
}
