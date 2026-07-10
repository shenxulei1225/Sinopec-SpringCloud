-- 开发联调 · equipment 业务实例样例（与 system seed 的 dynamic_model 按 code 对齐）
-- 用途：数据管理试验页 / 列表·树组件预览；tenant_id=1 与登录态一致
-- 幂等：按 code 存在则更新 name/model_id，不重复插入

SET search_path TO dynamicbusiness;

INSERT INTO ent_equipment (
    id,
    entity_type_code,
    model_id,
    name,
    code,
    tenant_id,
    creator,
    tree_path,
    sort,
    status,
    deleted,
    custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
    v.id,
    'equipment',
    m.id,
    v.name,
    v.code,
    1,
    'seed',
    v.tree_path,
    v.sort,
    1,
    false,
    '{}'::jsonb
FROM (
    VALUES
        (900001, 'DEV-EQ-CAM-001', '试验设备-网络摄像机-01', '/900001/', 1, 'MODEL-2af0515a5a36420086980b2322037edb'),
        (900002, 'DEV-EQ-CAM-002', '试验设备-网络摄像机-02', '/900002/', 2, 'MODEL-2af0515a5a36420086980b2322037edb'),
        (900003, 'DEV-EQ-CAM-003', '试验设备-网络摄像机-03', '/900003/', 3, 'MODEL-2af0515a5a36420086980b2322037edb'),
        (900004, 'DEV-EQ-PHONE-001', '试验设备-IP电话-01', '/900004/', 4, 'MODEL-50991eab2a714ed288d00c58ff01c810'),
        (900005, 'DEV-EQ-PHONE-002', '试验设备-IP电话-02', '/900005/', 5, 'MODEL-50991eab2a714ed288d00c58ff01c810'),
        (900006, 'DEV-EQ-O2-001', '试验设备-氧气传感器-01', '/900006/', 6, 'MODEL-123328b7bfe745ceb337d286ac64aa17'),
        (900007, 'DEV-EQ-O2-002', '试验设备-氧气传感器-02', '/900007/', 7, 'MODEL-123328b7bfe745ceb337d286ac64aa17'),
        (900008, 'DEV-EQ-LED-001', '试验设备-应急灯-01', '/900008/', 8, 'MODEL-1966e63fec48402f9b60280e66faea0f'),
        (900009, 'DEV-EQ-LED-002', '试验设备-应急灯-02', '/900009/', 9, 'MODEL-1966e63fec48402f9b60280e66faea0f'),
        (900010, 'DEV-EQ-SW-001', '试验设备-环网交换机-01', '/900010/', 10, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'),
        (900011, 'DEV-EQ-SW-002', '试验设备-环网交换机-02', '/900011/', 11, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'),
        (900012, 'DEV-EQ-UPS-001', '试验设备-UPS-01', '/900012/', 12, 'MODEL-68554821800248cfacacbac1a53cdf37')
) AS v(id, code, name, tree_path, sort, model_code)
JOIN dynamic_model m
  ON m.code = v.model_code
 AND m.entity_type_code = 'equipment'
 AND m.deleted = false
ON CONFLICT (id) DO UPDATE SET
    model_id = EXCLUDED.model_id,
    name = EXCLUDED.name,
    code = EXCLUDED.code,
    tenant_id = EXCLUDED.tenant_id,
    tree_path = EXCLUDED.tree_path,
    sort = EXCLUDED.sort,
    status = EXCLUDED.status,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- 同步序列，避免后续页面创建 id 冲突
SELECT setval(
    pg_get_serial_sequence('dynamicbusiness.ent_equipment', 'id'),
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_equipment),
        900012
    )
);
