-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.28
-- 日期: 2026-01-08
-- 描述: 将设备管理从 DEDICATED_STATIC 迁移到 DEDICATED_DYNAMIC
-- =====================================================

-- =====================================================
-- 第一步：更新 dynamic_business_type_config 配置
-- =====================================================

-- 更新设备管理配置：
-- 1. storage_type 从 DEDICATED_STATIC 改为 DEDICATED_DYNAMIC
-- 2. strategy_bean_name 设为 NULL（使用通用策略 DynamicTableStorageStrategy）
-- 3. 添加 physical_column_mapping 配置（保留现有物理列）
UPDATE dynamic_business_type_config 
SET 
    storage_type = 'DEDICATED_DYNAMIC',
    strategy_bean_name = NULL,
    physical_column_mapping = '{
        "code": {"column": "code", "type": "VARCHAR", "length": 100},
        "regionId": {"column": "region_id", "type": "BIGINT"},
        "manufacturer": {"column": "manufacturer", "type": "VARCHAR", "length": 200},
        "modelNumber": {"column": "model_number", "type": "VARCHAR", "length": 100},
        "serialNumber": {"column": "serial_number", "type": "VARCHAR", "length": 100},
        "purchaseDate": {"column": "purchase_date", "type": "DATE"},
        "warrantyExpiry": {"column": "warranty_expiry", "type": "DATE"},
        "healthScore": {"column": "health_score", "type": "DECIMAL", "precision": 5, "scale": 2},
        "lastMaintenanceTime": {"column": "last_maintenance_time", "type": "TIMESTAMP"},
        "equipmentStatus": {"column": "equipment_status", "type": "VARCHAR", "length": 50}
    }'::jsonb,
    update_time = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE business_type_code = 'equipment';

-- =====================================================
-- 第二步：注册设备表到 dynamic_dynamic_table
-- =====================================================

-- 注意：设备表名是 'biz_equipment'
-- 使用 biz_ 前缀与其他业务数据表保持一致
INSERT INTO dynamic_dynamic_table (
    model_id,
    business_type_code,
    table_name,
    table_comment,
    column_config,
    status,
    version,
    creator,
    create_time,
    tenant_id
) 
SELECT 
    0,  -- model_id 设为 0,表示业务类型级别的表（不是特定 Model 的表）
    'equipment',
    'biz_equipment',
    '设备管理专用表（从 DEDICATED_STATIC 迁移到 DEDICATED_DYNAMIC）',
    '{
        "code": {"column": "code", "type": "VARCHAR", "length": 100, "nullable": false},
        "region_id": {"column": "region_id", "type": "BIGINT", "nullable": true},
        "manufacturer": {"column": "manufacturer", "type": "VARCHAR", "length": 200, "nullable": true},
        "model_number": {"column": "model_number", "type": "VARCHAR", "length": 100, "nullable": true},
        "serial_number": {"column": "serial_number", "type": "VARCHAR", "length": 100, "nullable": true},
        "purchase_date": {"column": "purchase_date", "type": "DATE", "nullable": true},
        "warranty_expiry": {"column": "warranty_expiry", "type": "DATE", "nullable": true},
        "health_score": {"column": "health_score", "type": "DECIMAL", "precision": 5, "scale": 2, "nullable": true},
        "last_maintenance_time": {"column": "last_maintenance_time", "type": "TIMESTAMP", "nullable": true},
        "equipment_status": {"column": "equipment_status", "type": "VARCHAR", "length": 50, "nullable": true}
    }'::jsonb,
    1,  -- status: 正常
    1,  -- version: 初始版本
    'system',
    CURRENT_TIMESTAMP,
    0   -- tenant_id: 系统级
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_dynamic_table WHERE table_name = 'biz_equipment'
);

-- =====================================================
-- 第三步：添加审计日志记录
-- =====================================================

INSERT INTO dynamic_dynamic_table_audit_log (
    dynamic_table_id,
    operation_type,
    operation_desc,
    before_config,
    after_config,
    executed_sql,
    execute_result,
    operation_time,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
)
SELECT 
    dt.id,
    'MIGRATE',
    '从 DEDICATED_STATIC 迁移到 DEDICATED_DYNAMIC',
    '{"storage_type": "DEDICATED_STATIC", "strategy_bean_name": "equipmentEntityStorageStrategy"}'::jsonb,
    '{"storage_type": "DEDICATED_DYNAMIC", "strategy_bean_name": null, "physical_column_mapping": "configured"}'::jsonb,
    'UPDATE dynamic_business_type_config SET storage_type = ''DEDICATED_DYNAMIC'', strategy_bean_name = NULL WHERE business_type_code = ''equipment''',
    'SUCCESS',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    0
FROM dynamic_dynamic_table dt
WHERE dt.table_name = 'biz_equipment';

-- =====================================================
-- 说明
-- =====================================================
-- 
-- 迁移要点：
-- 1. 表名：biz_equipment（使用 biz_ 前缀与其他业务表保持一致）
-- 2. 表结构不变：所有物理列保持不变
-- 3. 数据零迁移：只更新配置,不迁移数据
-- 4. 策略变更：从 EquipmentEntityStorageStrategy 切换到 DynamicTableStorageStrategy
-- 
-- 迁移后的行为：
-- - 设备 CRUD 操作由 DynamicTableStorageStrategy 处理
-- - 物理列字段（code, manufacturer 等）存储在物理列中
-- - 用户自定义扩展字段存储在 custom_fields JSONB 中
-- - 查询性能不变（物理列有 B-Tree 索引）
-- 
-- 回滚方法（如需要）：
-- UPDATE dynamic_business_type_config 
-- SET storage_type = 'DEDICATED_STATIC', 
--     strategy_bean_name = 'equipmentEntityStorageStrategy',
--     physical_column_mapping = NULL
-- WHERE business_type_code = 'equipment';
-- 
-- DELETE FROM dynamic_dynamic_table WHERE table_name = 'biz_equipment';
