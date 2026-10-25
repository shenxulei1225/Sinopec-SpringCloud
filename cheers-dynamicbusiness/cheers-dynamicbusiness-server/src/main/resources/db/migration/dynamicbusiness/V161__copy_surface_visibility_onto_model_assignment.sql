-- 画面开关改到型号字段分配上。
-- 基础字段 type_config 里已经写成真或假的，抄到这个型号还没自己写过的分配行。
-- 抄完之后，创建、编辑、详情只认分配行。不同型号可以不一样。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_model_field_assignment a
SET create_visible = COALESCE(
        a.create_visible,
        CASE
            WHEN bf.type_config ? 'createVisible'
                THEN (bf.type_config ->> 'createVisible')::boolean
            ELSE a.create_visible
        END
    ),
    edit_visible = COALESCE(
        a.edit_visible,
        CASE
            WHEN bf.type_config ? 'editVisible'
                THEN (bf.type_config ->> 'editVisible')::boolean
            ELSE a.edit_visible
        END
    ),
    detail_visible = COALESCE(
        a.detail_visible,
        CASE
            WHEN bf.type_config ? 'detailVisible'
                THEN (bf.type_config ->> 'detailVisible')::boolean
            ELSE a.detail_visible
        END
    )
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_field f
    ON f.deleted = false
JOIN dynamicbusiness.dynamic_entity_type_base_field bf
    ON bf.deleted = false
   AND bf.tenant_id = m.tenant_id
   AND bf.entity_type_code = m.entity_type_code
   AND bf.field_code = f.code
   AND bf.type_config IS NOT NULL
WHERE a.deleted = false
  AND m.deleted = false
  AND a.model_id = m.id
  AND a.field_id = f.id
  AND a.tenant_id = m.tenant_id
  AND f.tenant_id = m.tenant_id
  AND (
      bf.type_config ? 'createVisible'
      OR bf.type_config ? 'editVisible'
      OR bf.type_config ? 'detailVisible'
  );
