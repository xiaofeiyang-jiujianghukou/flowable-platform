# CLAUDE.md — Flowable 工作流平台

## 项目概览

企业级工作流管理平台，4 个独立子项目 + Docker Compose 容器编排。

## 技术栈

- Java 25, Spring Boot 4.0.2, Spring Cloud 2025.1.1, Spring Cloud Alibaba 2025.1.0.0
- Flowable 8.0.0 (Process + DMN + CMMN), MyBatis-Plus 3.5.16 (boot4 starter)
- MySQL 8.0 (flowable 库 + user_center 库), Redis 7 (Token 会话), Nacos 2.5.1 (注册发现)
- Vue 3 + Element Plus 2.13 + bpmn-js/cmnn-js/dmn-js
- Docker Compose 容器化，nginx 反向代理（resolver 127.0.0.11 动态 DNS）

## 子项目

### flowable-work（流程引擎后端，端口 8080/58080）
- `ProcessController` — 流程定义列表(`/api/process/definitions`)、按 key 查询(`/definitions/key/{key}`)、任务提取(`/definitions/{id}/tasks`)、流程实例 CRUD
- `ModelController` — BPMN/CMMN/DMN 模型 CRUD + 部署。部署时 `fixBpmnXml()` 自动注入 process key/name/isExecutable=true，解决 bpmn-js 默认 id=Process_1 问题。删除模型级联删除部署
- `TaskController` / `HistoryController` — 任务与历史查询
- Flowable 8.0.0 补丁：`org.flowable.engine.configurator.ProcessEngineConfigurator` 等接口类（Flowable 8 移除了但 spring-boot-autoconfigure 仍引用）
- `application.yml` 排除 `CmmnEngineAutoConfiguration` 和 `DmnEngineAutoConfiguration`（手动配置 `CmmnDmnEngineConfig.java`）
- Swagger: `http://localhost:58080/swagger-ui.html`

### flowable-work-ui（流程设计器前端，端口 58081）
- `ModelList.vue` — 三 Tab 切换 BPMN/CMMN/DMN 模型列表
- `Designer.vue` — 统一设计器，根据模型 type 动态 import bpmn-js/cmnn-js/dmn-js。空模型用 `createDiagram()`，DMN 确保含 DMNDI 元素以显示 DRD 视图
- CSS 导入：`bpmn-js/dist/assets/*.css`, `dmn-js/dist/assets/*.css` 等
- nginx 代理 `/api` → `flowable-server:8080`

### user-center（用户中心后端，端口 8081/58082）
- 认证：`AuthController` — login/logout/session，BCrypt 密码，Redis 存储 SessionUser（32 位 Token），2h 滑动窗口
- `AuthInterceptor` — 拦截 `/api/**`（排除 login），验证 Bearer Token，续约逻辑
- `WebConfig` — 注册拦截器 + CORS（替代旧 CorsConfig.java）
- 用户/部门/角色/菜单 CRUD：`UserController`, `DepartmentController`, `RoleController`, `MenuController`
- `ProcessConfigController` — 流程配置（选择已部署流程 → 提取任务定义 → 配置处理人 → 发布）
- `ApprovalController` — 发起审批/待审批/已审批，通过 `FlowableClient` (Feign) 调用 flowable-server
- `DataInitializer` — 启动时 BCrypt 加密所有空密码为 "123456"
- 数据库：`user_center` 库，表 sys_user/sys_department/sys_role/sys_user_role/sys_menu/sys_role_menu/process_config
- 字符集统一 `utf8mb4_general_ci`（flowable 库 44 张表同字符集，22 张 ACT_ID_/ACT_GE_ 表保持 utf8mb3_bin 不可改）

### user-center-ui（用户中心前端，端口 58083）
- `utils/auth.ts` — `currentUser` (reactive ref) + `fetchAuth()` + `login()`/`logout()`
- `LoginView.vue` — 登录页
- `UserList.vue` / `DepartmentList.vue` / `RoleList.vue` — CRUD 页面，均使用 `fetchAuth`
- `MenuManage.vue` — 菜单列表 + 角色授权（el-tree 勾选）
- `ProcessConfig.vue` — 流程配置，打开新建弹窗时实时刷新定义列表
- `Approval.vue` — 发起/待审批/已审批
- `DeptTreeSelect.vue` + `TreeNode.vue` — 自定义部门树选择器，`manualExpanded` 持久化展开状态，只展开已选节点祖先链路，点击空白自动关闭
- 导航栏：`currentUser.menus` 动态渲染（响应式），登录/登出无需强刷

## 数据库

### flowable 库
- Flowable 引擎自动建表，含 ACT_RE_/ACT_RU_/ACT_HI_/ACT_ID_/ACT_DE_/ACT_CMMN_/ACT_DMN_/FLW_ 等表
- 现有表 66 张（44 utf8mb4_general_ci + 22 utf8mb3_bin）

### user_center 库
- 7 张业务表（全部 utf8mb4_general_ci）
- 初始化脚本：`mysql/init-scripts/02-user_center.sql`

## 关键注意事项

1. **Flowable 8.0.0 兼容性**：需补丁类 `ProcessEngineConfigurator`/`CmmnEngineConfigurator`/`DmnEngineConfigurator` + 对应 Spring 实现
2. **BPMN XML 修正**：bpmn-js 生成 `<bpmn:process id="Process_1">`，部署时 `fixBpmnXml()` 替换为模型 key，注入 name，设 isExecutable=true
3. **nginx 502**：容器重启后 IP 变化，nginx 配置必须加 `resolver 127.0.0.11; set $backend ...; proxy_pass http://$backend;` 动态 DNS
4. **排序规则**：所有业务表统一 `utf8mb4_general_ci`，Flowable 的 `utf8mb3_bin` 表不可改（ID 匹配需要二进制排序）
5. **DMN DRD 视图**：空模板必须含 `dmndi:DMNDI` 元素，否则直接跳转决策表编辑器
6. **前端响应式**：`currentUser` 必须用 `ref()` 而非 `localStorage.getItem()`，否则切换账号不刷新
7. **菜单中文乱码**：`docker exec` heredoc 会丢失 UTF-8，需 `docker cp` 文件方式导入

## 容器端口映射

| 容器 | 内部 | 外部 |
|------|------|------|
| flowable-mysql | 3306 | 53306 |
| redis | 6379 | 6380 |
| nacos | 8848 | 8848 |
| flowable-server | 8080 | 58080 |
| flowable-ui | 80 | 58081 |
| user-center | 8081 | 58082 |
| user-center-ui | 80 | 58083 |
