-- ============================================================================
-- sop · 09 清理 field_work_standard 残留（组件 props / 能力投影）
-- 现象：数据 Tab 列表仍带 entityTypeCode=field_work_standard → 查已改名的表 500
-- 前置：V56 已执行；类型真源为 sop
-- ============================================================================

SET search_path TO platformresource, dynamicbusiness;

-- 列表/树组件配置：数据来源编码与名称
UPDATE pr_component_props
SET
  name = replace(replace(name, 'field_work_standard', 'sop'), '现场作业标准', '标准作业流程SOP'),
  data_source = replace(data_source, 'field_work_standard', 'sop'),
  props = CASE
    WHEN props LIKE '%field_work_standard%' THEN replace(props, 'field_work_standard', 'sop')
    ELSE props
  END,
  props_override = CASE
    WHEN props_override IS NOT NULL AND props_override LIKE '%field_work_standard%'
      THEN replace(props_override, 'field_work_standard', 'sop')
    ELSE props_override
  END,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    data_source LIKE '%field_work_standard%'
    OR COALESCE(props, '') LIKE '%field_work_standard%'
    OR COALESCE(props_override, '') LIKE '%field_work_standard%'
    OR name LIKE '%field_work_standard%'
  );

-- 旧码能力投影软删（读路径以 sop 行为准）
UPDATE capability_component_projection
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;
