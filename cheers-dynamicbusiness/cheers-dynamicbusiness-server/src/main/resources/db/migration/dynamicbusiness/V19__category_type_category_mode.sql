-- 分类种类建立方式：SIMPLE（简单分类）/ ADVANCED（高级分类，节点可 1:1 绑实体）
ALTER TABLE dynamicbusiness.dynamic_category_type
    ADD COLUMN IF NOT EXISTS category_mode character varying(16) NOT NULL DEFAULT 'SIMPLE';

COMMENT ON COLUMN dynamicbusiness.dynamic_category_type.category_mode IS
    '分类建立方式：SIMPLE=简单分类（节点主要填名称）；ADVANCED=高级分类（分类即实体，新建节点可带模型与业务字段）';

-- 已知组织台账类种类标为高级分类（存量）
UPDATE dynamicbusiness.dynamic_category_type
SET category_mode = 'ADVANCED'
WHERE deleted = false
  AND category_type_code IN ('region', 'facility', 'zone');
