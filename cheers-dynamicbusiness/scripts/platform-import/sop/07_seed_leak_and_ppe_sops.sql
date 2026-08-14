-- ============================================================================
-- sop · 07 跑冒滴漏 / 工服穿戴检测（同一型号 sop 下的实例）
-- 步骤形状：code/order/title/required/stepType/description/parameterSlots
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 纠正：勿把具体 SOP 建成型号；软删误建在 sop 类型下的「跑冒滴漏检查流程」型号
UPDATE dynamic_model
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'sop'
  AND name = '跑冒滴漏检查流程';

INSERT INTO ent_sop_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, domain,
  version_no, publish_status, steps_json, creator, deleted
)
SELECT
  1,
  'sop',
  m.id,
  '跑冒滴漏检查',
  'SOP-LEAK-RUNDRIP-01',
  1,
  'inspection',
  1,
  'PUBLISHED',
  '[
    {
      "code": "s1",
      "order": 1,
      "title": "确认检查对象与安全条件",
      "required": true,
      "stepType": "confirm",
      "description": "核对设备/管段标识；确认可近距离观察，无动火等冲突作业",
      "parameterSlots": []
    },
    {
      "code": "s2",
      "order": 2,
      "title": "静密封点巡查",
      "required": true,
      "stepType": "inspect",
      "description": "法兰、丝扣、焊口、阀门填料函、仪表接头等静密封点，观察渗漏、油迹、结晶、异味",
      "parameterSlots": []
    },
    {
      "code": "s3",
      "order": 3,
      "title": "动密封与放空点巡查",
      "required": true,
      "stepType": "inspect",
      "description": "机泵机械密封、填料函、放空阀、取样阀、排水阀等，观察跑、冒、滴、漏",
      "parameterSlots": []
    },
    {
      "code": "s4",
      "order": 4,
      "title": "地面与附属设施",
      "required": true,
      "stepType": "inspect",
      "description": "接油盘、地沟、防火堤内积液、异常潮湿或油污",
      "parameterSlots": []
    },
    {
      "code": "s5",
      "order": 5,
      "title": "异常取证",
      "required": false,
      "stepType": "capture",
      "description": "发现异常时拍照或录像，标明位置与现象",
      "parameterSlots": [
        {"slotKey": "evidence_note", "valueType": "text", "required": false}
      ]
    },
    {
      "code": "s6",
      "order": 6,
      "title": "记录结论",
      "required": true,
      "stepType": "confirm",
      "description": "填写正常/异常；异常须写清部位与程度，并按规定上报",
      "parameterSlots": [
        {"slotKey": "conclusion", "valueType": "text", "required": true}
      ]
    }
  ]'::jsonb,
  'seed',
  false
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = 'SOP-LEAK-RUNDRIP-01'
  );

INSERT INTO ent_sop_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, domain,
  version_no, publish_status, steps_json, creator, deleted
)
SELECT
  1,
  'sop',
  m.id,
  '工服穿戴检测',
  'SOP-PPE-UNIFORM-01',
  1,
  'inspection',
  1,
  'PUBLISHED',
  '[
    {
      "code": "s1",
      "order": 1,
      "title": "确认检测场景",
      "required": true,
      "stepType": "confirm",
      "description": "确认作业区域、岗位要求的着装标准（工服、安全帽、劳保鞋等）",
      "parameterSlots": []
    },
    {
      "code": "s2",
      "order": 2,
      "title": "头部与躯干着装",
      "required": true,
      "stepType": "inspect",
      "description": "安全帽佩戴规范；工服整洁、纽扣/拉链到位；反光标识可见（若要求）",
      "parameterSlots": []
    },
    {
      "code": "s3",
      "order": 3,
      "title": "手足与防护用品",
      "required": true,
      "stepType": "inspect",
      "description": "劳保鞋、手套、护目镜等按岗位要求齐全且正确穿戴",
      "parameterSlots": []
    },
    {
      "code": "s4",
      "order": 4,
      "title": "禁穿与违章项",
      "required": true,
      "stepType": "inspect",
      "description": "检查是否穿拖鞋、短裤、赤膊等违章着装；是否佩戴与作业冲突的饰品",
      "parameterSlots": []
    },
    {
      "code": "s5",
      "order": 5,
      "title": "取证（可选）",
      "required": false,
      "stepType": "capture",
      "description": "不合格或抽查留档时拍照",
      "parameterSlots": [
        {"slotKey": "evidence_note", "valueType": "text", "required": false}
      ]
    },
    {
      "code": "s6",
      "order": 6,
      "title": "记录结论",
      "required": true,
      "stepType": "confirm",
      "description": "合格/不合格；不合格写明缺项并要求当场整改或登记",
      "parameterSlots": [
        {"slotKey": "conclusion", "valueType": "text", "required": true}
      ]
    }
  ]'::jsonb,
  'seed',
  false
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = 'SOP-PPE-UNIFORM-01'
  );
