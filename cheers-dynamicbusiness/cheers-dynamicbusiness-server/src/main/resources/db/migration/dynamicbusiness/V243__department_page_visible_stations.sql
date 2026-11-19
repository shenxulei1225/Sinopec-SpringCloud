-- 场站部门人员管理的数据页：左侧加上可见场站列表。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md
-- 点一座站，右上角跟着换，旁边的部门只留这座站的。部门栏本身不改成场站树。
SET search_path TO dynamicbusiness;

INSERT INTO platformresource.pr_component_props (
    is_template, component_id, component_code, data_source, template_id, schema_version,
    props, props_override, name, status, sort, description, creator, updater, tenant_id, deleted
)
SELECT
    is_template, component_id, component_code, data_source, template_id, schema_version,
    props, props_override, 'dm-tree:department:facility:场站', status, sort,
    '场站部门人员管理左侧的可见场站', 'V243', 'V243', tenant_id, false
FROM platformresource.pr_component_props src
WHERE src.id = 9856
  AND NOT EXISTS (
      SELECT 1 FROM platformresource.pr_component_props
      WHERE deleted = false AND name = 'dm-tree:department:facility:场站'
  );

INSERT INTO dm_data_tab_layout (
    tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta,
    creator, updater, deleted, layout_id
)
SELECT
    1, 'department', 'CATEGORY', 'tab-visible-stations', p.id, true,
    '{"label":"场站","columnKey":"facility","columnOrder":0,"columnSection":"filter","categoryTypeCode":"facility"}'::jsonb,
    'V243', 'V243', false, 5
FROM platformresource.pr_component_props p
WHERE p.deleted = false AND p.name = 'dm-tree:department:facility:场站'
  AND NOT EXISTS (
      SELECT 1 FROM dm_data_tab_layout
      WHERE deleted = false AND layout_id = 5 AND tab_id = 'tab-visible-stations'
  );

UPDATE dm_data_tab_layout
SET column_meta = column_meta || '{"columnOrder":1}'::jsonb,
    updater = 'V243',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND layout_id = 5 AND tab_id = 'tab-bf822969'
  AND COALESCE(column_meta->>'columnOrder', '') <> '1';
