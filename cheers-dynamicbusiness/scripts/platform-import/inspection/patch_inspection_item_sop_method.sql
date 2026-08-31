-- 【废弃·勿在新库执行】旧表 dynamic_inspection_item_sop 已由 Flyway V80 DROP。
-- 请改用：scripts/platform-import/recipes/inspection/03_sample_method_bindings.sql
-- ============================================================================
-- 检查项方法权威说明 + 样例挂接（幂等）——历史脚本，保留仅供对照
--
-- 权威表（历史）：dynamic_inspection_item_sop（Flyway V44）
--   inspection_item_id × execution_means → sop_id（SOP 模板实体 id，须 is_template=true）
-- 设备侧实例绑定（历史）：dynamic_equipment_inspection_sop_binding（Flyway V75）
--
-- 租户物理表：检查项 / SOP 读 ent_*_t1（tenant_id=1）

-- 样例：名称含「泄漏」的检查项 × MANUAL/UAV → 对应 SOP 模板
INSERT INTO dynamicbusiness.dynamic_inspection_item_sop (
  tenant_id, inspection_item_id, sop_id, execution_means, sort, creator, create_time, updater, update_time, deleted
)
SELECT
  1,
  i.id,
  s.id,
  v.means,
  v.sort_no,
  'seed',
  NOW(),
  'seed',
  NOW(),
  false
FROM dynamicbusiness.ent_inspection_item_t1 i
CROSS JOIN (
  VALUES
    ('SOP-TPL-MANUAL-LEAK', 'MANUAL', 10),
    ('SOP-TPL-UAV-LEAK', 'UAV', 20)
) AS v(sop_code, means, sort_no)
JOIN dynamicbusiness.ent_sop_t1 s
  ON s.tenant_id = 1
 AND s.deleted = false
 AND s.is_template = true
 AND s.code = v.sop_code
WHERE i.deleted = false
  AND i.tenant_id = 1
  AND (
    i.code ILIKE '%leak%'
    OR i.name LIKE '%跑冒滴漏%'
    OR i.name LIKE '%泄漏%'
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_inspection_item_sop x
    WHERE x.tenant_id = 1
      AND x.inspection_item_id = i.id
      AND x.sop_id = s.id
      AND x.deleted = false
  );
