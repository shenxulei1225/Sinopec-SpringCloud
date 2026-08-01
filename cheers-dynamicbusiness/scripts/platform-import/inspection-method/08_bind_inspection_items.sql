-- ============================================================================
-- inspection-method · 08 检查内容 ↔ 方法模板 完整对应（幂等）
-- 1) 补齐标准库缺口模板（消防/电源/链路/软件等）
-- 2) 按名称语义填空绑定；仅 method_template_id IS NULL，不覆盖人工选择
-- 前置：07_sample_templates 已执行（或本文件自带模板 INSERT）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1) 缺口方法模板
-- ---------------------------------------------------------------------------
WITH templates(code, name, sort, action_duration_sec) AS (
  VALUES
    ('IM-TPL-POWER-SELFTEST',   '上电自检检查模板',       270,  90),
    ('IM-TPL-ALARM-ZONE',       '防区报警状态检查模板',   280, 100),
    ('IM-TPL-LINK-COMM',        '链路通讯检查模板',       290,  70),
    ('IM-TPL-SOUND-LIGHT',      '声光报警检查模板',       300,  80),
    ('IM-TPL-DETECTOR',         '探测器探头检查模板',     310, 100),
    ('IM-TPL-COOLING-FAN',      '散热风扇检查模板',       320,  90),
    ('IM-TPL-ELECTRICAL-PANEL', '电气盘柜状态检查模板',   330, 120),
    ('IM-TPL-SOFTWARE-LOGIN',   '软件服务登录检查模板',   340,  90),
    ('IM-TPL-RADIO-RF',         '无线射频链路检查模板',   350, 120),
    ('IM-TPL-PASSAGE-CLEAR',    '通道环境畅通检查模板',   360,  60),
    ('IM-TPL-CABLE-JOINT',      '线缆接头检查模板',       370,  80),
    ('IM-TPL-INDICATOR-LIGHT',  '指示灯与显示检查模板',   380,  45)
)
INSERT INTO ent_inspection_method_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  parent_id, tree_path, sort, is_template, action_duration_sec,
  attrs, custom_fields, creator, deleted
)
SELECT
  1, 'inspection_method', m.id, t.name, t.code, 1,
  0, NULL, t.sort, TRUE, t.action_duration_sec,
  '{}'::jsonb, '{}'::jsonb, 'seed', FALSE
FROM templates t
CROSS JOIN dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_method'
  AND NOT EXISTS (
    SELECT 1 FROM ent_inspection_method_t1 e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = t.code
  );

UPDATE ent_inspection_method_t1 e
SET
  name = v.name,
  sort = v.sort,
  action_duration_sec = v.action_duration_sec,
  is_template = TRUE,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('IM-TPL-POWER-SELFTEST',   '上电自检检查模板',       270,  90),
    ('IM-TPL-ALARM-ZONE',       '防区报警状态检查模板',   280, 100),
    ('IM-TPL-LINK-COMM',        '链路通讯检查模板',       290,  70),
    ('IM-TPL-SOUND-LIGHT',      '声光报警检查模板',       300,  80),
    ('IM-TPL-DETECTOR',         '探测器探头检查模板',     310, 100),
    ('IM-TPL-COOLING-FAN',      '散热风扇检查模板',       320,  90),
    ('IM-TPL-ELECTRICAL-PANEL', '电气盘柜状态检查模板',   330, 120),
    ('IM-TPL-SOFTWARE-LOGIN',   '软件服务登录检查模板',   340,  90),
    ('IM-TPL-RADIO-RF',         '无线射频链路检查模板',   350, 120),
    ('IM-TPL-PASSAGE-CLEAR',    '通道环境畅通检查模板',   360,  60),
    ('IM-TPL-CABLE-JOINT',      '线缆接头检查模板',       370,  80),
    ('IM-TPL-INDICATOR-LIGHT',  '指示灯与显示检查模板',   380,  45)
) AS v(code, name, sort, action_duration_sec)
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;

-- ---------------------------------------------------------------------------
-- 2) 名称语义绑定（priority 越小越优先；一项只绑一次）
-- ---------------------------------------------------------------------------
WITH name_binds(name_pattern, method_code, priority) AS (
  VALUES
    -- 视频 / 安防画面
    ('%镜头清洁%',             'IM-TPL-CAM-LENS', 10),
    ('%探头清洁%',             'IM-TPL-DETECTOR', 10),
    ('%视频画面%',             'IM-TPL-CAM-IMAGE', 10),
    ('%画面显示%',             'IM-TPL-CAM-IMAGE', 10),
    ('%花屏%',                 'IM-TPL-CAM-IMAGE', 10),
    ('%黑屏%',                 'IM-TPL-CAM-IMAGE', 10),
    ('%亮度与色彩%',           'IM-TPL-CAM-IMAGE', 10),
    ('%录像回放%',             'IM-TPL-CAM-IMAGE', 10),
    ('%云台%',                 'IM-TPL-CAM-PTZ', 10),
    ('%变焦%',                 'IM-TPL-CAM-PTZ', 10),
    ('%补光%',                 'IM-TPL-CAM-IR', 10),
    ('%红外%',                 'IM-TPL-CAM-IR', 15),
    ('%时间OSD%',              'IM-TPL-CAM-OSD', 10),
    ('%OSD%',                  'IM-TPL-CAM-OSD', 15),
    -- 对讲 / 音频 / 无线
    ('%通话清晰%',             'IM-TPL-INTERCOM', 10),
    ('%试呼通话%',             'IM-TPL-INTERCOM', 10),
    ('%音频收发%',             'IM-TPL-INTERCOM', 10),
    ('%对讲%',                 'IM-TPL-INTERCOM', 15),
    ('%信道/组呼%',            'IM-TPL-RADIO-RF', 10),
    ('%中继转发%',             'IM-TPL-RADIO-RF', 10),
    ('%天线与馈线%',           'IM-TPL-RADIO-RF', 10),
    ('%驻波%',                 'IM-TPL-RADIO-RF', 10),
    ('%链路衰减%',             'IM-TPL-RADIO-RF', 10),
    ('%收发隔离%',             'IM-TPL-RADIO-RF', 10),
    ('%耦合指示%',             'IM-TPL-RADIO-RF', 10),
    ('%短信收发%',             'IM-TPL-RADIO-RF', 10),
    ('%SIM/信号%',             'IM-TPL-RADIO-RF', 10),
    -- 门禁
    ('%刷卡%',                 'IM-TPL-ACCESS-CARD', 10),
    ('%识别正常%',             'IM-TPL-ACCESS-CARD', 20),
    ('%读写/定位%',            'IM-TPL-ACCESS-CARD', 10),
    ('%门锁%',                 'IM-TPL-DOOR-LOCK', 10),
    ('%门磁%',                 'IM-TPL-DOOR-LOCK', 10),
    ('%出门按钮%',             'IM-TPL-DOOR-LOCK', 10),
    -- 消防 / 探测 / 声光
    ('%防区状态%',             'IM-TPL-ALARM-ZONE', 10),
    ('%异常报警%',             'IM-TPL-ALARM-ZONE', 15),
    ('%声光触发%',             'IM-TPL-SOUND-LIGHT', 10),
    ('%报警阈值%',             'IM-TPL-SOUND-LIGHT', 10),
    ('%模拟告警%',             'IM-TPL-SOUND-LIGHT', 10),
    ('%告警与复位%',           'IM-TPL-SOUND-LIGHT', 10),
    ('%探测器%',               'IM-TPL-DETECTOR', 10),
    ('%探测区域%',             'IM-TPL-DETECTOR', 10),
    ('%感应绳%',               'IM-TPL-DETECTOR', 10),
    ('%喷嘴无堵塞%',           'IM-TPL-DETECTOR', 10),
    ('%点亮与应急%',           'IM-TPL-EMERGENCY-LIGHT', 10),
    ('%应急%',                 'IM-TPL-EMERGENCY-LIGHT', 25),
    ('%点亮正常%',             'IM-TPL-EMERGENCY-LIGHT', 20),
    -- 电源 / 电池
    ('%电池状态%',             'IM-TPL-BATTERY-STATUS', 10),
    ('%电池电压%',             'IM-TPL-BATTERY-STATUS', 10),
    ('%电池外观%',             'IM-TPL-BATTERY-STATUS', 10),
    ('%电池柜%',               'IM-TPL-BATTERY-STATUS', 10),
    ('%市电/电池%',            'IM-TPL-BATTERY-STATUS', 10),
    ('%备用电源%',             'IM-TPL-BATTERY-STATUS', 10),
    ('%极柱与连接条%',         'IM-TPL-BATTERY-STATUS', 10),
    ('%开机与电量%',           'IM-TPL-BATTERY-STATUS', 10),
    ('%上电与自检%',           'IM-TPL-POWER-SELFTEST', 10),
    ('%上电自检%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%主机上电%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%上电与指示%',           'IM-TPL-POWER-SELFTEST', 10),
    ('%上电指示%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%上电/状态%',            'IM-TPL-POWER-SELFTEST', 10),
    ('%模块上电%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%设备上电%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%柜内设备上电%',         'IM-TPL-POWER-SELFTEST', 10),
    ('%PLC/控制器上电%',       'IM-TPL-POWER-SELFTEST', 10),
    ('%开机自检%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%PDU/供电%',             'IM-TPL-POWER-SELFTEST', 10),
    ('%电源与指示灯%',         'IM-TPL-POWER-SELFTEST', 10),
    ('%负载指示%',             'IM-TPL-POWER-SELFTEST', 15),
    ('%电压巡检%',             'IM-TPL-GAUGE-READ', 15),
    -- 网络 / 链路 / 通讯
    ('%网络连接%',             'IM-TPL-NETWORK-LINK', 10),
    ('%关键端口链路%',         'IM-TPL-NETWORK-LINK', 10),
    ('%上下行链路%',           'IM-TPL-NETWORK-LINK', 10),
    ('%配置/路由%',            'IM-TPL-NETWORK-LINK', 10),
    ('%光/电口%',              'IM-TPL-NETWORK-LINK', 10),
    ('%光纤%',                 'IM-TPL-NETWORK-LINK', 10),
    ('%光功率%',               'IM-TPL-NETWORK-LINK', 10),
    ('%链路通讯%',             'IM-TPL-LINK-COMM', 10),
    ('%通讯状态%',             'IM-TPL-LINK-COMM', 10),
    ('%控制端通讯%',           'IM-TPL-LINK-COMM', 10),
    ('%与主机通讯%',           'IM-TPL-LINK-COMM', 10),
    ('%与主机联动%',           'IM-TPL-LINK-COMM', 10),
    ('%通讯/报警%',            'IM-TPL-LINK-COMM', 10),
    ('%通讯正常%',             'IM-TPL-LINK-COMM', 15),
    ('%中继/SIP%',             'IM-TPL-LINK-COMM', 10),
    ('%分机注册%',             'IM-TPL-LINK-COMM', 10),
    ('%定位/上报%',            'IM-TPL-LINK-COMM', 15),
    -- 仪表读数
    ('%液位/温度%',            'IM-TPL-LEVEL-TEMP-READ', 10),
    ('%温湿度读数%',           'IM-TPL-LEVEL-TEMP-READ', 10),
    ('%温湿度联动%',           'IM-TPL-LEVEL-TEMP-READ', 15),
    ('%进出口压力%',           'IM-TPL-PRESSURE-READ', 10),
    ('%压力/状态%',            'IM-TPL-PRESSURE-READ', 10),
    ('%取压管路%',             'IM-TPL-PRESSURE-READ', 10),
    ('%表盘清晰%',             'IM-TPL-GAUGE-READ', 10),
    ('%读数在合理%',           'IM-TPL-GAUGE-READ', 10),
    ('%读数合理%',             'IM-TPL-GAUGE-READ', 10),
    ('%显示读数%',             'IM-TPL-GAUGE-READ', 10),
    ('%采集数据合理%',         'IM-TPL-GAUGE-READ', 15),
    ('%监测通道%',             'IM-TPL-GAUGE-READ', 15),
    -- 机泵阀门
    ('%防火堤%',               'IM-TPL-FIRE-DIKE', 10),
    ('%呼吸阀%',               'IM-TPL-RELIEF-VALVE', 10),
    ('%进出油阀门%',           'IM-TPL-VALVE-POSITION', 10),
    ('%阀门开关正确%',         'IM-TPL-VALVE-POSITION', 10),
    ('%阀位指示%',             'IM-TPL-VALVE-POSITION', 10),
    ('%表阀开关%',             'IM-TPL-VALVE-POSITION', 10),
    ('%开关箱状态%',           'IM-TPL-VALVE-POSITION', 15),
    ('%进出线开关%',           'IM-TPL-VALVE-POSITION', 15),
    ('%开关/接触器%',          'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%开关灵活%',             'IM-TPL-VALVE-OPERATE', 10),
    ('%手轮/执行机构%',        'IM-TPL-VALVE-OPERATE', 10),
    ('%阀体及法兰%泄漏%',      'IM-TPL-LEAK-CHECK', 10),
    ('%密封处%泄漏%',          'IM-TPL-LEAK-CHECK', 10),
    ('%无泄漏%',               'IM-TPL-LEAK-CHECK', 20),
    ('%泵体及基础%',           'IM-TPL-VIBRATION-LISTEN', 10),
    ('%运行声音%',             'IM-TPL-VIBRATION-LISTEN', 10),
    ('%运转声音%',             'IM-TPL-VIBRATION-LISTEN', 10),
    ('%无异响%',               'IM-TPL-VIBRATION-LISTEN', 20),
    ('%振动与紧固%',           'IM-TPL-VIBRATION-LISTEN', 10),
    ('%异常振动%',             'IM-TPL-VIBRATION-LISTEN', 10),
    ('%电机温度%',             'IM-TPL-MOTOR-TEMP', 10),
    ('%无过热%',               'IM-TPL-MOTOR-TEMP', 20),
    ('%润滑油位%',             'IM-TPL-LUBE-LEVEL', 10),
    ('%接地与防雷%',           'IM-TPL-GROUNDING-VISUAL', 10),
    ('%接地%',                 'IM-TPL-GROUNDING-VISUAL', 25),
    -- 散热 / 通风 / 风扇
    ('%风扇运行%',             'IM-TPL-COOLING-FAN', 10),
    ('%散热风扇%',             'IM-TPL-COOLING-FAN', 10),
    ('%风扇与温度%',           'IM-TPL-COOLING-FAN', 10),
    ('%通风散热%',             'IM-TPL-COOLING-FAN', 10),
    ('%进出风口%',             'IM-TPL-COOLING-FAN', 10),
    ('%叶片与防护网%',         'IM-TPL-COOLING-FAN', 10),
    ('%空调运行%',             'IM-TPL-COOLING-FAN', 15),
    ('%风机状态%',             'IM-TPL-COOLING-FAN', 15),
    -- 电气盘柜 / 控制
    ('%键盘操作%',             'IM-TPL-INDICATOR-LIGHT', 20),
    ('%按键与显示%',           'IM-TPL-INDICATOR-LIGHT', 10),
    ('%指示灯与显示%',         'IM-TPL-INDICATOR-LIGHT', 10),
    ('%指示灯正常%',           'IM-TPL-INDICATOR-LIGHT', 10),
    ('%指示灯%',               'IM-TPL-INDICATOR-LIGHT', 25),
    ('%主机指示%',             'IM-TPL-INDICATOR-LIGHT', 15),
    ('%模块指示%',             'IM-TPL-INDICATOR-LIGHT', 15),
    ('%发送卡指示%',           'IM-TPL-INDICATOR-LIGHT', 15),
    ('%显示/指示%',            'IM-TPL-INDICATOR-LIGHT', 10),
    ('%显示正常%',             'IM-TPL-INDICATOR-LIGHT', 20),
    ('%按钮动作%',             'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%启停控制%',             'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%启停联动%',             'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%开关控制正常%',         'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%切换控制%',             'IM-TPL-ELECTRICAL-PANEL', 10),
    ('%控制软件%',             'IM-TPL-SOFTWARE-LOGIN', 15),
    -- 线缆 / 接线
    ('%接线可靠%',             'IM-TPL-CABLE-JOINT', 10),
    ('%接线牢固%',             'IM-TPL-CABLE-JOINT', 10),
    ('%接线与密封%',           'IM-TPL-CABLE-JOINT', 10),
    ('%接线密封%',             'IM-TPL-CABLE-JOINT', 10),
    ('%模组与接线%',           'IM-TPL-CABLE-JOINT', 10),
    ('%线缆连接%',             'IM-TPL-CABLE-JOINT', 10),
    ('%外护套完好%',           'IM-TPL-CABLE-JOINT', 10),
    ('%接头密封%',             'IM-TPL-CABLE-JOINT', 10),
    ('%桥架/沟槽%',            'IM-TPL-CABLE-JOINT', 10),
    ('%绝缘密封%',             'IM-TPL-CABLE-JOINT', 10),
    ('%插拔口清洁%',           'IM-TPL-CABLE-JOINT', 15),
    ('%开口/接线盒%',          'IM-TPL-CABLE-JOINT', 10),
    -- 软件 / 服务
    ('%可登录%',               'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%服务可登录%',           'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%管理界面%',             'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%业务客户端%',           'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%业务软件%',             'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%存储服务%',             'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%关键服务进程%',         'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%远程管理%',             'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%关键功能可用%',         'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%授权/许可%',            'IM-TPL-SOFTWARE-LOGIN', 10),
    ('%日志无持续%',           'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%备份/告警%',            'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%策略生效%',             'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%磁盘/RAID%',            'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%磁盘阵列%',             'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%SMART/健康%',           'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%读写无异常%',           'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%打印测试%',             'IM-TPL-SOFTWARE-LOGIN', 15),
    ('%墨粉/纸张%',            'IM-TPL-SOFTWARE-LOGIN', 20),
    ('%键鼠%',                 'IM-TPL-SOFTWARE-LOGIN', 20),
    ('%键盘抽拉%',             'IM-TPL-SOFTWARE-LOGIN', 20),
    ('%无硬件告警%',           'IM-TPL-SOFTWARE-LOGIN', 20),
    ('%无异常告警%',           'IM-TPL-ALARM-ZONE', 25),
    -- 通道 / 环境 / 标识 / 外观
    ('%通道畅通%',             'IM-TPL-PASSAGE-CLEAR', 10),
    ('%周边通道%',             'IM-TPL-PASSAGE-CLEAR', 10),
    ('%周边无障碍%',           'IM-TPL-PASSAGE-CLEAR', 10),
    ('%周边无可燃%',           'IM-TPL-PASSAGE-CLEAR', 10),
    ('%标识标牌%',             'IM-TPL-MARKING-READ', 10),
    ('%安装位置正确%',         'IM-TPL-MARKING-READ', 10),
    ('%标识清晰%',             'IM-TPL-MARKING-READ', 15),
    ('%标识%',                 'IM-TPL-MARKING-READ', 30),
    ('%铭牌%',                 'IM-TPL-MARKING-READ', 20),
    ('%保温/防腐%',            'IM-TPL-VISUAL-01', 20),
    ('%罐体外观%',             'IM-TPL-VISUAL-01', 20),
    ('%外观完好%',             'IM-TPL-VISUAL-01', 30),
    ('%外观%',                 'IM-TPL-VISUAL-01', 40),
    ('%安装牢固%',             'IM-TPL-VISUAL-01', 30),
    ('%支撑与紧固%',           'IM-TPL-VISUAL-01', 25),
    ('%固定牢固%',             'IM-TPL-VISUAL-01', 30),
    ('%承重与水平%',           'IM-TPL-VISUAL-01', 25),
    ('%兼容型号%',             'IM-TPL-MARKING-READ', 25),
    ('%模块就位%',             'IM-TPL-INDICATOR-LIGHT', 20),
    ('%行走/定位%',            'IM-TPL-CAM-PTZ', 25),
    ('%传感器与摄像头%',       'IM-TPL-CAM-IMAGE', 20),
    ('%充电对接%',             'IM-TPL-POWER-SELFTEST', 20),
    ('%功能抽检%',             'IM-TPL-VISUAL-01', 40),
    ('%连接可靠、供电%',       'IM-TPL-POWER-SELFTEST', 20),
    -- 剩余细项（08 复跑补齐）
    ('%电池与充电%',           'IM-TPL-BATTERY-STATUS', 10),
    ('%开关机与电池%',         'IM-TPL-BATTERY-STATUS', 10),
    ('%柜门开合%',             'IM-TPL-DOOR-LOCK', 10),
    ('%锁具正常%',             'IM-TPL-DOOR-LOCK', 15),
    ('%切换各端口画面%',       'IM-TPL-CAM-IMAGE', 10),
    ('%解码画面%',             'IM-TPL-CAM-IMAGE', 10),
    ('%拼接画面%',             'IM-TPL-CAM-IMAGE', 10),
    ('%画面同步%',             'IM-TPL-CAM-IMAGE', 10),
    ('%温度正常%',             'IM-TPL-MOTOR-TEMP', 25),
    ('%柜内温度与散热%',       'IM-TPL-COOLING-FAN', 10),
    ('%网络/USB%',             'IM-TPL-NETWORK-LINK', 10),
    ('%网络链路%',             'IM-TPL-NETWORK-LINK', 10),
    ('%支架牢固%',             'IM-TPL-VISUAL-01', 25),
    ('%安装螺栓%',             'IM-TPL-VISUAL-01', 25),
    ('%馈线接头%',             'IM-TPL-RADIO-RF', 10),
    ('%接口紧固%',             'IM-TPL-CABLE-JOINT', 10),
    ('%接头紧固%',             'IM-TPL-CABLE-JOINT', 10),
    ('%接线紧固%',             'IM-TPL-CABLE-JOINT', 10),
    ('%按键与指示%',           'IM-TPL-INDICATOR-LIGHT', 10),
    ('%仪表指示%',             'IM-TPL-GAUGE-READ', 10)
)
UPDATE ent_inspection_item_t1 i
SET
  method_template_id = x.method_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  SELECT DISTINCT ON (i2.id)
    i2.id AS item_id,
    t.id AS method_id
  FROM ent_inspection_item_t1 i2
  JOIN name_binds nb ON i2.name LIKE nb.name_pattern
  JOIN ent_inspection_method_t1 t
    ON t.deleted = false AND t.tenant_id = 1 AND t.code = nb.method_code
  WHERE i2.deleted = false
    AND i2.tenant_id = 1
    AND i2.method_template_id IS NULL
  ORDER BY i2.id, nb.priority, nb.method_code
) x
WHERE i.id = x.item_id;

-- 仍未命中的检查内容保持 NULL，便于发现规则缺口；禁止整库兜底绑目视。
