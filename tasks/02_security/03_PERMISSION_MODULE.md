# 阶段 3：权限与角色管理模块

## 项目阶段
Security Extension

---

## 目标

实现完整 RBAC 权限系统：

- 角色管理
- 权限定义
- 用户-角色关联
- 方法级权限控制

---

## 前端技术规则

- UI 使用统一框架
- 权限菜单必须动态控制
- React Query 管理权限数据
- OpenAPI 类型自动生成

---

## 范围

允许修改：

- security config
- backend/src/main/**
- backend/src/test/**
- frontend/src/**

禁止修改：

- pom.xml
- OpenAPI 根定义
- 数据库核心结构

---

## 安全要求

- 所有接口默认受保护
- 不得关闭 CSRF
- 权限逻辑必须有测试
- 未授权访问必须返回 403

---

## 测试

mvn clean verify  
npm test  

---

## 输出

- 权限矩阵说明
- 安全测试结果
- 风险说明