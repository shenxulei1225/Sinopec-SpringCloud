-- 目录「模型管理」页签独立工作台布局挂载（与 data_layout_id 并列，互不共用）。
-- 栏行 / 栏间关系仍按 layoutId 存；目录注册编码仍写真实 entity_type.code，禁止伪编码。

ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS model_layout_id BIGINT;

COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.model_layout_id IS
    '该目录「模型管理」页签引用的工作台布局实例 id（非模版；可空，首次进入模型管理时 ensure）';
