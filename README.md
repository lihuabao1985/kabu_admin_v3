# kabu_admin_v3 Bootstrap

## Backend
- Spring Boot 3 + Maven
- MyBatis 基础配置
- Spring Security 全局默认保护（所有请求需要认证）
- MySQL 数据源配置（`application.yml`）
- OpenAPI First 根文档（`api/openapi.yaml`）

## Frontend
- React + TypeScript (strict)
- Vite 初始化
- Ant Design
- React Query
- React Hook Form
- OpenAPI TypeScript 类型生成脚本

## Build
```bash
mvn clean verify
cd frontend && npm install && npm run build
```
