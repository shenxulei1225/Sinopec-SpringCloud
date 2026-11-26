-- 神墩三路标准横断面：管道仓正下方的净宽是 4000 毫米，断面净高是 3500 毫米。
-- 来源：项目标段1/解压/神墩三路/给排水、消防/3 综合管廊标准横断面.dwg
-- 电力电信混合仓 2500、电力专用仓 2800 已与账上一致，不改。

UPDATE dynamicbusiness.ent_zone_t123 c
SET custom_fields = COALESCE(c.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_width_m', 4.0, 'ut_height_m', 3.5),
    updater = 'corridor-ss-section',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_zone_t123 z
WHERE c.tenant_id = 123
  AND COALESCE(c.deleted, false) = false
  AND c.model_id = 2319
  AND z.id = c.parent_id
  AND z.parent_id = 4
  AND COALESCE(z.deleted, false) = false;

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_number, creator, updater, tenant_id
)
SELECT c.id, 2319, v.code, v.num, 'corridor-ss-section', 'corridor-ss-section', 123
FROM dynamicbusiness.ent_zone_t123 c
JOIN dynamicbusiness.ent_zone_t123 z ON z.id = c.parent_id
CROSS JOIN (VALUES ('ut_width_m', 4.0), ('ut_height_m', 3.5)) AS v(code, num)
WHERE c.tenant_id = 123
  AND COALESCE(c.deleted, false) = false
  AND c.model_id = 2319
  AND z.parent_id = 4
  AND COALESCE(z.deleted, false) = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = c.id AND i.field_code = v.code AND i.tenant_id = 123 AND i.deleted = false
  );
