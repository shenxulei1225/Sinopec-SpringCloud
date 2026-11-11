-- ============================================================================
-- 报修（maintanance）标准字段与类型扩展字段库条目（tenant_id=1）
-- 用途：补齐报修时间/报修人等标准码；电脑/灯/阀门扩展枚举与文本。
-- 落库后仍须走「业务类型基础字段 create」与「型号 batch-assign」权威接口挂到型号；
-- 本脚本只负责字段库稳定编码，不直接改型号分配、不写专用表列。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, options, provider_code, semantic_type,
  index_strategy, tenant_id, creator, create_time, deleted
)
VALUES
  ('report_time', '报修时间', 'DATETIME', '报修业务标准字段：诉求发生/上报时间', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('reporter', '报修人', 'TEXT', '报修业务标准字段：上报人', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('location_description', '位置描述', 'TEXT', '报修业务标准字段：位置说明', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('ip_address', 'IP地址', 'TEXT', '电脑报修扩展：网络地址', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('host_name', '主机名', 'TEXT', '电脑报修扩展：主机名', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('computer_fault_category', '电脑故障分类', 'ENUM', '电脑报修扩展', 'BASE', 1,
   '[{"label":"无法开机","value":"power_off"},{"label":"蓝屏死机","value":"bsod"},{"label":"软件异常","value":"software"},{"label":"网络不通","value":"network"},{"label":"外设故障","value":"peripheral"},{"label":"其他","value":"other"}]'::jsonb,
   NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('lamp_pole_no', '灯杆号', 'TEXT', '灯报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('circuit_no', '回路号', 'TEXT', '灯报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('light_fault_symptom', '灯具故障表现', 'ENUM', '灯报修扩展', 'BASE', 1,
   '[{"label":"不亮","value":"dark"},{"label":"闪烁","value":"flicker"},{"label":"昼亮夜灭","value":"day_on_night_off"},{"label":"灯罩破损","value":"cover_broken"},{"label":"其他","value":"other"}]'::jsonb,
   NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('whole_circuit_fault', '是否整回路故障', 'BOOLEAN', '灯报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('distribution_box_note', '配电箱或回路位置', 'TEXT', '灯报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('valve_tag', '阀门位号', 'TEXT', '阀门报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('well_no', '井号', 'TEXT', '阀门报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('is_leak', '是否泄漏', 'BOOLEAN', '阀门报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('medium_pressure_note', '介质或压力现象', 'TEXT', '阀门报修扩展', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false),
  ('handle_time', '受理时间', 'DATETIME', '报修处置：受理时刻（与报修时间区分）', 'BASE', 1, NULL, NULL, NULL, 'NONE', 1, 'seed-repair-std', NOW(), false)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = COALESCE(EXCLUDED.options, dynamic_field.options),
  updater = 'seed-repair-std',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field SET options = '[
  {"label":"紧急","value":"urgent"},
  {"label":"高","value":"high"},
  {"label":"中","value":"medium"},
  {"label":"低","value":"low"}
]'::jsonb, updater='seed-repair-std', update_time=NOW()
WHERE code='priority' AND tenant_id=1 AND deleted=false;

UPDATE dynamic_field SET options = '[
  {"label":"外漏","value":"external_leak"},
  {"label":"内漏","value":"internal_leak"},
  {"label":"卡死","value":"stuck"},
  {"label":"开不到位","value":"not_full_open"},
  {"label":"执行器故障","value":"actuator"},
  {"label":"其他","value":"other"}
]'::jsonb, name='阀门故障类型', updater='seed-repair-std', update_time=NOW()
WHERE code='fault_type' AND tenant_id=1 AND deleted=false;
