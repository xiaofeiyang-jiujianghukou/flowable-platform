# Flowable 工作流平台

基于 **Flowable 8 + Spring Boot 4 + JDK 25** 的企业级工作流管理平台。

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 25 |
| Spring Boot | 4.0.2 |
| Flowable | 8.0.0 |
| MySQL | 8.0 |
| Docker | 28+ |

## 项目结构

```
flowable-platform/
├── docker-compose.yml      # 容器编排
├── init.sql                 # 业务表初始化
├── 项目设计文档.md          # 设计文档
└── flowable-work/           # Spring Boot 后端
    ├── pom.xml
    ├── Dockerfile
    └── src/main/
        ├── java/com/flowable/platform/
        │   ├── FlowablePlatformApplication.java
        │   ├── config/FlowableConfig.java
        │   ├── listener/ProcessEventListener.java
        │   └── service/ApprovalService.java
        └── resources/
            ├── application.yml
            ├── processes/leave-approval.bpmn20.xml
            └── static/ext/
                ├── custom.css
                └── custom.js
```

## 快速启动

### 1. 启动 MySQL + Flowable

```bash
docker compose up -d
```

### 2. 启动开发模式（H2 内存数据库）

```bash
cd flowable-work
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. 访问服务

- 后端 REST API: http://localhost:8080/flowable-rest
- H2 Console（开发模式）: http://localhost:8080/h2-console
- Actuator: http://localhost:8080/actuator

## REST API

Flowable 引擎提供完整的 REST API：

| 模块 | 路径 | 说明 |
|------|------|------|
| 流程定义 | `/flowable-rest/service/repository/process-definitions` | 流程定义管理 |
| 流程实例 | `/flowable-rest/service/runtime/process-instances` | 启动/查询流程实例 |
| 任务 | `/flowable-rest/service/runtime/tasks` | 待办/完成任务 |
| 历史 | `/flowable-rest/service/history/process-instances` | 历史数据查询 |
| DMN | `/flowable-rest/service/dmn/repository/decision-definitions` | 决策表管理 |

## 示例流程

请假审批流程（`leave-approval`）：

- 员工填写请假申请 → ≤3天由直线主管审批，>3天由部门经理审批 → 更新记录
- 自动部署到 Flowable 引擎，启动后在 API 中可查看

## 部署

```bash
# 构建
cd flowable-work && mvn clean package -DskipTests

# Docker 启动全部服务
cd .. && docker compose up -d --build
```
