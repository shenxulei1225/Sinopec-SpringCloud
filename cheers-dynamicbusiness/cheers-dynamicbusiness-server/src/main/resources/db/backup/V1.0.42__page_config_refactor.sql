-- =====================================================
-- 页面配置重构：配置与菜单解耦
-- 版本: V1.0.42
-- 描述: 移除 dynamic_page_config.menu_id,新增 dynamic_page_config.config_code 和 dynamic_menu.page_config_id
-- =====================================================

-- =====================================================
-- 1. 为 dynamic_page_config 表添加 config_code 字段
-- =====================================================
ALTER TABLE dynamic_page_config 
ADD COLUMN IF NOT EXISTS config_code VARCHAR(50);

COMMENT ON COLUMN dynamic_page_config.config_code IS '配置代码（唯一标识）';

-- 为现有数据生成 config_code（格式：page_config_{id}）
UPDATE dynamic_page_config 
SET config_code = 'page_config_' || id::text
WHERE config_code IS NULL;

-- 设置 config_code 为 NOT NULL
ALTER TABLE dynamic_page_config 
ALTER COLUMN config_code SET NOT NULL;

-- 创建唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_config_code ON dynamic_page_config(config_code);

-- =====================================================
-- 2. 为 dynamic_menu 表添加 page_config_id 字段
-- =====================================================
ALTER TABLE dynamic_menu 
ADD COLUMN IF NOT EXISTS page_config_id BIGINT;

COMMENT ON COLUMN dynamic_menu.page_config_id IS '页面配置ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_page_config_id ON dynamic_menu(page_config_id);

-- =====================================================
-- 3. 数据迁移：将 menu_id 关系转换为 page_config_id
-- =====================================================
-- 更新 dynamic_menu 表,将 dynamic_page_config.menu_id 的关系迁移到 dynamic_menu.page_config_id
UPDATE dynamic_menu m
SET page_config_id = pc.id
FROM dynamic_page_config pc
WHERE m.id = pc.menu_id
  AND m.deleted = false
  AND pc.deleted = false;

-- =====================================================
-- 4. 移除 dynamic_page_config.menu_id 字段（可选,建议先保留一段时间）
-- =====================================================
-- 注意：为了兼容性,暂时保留 menu_id 字段
-- 等所有代码迁移完成后,再执行以下语句移除：
-- ALTER TABLE dynamic_page_config DROP COLUMN menu_id;

-- =====================================================
-- 脚本执行完成
-- =====================================================
















