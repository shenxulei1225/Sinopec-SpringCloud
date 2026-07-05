-- 清除「所属区域」字段（field_id=45）中误存为 equipment 实体 id 的脏值
-- 根因：target_business_type 为空 + crud-form refTarget 指向 equipment，picker 选中了同表设备 id（如 64/65）
SET search_path TO dynamicbusiness;

UPDATE biz_equipment e
SET custom_fields = e.custom_fields - '45',
    updater = 'migration',
    update_time = NOW()
WHERE e.custom_fields ? '45'
  AND EXISTS (
    SELECT 1
    FROM biz_equipment ref_eq
    WHERE ref_eq.id = (e.custom_fields ->> '45')::bigint
      AND ref_eq.deleted = false
  )
  AND NOT EXISTS (
    SELECT 1
    FROM biz_region ref_r
    WHERE ref_r.id = (e.custom_fields ->> '45')::bigint
      AND ref_r.deleted = false
  );
