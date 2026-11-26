-- 神墩三路工艺说明：吊装口尺寸统一 6.5×1.2 米。说明没有写高度。
-- 来源：项目标段1/解压/神墩三路/给排水、消防/RGE541511-DDS.41-1神墩三路工艺设计说明.dwg

UPDATE dynamicbusiness.ent_constructure_t123
SET f_54979aad90734c22bf04158d8b53bc8d = 6.5,
    f_f2fd5d318e6f492288b9dc713a2092b0 = 1.2,
    updater = 'corridor-ss-hoist-size',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND model_id = 2568
  AND name LIKE '神墩三路%'
  AND COALESCE(deleted, false) = false;

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_number, creator, updater, tenant_id
)
SELECT e.id, 2568, v.code, v.num, 'corridor-ss-hoist-size', 'corridor-ss-hoist-size', 123
FROM dynamicbusiness.ent_constructure_t123 e
CROSS JOIN (VALUES
    ('F-54979aad90734c22bf04158d8b53bc8d', 6.5),
    ('F-f2fd5d318e6f492288b9dc713a2092b0', 1.2)
) AS v(code, num)
WHERE e.tenant_id = 123
  AND e.model_id = 2568
  AND e.name LIKE '神墩三路%'
  AND COALESCE(e.deleted, false) = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id AND i.field_code = v.code AND i.tenant_id = 123 AND i.deleted = false
  );
