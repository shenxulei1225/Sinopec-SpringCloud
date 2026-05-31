-- 为 dynamic_business_type 表新增 type_level 字段,用于区分系统级与用户级业务类型
-- 默认：USER
--
-- 迁移策略：
-- 1) 新增 type_level 字段,默认 USER
-- 2) 对历史数据进行尽力迁移：如果 description 中残留 typeLevel=SYSTEM 的 JSON 片段,则标记为 SYSTEM

ALTER TABLE dynamic_business_type
    ADD COLUMN IF NOT EXISTS type_level VARCHAR(16) NOT NULL DEFAULT 'USER';

COMMENT ON COLUMN dynamic_business_type.type_level IS '业务类型级别：SYSTEM=系统内置,USER=用户创建';

-- 兼容历史：如果 description 中曾存过 {"typeLevel":"SYSTEM"} 之类片段,则标记为 SYSTEM
UPDATE dynamic_business_type
SET type_level = 'SYSTEM'
WHERE type_level = 'USER'
  AND description IS NOT NULL
  AND description LIKE '%"typeLevel":"SYSTEM"%';

