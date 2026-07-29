-- 分类种类：实体与分类挂靠是单归属还是多归属（与 ADVANCED/SIMPLE 正交）
ALTER TABLE dynamicbusiness.dynamic_category_type
    ADD COLUMN IF NOT EXISTS entity_association_mode character varying(16) NOT NULL DEFAULT 'MULTI';

COMMENT ON COLUMN dynamicbusiness.dynamic_category_type.entity_association_mode IS
    '实体-分类挂靠：SINGLE=同一实体在本种类树上只能挂一个节点（换挂）；MULTI=可挂多个（加挂）。默认 MULTI。';

-- 运营区域：站场所属区域为单选心智 → 单归属
UPDATE dynamicbusiness.dynamic_category_type
SET entity_association_mode = 'SINGLE'
WHERE deleted = false
  AND category_type_code = 'region';
