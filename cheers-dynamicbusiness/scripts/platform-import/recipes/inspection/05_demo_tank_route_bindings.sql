-- ============================================================================
-- recipes/inspection · 05 储罐演示：设备↔停靠点 + SOP 实例可展开点位
--
-- 场景：任务创建第 2 步「路线规划」不再显示绑定缺口；开始规划时 SOP 能展开停靠站。
-- 1) inspection_object_station_binding：北 1# / 南 10# → 金桥厂区路网停靠点
-- 2) 演示 SOP 实例：改挂 LEAK 动作树模板 + 按设备写 location_ref（点位 id）
-- creator = recipe-inspection-demo
-- ============================================================================

-- ---------------------------------------------------------------------------
-- A) 设备 ↔ 停靠点（inspection_task 库）
-- ---------------------------------------------------------------------------
INSERT INTO inspection_task.inspection_object_station_binding (
  id, facility_id, object_id, station_node_id,
  work_minutes, sort_no,
  creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
  v.id,
  44,
  v.object_id,
  v.station_node_id,
  15,
  0,
  'recipe-inspection-demo',
  NOW(),
  'recipe-inspection-demo',
  NOW(),
  false,
  1
FROM (
  VALUES
    (100001::bigint, 900104::bigint, 'GDXJD-1001-1001-001'::varchar),
    (100002::bigint, 900114::bigint, 'GDXJD-1002-1010-001'::varchar)
) AS v(id, object_id, station_node_id)
WHERE NOT EXISTS (
  SELECT 1
  FROM inspection_task.inspection_object_station_binding b
  WHERE b.deleted = false
    AND b.facility_id = 44
    AND b.object_id = v.object_id
    AND b.station_node_id = v.station_node_id
);

-- ---------------------------------------------------------------------------
-- B) 演示 SOP 实例：挂 LEAK 模板 + 设备专属 location_ref（dynamicbusiness）
-- ---------------------------------------------------------------------------
SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tpl_manual bigint;
  v_tpl_uav bigint;
  v_point_north bigint := 70;  -- PN-44-GDXJD-1001-1001-001 北罐组西罐1顶
  v_point_south bigint := 79;  -- PN-44-GDXJD-1002-1010-001 南罐组东罐10顶
BEGIN
  SELECT id INTO v_tpl_manual
  FROM ent_sop_t1
  WHERE deleted = false AND tenant_id = 1 AND code = 'SOP-TPL-MANUAL-LEAK'
  LIMIT 1;

  SELECT id INTO v_tpl_uav
  FROM ent_sop_t1
  WHERE deleted = false AND tenant_id = 1 AND code = 'SOP-TPL-UAV-LEAK'
  LIMIT 1;

  IF v_tpl_manual IS NULL OR v_tpl_uav IS NULL THEN
    RAISE NOTICE 'skip 05 B: LEAK 模板不存在，请先跑 sop/import.sh';
    RETURN;
  END IF;

  UPDATE ent_sop_t1 inst
  SET sop_template_id = CASE WHEN inst.execution_means = 'UAV' THEN v_tpl_uav ELSE v_tpl_manual END,
      param_override_json = CASE
        WHEN inst.execution_means = 'UAV' THEN jsonb_build_object(
          'n-1', jsonb_build_object('location_ref', CASE WHEN inst.code LIKE '%900104%' THEN v_point_north::text ELSE v_point_south::text END),
          'n-3', jsonb_build_object('yaw', 0, 'pitch', 0),
          'n-4', jsonb_build_object('shot_count', 3)
        )
        ELSE jsonb_build_object(
          'n-1', jsonb_build_object('location_ref', CASE WHEN inst.code LIKE '%900104%' THEN v_point_north::text ELSE v_point_south::text END),
          'n-2', jsonb_build_object('yaw', 0, 'pitch', 0)
        )
      END,
      updater = 'recipe-inspection-demo',
      update_time = CURRENT_TIMESTAMP
  WHERE inst.deleted = false
    AND inst.tenant_id = 1
    AND inst.creator = 'recipe-inspection-demo'
    AND inst.is_template = false;

  RAISE NOTICE 'recipes/inspection 05: 设备停靠点绑定 + SOP 实例点位参数已写入';
END $$;
