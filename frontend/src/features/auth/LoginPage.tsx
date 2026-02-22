import { useState } from 'react'
import { Alert, Button, Card, Form, Input, Space, Typography } from 'antd'
import { useLoginMutation } from './useAuthQueries'

interface LoginPageProps {
  onLoginSuccess: () => void
}

export function LoginPage({ onLoginSuccess }: LoginPageProps) {
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const loginMutation = useLoginMutation()

  const onFinish = async (values: { username: string; password: string }) => {
    setErrorMessage(null)
    try {
      await loginMutation.mutateAsync(values)
      onLoginSuccess()
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : '登录失败')
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #e8eef8 0%, #f7f9fc 100%)',
        padding: 16
      }}
    >
      <Card title="Kabu 管理后台登录" style={{ width: 420 }}>
        <Space direction="vertical" style={{ width: '100%' }} size={16}>
          <Typography.Text type="secondary">
            请使用后端账号登录，登录后会复用会话 Cookie 访问 API。
          </Typography.Text>
          {errorMessage ? <Alert type="error" showIcon message={errorMessage} /> : null}
          <Form layout="vertical" onFinish={onFinish}>
            <Form.Item
              label="用户名"
              name="username"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input autoComplete="username" />
            </Form.Item>
            <Form.Item
              label="密码"
              name="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password autoComplete="current-password" />
            </Form.Item>
            <Button type="primary" htmlType="submit" block loading={loginMutation.isPending}>
              登录
            </Button>
          </Form>
        </Space>
      </Card>
    </div>
  )
}
