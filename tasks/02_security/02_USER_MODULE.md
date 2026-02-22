# 阶段 2：用户管理模块

## 项目阶段
Feature Extension

---

## 目标

实现用户管理模块：

- 创建用户
- 更新用户
- 删除用户
- 查询用户列表

---

## 前端技术规则

- UI框架 Ant Design
- 数据请求必须使用 React Query
- 表单必须使用 React Hook Form
- API 类型必须来自 OpenAPI 自动生成
- TypeScript strict 模式

---

## 范围

允许修改：

- backend/src/main/**
- backend/src/test/**
- frontend/src/**

禁止修改：

- pom.xml
- application.yml
- Security 基础配置
- OpenAPI 根结构

---

## 后端要求

- 严格分层
- 无 SELECT *
- Service 必须有单元测试
- DTO 不暴露实体
- 接口符合 OpenAPI

---

## 测试

mvn clean verify  
npm test  
npm run build  

---

## 输出

- Diff 摘要
- 测试结果
- 风险分析