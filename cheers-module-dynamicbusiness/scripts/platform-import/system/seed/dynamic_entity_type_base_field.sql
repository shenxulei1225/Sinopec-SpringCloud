-- ============================================================================
-- 系统 · dynamic_entity_type_base_field
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_entity_type_base_field: 113 row(s), upsert by (entity_type_code, field_code)

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shi_fou_han_shui',
  '是否含税', 'ENUM',
  TRUE, '是',
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shou_fei_shi_jian',
  '收费时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shou_fei_zhuang_tai',
  '收费状态', 'ENUM',
  TRUE, '未收费',
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'customer', 'lian_xi_dian_hua',
  '联系电话', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'customer', 'REL_EQUIPMENT',
  '关联设备', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'emergency_resource', 'ke_yong_shu_liang',
  '可用数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'emergency_resource', 'zong_shu_liang',
  '总数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'archive_no',
  '档案编号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  14, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'asset_code',
  '资产编号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  11, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'barcode',
  '条码/二维码', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  13, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'brand',
  '品牌', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  32, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'commission_date',
  '投运日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  61, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'coordinate_3d',
  '三维坐标', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'coordinate_gis',
  'GIS坐标', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'cost_center',
  '成本中心', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  90, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'criticality',
  '重要等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'custodian',
  '使用人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'equipment_model',
  '规格型号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  30, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'equipment_type',
  '设备类型', 'NUMBER',
  TRUE, NULL,
  'equipment_type 分类维度；特种设备等通过分类体现', NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'expected_service_life',
  '设计使用年限', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  64, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'explosion_proof_grade',
  '防爆等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  81, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'install_date',
  '安装日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'install_location',
  '安装位置', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'maintainer',
  '维护人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  52, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'maintenance_cycle',
  '维保周期', 'TEXT',
  FALSE, NULL,
  '策略配置（如 90天），非实时下次维保时间', NULL,
  65, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'manufacturer',
  '生产厂家', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  31, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'model_3d',
  '三维模型', 'TEXT',
  FALSE, NULL,
  '三维模型资源标识或路径', NULL,
  43, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'place_of_origin',
  '产地', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  34, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'purchase_date',
  '采购日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  62, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_FACILITY',
  '所属设施', 'REF',
  TRUE, NULL,
  '设备归属设施点；列表批量补全名称', '{"refField": "F-spatial-equipment-ref-facility"}',
  42, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_HEALTH_SCORE',
  '健康度', 'REF',
  FALSE, NULL,
  '健康评估服务', NULL,
  902, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_LAST_INSPECTION',
  '上次检验时间', 'REF',
  FALSE, NULL,
  NULL, NULL,
  905, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_LAST_MAINTENANCE',
  '上次维保时间', 'REF',
  FALSE, NULL,
  '事实来源 maintenance 模块', NULL,
  903, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_NEXT_INSPECTION',
  '下次检验时间', 'REF',
  FALSE, NULL,
  NULL, NULL,
  906, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_NEXT_MAINTENANCE',
  '下次维保时间', 'REF',
  FALSE, NULL,
  '事实来源 maintenance 模块；按此筛选由 maintenance API 驱动', NULL,
  904, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_OPERATION_STATUS',
  '运行状态', 'REF',
  FALSE, NULL,
  '运行态/SCADA；全设备列表展示', NULL,
  901, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_REGION',
  '所属区域', 'REF_Multi',
  FALSE, NULL,
  '全设备共有；关联存关联表，列表批量补区域名', '{"refField": "F-cc746ce0224145af88d5428d0b03213a", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  44, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_ZONE',
  '所属分区', 'REF',
  FALSE, NULL,
  '可选站内精细定位', '{"refField": "F-spatial-equipment-ref-zone"}',
  43, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'responsible_person',
  '负责人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'safety_level',
  '安全等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  80, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'serial_number',
  '出厂序列号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  12, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'supplier',
  '供应商', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  33, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'warranty_expiry',
  '质保到期日', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  63, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'waterproof_grade',
  '防水等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  82, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'address',
  '地址', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'facility_type',
  '设施类型', 'ENUM',
  TRUE, NULL,
  '与 model 或分类对齐', NULL,
  25, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'latitude',
  '纬度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  22, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'longitude',
  '经度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'REF_REGION',
  '所属区域', 'REF',
  TRUE, NULL,
  '设施所属管网运营区域（ent_region.id）；区域树来自 category，非 parent_id 区划树', '{"refField": "F-spatial-facility-ref-region"}',
  10, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'facility', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_level',
  '故障等级', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_no',
  '故障编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_type',
  '故障类型', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'occur_time',
  '发生时间', 'DATETIME',
  TRUE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'phenomenon',
  '故障现象', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'resolved',
  '是否已解决', 'BOOLEAN',
  TRUE, NULL,
  NULL, NULL,
  6, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'inspection_point', 'REL_REGION',
  '所属区域', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "F-cc746ce0224145af88d5428d0b03213a", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'actual_end_time',
  '实际完成时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'actual_start_time',
  '实际开始时间', 'DATETIME',
  FALSE, NULL,
  '工单执行时写入，非外部模块衍生', NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'deadline',
  '要求完成时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  31, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'estimated_cost',
  '预估费用', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'estimated_duration',
  '计划工时(h)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  32, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'executor',
  '执行人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintainer_team',
  '执行班组', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintenance_content',
  '维护内容', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  33, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintenance_type',
  '维护类型', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'order_status',
  '工单状态', 'ENUM',
  TRUE, NULL,
  '工单流转态（待派工/进行中等），非实体 status 启用禁用', NULL,
  22, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'plan_time',
  '计划时间', 'DATETIME',
  TRUE, NULL,
  NULL, NULL,
  30, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'priority',
  '优先级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'REF_EQUIPMENT',
  '维护对象', 'REF_Multi',
  TRUE, NULL,
  '待确认：报修与工单是否共有；若仅工单需要则降为模型字段', '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  34, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'source_type',
  '工单来源', 'ENUM',
  FALSE, NULL,
  '计划生成/报修触发/巡检消缺/手工创建等', NULL,
  23, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'supervisor',
  '监督人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'install_date',
  '安装日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'manufacturer',
  '生产厂家', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_code',
  '管线编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_model',
  '管线型号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_name',
  '管线名称', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'status',
  '状态', 'ENUM',
  TRUE, NULL,
  NULL, '{"options": ["运行", "停止", "故障", "维护"], "libraryFieldCode": "F-e8289a51a3dd41c6b7f1539efd003258"}',
  3, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'admin_code',
  '区划代码', 'TEXT',
  FALSE, NULL,
  '国标行政区划码 [已废弃：改用 category 树层级] [已废弃：改用 category 树层级]', NULL,
  16, 0,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'belong_department',
  '管理部门', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_crs',
  '坐标系', 'TEXT',
  FALSE, 'EPSG:4326',
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_geojson',
  '边界几何', 'JSON',
  FALSE, NULL,
  '地图多边形顶点集合（GeoJSON）', NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_status',
  '边界状态', 'ENUM',
  FALSE, 'none',
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'centroid_lat',
  '质心纬度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  46, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'centroid_lng',
  '质心经度', 'NUMBER',
  FALSE, NULL,
  '区域中心点，与边界一并保存', NULL,
  45, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'description',
  '区域说明', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'establish_date',
  '设立日期', 'DATE',
  FALSE, NULL,
  '区域划定/启用，不单独设启用日期', NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'max_height_m',
  '最大高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  44, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'min_height_m',
  '最小高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  43, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'region_level',
  '区划级别', 'ENUM',
  TRUE, NULL,
  'country/province/city/district；与 model 对齐 [已废弃：改用 category 树层级] [已废弃：改用 category 树层级]', NULL,
  15, 0,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'region_type',
  '区域类型', 'NUMBER',
  TRUE, NULL,
  'region 分类维度；编码/名称见实体 code、name', NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'responsible_person',
  '负责人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'safety_level',
  '安全等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  80, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'min_stock',
  '最低库存', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'REL_EQUIPMENT',
  '关联设备', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'spare_part_code',
  '备件编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'spare_part_name',
  '备件名称', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'stock_quantity',
  '库存数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'unit',
  '单位', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'boundary_crs',
  '坐标系', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'boundary_geojson',
  '边界几何', 'JSON',
  FALSE, NULL,
  NULL, NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'boundary_status',
  '边界状态', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'centroid_lat',
  '质心纬度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  46, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'centroid_lng',
  '质心经度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  45, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'description',
  '分区说明', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'max_height_m',
  '最大高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  44, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'min_height_m',
  '最小高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  43, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'REF_FACILITY',
  '所属设施', 'REF',
  TRUE, NULL,
  '分区归属设施点', '{"refField": "F-spatial-zone-ref-facility"}',
  10, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'zone', 'zone_type',
  '分区类型', 'ENUM',
  TRUE, NULL,
  '与 model 对齐', NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
