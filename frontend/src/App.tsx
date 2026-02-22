import { Button, Card, Form, Input, Typography } from 'antd'
import { Controller, useForm } from 'react-hook-form'

interface BootstrapForm {
  projectName: string
}

export function App() {
  const { control, handleSubmit } = useForm<BootstrapForm>({
    defaultValues: { projectName: 'kabu-admin-v3' }
  })

  const onSubmit = (data: BootstrapForm) => {
    console.log('bootstrap form submit', data)
  }

  return (
    <Card style={{ maxWidth: 640, margin: '48px auto' }}>
      <Typography.Title level={3}>Kabu Admin V3 Frontend Bootstrap</Typography.Title>
      <Typography.Paragraph>
        React + TypeScript strict + Ant Design + React Query + React Hook Form initialized.
      </Typography.Paragraph>
      <Form layout="vertical" onFinish={handleSubmit(onSubmit)}>
        <Form.Item label="Project Name">
          <Controller
            name="projectName"
            control={control}
            render={({ field }) => <Input {...field} />}
          />
        </Form.Item>
        <Button type="primary" htmlType="submit">
          Submit
        </Button>
      </Form>
    </Card>
  )
}
