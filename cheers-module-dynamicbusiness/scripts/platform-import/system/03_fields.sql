-- ============================================================================
-- 系统共用 · 03 字段（全部业务默认字段 + 完整字段库 dynamic_field）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/02_business_types.sql
-- 含源库全部 dynamic_field（含未挂模型的字段），字段库只在 system 维护一份
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_business_type_base_field: 92 row(s), upsert by (business_type_code, field_code)

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shi_fou_han_shui',
  '是否含税', 'ENUM',
  TRUE, '是',
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shou_fei_shi_jian',
  '收费时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'billing', 'shou_fei_zhuang_tai',
  '收费状态', 'ENUM',
  TRUE, '未收费',
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'customer', 'lian_xi_dian_hua',
  '联系电话', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'customer', 'REL_EQUIPMENT',
  '关联设备', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'emergency_resource', 'ke_yong_shu_liang',
  '可用数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'emergency_resource', 'zong_shu_liang',
  '总数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'archive_no',
  '档案编号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  14, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'asset_code',
  '资产编号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  11, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'barcode',
  '条码/二维码', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  13, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'brand',
  '品牌', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  32, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'commission_date',
  '投运日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  61, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'coordinate_3d',
  '三维坐标', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'coordinate_gis',
  'GIS坐标', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'cost_center',
  '成本中心', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  90, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'criticality',
  '重要等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'custodian',
  '使用人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'equipment_model',
  '规格型号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  30, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'equipment_type',
  '设备类型', 'NUMBER',
  TRUE, NULL,
  'equipment_type 分类维度；特种设备等通过分类体现', NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'expected_service_life',
  '设计使用年限', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  64, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'explosion_proof_grade',
  '防爆等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  81, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'install_date',
  '安装日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'install_location',
  '安装位置', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'maintainer',
  '维护人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  52, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'maintenance_cycle',
  '维保周期', 'TEXT',
  FALSE, NULL,
  '策略配置（如 90天），非实时下次维保时间', NULL,
  65, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'manufacturer',
  '生产厂家', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  31, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'model_3d',
  '三维模型', 'TEXT',
  FALSE, NULL,
  '三维模型资源标识或路径', NULL,
  43, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'place_of_origin',
  '产地', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  34, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'purchase_date',
  '采购日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  62, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_HEALTH_SCORE',
  '健康度', 'REF',
  FALSE, NULL,
  '健康评估服务', NULL,
  902, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_LAST_INSPECTION',
  '上次检验时间', 'REF',
  FALSE, NULL,
  NULL, NULL,
  905, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_LAST_MAINTENANCE',
  '上次维保时间', 'REF',
  FALSE, NULL,
  '事实来源 maintenance 模块', NULL,
  903, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_NEXT_INSPECTION',
  '下次检验时间', 'REF',
  FALSE, NULL,
  NULL, NULL,
  906, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_NEXT_MAINTENANCE',
  '下次维保时间', 'REF',
  FALSE, NULL,
  '事实来源 maintenance 模块；按此筛选由 maintenance API 驱动', NULL,
  904, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_OPERATION_STATUS',
  '运行状态', 'REF',
  FALSE, NULL,
  '运行态/SCADA；全设备列表展示', NULL,
  901, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'REF_REGION',
  '所属区域', 'REF_Multi',
  FALSE, NULL,
  '全设备共有；关联存关联表，列表批量补区域名', '{"refField": "F-cc746ce0224145af88d5428d0b03213a", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  44, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'responsible_person',
  '负责人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'safety_level',
  '安全等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  80, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'serial_number',
  '出厂序列号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  12, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'supplier',
  '供应商', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  33, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'warranty_expiry',
  '质保到期日', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  63, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'equipment', 'waterproof_grade',
  '防水等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  82, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_level',
  '故障等级', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_no',
  '故障编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'fault_type',
  '故障类型', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'occur_time',
  '发生时间', 'DATETIME',
  TRUE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'phenomenon',
  '故障现象', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'fault', 'resolved',
  '是否已解决', 'BOOLEAN',
  TRUE, NULL,
  NULL, NULL,
  6, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'inspection_point', 'REL_REGION',
  '所属区域', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "F-cc746ce0224145af88d5428d0b03213a", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'actual_end_time',
  '实际完成时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'actual_start_time',
  '实际开始时间', 'DATETIME',
  FALSE, NULL,
  '工单执行时写入，非外部模块衍生', NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'deadline',
  '要求完成时间', 'DATETIME',
  FALSE, NULL,
  NULL, NULL,
  31, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'estimated_cost',
  '预估费用', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'estimated_duration',
  '计划工时(h)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  32, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'executor',
  '执行人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintainer_team',
  '执行班组', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintenance_content',
  '维护内容', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  33, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'maintenance_type',
  '维护类型', 'ENUM',
  TRUE, NULL,
  NULL, NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'order_status',
  '工单状态', 'ENUM',
  TRUE, NULL,
  '工单流转态（待派工/进行中等），非实体 status 启用禁用', NULL,
  22, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'plan_time',
  '计划时间', 'DATETIME',
  TRUE, NULL,
  NULL, NULL,
  30, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'priority',
  '优先级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'REF_EQUIPMENT',
  '维护对象', 'REF_Multi',
  TRUE, NULL,
  '待确认：报修与工单是否共有；若仅工单需要则降为模型字段', '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  34, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'source_type',
  '工单来源', 'ENUM',
  FALSE, NULL,
  '计划生成/报修触发/巡检消缺/手工创建等', NULL,
  23, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'maintenance', 'supervisor',
  '监督人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'install_date',
  '安装日期', 'DATE',
  FALSE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'manufacturer',
  '生产厂家', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_code',
  '管线编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_model',
  '管线型号', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'pipeline_name',
  '管线名称', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'pipeline', 'status',
  '状态', 'ENUM',
  TRUE, NULL,
  NULL, '{"options": ["运行", "停止", "故障", "维护"], "libraryFieldCode": "F-e8289a51a3dd41c6b7f1539efd003258"}',
  3, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'belong_department',
  '管理部门', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  51, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_crs',
  '坐标系', 'TEXT',
  FALSE, 'EPSG:4326',
  NULL, NULL,
  41, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_geojson',
  '边界几何', 'JSON',
  FALSE, NULL,
  '地图多边形顶点集合（GeoJSON）', NULL,
  40, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'boundary_status',
  '边界状态', 'ENUM',
  FALSE, 'none',
  NULL, NULL,
  42, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'centroid_lat',
  '质心纬度', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  46, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'centroid_lng',
  '质心经度', 'NUMBER',
  FALSE, NULL,
  '区域中心点，与边界一并保存', NULL,
  45, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'description',
  '区域说明', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  21, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'establish_date',
  '设立日期', 'DATE',
  FALSE, NULL,
  '区域划定/启用，不单独设启用日期', NULL,
  60, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'max_height_m',
  '最大高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  44, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'min_height_m',
  '最小高度(m)', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  43, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'region_type',
  '区域类型', 'NUMBER',
  TRUE, NULL,
  'region 分类维度；编码/名称见实体 code、name', NULL,
  20, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'remark',
  '备注', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  91, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'responsible_person',
  '负责人', 'TEXT',
  FALSE, NULL,
  NULL, NULL,
  50, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'region', 'safety_level',
  '安全等级', 'ENUM',
  FALSE, NULL,
  NULL, NULL,
  80, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'min_stock',
  '最低库存', 'NUMBER',
  FALSE, NULL,
  NULL, NULL,
  5, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'REL_EQUIPMENT',
  '关联设备', 'REF_Multi',
  FALSE, NULL,
  NULL, '{"refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id", "dataFormat": "labeled", "supportLabels": true, "allowCustomLabels": true}',
  900, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'spare_part_code',
  '备件编号', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  1, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'spare_part_name',
  '备件名称', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  2, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'stock_quantity',
  '库存数量', 'NUMBER',
  TRUE, NULL,
  NULL, NULL,
  3, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  'spare_parts', 'unit',
  '单位', 'TEXT',
  TRUE, NULL,
  NULL, NULL,
  4, 1,
  1, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_field: 269 row(s), upsert by code

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-001e86fbdfe7468eb822e9330abfb06d', '关联测建表', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到测建表',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0054227dff284d18be8cb7a59c9f9208', '描述', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-018f18b9116c4e7a8abe7878145aa98b', '关联巡检管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到巡检管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-024b45dee6d948c7b703cd33a787d81a', '产品图片', 'IMAGE',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-03c491bf9ef745c3859042ebbc8ba350', '管线地面有无塌陷', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0436a31b31dc4b4d8a5314177e254312', 'SLA截止', 'DATETIME',
  NULL, 'SLA 约定的最晚完成时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-06cf3dcb998f4c4392cb6a5a67fa1289', '镜头类型', 'ENUM',
  NULL, '摄像机镜头类型',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"定焦","value":"fixed"},{"label":"变焦","value":"zoom"},{"label":"电动变焦","value":"motorized_zoom"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-08a164e893064fc090f611deca4654ab', '所属管廊段', 'REFERENCE',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0970197515b249e1b6ba44002abb11e8', '底径长度', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0b1047d4121b4b23af936cf6e6f06b7b', '附件', 'LONG_TEXT',
  NULL, '附件列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0b5a1e6027a44d7381d4be263c4d83c6', '外观是否完好', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"是","value":"是"},{"label":"否","value":"否"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0d3b7aafc63c4c1ebfd8dc6681d0bd41', '时长', 'INTEGER',
  'min', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0ddaa2df8e4645fcbb71d0a425e25e5e', '管廊区间', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1011bb8c2edb4ee38e7502fb1435b591', '防火区起点路名', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-154c668b39c14dffb5565cd44817549b', '氧气浓度', 'FLOAT',
  '%', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-18af5bff1c894e2da694e5fa35d8c7af', '设备编号', 'TEXT',
  NULL, '设备唯一标识编号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-18d0ce546f434b9e96b799d1ade2b46b', '联系方式', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1920066551d944b3b7fd8b0fc7e1c094', '宽度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d3bcec7a5df4ff985610db69a2c467b', '报修时间', 'DATETIME',
  NULL, '报修时间（ISO）',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d6369c7d59a4598b2346df632babed9', '关联设备故障', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到设备故障',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d9efb587ce0496aa8a967ebd7216627', '接单人', 'TEXT',
  NULL, '实际接单处理人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1ee037ca81bb475f94b3526134ea85bb', '工作温度', 'NUMBER',
  '℃', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2018852d847d4ae3a29d143ef78a921a', '咨询业务详情', 'TEXT',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-209f806919c0492b97ee9a8aa98aaee9', '处理班组', 'ENUM',
  NULL, '班组/外委单位（示例枚举）',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "电力运维班", "value": "power_team"}, {"label": "环控运维班", "value": "hvac_team"}, {"label": "消防维保（外委）", "value": "fire_vendor"}, {"label": "通信运维班", "value": "comm_team"}, {"label": "综合运维班", "value": "general_team"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-224b4af4e1474d5d85002c03d0bdf89a', '转单原因', 'LONG_TEXT',
  NULL, '转单原因说明',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2407b65c556643adb72f94c0efbbba47', '容量', 'NUMBER',
  'L', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-24442601845a4c7eac78f96b037da45c', '所在路段', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2459441906934083b0a9379196bc1772', '结束时间', 'DATE',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-24c6fd9ee4b54789825ed96a82dd49b5', '舱室类型', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-254fc29f33e0408096351d37ca55afd1', '人员', 'BATCH_ENTITY_REF',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-26bab227792c44fe8c8c4d6bb6037f3e', '阀门有无锈蚀渗漏', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-28655458c4bc41d680b78576cc9cfd70', '用途', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2908d7224a2341e39f413e4905c6a46a', '宽度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3071c55d68ac4e03872d9758512aa17e', '故障描述', 'LONG_TEXT',
  NULL, '故障现象、影响范围等详细描述',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-31cd60a2e6194fa5903b92a873af8e1f', '测试', 'BOOLEAN',
  '', '哦哦',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-31d7dc332d2846c9837fb0d7f9e2ec51', '工作记录', 'LONG_TEXT',
  NULL, '维修过程中的详细工作记录',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-32c5283caa6a4388a3c2b3e69e68f1a8', '关联应急资源', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到应急资源',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3775e93edf70456db41c72ecc15d5c9b', '照明设备数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-39ab7f71e1d945fcbe6af9c4fbae1b25', '工时(小时)', 'NUMBER',
  '小时', '实际工时统计',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3c6a384288b84d4e9086206ff2673eb3', '协作人员', 'LONG_TEXT',
  NULL, '协作处理人员列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d1012a56e8e448aa096bae0f353c21a', '关联备件管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到备件管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d37caf40b634e3c9527bba283d92622', '升级原因', 'LONG_TEXT',
  NULL, '紧急程度升级的原因说明',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d7fedf4c352424abeb1b11805dfd331', '精度', 'NUMBER',
  '%', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3dc0dc43a3894d698a38c2550ce47fdb', '根因分析', 'LONG_TEXT',
  NULL, '根因、复发原因、整改建议',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3fc2a30d4e4a4114878b4f77c331e76f', '敷设方式', 'ENUM',
  '', '',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "直埋", "value": "直埋"}, {"label": "架空", "value": "架空"}, {"label": "管廊", "value": "管廊"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-40617730a826424faa4b40ccd8a58414', '派工人', 'TEXT',
  NULL, '派工操作人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-40d48dc5caec46ac9e482f03f8102f59', '客户', 'ENTITY_REF',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-433ed7705e5f476fa193a26b642d43ab', '存放坐标', 'COORDINATE',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-43ba10972c084c44a24a22ffb0b2185f', '电流', 'NUMBER',
  'A', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-443c8879f02b479e8af045a80c9fdcca', '故障位置', 'TEXT',
  NULL, '设备位置/区域/管廊段',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4651095e38ca4f1cbe8cb32beb8c2142', '测量精度', 'NUMBER',
  '%', '传感器测量精度百分比',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-49523f29afa94650ac591f607043dd65', '压力等级', 'NUMBER',
  'MPa', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-49e264c6d87f4498b3c8e110f423b3a0', '排水泵数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4a5dd27e3302418cb54352c0f95dd473', '关联啊实打实', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到啊实打实',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4b75b7f9889d4f1d90afd19c03c934af', '运行状态', 'ENUM',
  NULL, '设备当前运行状态',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"运行中","value":"running"},{"label":"停用","value":"stopped"},{"label":"维修中","value":"maintenance"},{"label":"故障","value":"fault"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4c2787cd47d74621a3a8868c9bad729b', '舱间间隔（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4d6aa265bbff4ed69227aa623f9dae8f', '电压等级', 'NUMBER',
  'kV', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4f3b40defe104f1b94c3462c568bfb79', '报修人', 'TEXT',
  NULL, '报修人姓名/账号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-51b7d63aa97942439611cff6370fe65e', '管径', 'NUMBER',
  'mm', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-524fd7c27947494384391f83aabbb9e6', '转单目标', 'TEXT',
  NULL, '转单后的接单人/班组',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-559d5ad091e644a798aa9efef8d06c9c', '测量范围', 'TEXT',
  NULL, '传感器测量范围（如：0-100%、-40~85℃）',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-57d6bdf06b284821bd003d0fdba16157', '材质', 'ENUM',
  '', '',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "铜", "value": "铜"}, {"label": "铝", "value": "铝"}, {"label": "其他", "value": "其他"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-58b6dbbc32f1438b96188087aa68ae4e', '安装日期', 'DATE',
  NULL, '设备安装日期',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-59e3752f5d954a818b71effb661ed0a0', '舱室配置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-59e5799b2eaa4b0aa9c164f8d440409b', '检测范围', 'NUMBER',
  'ppm', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5aa8a20200ca4227962015c4ea17e09f', '标准段覆土深度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5b561c0ac2fe41d59a7b1831a93d1637', '品牌', 'TEXT',
  NULL, '设备品牌/厂商',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5bcfff95f7a74c38ab5af0dd75c12e1c', '关联管线管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到管线管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5bf95bdfcf4b459a84aa21b76a3ad965', '本段实际长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5c4fbd6a48ca457ba09a97c4ccea8e71', '满意度', 'ENUM',
  NULL, '回访/评价',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "1-非常不满意", "value": "1"}, {"label": "2-不满意", "value": "2"}, {"label": "3-一般", "value": "3"}, {"label": "4-满意", "value": "4"}, {"label": "5-非常满意", "value": "5"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5d200e3b62354fc9854878532336ec63', '关联数据点', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到数据点',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5d2f22057f0f4b50b6b0e24d2dc86a66', '距离路中心距离', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5e33e7804dd746958c4f8c97203b1f16', '职务', 'ENUM',
  '', '用于队伍管理的职务选项',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"队长","value":"队长"},{"label":"副队长","value":"副队长"},{"label":"成员","value":"成员"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6116465add0949b593f1dcd3f2b6e29d', '挂起原因', 'LONG_TEXT',
  NULL, '挂起原因（等待备件/等待许可/天气等）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6174ace4a9dc43d3a8ee84f6d9a2ceef', '路线规划', 'BATCH_ENTITY_REF',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-64a9042c3a464ffda9040119e4c8f0af', '起始桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-652ef6ef44c247958f9d937722aa4e18', '高度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-65e1d5ebc4e4442d9483aa3f4313f7a9', '验收结果', 'ENUM',
  NULL, '验收结果',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "通过", "value": "passed"}, {"label": "不通过", "value": "rejected"}, {"label": "待验收", "value": "pending"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-66264ae122c54504a8fa0679079772fc', '分辨率', 'ENUM',
  NULL, '摄像机分辨率',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"720P","value":"720p"},{"label":"1080P","value":"1080p"},{"label":"2K","value":"2k"},{"label":"4K","value":"4k"},{"label":"8K","value":"8k"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-66324f09cb7e4913a6af86b1e4f7c7b7', '墙体厚度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-672e3aca3c9c46cb8f0c8fe85217b708', '供电方式', 'ENUM',
  '', '',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "AC", "value": "AC"}, {"label": "DC", "value": "DC"}, {"label": "电池", "value": "电池"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-67aeb9609c7945cfaa3f5806e27e6d90', '排序号', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-67e85d7f261c43b1b99dab1cc0a8885a', '收费标准', 'FLOAT',
  '元/(人·天)', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-684f5feb7c5d4988871dcf14ad06812f', '特殊说明', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-68ef79eb88454c8fa11a1e9baa987d88', '湿度记录', 'FLOAT',
  '%', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6bb56dc7ac014d1ea68632a1ac606dfc', '防火分区数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6bd033593d1d4c3b98b82fab6fa2d866', '关联路线管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到路线管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6c74f28ef78e4a6d84566f97e072b7cf', '关联点位管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到点位管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6e001ac5256943b6840f5b9a3a17ac0b', '转单来源', 'TEXT',
  NULL, '转单前的接单人/班组',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6e612c0931ad42b3b308898469497eec', '定位线距离道路中心线', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-72697688b36e4399b25121108ef76b9a', '名称', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-737b95fd043748b3a565edbd6735733e', '管廊段标号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-771506dcac4d4ee08dc3e39ab5cf3a18', '防火区终点路名', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-778eb98265914d7b8e65932a1aa0c355', '埋地深度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7afc7657a00849c99087fad813f904b6', '舱室数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7b89b48f8a854b72af3e686857e118f5', '关联测测1', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测测1',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c115d3b6ced4f4d924ef114e12ada27', '温度记录', 'FLOAT',
  '℃', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c6e9af941d04fcfbb36dbe80134f119', '现场照片', 'LONG_TEXT',
  NULL, '现场照片URL列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c79907d01cc44e5bafe7fae273a336c', '截面积', 'NUMBER',
  'mm²', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7e858ec039604fd5841722ddf3d3176a', '关联设备', 'ENTITY_REF',
  NULL, '关联的设备实体',
  'USER', 1,
  NULL, 'GIN',
  '["equipment"]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8a103735cdc646d8a454e98913c1a6b4', '房间卫生是否达标', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8ab9c1773cb94097aeeaa4427115f30e', '管廊类型', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8ae17b99e1b44e8dbacbb68645bf4d7c', '长度显示', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8bf33d80787a411cb398f74c54b7843b', '关联告警', 'ENTITY_REF',
  NULL, '关联的告警实体',
  'USER', 1,
  NULL, 'GIN',
  '["alarm"]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8c030a9ea7434ae293cdabc3e379c1c2', '企业地址', 'TEXT',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8e92b92f84e74cfd853cdd8cf4f6361a', '故障等级', 'ENUM',
  NULL, '故障影响等级',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "一般", "value": "minor"}, {"label": "较重", "value": "major"}, {"label": "严重", "value": "critical"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-910c98bffcab46c7a80c3def7cf5a953', '是否能云台控制、变焦', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-92d768ce991649fca38c345cf9959e05', '验收时间', 'DATETIME',
  NULL, '验收时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-94807db40a4b4e59a482888fca4b40df', '夜视功能', 'BOOLEAN',
  NULL, '是否支持夜视/红外功能',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9489c5ee50f5496d9128e6202987f83c', '管廊段起点', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-95373b75983148d7aaa6b83e36cd6c5b', '区间长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-981ca5abfa2840eeb5b4a97918029e9f', '费用(元)', 'NUMBER',
  '元', '维修费用（含人工/材料/外委）',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-984f2b3cfb6b4931a4dbc4506c116522', 'IP地址', 'TEXT',
  NULL, '设备网络IP地址',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9abe403c0a71458586572a4ea0837b03', '是否共设', 'BOOLEAN',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9c3d7e67947e470dbdecc30c6932f0db', '二氧化碳浓度', 'FLOAT',
  '%', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9c447cc5208a496289c108e57289df94', '队伍位置', 'COORDINATE',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9d05b9b3a3d24a6da5a41c8143bd448e', '外委单位', 'TEXT',
  NULL, '外委单位名称/联系人/电话',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9d6424ad23d94095a2421741cee572e5', '关联客户管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到客户管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9f5694f45fa34c72a667bbf2907859d2', '关联测试存储', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测试存储',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9f8725e4340e4dec9f5aba94499e48dd', '链接', 'LINK',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-a4922b3105df498d94557170f8b2c9da', '门窗是否关闭', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ace8c92532f7469d8026253f71b94ed3', '泵体及基础是否牢固', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ada5d166441647b39b0fa3a0aa45def7', '防火分区编号范围', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-aedecf6a99c248b689c6647ac230ac91', '电缆电压等级', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b035eeaa884243269178a06374ae8438', '优先级', 'ENUM',
  NULL, '工单优先级',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "低", "value": "low"}, {"label": "中", "value": "medium"}, {"label": "高", "value": "high"}, {"label": "紧急", "value": "urgent"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b20b046c96044c61998ade2424c45c20', 'ACU设备数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b32284d1d6f743b288c6b3244dc668bf', '存放地址', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b42b6e004eb944e993e00f5fa734a37a', '响应时间', 'NUMBER',
  '秒', '传感器响应时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b4677ba419b14289824604c3f30d2ea5', '显示编号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b5fdba42c7814e93a0a4966af64af608', '所属防火区', 'REFERENCE',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b86dc8d38487498d98f7e6901c459836', 'ACU设备ID', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b8a976c165824f848daac3c7bbe0fa1f', '评价备注', 'LONG_TEXT',
  NULL, '评价/回访备注',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b8b4fa8b0a4342348f69257aaa2101df', '结束位置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b93592352cb5449191b7d6e0037afd9b', '总部地址', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-bcb94455381c4776a9f2ccadf0a04897', '管廊段终点', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c053cdcd97ef45b19888124a89428550', '难度', 'ENUM',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"简单","value":"简单"},{"label":"困难","value":"困难"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c0598f91180140e387382ebb2b5855de', '开始时间', 'DATE',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c140d56fbb634e88a6a2feeb63f3b764', '维保到期', 'DATE',
  NULL, '设备维保到期日期',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c22a9c186dcc452f98cd7eaa5f3f565c', '负责人', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c367374d455f4024b29560be44f7c031', '维修类型', 'ENUM',
  NULL, '维修类型分类',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "故障维修", "value": "fault"}, {"label": "预防性维护", "value": "preventive"}, {"label": "紧急抢修", "value": "emergency"}, {"label": "整改工单", "value": "rectification"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c5627169a713492ca43b67a8f8a1f57c', '壁厚', 'NUMBER',
  'mm', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c59629795d7444949eb1f8d5e57e1492', '关联应急队伍', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到应急队伍',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c7d5a8c7a9904e5a9d11492072e922b6', '中心位置桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c89855b21acd4cae870535cc1e29b3f9', '所在道路名称', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c8dc3d61b1494b61a1197065fa41501d', '验收人', 'TEXT',
  NULL, '验收操作人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c95d3b404925410fb33a523369f80dce', '附件上传', 'UPLOAD',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c9c3f651b8e048c0a78521ae34a18f60', '共设长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cb25c908b0ff485199c1800d353d3db8', '联系人', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cc746ce0224145af88d5428d0b03213a', '所属区域', 'ENTITY_REF',
  NULL, '设备所属区域（关联区域分类）',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cd09db6ca3134996a6468594f64bc97e', '管廊尺寸', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ce35984ef8ac41db865b11d5a7c726c2', '长度', 'FLOAT',
  'cm', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cefb517417c643cdbd044929d1fe03f5', '处置结果', 'LONG_TEXT',
  NULL, '维修处置说明/更换部件/参数调整等',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d2660012902946e383052a6e3186cbca', '关联收费管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到收费管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d548b8e0ca654ab68222cbda0b08d17b', '关联测试业务', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测试业务',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d55683af52444a7587242fbb3542a932', '工作湿度', 'NUMBER',
  '%', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d81ed32c5c2a4e6288c70e76865dc063', '任务名称', 'STRING',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d8cf14b223bd41189e6dc48efd36b8bb', '有无泄露', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d9b5d2f7cb524d17bb2223a9e5a56f4c', '到场时间', 'DATETIME',
  NULL, '到场时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-dba78a08bcb04c3ca6a6a07c62dfd671', '功率', 'NUMBER',
  'kW', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-dc7aaac23a384e4b85ea70d2c35efe4a', '使用备件', 'LONG_TEXT',
  NULL, '备件清单（可写 JSON/文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ded6261e8b17477c9873ec0fd9d6c4a5', '关联检查内容', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到检查内容',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e0c3c64565e748d3b5a811014384e1e6', '电压', 'NUMBER',
  'V', '',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e18365a3a3ec4485ab699b46d6f820c2', '规格型号', 'TEXT',
  NULL, '设备规格型号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e30a052673b6438d86ee0a99585cb9bb', '质量检查', 'LONG_TEXT',
  NULL, '质量检查结果、测试数据等',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e3392e0afa374691a4795dd236cc6cb0', '安防人员是否在位', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e43b835f6f59492ab90a33abd53d9062', '入廊管线类型', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e4b8535544194dcf9de2cf178e316ea6', '安全员', 'ENTITY_REF',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e78e5b908e2a461f81776991f85863ed', '接线是否松动', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"是","value":"是"},{"label":"否","value":"否"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e81062a8ef8d4e8eb847543ca50d9279', '结束桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8289a51a3dd41c6b7f1539efd003258', '状态', 'ENUM',
  NULL, '工单状态（简化）',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "待受理", "value": "new"}, {"label": "待派工", "value": "to_dispatch"}, {"label": "已派工", "value": "dispatched"}, {"label": "处理中", "value": "in_progress"}, {"label": "已挂起", "value": "paused"}, {"label": "待验收", "value": "to_accept"}, {"label": "验收不通过", "value": "rejected"}, {"label": "已关闭", "value": "closed"}, {"label": "已取消", "value": "cancelled"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8b81a761f914a6d9384a620c4ec9565', '起始位置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8c7851b5ade42feb7f72eb1ab59b3b5', '高度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e9a39f2dbe0d425abea3ee1d4e911b6d', '备注', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ec6b7dec58434d76a6b0e9aeb79c197b', '完工时间', 'DATETIME',
  NULL, '维修完成时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ed97f195ccbe495d8766026635e4f5e3', '来源', 'ENUM',
  NULL, '工单来源',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "告警联动", "value": "alarm"}, {"label": "巡检发现", "value": "inspection"}, {"label": "人工报修", "value": "manual"}, {"label": "计划维护", "value": "plan"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-ACKT', '确认时间', 'DATETIME',
  NULL, '值班确认时间',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-ACKU', '确认人', 'STRING',
  NULL, '确认账号或姓名',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-CLRT', '清除时间', 'DATETIME',
  NULL, '告警恢复/清除时间',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-CODE', '告警编号', 'STRING',
  NULL, '平台内唯一告警流水号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-DET', '告警详情', 'LONG_TEXT',
  NULL, '快照值、阈值、附加属性 JSON 可读描述',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-LVL', '告警级别', 'ENUM',
  NULL, '处置优先级',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"提示","value":"INFO"},{"label":"一般","value":"WARN"},{"label":"重要","value":"MAJOR"},{"label":"紧急","value":"CRIT"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-SRC', '告警来源', 'STRING',
  NULL, '子系统、规则或采集通道',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-ST', '告警状态', 'ENUM',
  NULL, '处置闭环状态',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"未确认","value":"NEW"},{"label":"已确认","value":"ACK"},{"label":"处理中","value":"WORK"},{"label":"已清除","value":"CLEARED"},{"label":"已关闭","value":"CLOSED"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-TAG', '测点编码', 'STRING',
  NULL, '点位/标签编码',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-TITLE', '告警标题', 'STRING',
  NULL, '简述或规则名称',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-TRIG', '触发时间', 'DATETIME',
  NULL, '首次触发或当前次触发时间',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-A-TYPE', '告警类型', 'ENUM',
  NULL, '来源领域',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"设备","value":"DEV"},{"label":"动环","value":"ENV"},{"label":"安防","value":"SEC"},{"label":"工艺","value":"PROC"},{"label":"通信","value":"COMM"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-AMT', '合同金额', 'DECIMAL',
  NULL, '含税或协议金额（币种见币种字段）',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-CCY', '币种', 'STRING',
  NULL, 'CNY/USD 等',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-CNAME', '合同名称', 'STRING',
  NULL, '合同标题',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-CNO', '合同编号', 'STRING',
  NULL, '合同唯一编号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-EFFD', '生效日期', 'DATE',
  NULL, '合同起算日',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-EXPD', '到期日期', 'DATE',
  NULL, '合同截止日',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-PA', '甲方', 'STRING',
  NULL, '甲方主体名称',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-PAY', '付款条件', 'LONG_TEXT',
  NULL, '账期、里程碑付款说明',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-PB', '乙方', 'STRING',
  NULL, '乙方主体名称',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-SIGND', '签订日期', 'DATE',
  NULL, '签署日期',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-C-TYPE', '合同类型', 'ENUM',
  NULL, '合同业务类型',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"采购","value":"PUR"},{"label":"销售","value":"SAL"},{"label":"服务","value":"SVC"},{"label":"框架","value":"FRM"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-ASSETNO', '设备资产编号', 'STRING',
  NULL, '唯一资产标签/台账编号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-DEVNAME', '设备名称', 'STRING',
  NULL, '资产显示名称',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-LOCATION', '安装位置', 'STRING',
  NULL, '区域/站点/工艺位置描述',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-MFR', '制造商', 'STRING',
  NULL, '生产厂家或品牌',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-OWNER', '设备负责人', 'STRING',
  NULL, '运维责任人（可后续改为人员关联）',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-PMLD', '最近保养日期', 'DATE',
  NULL, '上次预防性维护日期',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-RATEDKW', '额定功率', 'DECIMAL',
  'kW', '额定功率（kW），用于能耗分析',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-SERIAL', '出厂序列号', 'STRING',
  NULL, '设备出厂 SN',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-D-WEND', '质保到期日', 'DATE',
  NULL, '保修截止日',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-CODE', '客户编码', 'STRING',
  NULL, '主数据编码',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-CRED', '信用评级', 'ENUM',
  NULL, '风控/账期参考',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"A","value":"A"},{"label":"B","value":"B"},{"label":"C","value":"C"},{"label":"D","value":"D"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-CT', '主要联系人', 'STRING',
  NULL, '对接人姓名',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-EMAIL', '联系邮箱', 'STRING',
  NULL, '电子邮箱',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-IND', '所属行业', 'STRING',
  NULL, '行业分类',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-LVL', '客户等级', 'ENUM',
  NULL, '分层经营标识',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"普通","value":"STD"},{"label":"VIP","value":"VIP"},{"label":"战略","value":"STR"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-NAME', '客户名称', 'STRING',
  NULL, '客户全称',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-PHONE', '联系电话', 'STRING',
  NULL, '联系电话',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-REG', '所在地区', 'STRING',
  NULL, '省市区或销售片区',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-K-SALES', '销售负责人', 'STRING',
  NULL, '客户经理',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-ACTN', '维修措施', 'LONG_TEXT',
  NULL, '处理步骤与工艺说明',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-CAT', '故障类别', 'ENUM',
  NULL, '专业分类',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"机械","value":"MECH"},{"label":"电气","value":"ELEC"},{"label":"仪表","value":"INST"},{"label":"其他","value":"OTHER"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-DONE', '完成时间', 'DATETIME',
  NULL, '维修完工时间',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-DOWNH', '停机时长', 'DECIMAL',
  NULL, '停机小时数',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-PARTS', '更换配件', 'STRING',
  NULL, '备件/耗材说明',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-SEV', '严重等级', 'ENUM',
  NULL, '对生产/安全的影响',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"一般","value":"P3"},{"label":"重要","value":"P2"},{"label":"紧急","value":"P1"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-ST', '维修状态', 'ENUM',
  NULL, '维修流程节点',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"待接单","value":"OPEN"},{"label":"维修中","value":"WORKING"},{"label":"待验收","value":"VERIFY"},{"label":"已关闭","value":"CLOSED"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-SYM', '故障现象', 'LONG_TEXT',
  NULL, '异常描述与报修信息',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-TAG', '关联设备位号', 'STRING',
  NULL, '工艺位号或资产定位编码',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-TECH', '维修人员', 'STRING',
  NULL, '执行人',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-M-WONO', '维修工单号', 'STRING',
  NULL, '维保单据号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-ACTD', '实际维护日', 'DATE',
  NULL, '实际完成日期',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-CYC', '维护周期', 'STRING',
  NULL, '如：每月、每季、运行 500h',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-DESC', '维护内容', 'LONG_TEXT',
  NULL, '检查与保养项说明',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-HRS', '维护工时', 'DECIMAL',
  NULL, '投入人工小时',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-KIND', '维护类型', 'ENUM',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"日常保养","value":"DAILY"},{"label":"定期点检","value":"INSPECT"},{"label":"润滑","value":"LUBE"},{"label":"校准","value":"CAL"},{"label":"更换耗材","value":"CONS"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-NEXT', '下次维护日', 'DATE',
  NULL, '下次计划日期',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-OWNER', '维护责任人', 'STRING',
  NULL, '计划负责人',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-PCODE', '维护计划编号', 'STRING',
  NULL, '维保计划或标准包编码',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-PLND', '计划维护日', 'DATE',
  NULL, '本次计划执行日期',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-RATE', '检查项完成率', 'INTEGER',
  NULL, '完成项占百分比 0-100',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-P-RES', '维护结果', 'ENUM',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"正常","value":"OK"},{"label":"异常","value":"NOK"},{"label":"延期","value":"DELAY"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-ACE', '实际结束时间', 'DATETIME',
  NULL, '实际完工',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-ACS', '实际开始时间', 'DATETIME',
  NULL, '实际开工',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-CODE', '任务编号', 'STRING',
  NULL, '流程/工单流水号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-PCT', '任务进度', 'INTEGER',
  NULL, '完成百分比 0-100',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-PLE', '计划结束时间', 'DATETIME',
  NULL, '计划完工',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-PLS', '计划开始时间', 'DATETIME',
  NULL, '计划开工',
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-STAT', '任务状态', 'ENUM',
  NULL, '执行阶段',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"待办","value":"TODO"},{"label":"进行中","value":"DOING"},{"label":"阻塞","value":"BLOCKED"},{"label":"已完成","value":"DONE"},{"label":"已取消","value":"CANCELLED"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-TITLE', '任务标题', 'STRING',
  NULL, '任务摘要',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-T-TYPE', '任务类型', 'ENUM',
  NULL, '任务分类',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"需求","value":"REQ"},{"label":"缺陷","value":"BUG"},{"label":"变更","value":"CHG"},{"label":"日常","value":"OPS"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-ACE', '实际完工', 'DATETIME',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-ACS', '实际开工', 'DATETIME',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-DEPT', '请求部门', 'STRING',
  NULL, '需求提出部门',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-DESC', '工作描述', 'LONG_TEXT',
  NULL, '作业范围及步骤摘要',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-KIND', '工单类型', 'ENUM',
  NULL, '作业类别',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"检修","value":"OH"},{"label":"缺陷","value":"DEF"},{"label":"技改","value":"MOD"},{"label":"外委","value":"OUT"},{"label":"其他","value":"OTH"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-NO', '工单编号', 'STRING',
  NULL, '现场作业单唯一号',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-PLE', '计划完工', 'DATETIME',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-PLS', '计划开工', 'DATETIME',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-SAFE', '安全措施', 'STRING',
  NULL, '隔离、挂牌、动火等要求摘要',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-SIGN', '签发人', 'STRING',
  NULL, '工作许可签发',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-ST', '工单状态', 'ENUM',
  NULL, '派发与执行状态',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"草稿","value":"DRAFT"},{"label":"已派发","value":"DISP"},{"label":"执行中","value":"EXEC"},{"label":"待验收","value":"VERIFY"},{"label":"已完成","value":"DONE"},{"label":"已关闭","value":"CLOSED"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ENT-W-TEAM', '执行班组', 'STRING',
  NULL, '负责班组或承包商',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f2bbdc83208843e28026f785c7195ce5', '通信方式', 'ENUM',
  '', '',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "RS485", "value": "RS485"}, {"label": "Modbus", "value": "Modbus"}, {"label": "4G", "value": "4G"}, {"label": "WiFi", "value": "WiFi"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f3dd829ea93b4666963866d6340d9d8a', '排风机数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f79945273262403ca68c2894da465a63', '部署安装内容', 'TEXT',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f8b34842206643ddaf086605f15d2288', '点位位置', 'ENTITY_REF',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f8d26649047f4f719c6835ce513a8748', '关联应急管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联应急管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f91ec5e752884faa8a792860f2086e0f', '有无腐蚀', 'BOOLEAN',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f96d271a9acf48f894d9aff1a06d2a9e', '客户住址', 'TEXT',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f9afe7748fb040f29c8f3450e48c30d3', '队伍状态', 'ENUM',
  '', '',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"出动","value":"出动"},{"label":"待命","value":"待命"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id', '所属设备管理', 'ENTITY_REF',
  NULL, '系统自动创建的关联字段，关联到 可燃气体探测器',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
