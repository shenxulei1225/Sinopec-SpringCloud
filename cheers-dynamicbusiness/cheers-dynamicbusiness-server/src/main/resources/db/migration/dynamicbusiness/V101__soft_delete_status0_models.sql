-- 历史「删除公司规格 → 停用 status=0」残留：改为真正软删，释放名称占用并与删除口径对齐。
-- 不做启用/停用产品能力；status=0 不再作为删除的替代态。
UPDATE dynamic_model
SET deleted = true,
    update_time = NOW()
WHERE deleted = false
  AND status = 0;

-- 同步软删这些型号上的分类关联（主表 + 租户分表，避免残留指向已删型号）
UPDATE dynamic_model_category_relation r
SET deleted = true,
    update_time = NOW()
WHERE r.deleted = false
  AND EXISTS (
    SELECT 1
    FROM dynamic_model m
    WHERE m.id = r.model_id
      AND m.deleted = true
      AND m.status = 0
  );

UPDATE dynamic_model_category_relation_t1 r
SET deleted = true,
    update_time = NOW()
WHERE r.deleted = false
  AND EXISTS (
    SELECT 1
    FROM dynamic_model m
    WHERE m.id = r.model_id
      AND m.deleted = true
      AND m.status = 0
  );
