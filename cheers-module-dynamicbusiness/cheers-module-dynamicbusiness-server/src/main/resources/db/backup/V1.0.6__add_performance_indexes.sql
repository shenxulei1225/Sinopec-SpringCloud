-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.6
-- 日期: 2026-01-03
-- 描述: 添加性能优化索引
-- 目标: 
--   - 分类树查询 ≤500ms
--   - 字段查询 ≤300ms
--   - 实体树查询 ≤500ms
-- =====================================================

-- =====================================================
-- 1. 分类表 (dynamic_category) 索引优化
-- =====================================================

-- 业务类型编码索引（分类树查询的主要过滤条件）
CREATE INDEX IF NOT EXISTS idx_category_business_type_code 
    ON dynamic_category(business_type_code);

-- 父分类ID索引（树形结构查询）
CREATE INDEX IF NOT EXISTS idx_category_parent_id 
    ON dynamic_category(parent_id);

-- 状态索引（状态过滤）
CREATE INDEX IF NOT EXISTS idx_category_status 
    ON dynamic_category(status);

-- 复合索引：业务类型 + 父分类 + 排序（树形查询优化）
CREATE INDEX IF NOT EXISTS idx_category_tree_query 
    ON dynamic_category(business_type_code, parent_id, sort);

-- 复合索引：业务类型 + 状态（带状态过滤的树查询）
CREATE INDEX IF NOT EXISTS idx_category_business_status 
    ON dynamic_category(business_type_code, status);

-- 名称模糊搜索索引（PostgreSQL 支持 pg_trgm 扩展）
-- 注意：需要先启用 pg_trgm 扩展
-- CREATE EXTENSION IF NOT EXISTS pg_trgm;
-- CREATE INDEX IF NOT EXISTS idx_category_name_trgm 
--     ON dynamic_category USING gin (name gin_trgm_ops);

-- 租户ID索引（多租户查询）
CREATE INDEX IF NOT EXISTS idx_category_tenant_id 
    ON dynamic_category(tenant_id);

-- 删除标记索引（软删除过滤）
CREATE INDEX IF NOT EXISTS idx_category_deleted 
    ON dynamic_category(deleted);

-- 复合索引：业务类型 + 删除标记（常用查询组合）
CREATE INDEX IF NOT EXISTS idx_category_business_deleted 
    ON dynamic_category(business_type_code, deleted);

-- =====================================================
-- 2. 字段表 (dynamic_field) 索引优化
-- =====================================================

-- 字段编码索引（唯一性查询）
CREATE INDEX IF NOT EXISTS idx_field_code 
    ON dynamic_field(code);

-- 字段名称索引（名称查询和唯一性验证）
CREATE INDEX IF NOT EXISTS idx_field_name 
    ON dynamic_field(name);

-- 字段类型索引（按类型过滤）
CREATE INDEX IF NOT EXISTS idx_field_type 
    ON dynamic_field(type);

-- 字段来源索引（系统字段/用户字段过滤）
CREATE INDEX IF NOT EXISTS idx_field_source 
    ON dynamic_field(source);

-- 状态索引（状态过滤）
CREATE INDEX IF NOT EXISTS idx_field_status 
    ON dynamic_field(status);

-- 租户ID索引（多租户查询）
CREATE INDEX IF NOT EXISTS idx_field_tenant_id 
    ON dynamic_field(tenant_id);

-- 删除标记索引（软删除过滤）
CREATE INDEX IF NOT EXISTS idx_field_deleted 
    ON dynamic_field(deleted);

-- 复合索引：类型 + 状态 + 删除标记（常用查询组合）
CREATE INDEX IF NOT EXISTS idx_field_type_status_deleted 
    ON dynamic_field(type, status, deleted);

-- 复合索引：租户 + 名称 + 删除标记（名称唯一性验证）
CREATE INDEX IF NOT EXISTS idx_field_tenant_name_deleted 
    ON dynamic_field(tenant_id, name, deleted);

-- 创建时间索引（排序优化）
CREATE INDEX IF NOT EXISTS idx_field_create_time 
    ON dynamic_field(create_time);

-- =====================================================
-- 3. 实体表 (dynamic_entity) 索引优化
-- =====================================================

-- 业务类型编码索引
CREATE INDEX IF NOT EXISTS idx_entity_business_type_code 
    ON dynamic_entity(business_type_code);

-- 模型ID索引（实体树查询的主要过滤条件）
CREATE INDEX IF NOT EXISTS idx_entity_model_id 
    ON dynamic_entity(model_id);

-- 状态索引
CREATE INDEX IF NOT EXISTS idx_entity_status 
    ON dynamic_entity(status);

-- 租户ID索引
CREATE INDEX IF NOT EXISTS idx_entity_tenant_id 
    ON dynamic_entity(tenant_id);

-- 删除标记索引
CREATE INDEX IF NOT EXISTS idx_entity_deleted 
    ON dynamic_entity(deleted);

-- 复合索引：模型ID + 删除标记（实体树查询优化）
CREATE INDEX IF NOT EXISTS idx_entity_model_deleted 
    ON dynamic_entity(model_id, deleted);

-- 复合索引：业务类型 + 模型ID + 状态 + 删除标记（常用查询组合）
CREATE INDEX IF NOT EXISTS idx_entity_business_model_status_deleted 
    ON dynamic_entity(business_type_code, model_id, status, deleted);

-- 名称索引（名称搜索）
CREATE INDEX IF NOT EXISTS idx_entity_name 
    ON dynamic_entity(name);

-- 创建时间索引（排序优化）
CREATE INDEX IF NOT EXISTS idx_entity_create_time 
    ON dynamic_entity(create_time);

-- 自定义字段全文搜索索引（PostgreSQL GIN 索引）
-- 注意：custom_fields 是 VARCHAR/TEXT 类型,存储 JSON 字符串
-- 对于 PostgreSQL,可以使用 GIN 索引加速 LIKE 查询
-- CREATE INDEX IF NOT EXISTS idx_entity_custom_fields_gin 
--     ON dynamic_entity USING gin (to_tsvector('simple', custom_fields));

-- =====================================================
-- 4. 实体分类关联表 (dynamic_entity_category_relation) 索引优化
-- =====================================================

-- 实体ID索引（按实体查询分类）
CREATE INDEX IF NOT EXISTS idx_entity_category_entity_id 
    ON dynamic_entity_category_relation(entity_id);

-- 分类ID索引（按分类查询实体）
CREATE INDEX IF NOT EXISTS idx_entity_category_category_id 
    ON dynamic_entity_category_relation(category_id);

-- 复合索引：实体ID + 分类ID（关联查询优化）
CREATE INDEX IF NOT EXISTS idx_entity_category_entity_category 
    ON dynamic_entity_category_relation(entity_id, category_id);

-- 删除标记索引
CREATE INDEX IF NOT EXISTS idx_entity_category_deleted 
    ON dynamic_entity_category_relation(deleted);

-- =====================================================
-- 5. 模型表 (dynamic_model) 索引优化
-- =====================================================

-- 模型编码索引
CREATE INDEX IF NOT EXISTS idx_model_code 
    ON dynamic_model(code);

-- 业务类型编码索引
CREATE INDEX IF NOT EXISTS idx_model_business_type_code 
    ON dynamic_model(business_type_code);

-- 状态索引
CREATE INDEX IF NOT EXISTS idx_model_status 
    ON dynamic_model(status);

-- 删除标记索引
CREATE INDEX IF NOT EXISTS idx_model_deleted 
    ON dynamic_model(deleted);

-- 复合索引：业务类型 + 状态 + 删除标记
CREATE INDEX IF NOT EXISTS idx_model_business_status_deleted 
    ON dynamic_model(business_type_code, status, deleted);

-- =====================================================
-- 6. 模型字段分配表 (dynamic_model_field_assignment) 索引优化
-- =====================================================

-- 模型ID索引
CREATE INDEX IF NOT EXISTS idx_model_field_model_id 
    ON dynamic_model_field_assignment(model_id);

-- 字段ID索引
CREATE INDEX IF NOT EXISTS idx_model_field_field_id 
    ON dynamic_model_field_assignment(field_id);

-- 复合索引：模型ID + 字段ID
CREATE INDEX IF NOT EXISTS idx_model_field_model_field 
    ON dynamic_model_field_assignment(model_id, field_id);

-- 删除标记索引
CREATE INDEX IF NOT EXISTS idx_model_field_deleted 
    ON dynamic_model_field_assignment(deleted);

-- 7. 单位表 (dynamic_unit) 索引优化（如果存在）
--    注意：当前环境可能尚未创建 dynamic_unit 表,这里增加存在性判断,避免 Flyway 执行失败
-- =====================================================

DO $$
BEGIN
    -- 仅当当前 schema 下存在 dynamic_unit 表时才创建索引
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'dynamic_unit'
    ) THEN
        -- 单位编码索引
        CREATE INDEX IF NOT EXISTS idx_unit_code
            ON dynamic_unit(code);

        -- 单位名称索引
        CREATE INDEX IF NOT EXISTS idx_unit_name
            ON dynamic_unit(name);

        -- 单位类型索引
        CREATE INDEX IF NOT EXISTS idx_unit_type
            ON dynamic_unit(type);

        -- 状态索引
        CREATE INDEX IF NOT EXISTS idx_unit_status
            ON dynamic_unit(status);

        -- 删除标记索引
        CREATE INDEX IF NOT EXISTS idx_unit_deleted
            ON dynamic_unit(deleted);
    END IF;
END
$$;

-- =====================================================
-- 索引注释
-- =====================================================

COMMENT ON INDEX idx_category_business_type_code IS '分类业务类型索引,用于分类树查询';
COMMENT ON INDEX idx_category_parent_id IS '分类父ID索引,用于树形结构查询';
COMMENT ON INDEX idx_category_tree_query IS '分类树查询复合索引';
COMMENT ON INDEX idx_field_type_status_deleted IS '字段类型状态复合索引';
COMMENT ON INDEX idx_entity_model_deleted IS '实体模型删除标记复合索引';
COMMENT ON INDEX idx_entity_business_model_status_deleted IS '实体常用查询复合索引';
