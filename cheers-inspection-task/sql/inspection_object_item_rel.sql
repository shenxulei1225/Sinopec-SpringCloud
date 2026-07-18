-- ===========================================
-- 巡检对象-检查项关联表
-- ===========================================

CREATE TABLE IF NOT EXISTS `inspection_object_item_rel` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `object_id` bigint NOT NULL COMMENT '巡检对象ID',
    `item_id` bigint NOT NULL COMMENT '检查项ID',
    `required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必检',
    `sort_no` int NOT NULL DEFAULT '0' COMMENT '排序号',
    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_object_id` (`object_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='巡检对象-检查项关联表';
