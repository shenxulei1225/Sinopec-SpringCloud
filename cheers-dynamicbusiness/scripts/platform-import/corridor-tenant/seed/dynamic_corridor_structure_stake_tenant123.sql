-- 神墩三路平面图：构筑物标识旁 8 米内的桩号，落入已核对防火区，且该区只有一条同种口，才写入桩号。
-- 来源：智慧管廊/整理图纸/神墩三路综合管廊平面(7.13）.dwg

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, updater, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, 12971, false, false, false, false,
       6, 'BASE', 'corridor-struct-stake', 'corridor-struct-stake', 123, m.code, 'stake_number',
       true, true, true
FROM dynamicbusiness.dynamic_model m
WHERE m.id IN (2566, 2568, 2571, 2575)
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.model_id = m.id AND a.field_id = 12971 AND a.tenant_id = 123 AND a.deleted = false
  );

UPDATE dynamicbusiness.ent_constructure_t123 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || jsonb_build_object('stake_number', v.stake),
    updater = 'corridor-struct-stake',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (2568, '神墩三路2#吊装口', 'SS0+228'),
    (2568, '神墩三路4#吊装口', 'SS0+542'),
    (2568, '神墩三路6#吊装口', 'SS0+810'),
    (2568, '神墩三路8#吊装口', 'SS1+301'),
    (2568, '神墩三路11#吊装口', 'SS1+707'),
    (2568, '神墩三路13#吊装口', 'SS2+072'),
    (2568, '神墩三路15#吊装口', 'SS2+442'),
    (2568, '神墩三路17#吊装口', 'SS2+792'),
    (2568, '神墩三路21#吊装口', 'SS3+531'),
    (2568, '神墩三路23#吊装口', 'SS3+831'),
    (2575, '神墩三路23#端井', 'SS3+970'),
    (2571, '神墩三路4#人员出入口', 'SS0+511.5'),
    (2571, '神墩三路20#人员出入口', 'SS3+427'),
    (2566, '神墩三路2#引出门', 'SS0+251'),
    (2566, '神墩三路3#引出门', 'SS0+448'),
    (2566, '神墩三路6#引出门', 'SS0+970'),
    (2566, '神墩三路8#引出门', 'SS1+273'),
    (2566, '神墩三路9#引出门', 'SS1+398'),
    (2566, '神墩三路11#引出门', 'SS1+746'),
    (2566, '神墩三路12#引出门', 'SS1+912'),
    (2566, '神墩三路13#引出门', 'SS2+029'),
    (2566, '神墩三路14#引出门', 'SS2+196'),
    (2566, '神墩三路15#引出门', 'SS2+377'),
    (2566, '神墩三路20#引出门', 'SS3+324'),
    (2566, '神墩三路21#引出门', 'SS3+585'),
    (2566, '神墩三路22#引出门', 'SS3+760')
) AS v(model_id, name, stake)
WHERE e.tenant_id = 123
  AND COALESCE(e.deleted, false) = false
  AND e.model_id = v.model_id
  AND e.name = v.name;
