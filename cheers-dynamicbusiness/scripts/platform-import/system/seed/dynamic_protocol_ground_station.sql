-- 巡检地面站 WebSocket 指令协议（知仁机器人 v0.5.8 / 东方无人机 v0.5.2）
-- 权威：参考资料 docx + 进行指令报文.txt + 地面站迁移设计定稿
-- 写入租户物理表；导入后须 evict 分类树缓存
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, tree_path, level, sort, status,
  description, tenant_id, creator, parent_code
)
SELECT p.id, v.name, v.code, 'data_protocol', p.tree_path, COALESCE(p.level,1)+1, v.sort, 1,
       v.description, 1, 'seed', p.code
FROM dynamic_category p
JOIN (VALUES
  ('cat-protocol-gs', '巡检', 5, '巡检共用外层指令'),
  ('cat-protocol-robot', '机器人', 6, '机器人对接协议指令'),
  ('cat-protocol-uav', '无人机', 7, '无人机对接协议指令')
) AS v(code, name, sort, description)
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'data_protocol_root'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = v.code
);

UPDATE dynamic_category c
SET tree_path = p.tree_path || c.id || '/',
    level = COALESCE(p.level, 1) + 1,
    parent_code = p.code,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category p
WHERE c.deleted = false AND c.tenant_id = 1 AND c.code LIKE 'cat-protocol-%'
  AND p.id = c.parent_id
  AND (c.tree_path IS NULL OR c.tree_path = p.tree_path OR c.tree_path NOT LIKE p.tree_path || c.id || '/%');

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT m.id, c.id, 'data_protocol', m.sort, 'seed', 1, m.code, c.code, false
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1
 AND c.code IN ('cat-protocol-gs', 'cat-protocol-robot', 'cat-protocol-uav')
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_ws'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation_t1 r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_code = m.code AND r.category_code = c.code
      AND r.entity_type_code = 'data_protocol'
  );

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT 1, 'data_protocol', m.id, v.name, v.code, 1, v.custom_fields::jsonb, 'seed', false
FROM dynamic_model m
JOIN (VALUES
  ('proto-gs-500101-status', '地面站·获取设备实时状态指令', $${"opcode":500101,"command_schema_json":{"outbound":{"msgId":"","opcode":500101,"deviceId":""},"inboundSuccess":{"msg":"","code":0,"data":{"lat":"","lng":"","time":0,"fault":0,"height":"","status":0,"dataLink":0,"deviceId":"","electricity":"","faultReasion":""},"msgId":"","opcode":500101},"inboundFailure":{"msg":"","code":0,"msgId":"","opcode":500101}},"field_translation_rules_json":{"outbound":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"指令编码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true}],"inboundSuccess":[{"code":"opcode","label":"指令编码","path":"opcode","type":"integer","required":true},{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"code","label":"应答码","path":"code","type":"integer","required":true},{"code":"msg","label":"应答说明","path":"msg","type":"string","required":false},{"code":"data","label":"状态数据","path":"data","type":"object","required":true},{"code":"data.deviceId","label":"设备逻辑标识","path":"data.deviceId","type":"string","required":true},{"code":"data.time","label":"状态时间","path":"data.time","type":"integer","required":false},{"code":"data.status","label":"设备状态码","path":"data.status","type":"integer","required":false},{"code":"data.fault","label":"设备故障码","path":"data.fault","type":"integer","required":false},{"code":"data.faultReasion","label":"故障原因","path":"data.faultReasion","type":"string","required":false},{"code":"data.dataLink","label":"数据链路状态","path":"data.dataLink","type":"integer","required":false},{"code":"data.electricity","label":"设备电压","path":"data.electricity","type":"string","required":false},{"code":"data.lat","label":"纬度","path":"data.lat","type":"string","required":false},{"code":"data.lng","label":"经度","path":"data.lng","type":"string","required":false},{"code":"data.height","label":"GPS海拔高度","path":"data.height","type":"string","required":false}],"inboundFailure":[{"code":"opcode","label":"指令编码","path":"opcode","type":"integer","required":true},{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"code","label":"应答码","path":"code","type":"integer","required":true},{"code":"msg","label":"失败原因","path":"msg","type":"string","required":true}]},"step_signal_mapping_rules_json":[],"sample_messages_json":{"outbound":{"msgId":"1618987228144","opcode":500101,"deviceId":"2c8ac20fa02f4912aa06f9535cddfa2caas"},"inboundSuccess":{"opcode":500101,"msgId":"1618987228144","code":200,"msg":"ok","data":{"deviceId":"2c8ac20fa02f4912aa06f9535cddfa2caas","time":1624511261,"status":400101,"fault":100000,"faultReasion":"no error","dataLink":1,"electricity":"82.9","lat":"31.0383416830","lng":"121.2668456836","height":"17.874"}},"inboundFailure":{"opcode":500101,"msgId":"1618987228144","code":500,"msg":"设备离线"}}}$$),
  ('proto-gs-500102-report-switch', '地面站·启动或关闭定时上报指令', $${"opcode":500102,"command_schema_json":{"msgId":"","opcode":500102,"deviceId":"","enable":false,"frequency":0,"total":0},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"enable","label":"是否启动上报","path":"enable","type":"boolean","required":false},{"code":"frequency","label":"上报频率（秒）","path":"frequency","type":"integer","required":false},{"code":"total","label":"上报总次数（-1不限制）","path":"total","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"msgId":"1618987228145","opcode":500102,"deviceId":"2c8ac20fa02f4912aa06f9535cddfa2caas","enable":true,"frequency":10,"total":100}}$$),
  ('proto-gs-500103-report-push', '地面站·定时上报设备状态指令', $${"opcode":500103,"command_schema_json":{"opcode":500103,"data":{"deviceId":"","electricity":"","lat":"","lng":""}},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"data","label":"data","path":"data","type":"object","required":false},{"code":"data.deviceId","label":"设备逻辑标识","path":"data.deviceId","type":"string","required":true},{"code":"data.time","label":"状态时间","path":"data.time","type":"string","required":false},{"code":"data.status","label":"设备状态码","path":"data.status","type":"string","required":false},{"code":"data.fault","label":"设备故障码","path":"data.fault","type":"string","required":false},{"code":"data.electricity","label":"设备电压","path":"data.electricity","type":"string","required":false},{"code":"data.lat","label":"纬度","path":"data.lat","type":"string","required":false},{"code":"data.lng","label":"经度","path":"data.lng","type":"string","required":false},{"code":"data.height","label":"GPS海拔高度","path":"data.height","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500103,"data":{"deviceId":"demo","electricity":"80","lat":"31.0","lng":"121.2"}}}$$),
  ('proto-gs-500105-heartbeat', '地面站·心跳指令', $${"opcode":500105,"command_schema_json":{"opcode":500105,"msgId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500105,"msgId":"hb-1"}}$$),
  ('proto-gs-500106-ack', '地面站·应答ACK指令', $${"opcode":500106,"command_schema_json":{"opcode":500106,"msgId":"","data":{"opcode":500105}},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"data","label":"data","path":"data","type":"object","required":false},{"code":"data.opcode","label":"被应答的外层操作码","path":"data.opcode","type":"integer","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500106,"msgId":"ack-1","data":{"opcode":500105}}}$$),
  ('proto-gs-500201-start', '地面站·启动执行已下发任务指令', $${"opcode":500201,"command_schema_json":{"opcode":500201,"msgId":"","taskId":"","deviceId":"","templateId":"","executionTime":0},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"executionTime","label":"计划执行时间","path":"executionTime","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500201,"msgId":"1617007530982","taskId":"111122223301","deviceId":"2c8ac20fa02f4912aa06f9535cddfa2caas","templateId":"100112","executionTime":1783318683}}$$),
  ('proto-gs-500202-action-result', '地面站·单条指令操作结果', $${"opcode":500202,"command_schema_json":{"opcode":500202,"taskId":"","packages":[{"sequence":0,"reportPoint":{"pointId":""},"result":{"resultCode":0,"msg":""}}]},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"packages","label":"指令结果列表","path":"packages","type":"array","required":true},{"code":"packages.sequence","label":"动作序号","path":"packages[].sequence","type":"integer","required":true},{"code":"packages.reportPoint","label":"reportPoint","path":"packages[].reportPoint","type":"object","required":false},{"code":"packages.reportPoint.pointId","label":"上报点位标识","path":"packages[].reportPoint.pointId","type":"string","required":false},{"code":"packages.result","label":"result","path":"packages[].result","type":"object","required":false},{"code":"packages.result.resultCode","label":"指令执行结果码","path":"packages[].result.resultCode","type":"integer","required":false},{"code":"packages.result.msg","label":"操作结果说明","path":"packages[].result.msg","type":"string","required":false},{"code":"packages.result.operatePoint","label":"执行时操作点","path":"packages[].result.operatePoint","type":"string","required":false},{"code":"packages.result.operatePoint.lat","label":"操作点纬度","path":"packages[].result.operatePoint.lat","type":"string","required":false},{"code":"packages.result.operatePoint.lng","label":"操作点经度","path":"packages[].result.operatePoint.lng","type":"string","required":false},{"code":"packages.result.operatePoint.height","label":"操作点高度（GPS海拔）","path":"packages[].result.operatePoint.height","type":"string","required":false},{"code":"packages.result.operatePoint.pointId","label":"操作点位标识","path":"packages[].result.operatePoint.pointId","type":"string","required":false},{"code":"packages.result.operatePoint.optime","label":"操作时间（秒级时间戳）","path":"packages[].result.operatePoint.optime","type":"string","required":false},{"code":"packages.result.operatePoint.electric","label":"当前电压百分比","path":"packages[].result.operatePoint.electric","type":"string","required":false},{"code":"packages.result.operatePoint.distance","label":"行驶距离","path":"packages[].result.operatePoint.distance","type":"string","required":false},{"code":"packages.result.operatePoint.fault","label":"设备故障码","path":"packages[].result.operatePoint.fault","type":"string","required":false},{"code":"packages.result.operatePoint.status","label":"设备任务状态","path":"packages[].result.operatePoint.status","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500202,"taskId":"111122223301","packages":[{"sequence":0,"reportPoint":{"pointId":"p1"},"result":{"resultCode":200,"msg":"ok"}}]}}$$),
  ('proto-gs-500203-task-status', '地面站·任务状态指令', $${"opcode":500203,"command_schema_json":{"opcode":500203,"status":0,"taskId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"status","label":"任务状态码","path":"status","type":"integer","required":false}],"step_signal_mapping_rules_json":[{"ruleId":"task-status","stepRefType":"NODE_KEY","stepRef":"n-task","statusPath":"status","statusMap":{"300101":"RUNNING","300102":"FAILED","300103":"DONE","300104":"FAILED","300105":"DONE"}}],"sample_messages_json":{"opcode":500203,"status":300101,"taskId":"111122223301"}}$$),
  ('proto-gs-500204-fault', '地面站·设备故障指令', $${"opcode":500204,"command_schema_json":{"opcode":500204,"fault":0,"faultReasion":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"fault","label":"设备故障码","path":"fault","type":"integer","required":false},{"code":"faultReasion","label":"故障原因","path":"faultReasion","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500204,"fault":100001,"faultReasion":"motor error"}}$$),
  ('proto-gs-500205-stop', '地面站·任务终止指令', $${"opcode":500205,"command_schema_json":{"opcode":500205,"msgId":"","taskId":"","deviceId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500205,"msgId":"1617007530982","taskId":"111122223301","deviceId":"2c8ac20fa02f4912aa06f9535cddfa2caas"}}$$),
  ('proto-gs-500301-get-package', '地面站·获取指令包数据', $${"opcode":500301,"command_schema_json":{"opcode":500301,"msgId":"","templateId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500301,"msgId":"1617007530982","templateId":"100111"}}$$),
  ('proto-gs-500302-delete-package', '地面站·删除指令包数据', $${"opcode":500302,"command_schema_json":{"opcode":500302,"msgId":"","templateId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500302,"msgId":"1617007530986","templateId":"100111"}}$$),
  ('proto-gs-500303-list-templates', '地面站·列出任务模板编号指令', $${"opcode":500303,"command_schema_json":{"opcode":500303,"msgId":""},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500303,"msgId":"1617007530984"}}$$),
  ('proto-gs-500403-online-event', '地面站·在线或掉线事件指令', $${"opcode":500403,"command_schema_json":{"opcode":500403,"event":"","time":0},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"event","label":"事件类型","path":"event","type":"string","required":false},{"code":"time","label":"事件时间","path":"time","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":500403,"event":"online","time":1624511261}}$$),
  ('proto-robot-200102-move', '机器人·移动指令', $${"opcode":200102,"command_schema_json":{"sequence":0,"opcode":200102,"request":{"pointId":"","lat":"","lng":"","heading":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.pointId","label":"对应机器人勘察地图上的点位id","path":"request.pointId","type":"string","required":false},{"code":"request.lat","label":"纬度","path":"request.lat","type":"string","required":false},{"code":"request.lng","label":"经度","path":"request.lng","type":"string","required":false},{"code":"request.heading","label":"航向","path":"request.heading","type":"number","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200102,"request":{"pointId":"16de65073e804a4898305c5886246467","lat":"39.1277217676","lng":"117.0190597072","heading":179.0656}}}$$),
  ('proto-robot-200301-photo', '机器人·拍照指令', $${"opcode":200301,"command_schema_json":{"sequence":0,"opcode":200301,"request":{"parking":false,"number":0,"pointId":"","lat":"","lng":"","heading":0,"angleVertical":"","focuses":"","time":0,"angleLevel":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.lat","label":"拍摄目标的纬度","path":"request.lat","type":"string","required":false},{"code":"request.lng","label":"拍摄目标的经度","path":"request.lng","type":"string","required":false},{"code":"request.height","label":"拍摄目标的地面高度","path":"request.height","type":"string","required":false},{"code":"request.angleLevel","label":"拍摄目标的偏角","path":"request.angleLevel","type":"string","required":false},{"code":"request.angleVertical","label":"拍摄目标的仰角","path":"request.angleVertical","type":"string","required":false},{"code":"request.number","label":"要求拍摄的照片张数","path":"request.number","type":"integer","required":false},{"code":"request.focuses","label":"照相机的焦距","path":"request.focuses","type":"string","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false},{"code":"request.pointId","label":"点位标识","path":"request.pointId","type":"string","required":false},{"code":"request.heading","label":"航向","path":"request.heading","type":"number","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":3,"opcode":200301,"request":{"parking":true,"number":1,"pointId":"16de65073e804a4898305c5886246467","lat":"39.1277217676","lng":"117.0190597072","heading":179.0656,"angleVertical":"10","focuses":"2","time":20,"angleLevel":"10"}}}$$),
  ('proto-robot-200501-status-report', '机器人·定时上报设备状态指令', $${"opcode":200501,"command_schema_json":{"opcode":200501,"request":{}},"field_translation_rules_json":[{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":200501,"request":{}}}$$),
  ('proto-robot-200403-gas-report', '机器人·气体检测数据上报指令', $${"opcode":200403,"command_schema_json":{"opcode":200403,"collect":{"CH4":"","H2S":""}},"field_translation_rules_json":[{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"collect","label":"collect","path":"collect","type":"object","required":false},{"code":"collect.CH4","label":"甲烷浓度值","path":"collect.CH4","type":"string","required":false},{"code":"collect.H2S","label":"硫化氢浓度值","path":"collect.H2S","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"opcode":200403,"collect":{"CH4":"0.1","H2S":"0.0"}}}$$),
  ('proto-robot-500104-package', '机器人·巡检任务指令包下发', $${"opcode":500104,"command_schema_json":{"msgId":"","opcode":500104,"templateId":"","packages":[{"request":{"pointId":"","lat":"","lng":"","heading":0},"sequence":0,"opcode":200102},{"request":{"parking":"","number":"","pointId":"","lat":"","lng":"","heading":0,"angleVertical":"","focuses":"","time":"","angleLevel":""},"sequence":0,"opcode":200301}]},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"packages","label":"有序动作集合","path":"packages","type":"array","required":true},{"code":"packages.sequence","label":"动作序号","path":"packages[].sequence","type":"integer","required":true},{"code":"packages.opcode","label":"内层动作码","path":"packages[].opcode","type":"integer","required":true},{"code":"packages.request","label":"动作请求参数（随内层动作码变化）","path":"packages[].request","type":"object","required":true},{"code":"packages.request.pointId","label":"点位标识","path":"packages[].request.pointId","type":"string","required":false},{"code":"packages.request.lat","label":"拍摄目标的纬度","path":"packages[].request.lat","type":"string","required":false},{"code":"packages.request.lng","label":"拍摄目标的经度","path":"packages[].request.lng","type":"string","required":false},{"code":"packages.request.heading","label":"航向","path":"packages[].request.heading","type":"number","required":false},{"code":"packages.request.height","label":"拍摄目标的地面高度","path":"packages[].request.height","type":"string","required":false},{"code":"packages.request.angleLevel","label":"拍摄目标的偏角","path":"packages[].request.angleLevel","type":"string","required":false},{"code":"packages.request.angleVertical","label":"拍摄目标的仰角","path":"packages[].request.angleVertical","type":"string","required":false},{"code":"packages.request.number","label":"要求拍摄的照片张数","path":"packages[].request.number","type":"string","required":false},{"code":"packages.request.focuses","label":"照相机的焦距","path":"packages[].request.focuses","type":"string","required":false},{"code":"packages.request.parking","label":"该操作是否需要驻停","path":"packages[].request.parking","type":"string","required":false},{"code":"packages.request.time","label":"驻停时长（秒）","path":"packages[].request.time","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"msgId":"ce577e3c81af4687b55a3982abc28a70","opcode":500104,"templateId":"1001234","packages":[{"request":{"pointId":"16de65073e804a4898305c5886246467","lat":"39.1277217676","lng":"117.0190597072","heading":179.0656},"sequence":0,"opcode":200102},{"request":{"parking":"true","number":"1","pointId":"16de65073e804a4898305c5886246467","lat":"39.1277217676","lng":"117.0190597072","heading":179.0656,"angleVertical":"10","focuses":"2","time":"20","angleLevel":"10"},"sequence":3,"opcode":200301}]}}$$),
  ('proto-uav-200101-takeoff', '无人机·起飞指令', $${"opcode":200101,"command_schema_json":{"sequence":0,"opcode":200101,"request":{"lng":"","lat":"","height":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.lng","label":"起飞阶段截止点的经度","path":"request.lng","type":"string","required":false},{"code":"request.lat","label":"起飞阶段截止点的纬度","path":"request.lat","type":"string","required":false},{"code":"request.height","label":"起飞阶段截止点的高度","path":"request.height","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200101,"request":{"lng":"117.0190597072","lat":"39.1277217676","height":"30"}}}$$),
  ('proto-uav-200102-move', '无人机·移动指令', $${"opcode":200102,"command_schema_json":{"sequence":0,"opcode":200102,"request":{"lng":"","lat":"","height":"","pointId":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.lng","label":"指定位置点的经度","path":"request.lng","type":"string","required":false},{"code":"request.lat","label":"指定位置点的纬度","path":"request.lat","type":"string","required":false},{"code":"request.height","label":"指定位置点的地面高度","path":"request.height","type":"string","required":false},{"code":"request.pointId","label":"对应无人机勘察地图上的点位id","path":"request.pointId","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200102,"request":{"lng":"117.0190597072","lat":"39.1277217676","height":"30","pointId":"uav-p1"}}}$$),
  ('proto-uav-200103-land', '无人机·降落指令', $${"opcode":200103,"command_schema_json":{"sequence":0,"opcode":200103,"request":{"blank":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.blank","label":"占位字段，默认填写0000","path":"request.blank","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200103,"request":{"blank":"0000"}}}$$),
  ('proto-uav-200104-descend', '无人机·降低飞行高度指令', $${"opcode":200104,"command_schema_json":{"sequence":0,"opcode":200104,"request":{"height":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.height","label":"目标地面高度（米）","path":"request.height","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200104,"request":{"height":"15"}}}$$),
  ('proto-uav-200105-climb', '无人机·爬升飞行高度指令', $${"opcode":200105,"command_schema_json":{"sequence":0,"opcode":200105,"request":{"height":""}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.height","label":"目标地面高度（米）","path":"request.height","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200105,"request":{"height":"40"}}}$$),
  ('proto-uav-200201-record-on', '无人机·启动录像指令', $${"opcode":200201,"command_schema_json":{"sequence":0,"opcode":200201,"request":{"rstp":"","angleLevel":"","angleVertical":"","focuses":0,"parking":false,"time":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.rstp","label":"视频流收集服务地址","path":"request.rstp","type":"string","required":false},{"code":"request.angleLevel","label":"摄像机云台水平旋转度数","path":"request.angleLevel","type":"string","required":false},{"code":"request.angleVertical","label":"摄像机云台垂直旋转度数","path":"request.angleVertical","type":"string","required":false},{"code":"request.focuses","label":"摄像头焦距","path":"request.focuses","type":"integer","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false},{"code":"request.rotate","label":"是否旋转拍摄","path":"request.rotate","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200201,"request":{"rstp":"rtsp://admin:12345@192.168.1.64:554/Streaming/Channels/1","angleLevel":"0","angleVertical":"10","focuses":2,"parking":true,"time":10}}}$$),
  ('proto-uav-200202-record-off', '无人机·关闭录像指令', $${"opcode":200202,"command_schema_json":{"sequence":0,"opcode":200202,"request":{"blank":"","parking":false,"time":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.blank","label":"占位字段，默认填写0000","path":"request.blank","type":"string","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200202,"request":{"blank":"0000","parking":false,"time":0}}}$$),
  ('proto-uav-200301-photo', '无人机·拍照指令', $${"opcode":200301,"command_schema_json":{"sequence":0,"opcode":200301,"request":{"lat":"","lng":"","height":"","angleLevel":"","angleVertical":"","number":0,"focuses":"","parking":false,"time":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.lat","label":"拍摄目标的纬度","path":"request.lat","type":"string","required":false},{"code":"request.lng","label":"拍摄目标的经度","path":"request.lng","type":"string","required":false},{"code":"request.height","label":"拍摄目标的地面高度","path":"request.height","type":"string","required":false},{"code":"request.angleLevel","label":"拍摄目标的偏角","path":"request.angleLevel","type":"string","required":false},{"code":"request.angleVertical","label":"拍摄目标的仰角","path":"request.angleVertical","type":"string","required":false},{"code":"request.number","label":"要求拍摄的照片张数","path":"request.number","type":"integer","required":false},{"code":"request.focuses","label":"照相机的焦距","path":"request.focuses","type":"string","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200301,"request":{"lat":"39.1277217676","lng":"117.0190597072","height":"30","angleLevel":"10","angleVertical":"10","number":1,"focuses":"2","parking":true,"time":20}}}$$),
  ('proto-uav-200401-gas-on', '无人机·启动气体检测指令', $${"opcode":200401,"command_schema_json":{"sequence":0,"opcode":200401,"request":{"frequency":0,"parking":false,"time":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.frequency","label":"气体检测上报频率（秒）","path":"request.frequency","type":"integer","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200401,"request":{"frequency":30,"parking":true,"time":10}}}$$),
  ('proto-uav-200402-gas-off', '无人机·关闭气体检测指令', $${"opcode":200402,"command_schema_json":{"sequence":0,"opcode":200402,"request":{"blank":"","parking":false,"time":0}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true},{"code":"request.blank","label":"占位字段，默认填写0000","path":"request.blank","type":"string","required":false},{"code":"request.parking","label":"该操作是否需要驻停","path":"request.parking","type":"boolean","required":false},{"code":"request.time","label":"驻停时长（秒）","path":"request.time","type":"integer","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200402,"request":{"blank":"0000","parking":false,"time":0}}}$$),
  ('proto-uav-200501-status-report', '无人机·定时上报设备状态指令', $${"opcode":200501,"command_schema_json":{"sequence":0,"opcode":200501,"request":{}},"field_translation_rules_json":[{"code":"sequence","label":"动作序号","path":"sequence","type":"integer","required":true},{"code":"opcode","label":"内层动作码","path":"opcode","type":"integer","required":true},{"code":"request","label":"request","path":"request","type":"object","required":true}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"sequence":0,"opcode":200501,"request":{}}}$$),
  ('proto-uav-500104-package', '无人机·巡检任务指令包下发', $${"opcode":500104,"command_schema_json":{"msgId":"","opcode":500104,"templateId":"","packages":[{"sequence":0,"opcode":200101,"request":{"lng":"","lat":"","height":""}},{"sequence":0,"opcode":200102,"request":{"lng":"","lat":"","height":"","pointId":""}},{"sequence":0,"opcode":200301,"request":{"lat":"","lng":"","height":"","angleLevel":"","angleVertical":"","number":0,"focuses":"","parking":false,"time":0}},{"sequence":0,"opcode":200103,"request":{"blank":""}}]},"field_translation_rules_json":[{"code":"msgId","label":"消息编号","path":"msgId","type":"string","required":true},{"code":"opcode","label":"外层操作码","path":"opcode","type":"integer","required":true},{"code":"deviceId","label":"设备逻辑标识","path":"deviceId","type":"string","required":true},{"code":"templateId","label":"任务模板编号","path":"templateId","type":"string","required":true},{"code":"taskId","label":"任务编号","path":"taskId","type":"string","required":true},{"code":"packages","label":"有序动作集合","path":"packages","type":"array","required":true},{"code":"packages.sequence","label":"动作序号","path":"packages[].sequence","type":"integer","required":true},{"code":"packages.opcode","label":"内层动作码","path":"packages[].opcode","type":"integer","required":true},{"code":"packages.request","label":"动作请求参数（随内层动作码变化）","path":"packages[].request","type":"object","required":true},{"code":"packages.request.lng","label":"拍摄目标的经度","path":"packages[].request.lng","type":"string","required":false},{"code":"packages.request.lat","label":"拍摄目标的纬度","path":"packages[].request.lat","type":"string","required":false},{"code":"packages.request.height","label":"拍摄目标的地面高度","path":"packages[].request.height","type":"string","required":false},{"code":"packages.request.pointId","label":"对应无人机勘察地图上的点位id","path":"packages[].request.pointId","type":"string","required":false},{"code":"packages.request.angleLevel","label":"拍摄目标的偏角","path":"packages[].request.angleLevel","type":"string","required":false},{"code":"packages.request.angleVertical","label":"拍摄目标的仰角","path":"packages[].request.angleVertical","type":"string","required":false},{"code":"packages.request.number","label":"要求拍摄的照片张数","path":"packages[].request.number","type":"string","required":false},{"code":"packages.request.focuses","label":"照相机的焦距","path":"packages[].request.focuses","type":"string","required":false},{"code":"packages.request.parking","label":"该操作是否需要驻停","path":"packages[].request.parking","type":"string","required":false},{"code":"packages.request.time","label":"驻停时长（秒）","path":"packages[].request.time","type":"string","required":false}],"step_signal_mapping_rules_json":[],"sample_messages_json":{"msgId":"uav-500104-demo","opcode":500104,"templateId":"100201","packages":[{"sequence":0,"opcode":200101,"request":{"lng":"117.0190597072","lat":"39.1277217676","height":"30"}},{"sequence":1,"opcode":200102,"request":{"lng":"117.01906","lat":"39.12773","height":"30","pointId":"uav-p1"}},{"sequence":2,"opcode":200301,"request":{"lat":"39.12773","lng":"117.01906","height":"30","angleLevel":"10","angleVertical":"10","number":1,"focuses":"2","parking":true,"time":20}},{"sequence":3,"opcode":200103,"request":{"blank":"0000"}}]}}$$)
) AS v(code, name, custom_fields)
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_ws'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'data_protocol', e.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 e
JOIN (VALUES
  ('proto-gs-500101-status', 'cat-protocol-gs'),
  ('proto-gs-500102-report-switch', 'cat-protocol-gs'),
  ('proto-gs-500103-report-push', 'cat-protocol-gs'),
  ('proto-gs-500105-heartbeat', 'cat-protocol-gs'),
  ('proto-gs-500106-ack', 'cat-protocol-gs'),
  ('proto-gs-500201-start', 'cat-protocol-gs'),
  ('proto-gs-500202-action-result', 'cat-protocol-gs'),
  ('proto-gs-500203-task-status', 'cat-protocol-gs'),
  ('proto-gs-500204-fault', 'cat-protocol-gs'),
  ('proto-gs-500205-stop', 'cat-protocol-gs'),
  ('proto-gs-500301-get-package', 'cat-protocol-gs'),
  ('proto-gs-500302-delete-package', 'cat-protocol-gs'),
  ('proto-gs-500303-list-templates', 'cat-protocol-gs'),
  ('proto-gs-500403-online-event', 'cat-protocol-gs'),
  ('proto-robot-200102-move', 'cat-protocol-robot'),
  ('proto-robot-200301-photo', 'cat-protocol-robot'),
  ('proto-robot-200501-status-report', 'cat-protocol-robot'),
  ('proto-robot-200403-gas-report', 'cat-protocol-robot'),
  ('proto-robot-500104-package', 'cat-protocol-robot'),
  ('proto-uav-200101-takeoff', 'cat-protocol-uav'),
  ('proto-uav-200102-move', 'cat-protocol-uav'),
  ('proto-uav-200103-land', 'cat-protocol-uav'),
  ('proto-uav-200104-descend', 'cat-protocol-uav'),
  ('proto-uav-200105-climb', 'cat-protocol-uav'),
  ('proto-uav-200201-record-on', 'cat-protocol-uav'),
  ('proto-uav-200202-record-off', 'cat-protocol-uav'),
  ('proto-uav-200301-photo', 'cat-protocol-uav'),
  ('proto-uav-200401-gas-on', 'cat-protocol-uav'),
  ('proto-uav-200402-gas-off', 'cat-protocol-uav'),
  ('proto-uav-200501-status-report', 'cat-protocol-uav'),
  ('proto-uav-500104-package', 'cat-protocol-uav')
) AS map(entity_code, category_code) ON e.deleted = false AND e.tenant_id = 1 AND e.code = map.entity_code
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = map.category_code
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1 (
  entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
)
SELECT e.id, e.model_id, f.field_code, f.value_string, f.value_number, 'seed', 1, false
FROM ent_data_protocol_t1 e
JOIN (VALUES
  ('proto-gs-500101-status', 'opcode', NULL, 500101::numeric),
  ('proto-gs-500102-report-switch', 'opcode', NULL, 500102::numeric),
  ('proto-gs-500103-report-push', 'opcode', NULL, 500103::numeric),
  ('proto-gs-500105-heartbeat', 'opcode', NULL, 500105::numeric),
  ('proto-gs-500106-ack', 'opcode', NULL, 500106::numeric),
  ('proto-gs-500201-start', 'opcode', NULL, 500201::numeric),
  ('proto-gs-500202-action-result', 'opcode', NULL, 500202::numeric),
  ('proto-gs-500203-task-status', 'opcode', NULL, 500203::numeric),
  ('proto-gs-500204-fault', 'opcode', NULL, 500204::numeric),
  ('proto-gs-500205-stop', 'opcode', NULL, 500205::numeric),
  ('proto-gs-500301-get-package', 'opcode', NULL, 500301::numeric),
  ('proto-gs-500302-delete-package', 'opcode', NULL, 500302::numeric),
  ('proto-gs-500303-list-templates', 'opcode', NULL, 500303::numeric),
  ('proto-gs-500403-online-event', 'opcode', NULL, 500403::numeric),
  ('proto-robot-200102-move', 'opcode', NULL, 200102::numeric),
  ('proto-robot-200301-photo', 'opcode', NULL, 200301::numeric),
  ('proto-robot-200501-status-report', 'opcode', NULL, 200501::numeric),
  ('proto-robot-200403-gas-report', 'opcode', NULL, 200403::numeric),
  ('proto-robot-500104-package', 'opcode', NULL, 500104::numeric),
  ('proto-uav-200101-takeoff', 'opcode', NULL, 200101::numeric),
  ('proto-uav-200102-move', 'opcode', NULL, 200102::numeric),
  ('proto-uav-200103-land', 'opcode', NULL, 200103::numeric),
  ('proto-uav-200104-descend', 'opcode', NULL, 200104::numeric),
  ('proto-uav-200105-climb', 'opcode', NULL, 200105::numeric),
  ('proto-uav-200201-record-on', 'opcode', NULL, 200201::numeric),
  ('proto-uav-200202-record-off', 'opcode', NULL, 200202::numeric),
  ('proto-uav-200301-photo', 'opcode', NULL, 200301::numeric),
  ('proto-uav-200401-gas-on', 'opcode', NULL, 200401::numeric),
  ('proto-uav-200402-gas-off', 'opcode', NULL, 200402::numeric),
  ('proto-uav-200501-status-report', 'opcode', NULL, 200501::numeric),
  ('proto-uav-500104-package', 'opcode', NULL, 500104::numeric)
) AS f(entity_code, field_code, value_string, value_number)
  ON e.deleted = false AND e.tenant_id = 1 AND e.code = f.entity_code
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = f.field_code
);

-- 存量：去掉跨协议绰号；页签两列改名。完整三卷按协议原文回填见 complete_protocol_tabs_from_docs.py / dynamic_protocol_ground_station_tabs.sql
UPDATE ent_data_protocol_t1
SET
  custom_fields = (
    (custom_fields
      - 'protocol_command_code'
      - 'ws_opcode'
      - 'protocol_command_schema_json'
      - 'protocol_field_translation_rules_json'
      - 'protocol_step_signal_mapping_rules_json'
      - 'protocol_sample_messages_json'
      - 'command_schema_json'
      - 'field_translation_rules_json'
      - 'step_signal_mapping_rules_json'
      - 'sample_messages_json')
    || jsonb_strip_nulls(jsonb_build_object(
      'opcode', COALESCE(custom_fields->'opcode', custom_fields->'ws_opcode'),
      'command_json', COALESCE(
        custom_fields->'command_json',
        custom_fields->'command_schema_json',
        custom_fields->'protocol_command_schema_json'
      ),
      'field_description_json', COALESCE(
        custom_fields->'field_description_json',
        custom_fields->'field_translation_rules_json',
        custom_fields->'protocol_field_translation_rules_json'
      )
    ))
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND entity_type_code = 'data_protocol'
  AND (
    custom_fields ? 'protocol_command_code'
    OR custom_fields ? 'ws_opcode'
    OR custom_fields ? 'protocol_command_schema_json'
    OR custom_fields ? 'protocol_field_translation_rules_json'
    OR custom_fields ? 'protocol_step_signal_mapping_rules_json'
    OR custom_fields ? 'protocol_sample_messages_json'
  );

UPDATE ent_data_protocol_t1 e
SET name = v.new_name, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('proto-gs-500101-status', '获取设备实时状态'),
  ('proto-gs-500102-report-switch', '启动或关闭定时上报'),
  ('proto-gs-500103-report-push', '定时上报设备状态'),
  ('proto-gs-500105-heartbeat', '心跳'),
  ('proto-gs-500106-ack', '回执'),
  ('proto-gs-500201-start', '启动已下发任务'),
  ('proto-gs-500202-action-result', '单条指令操作结果'),
  ('proto-gs-500203-task-status', '任务状态'),
  ('proto-gs-500204-fault', '设备故障'),
  ('proto-gs-500205-stop', '终止任务'),
  ('proto-gs-500301-get-package', '获取指令包'),
  ('proto-gs-500302-delete-package', '删除指令包'),
  ('proto-gs-500303-list-templates', '列出任务模板'),
  ('proto-gs-500401-stream-on', '启动推流'),
  ('proto-gs-500402-stream-off', '停止推流'),
  ('proto-gs-500403-online-event', '在线或掉线'),
  ('proto-robot-200102-move', '移动'),
  ('proto-robot-200301-photo', '拍照'),
  ('proto-robot-200501-status-report', '定时上报设备状态'),
  ('proto-robot-200403-gas-report', '气体检测数据上报'),
  ('proto-robot-500104-package', '巡检任务指令包'),
  ('proto-uav-200101-takeoff', '起飞'),
  ('proto-uav-200102-move', '移动'),
  ('proto-uav-200103-land', '降落'),
  ('proto-uav-200104-descend', '降低飞行高度'),
  ('proto-uav-200105-climb', '爬升飞行高度'),
  ('proto-uav-200201-record-on', '启动录像'),
  ('proto-uav-200202-record-off', '关闭录像'),
  ('proto-uav-200301-photo', '拍照'),
  ('proto-uav-200401-gas-on', '启动气体检测'),
  ('proto-uav-200402-gas-off', '关闭气体检测'),
  ('proto-uav-200501-status-report', '定时上报设备状态'),
  ('proto-uav-500104-package', '巡检任务指令包')
) AS v(code, new_name)
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code AND e.name IS DISTINCT FROM v.new_name;

-- 索引：停用跨协议绰号，报文编号只认 opcode
UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
WHERE idx.deleted = false AND idx.tenant_id = 1
  AND e.id = idx.entity_id AND e.tenant_id = 1 AND e.deleted = false
  AND e.entity_type_code = 'data_protocol'
  AND idx.field_code IN ('protocol_command_code', 'ws_opcode');

-- 存量：样例必须是线上报文。自造 request/responseSuccess 外壳拆回下发包；500101 按下发+应答两份重写。
UPDATE ent_data_protocol_t1
SET
  custom_fields = jsonb_set(
    custom_fields,
    '{sample_messages_json}',
    custom_fields->'sample_messages_json'->'request'
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND entity_type_code = 'data_protocol'
  AND code <> 'proto-gs-500101-status'
  AND custom_fields->'sample_messages_json' ? 'request'
  AND custom_fields->'sample_messages_json' ? 'responseSuccess';

-- 指令 JSON 的空包与字段说明的类型/必填写在上面 VALUES 里。
-- 导入后跑 normalize_protocol_tab_fields.py：收成两列、单包改 outbound、丢掉对不上空包的说明。
