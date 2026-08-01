-- 数据类型：型号工作台模式（多型号 / 单型号）
-- SINGLE：用户侧默认不选型号；创建时自动使用该类型唯一（或编码匹配）型号。

ALTER TABLE dynamic_entity_type
    ADD COLUMN IF NOT EXISTS model_workbench_mode VARCHAR(16) NOT NULL DEFAULT 'MULTI';

COMMENT ON COLUMN dynamic_entity_type.model_workbench_mode IS
    '型号工作台模式：MULTI=多型号（默认）；SINGLE=单型号（分类+实体，创建不选手动选型号）';

UPDATE dynamic_entity_type
SET model_workbench_mode = 'SINGLE'
WHERE deleted = false
  AND code = 'inspection_item'
  AND (model_workbench_mode IS NULL OR model_workbench_mode = 'MULTI');
