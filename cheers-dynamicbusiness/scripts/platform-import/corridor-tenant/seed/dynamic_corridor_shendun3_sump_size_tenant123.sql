-- 神墩三路工艺说明：综合管道舱、电力电信舱、电力专用舱的集水坑都是 2.0×1.2×1.5 米。
-- 来源：项目标段1/解压/神墩三路/给排水、消防/RGE541511-DDS.41-1神墩三路工艺设计说明.dwg
-- 账上原长宽高不是这个尺寸。三种舱尺寸相同，所以这一路每条集水坑都写成这一组数。

UPDATE dynamicbusiness.ent_constructure_t123
SET f_54979aad90734c22bf04158d8b53bc8d = 2.0,
    f_f2fd5d318e6f492288b9dc713a2092b0 = 1.2,
    f_b54dfb3b3ffe47dcac92c0ab127c338d = 1.5,
    updater = 'corridor-ss-sump-size',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND model_id = 2564
  AND name LIKE '神墩三路%'
  AND COALESCE(deleted, false) = false;

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_number, creator, updater, tenant_id
)
SELECT e.id, 2564, v.code, v.num, 'corridor-ss-sump-size', 'corridor-ss-sump-size', 123
FROM dynamicbusiness.ent_constructure_t123 e
CROSS JOIN (VALUES
    ('F-54979aad90734c22bf04158d8b53bc8d', 2.0),
    ('F-f2fd5d318e6f492288b9dc713a2092b0', 1.2),
    ('F-b54dfb3b3ffe47dcac92c0ab127c338d', 1.5)
) AS v(code, num)
WHERE e.tenant_id = 123
  AND e.model_id = 2564
  AND e.name LIKE '神墩三路%'
  AND COALESCE(e.deleted, false) = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id AND i.field_code = v.code AND i.tenant_id = 123 AND i.deleted = false
  );
