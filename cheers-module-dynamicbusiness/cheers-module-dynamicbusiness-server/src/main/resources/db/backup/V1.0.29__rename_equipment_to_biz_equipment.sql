-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.29
-- 日期: 2026-01-08
-- 描述: 将设备表从 equipment 重命名为 biz_equipment,统一命名规范
-- =====================================================

-- =====================================================
-- 第一步：重命名表
-- =====================================================

-- 重命名设备表
ALTER TABLE equipment RENAME TO biz_equipment;

-- 重命名序列（如果存在）
ALTER SEQUENCE IF EXISTS equipment_seq RENAME TO biz_equipment_seq;

-- =====================================================
-- 第二步：更新索引名称
-- =====================================================

-- 重命名索引
ALTER INDEX IF EXISTS idx_equipment_code RENAME TO idx_biz_equipment_code;
ALTER INDEX IF EXISTS idx_equipment_model RENAME TO idx_biz_equipment_model;
ALTER INDEX IF EXISTS idx_equipment_parent RENAME TO idx_biz_equipment_parent;
ALTER INDEX IF EXISTS idx_equipment_status RENAME TO idx_biz_equipment_status;
ALTER INDEX IF EXISTS idx_equipment_tenant_deleted RENAME TO idx_biz_equipment_tenant_deleted;
ALTER INDEX IF EXISTS idx_equipment_region RENAME TO idx_biz_equipment_region;
ALTER INDEX IF EXISTS idx_equipment_health RENAME TO idx_biz_equipment_health;
ALTER INDEX IF EXISTS idx_equipment_equipment_status RENAME TO idx_biz_equipment_equipment_status;
ALTER INDEX IF EXISTS idx_equipment_custom_fields RENAME TO idx_biz_equipment_custom_fields;
ALTER INDEX IF EXISTS uk_equipment_code RENAME TO uk_biz_equipment_code;

-- =====================================================
-- 第三步：更新约束名称
-- =====================================================

-- 重命名约束（PostgreSQL 语法）
ALTER TABLE biz_equipment RENAME CONSTRAINT chk_equipment_health_score TO chk_biz_equipment_health_score;
ALTER TABLE biz_equipment RENAME CONSTRAINT chk_equipment_status TO chk_biz_equipment_status;

-- =====================================================
-- 第四步：更新 dynamic_business_type_config 配置
-- =====================================================

-- 更新设备管理配置中的表名
UPDATE dynamic_business_type_config 
SET 
    dedicated_table_name = 'biz_equipment',
    update_time = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE business_type_code = 'equipment';

-- =====================================================
-- 第五步：更新 dynamic_dynamic_table 配置
-- =====================================================

-- 更新动态表注册中的表名
UPDATE dynamic_dynamic_table 
SET 
    table_name = 'biz_equipment',
    table_comment = '设备管理专用表（已重命名为 biz_equipment）',
    update_time = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE table_name = 'equipment';

-- =====================================================
-- 第六步：更新表注释
-- =====================================================

COMMENT ON TABLE biz_equipment IS '设备管理专用表（业务数据表,使用 biz_ 前缀）';

-- =====================================================
-- 说明
-- =====================================================
-- 
-- 重命名原因：
-- 1. 统一命名规范：业务数据表使用 biz_ 前缀
-- 2. 与其他业务表保持一致（如 biz_maintenance_task）
-- 3. 便于区分系统表（dynamic_）和业务表（biz_）
-- 
-- 影响范围：
-- 1. EquipmentDO.java 的 @TableName 注解需要更新
-- 2. EquipmentEntityStorageStrategy 中的表名引用需要更新
-- 3. 相关 SQL 脚本中的表名引用需要更新
-- 
-- 回滚方法（如需要）：
-- ALTER TABLE biz_equipment RENAME TO equipment;
-- ALTER SEQUENCE IF EXISTS biz_equipment_seq RENAME TO equipment_seq;
-- UPDATE dynamic_business_type_config SET dedicated_table_name = 'equipment' WHERE business_type_code = 'equipment';
-- UPDATE dynamic_dynamic_table SET table_name = 'equipment' WHERE table_name = 'biz_equipment';

