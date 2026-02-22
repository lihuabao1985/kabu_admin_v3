# 阶段 4：核心业务模块

## 项目阶段
Business Feature

---

## 目标

实现核心业务功能模块。

（填写具体业务）

---

## 前端技术规则

- UI 使用统一框架
- 复杂表格必须使用组件库提供的 DataGrid
- 数据必须使用 React Query
- 表单必须使用 React Hook Form
- 类型必须来自 OpenAPI

---

## 范围

允许修改：

- backend/src/main/**
- backend/src/test/**
- frontend/src/**

禁止修改：

- pom.xml
- Security 核心配置
- OpenAPI 核心定义

---

## 后端要求

- 严格分层
- 必须考虑事务
- 必须考虑并发安全
- 无 SELECT *
- 必须有单元测试 + 集成测试

---

## 性能要求

- 无 N+1
- 查询必须有索引
- 复杂 SQL 必须说明

---

## 测试

mvn clean verify  
npm test  
npm run build  

---

## 输出

- 业务流程说明
- 数据流说明
- 风险分析
- 回滚方案