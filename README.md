# Flowable 工作流平台

基于 **Flowable 8 + Spring Boot 4 + JDK 25** 的企业级工作流管理平台。

## 快速启动

```bash
docker compose up -d
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 流程设计器 | http://localhost:58081 | BPMN/CMMN/DMN 建模 |
| 流程引擎 Swagger | http://localhost:58080/swagger-ui.html | REST API 文档 |
| 用户中心 | http://localhost:58083 | 用户/部门/角色/审批管理 |
| 用户中心 Swagger | http://localhost:58082/swagger-ui.html | REST API 文档 |
| Nacos | http://localhost:8848/nacos | nacos/nacos |

## 默认账号

所有用户密码均为 `123456`

| 用户名 | 角色 | 权限 |
|--------|------|------|
| admin | 管理员 | 全部菜单 |
| zhangsan | 直线主管 | 用户管理、审批管理 |

## 开发

```bash
# 流程引擎
cd flowable-work && mvn spring-boot:run

# 流程设计器
cd flowable-work-ui && npm run dev

# 用户中心后端
cd user-center && mvn spring-boot:run

# 用户中心前端
cd user-center-ui && npm run dev
```

## 项目结构

```
flowable-platform/
├── flowable-work/           # 流程引擎 Spring Boot
├── flowable-work-ui/        # 流程设计器 Vue + bpmn-js
├── user-center/             # 用户中心 Spring Boot + MyBatis-Plus
├── user-center-ui/          # 用户中心前端 Vue + Element Plus
├── mysql/init-scripts/      # 数据库初始化
└── docker-compose.yml       # 容器编排
```
