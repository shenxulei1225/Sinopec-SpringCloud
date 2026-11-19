-- 管廊预案补预警步骤。
-- 触电：预防与预警 5.1 第⑧条指向附表一，原先未写入。
-- 防汛：4.2 前期预防准备原先未进入预警，也未进入响应。达到预警值后的报告和启动，预警里已有，不重复。
-- 爆管：5.1 防破排渍保证措施原先未进入预警，也未进入响应。
-- 其余六份专项预案的预防与预警已逐条写入。交通事故原文没有预防与预警，本脚本不补。

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "明确电气设备责任人",
    "detailDescription": "明确电气设备的责任人。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "执行检查制度和倒闸程序",
    "detailDescription": "按照有关规定建立并严格执行检查制度，严格按照倒闸作业操作程序。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "定期监测供电和用电设备",
    "detailDescription": "定期对供电线路、设备间、变配电设施和用电设备进行监测。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检测漏电保护和接地保护",
    "detailDescription": "定期检测漏电保护器及接地保护是否完好。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立隐患报告和整改制度",
    "detailDescription": "建立隐患报告、整改制度。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "合理分配电力负荷",
    "detailDescription": "加强用电管理，合理分配电力负荷，避免过载引发事故。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "遵守电气安全操作规程",
    "detailDescription": "严格遵守电气安全操作规程，不违规带电操作。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "发现触电后立即报告",
    "detailDescription": "现场工作人员发现有人员触电时，用口头、电话或对讲器，第一时间按程序报告。"
  },
  {
    "order": 9,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "告知相关作业人员",
    "detailDescription": "有关负责人通过快速手段告知相关作业人员。"
  },
  {
    "order": 10,
    "nodeKey": "w10",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "组织救援",
    "detailDescription": "组织相应的救援行动。"
  },
  {
    "order": 11,
    "nodeKey": "w11",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  },
  {
    "order": 12,
    "nodeKey": "w12",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "对照触电危险清单落实控制措施",
    "detailDescription": "应急事故和情况的识别见附表一《触电危险清单表》。按临时用电、电气焊作业、手持电动工具作业核对危险源，并落实表中的控制措施。"
  }
]$json$::jsonb
WHERE tenant_id = 123 AND deleted = false AND name = '触电事故应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "成立防洪领导小组",
    "detailDescription": "成立以总经理为组长的防洪领导小组。"
  },
  {
    "order": 2,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "成立抗洪突击队",
    "detailDescription": "成立抗洪突击队。"
  },
  {
    "order": 3,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查排水沟和污水井",
    "detailDescription": "汛前检查排水沟、污水井是否有淤泥堵塞。"
  },
  {
    "order": 4,
    "nodeKey": "w10",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查排水管道与市政接口",
    "detailDescription": "检查排水管道与市政管网接口，保证畅通。"
  },
  {
    "order": 5,
    "nodeKey": "w11",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查排污泵自动抽排",
    "detailDescription": "检查排污水泵自动抽排系统是否灵敏、抽排效果是否正常，特别保证监控中心及连廊段水泵正常运行。"
  },
  {
    "order": 6,
    "nodeKey": "w12",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "每日巡查管廊出入口堵头",
    "detailDescription": "对引出口、吊装口、换舱口每日巡查，查看堵头是否松动、是否密封不严。低于地面标高的逃生口重点关注。"
  },
  {
    "order": 7,
    "nodeKey": "w13",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "出入口异常时上报并抽排",
    "detailDescription": "出入口出现异常时立即上报，并及时安排水泵抽排。"
  },
  {
    "order": 8,
    "nodeKey": "w14",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检修备用发电机和控制线路",
    "detailDescription": "检修备用发电机和控制线路。"
  },
  {
    "order": 9,
    "nodeKey": "w15",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "备足发电机燃料",
    "detailDescription": "确认发电机燃料充足，保证应急电源安全可靠。"
  },
  {
    "order": 10,
    "nodeKey": "w16",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查防汛物资",
    "detailDescription": "检查手电筒及各类防汛物资是否完好。"
  },
  {
    "order": 11,
    "nodeKey": "w17",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查设备机房应急照明",
    "detailDescription": "检查设备机房应急照明。"
  },
  {
    "order": 12,
    "nodeKey": "w18",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "汛期前编制防洪应急措施",
    "detailDescription": "汛期前编制防洪应急措施。"
  },
  {
    "order": 13,
    "nodeKey": "w19",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "加强员工防汛教育",
    "detailDescription": "针对运营工作特点，加强对员工的教育管理。"
  },
  {
    "order": 14,
    "nodeKey": "w20",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "每天收集气象和水文",
    "detailDescription": "固定专人每天定时收看当地天气预报，收集武汉地区气象及水文资料。"
  },
  {
    "order": 15,
    "nodeKey": "w21",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "上报重要气象信息",
    "detailDescription": "将重要气象和水文信息及时上报项目负责人。"
  },
  {
    "order": 16,
    "nodeKey": "w22",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配足防洪物资并禁止挪用",
    "detailDescription": "配足沙袋、帆布蓬、大功率抽水机等防洪物资，并禁止挪用。"
  },
  {
    "order": 17,
    "nodeKey": "w23",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配备发电机保障停电排水",
    "detailDescription": "配备发电机，保证汛期突然停电时仍能防排水。"
  },
  {
    "order": 18,
    "nodeKey": "w24",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "疏通现场排水系统",
    "detailDescription": "根据地形疏通现场排水系统，保证水流畅通、不积水。"
  },
  {
    "order": 19,
    "nodeKey": "w25",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立防汛联动机制",
    "detailDescription": "建立与运维项目部、防汛物资供应商、就近医院的联动机制，汛情时互相协调。"
  },
  {
    "order": 20,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "专人24小时值班",
    "detailDescription": "防台、防汛、防暴雨应急处理领导小组设专人24小时值班。"
  },
  {
    "order": 21,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "掌握气象和汛情",
    "detailDescription": "与气象部门联系，及时掌握降雨量、上游水位等气象信息和汛情动态。"
  },
  {
    "order": 22,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "监控预警系统监视现场",
    "detailDescription": "依据综合管廊监控预警系统随时监控现场情况。"
  },
  {
    "order": 23,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "危险信号后报告领导小组",
    "detailDescription": "监控预警系统发出危险信号后，当班值守人员立即向项目公司应急领导小组报告。"
  },
  {
    "order": 24,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "按实际情况启动预案",
    "detailDescription": "经过信息反馈和实际情况，启动实施相应的应急救援预案。"
  },
  {
    "order": 25,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb
WHERE tenant_id = 123 AND deleted = false AND name = '防汛防暴雨应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "定期检查排水设施和抽水设备",
    "detailDescription": "定期检查排水沟、排水管及抽水设备是否可靠。"
  },
  {
    "order": 2,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "制定防汛排渍措施",
    "detailDescription": "针对运营工作制定防汛排渍措施。"
  },
  {
    "order": 3,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "加强员工教育管理",
    "detailDescription": "加强对员工的教育管理。"
  },
  {
    "order": 4,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "收集入廊管线单位资料",
    "detailDescription": "收集入廊管线单位资料。"
  },
  {
    "order": 5,
    "nodeKey": "w10",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配备发电机和大功率抽水机",
    "detailDescription": "配备发电机、大功率抽水机，保证突然停电时仍能排水。"
  },
  {
    "order": 6,
    "nodeKey": "w11",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "疏通现场排水系统",
    "detailDescription": "根据地形疏通现场排水系统，保证水流畅通、不积水。"
  },
  {
    "order": 7,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "专人24小时值班掌握管线",
    "detailDescription": "监控中心设专人24小时值班，及时掌握供水、中水、污水管线运行情况。"
  },
  {
    "order": 8,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "监控预警系统监视现场",
    "detailDescription": "依据综合管廊监控预警系统随时监控现场情况。"
  },
  {
    "order": 9,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "危险信号后报告领导小组",
    "detailDescription": "监控预警系统发出危险信号后，当班值守人员立即向光谷管廊应急救援领导小组报告。"
  },
  {
    "order": 10,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "按实际情况启动预案",
    "detailDescription": "经过信息反馈和实际情况，启动实施相应的应急救援预案。"
  },
  {
    "order": 11,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb
WHERE tenant_id = 123 AND deleted = false AND name = '爆管应急预案';

