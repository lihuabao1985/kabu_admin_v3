# 阶段 1：系统基础框架生成（Initial Bootstrap）

## 项目阶段
Bootstrap（首次生成）

---

## 目标

生成完整基础工程框架，不实现任何业务逻辑，仅构建可运行基础架构：

- Spring Boot 主工程
- Maven 构建结构
- MyBatis 基础配置
- Spring Security 默认全局保护配置
- OpenAPI 根结构
- React + TypeScript 前端初始化
- MySQL 数据源配置
- 前端技术栈标准集成
- 基础目录结构

---

## 技术栈

Backend:
- Spring Boot
- MyBatis
- Lombok
- JUnit5
- Mockito
- SpringBootTest
- Spring Security
- Maven

Frontend:
- React
- TypeScript

数据库:
- MySQL

接口:
- OpenAPI First

---

## 前端技术选型规则

UI 框架：Ant Design

统一要求：

- 数据层：React Query
- 表单：React Hook Form
- 类型安全：OpenAPI 自动生成 TypeScript 类型
- TypeScript strict 模式

---

## 允许创建

- pom.xml
- application.yml
- security config
- openapi.yaml
- 前端 package.json
- React 初始化结构

---

## 禁止

- 不得添加非必要依赖
- 不得引入缓存或消息队列
- 不得引入复杂架构

---

## 构建验证

必须通过：

mvn clean verify  
npm run build  

---

## 输出

- 项目结构说明
- 技术栈说明
- 构建结果
- 风险说明