USE user_center;

/*
 Navicat Premium Dump SQL

 Source Server         : flowable
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : 127.0.0.1:53306
 Source Schema         : user_center

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 30/04/2026 17:21:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for process_config
-- ----------------------------
DROP TABLE IF EXISTS `process_config`;
CREATE TABLE `process_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置名称',
  `process_definition_key` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程定义Key',
  `process_definition_id` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '流程定义ID',
  `process_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程定义名称',
  `task_assignees` json DEFAULT NULL COMMENT '任务处理人配置',
  `published` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已发布',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_definition_key` (`process_definition_key`),
  KEY `idx_published` (`published`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='流程配置表';

-- ----------------------------
-- Records of process_config
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_department
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT '0' COMMENT '上级部门ID',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- ----------------------------
-- Records of sys_department
-- ----------------------------
BEGIN;
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (1, '总经办', 7, 1, '2026-04-30 06:14:03', NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (2, '技术部', 7, 2, '2026-04-30 06:14:03', NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (3, '人事部', 7, 3, '2026-04-30 06:14:03', NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (4, '财务部', 7, 4, '2026-04-30 06:14:03', NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (5, '业务前端组', 2, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (6, '业务架构组', 2, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (7, '唯寻教育科技有限公司', 0, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (8, 'HR组', 3, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (9, 'HRBP组', 3, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (10, '财务分析组', 4, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (11, '财务测算组', 4, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (12, '总助', 1, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (13, '宣传部', 1, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (14, '销售部', 7, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (15, '销售一部', 14, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (16, '销售二部', 14, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (17, '销售一组', 15, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (18, '销售一组', 16, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (19, '销售二组', 16, 0, NULL, NULL);
INSERT INTO `sys_department` (`id`, `name`, `parent_id`, `sort_order`, `created_at`, `updated_at`) VALUES (20, '销售二组', 15, 0, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `path` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `icon` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT '0',
  `permission_code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sort_order` int DEFAULT '0',
  `type` varchar(16) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'menu',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (1, '用户管理', '/', 'User', 0, 'user:list', 1, 'menu', '2026-04-30 08:53:00');
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (2, '部门管理', '/departments', 'OfficeBuilding', 0, 'dept:list', 2, 'menu', '2026-04-30 08:53:00');
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (3, '角色管理', '/roles', 'Setting', 0, 'role:list', 3, 'menu', '2026-04-30 08:53:00');
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (4, '流程配置', '/process-config', 'Operation', 0, 'process:config', 4, 'menu', '2026-04-30 08:53:00');
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (5, '审批管理', '/approval', 'Checked', 0, 'approval:list', 5, 'menu', '2026-04-30 08:53:00');
INSERT INTO `sys_menu` (`id`, `name`, `path`, `icon`, `parent_id`, `permission_code`, `sort_order`, `type`, `created_at`) VALUES (6, '菜单管理', '/menus', 'Menu', 0, 'menu:manage', 6, 'menu', '2026-04-30 08:53:00');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `description` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`) VALUES (1, '直线主管', 'lineManager', '≤3天请假审批人', '2026-04-30 06:14:03');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`) VALUES (2, '部门经理', 'deptManager', '>3天请假审批人', '2026-04-30 06:14:03');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`) VALUES (3, 'HR', 'hr', '人事管理', '2026-04-30 06:14:03');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`) VALUES (4, '员工', 'employee', '普通员工', '2026-04-30 06:14:03');
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `created_at`) VALUES (5, '超级管理员', 'admin', '超级管理员', '2026-04-30 08:36:05');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `menu_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_code`,`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (13, 'admin', 1);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (9, 'admin', 2);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (12, 'admin', 3);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (11, 'admin', 4);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (8, 'admin', 5);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (10, 'admin', 6);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (17, 'deptManager', 1);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (18, 'deptManager', 5);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (22, 'employee', 1);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (23, 'employee', 5);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (19, 'hr', 1);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (20, 'hr', 2);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (21, 'hr', 5);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (15, 'lineManager', 1);
INSERT INTO `sys_role_menu` (`id`, `role_code`, `menu_id`) VALUES (16, 'lineManager', 5);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `department_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `email` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话',
  `password` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'BCrypt加密密码',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '启用状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (1, 'admin', '管理员', 1, 'admin@company.com', '13800000000', '$2a$10$mNGmLCvaTqWivqWxCwy6Z.KJPo6Hj4CXr7CoTum4QMIH8wRQl3fUm', 1, '2026-04-30 06:14:04', '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (2, 'zhangsan', '张三', 2, 'zhangsan@company.com', '13800000001', '$2a$10$BUTqAeGBy79phjBz13RA4O9OqonwjVgBYQI1F55X1s/K34XpIuZNy', 1, '2026-04-30 06:14:04', '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (3, 'lisi', '李四', 8, 'lisi@company.com', '13800000002', '$2a$10$3r885EloqJBznApredfYdeFXuE0ZNWICQMbVyGub/jj8bFUGlcpJK', 1, '2026-04-30 06:14:04', '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (4, 'wangwu', '王五', 4, 'wangwu@company.com', '13800000003', '$2a$10$nc79BUq19.s1HyxA39wFheMyW0nAkQrCmMQcMQfxrMzjKyr25jg5.', 1, '2026-04-30 06:14:04', '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (5, 'zhaoliu', '赵六', 2, 'zhaoliu@company.com', '13800000004', '$2a$10$RhjhRuNECc5v4MdjkPBg4.qjyz.nM8HV45LGtzo8/HaRcbaxf0ZDi', 1, '2026-04-30 06:14:04', '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (6, 'maqi', '马七', 16, NULL, NULL, '$2a$10$erX1GIoIivNZ1tuAxvwFgu8HElDSSi4aqn4RK9v4M9etCSBm5beue', 1, NULL, '2026-04-30 08:06:39');
INSERT INTO `sys_user` (`id`, `username`, `name`, `department_id`, `email`, `phone`, `password`, `enabled`, `created_at`, `updated_at`) VALUES (7, 'wangba', '王八', 19, NULL, NULL, '$2a$10$MAnYqzcCmdVpafSfPKjvcupoJDBajZekpXdbdACMBZeHXno5SKo0y', 1, NULL, '2026-04-30 08:06:39');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_code`) VALUES (5, 1, 'admin');
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_code`) VALUES (1, 2, 'lineManager');
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_code`) VALUES (2, 3, 'deptManager');
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_code`) VALUES (3, 4, 'hr');
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_code`) VALUES (4, 5, 'employee');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
