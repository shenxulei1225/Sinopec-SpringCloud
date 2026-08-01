-- 视图配置表
CREATE TABLE `system_view_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key` varchar(50) NOT NULL COMMENT '视图唯一标识',
  `module` varchar(20) NOT NULL COMMENT '业务模块',
  `type` varchar(20) NOT NULL COMMENT '视图类型',
  `label` varchar(100) NOT NULL COMMENT '显示名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `data_config` json DEFAULT NULL COMMENT '数据接口配置',
  `layout_config` json DEFAULT NULL COMMENT '布局配置',
  `api_config` json DEFAULT NULL COMMENT 'API配置',
  `layouts` json NOT NULL COMMENT '布局模板数组',
  `status` tinyint DEFAULT 1 COMMENT '状态:0禁用1启用',
  `sort` int DEFAULT 0 COMMENT '排序',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint DEFAULT 0 NOT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`key`),
  KEY `idx_module_type` (`module`, `type`),
  KEY `idx_status_sort` (`status`, `sort`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='视图配置表';

-- 初始化设施分类视图配置
INSERT INTO `system_view_config` (`key`, `module`, `type`, `label`, `icon`, `data_config`, `api_config`, `layouts`, `sort`) VALUES
('facility-category', 'facility', 'tree', '设施分类', 'folder', '{"url": "/dynamicbusiness/category/tree", "method": "GET", "params": {"categoryTypeCode": "FACILITY"}}', '{"dataEndpoint": {"url": "/dynamicbusiness/category/tree", "method": "GET"}, "createEndpoint": {"url": "/dynamicbusiness/category/create", "method": "POST"}, "updateEndpoint": {"url": "/dynamicbusiness/category/update", "method": "PUT"}, "deleteEndpoint": {"url": "/dynamicbusiness/category/delete", "method": "DELETE"}, "dragEndpoint": {"url": "/dynamicbusiness/category/drag", "method": "POST"}}', '[{"id": "default", "name": "默认布局", "isSystem": true, "schema": {"type": "tree"}}]', 1);
