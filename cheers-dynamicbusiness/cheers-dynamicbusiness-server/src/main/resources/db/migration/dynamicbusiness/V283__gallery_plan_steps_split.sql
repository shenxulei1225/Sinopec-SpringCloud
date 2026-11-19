-- 管廊应急预案：一段文字里的几件事拆成可单独完成的步骤。
-- 预警步骤整列重写。响应步骤只替换仍把两件事写在同一步的节点。

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
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司部按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "确定情节并报告领导小组",
          "detailDescription": "接到触电信息后，确定事故情节，立即报告应急领导小组。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "启动紧急预案",
          "detailDescription": "应急领导小组启动紧急预案。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打120",
          "detailDescription": "迅速拨打120。"
        },
        {
          "order": 7,
          "nodeKey": "s4-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "报告上级领导",
          "detailDescription": "及时报告上级领导。"
        },
        {
          "order": 8,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "触电事故发生后",
          "detailDescription": "（2）触电事故发生后,立即停止现场作业活动,将伤员放置平坦的地方,现场有救护经验的人员立即对伤员按应急措施实施紧急救护。"
        },
        {
          "order": 9,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "指挥现场救护",
          "detailDescription": "现场最高负责人担任救护指挥员，指挥现场救护。"
        },
        {
          "order": 10,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打120请求救护",
          "detailDescription": "伤员得到急救的同时，用手机或其他通讯设施拨打120，要求紧急救护。"
        },
        {
          "order": 11,
          "nodeKey": "s6-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向安全负责人和经理报告",
          "detailDescription": "打电话向安全负责人、经理及其他负责人报告。"
        },
        {
          "order": 12,
          "nodeKey": "s6-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保护事故现场",
          "detailDescription": "保护事故现场。"
        },
        {
          "order": 13,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "安全负责人接到报告后",
          "detailDescription": "（4）安全负责人接到报告后，问清楚救护中心地点，与救护中心取得联系，落实后勤保障工作，确保伤员能立即得到救护，不因后勤不到位而影响急救。"
        },
        {
          "order": 14,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "按四不放过查处",
          "detailDescription": "触电事故按现场调查处理程序，及时按四不放过原则查处。"
        },
        {
          "order": 15,
          "nodeKey": "s8-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "分析原因并编写调查报告",
          "detailDescription": "事故后分析原因，编写调查报告。"
        },
        {
          "order": 16,
          "nodeKey": "s8-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "采取纠正措施并改进预案",
          "detailDescription": "采取纠正和预防措施，对预案进行评价并改善预案。"
        },
        {
          "order": 17,
          "nodeKey": "s8-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向上级单位报告",
          "detailDescription": "事故情况及时报告上级单位。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "4、处置措施\n4.2 处置方法\n（1）将电源开关刀拉掉或将电源插头拔掉，切断电源。\n（2）用干燥的绝缘木棒、竹竿、布带等物将电源线从触电者身上拨离或者将触电者拨离电源，必要时可用绝缘工具（如带有绝缘柄的电工钳、木柄斧头以及锄头）切断电源线。\n（3） 救护人可戴上手套或在手上包缠干燥的衣服、围巾、帽子等绝缘物品拖拽触电者，使之脱离电源。\n（4）如果触电者由于痉挛手指紧握导线缠绕在身上，救护人可先用干燥的木板塞进触电者身下使其与地绝缘来隔断入地电流，然后再采取其它办法把电源切断。\n（5）如果触电者触及断落在地上的带电高压导线，且尚未确证线路无电之前，救护人员不可进入断线落地点8～10米的范围内， 防止跨步电压触电。进入该范围的救护人员应穿上绝缘靴或临时双脚并拢跳跃地接近触电者。触电者脱离带电导线后应迅速将其带至8～10米以外立即开始触电急救。只有在确认线路已经无电，才可在触电者离开触电导线后就地急救。\n（6）确定触电人员的伤害状况，根据其体征采取现场急救措施。\n坚信假死状态，在触电人员确实脱离危险或120急救人员到达现场之前坚持进行抢救。\n（7）夜晚发生触电事故时，应考虑切断电源后的临时照明， 以利救护。\n（8）带有绝缘柄的电工钳、木柄斧头以及锄头）切断电源线。\n（9）救护人可戴上手套或在手上包缠干燥的衣服、围巾、帽子等绝缘物品拖拽触电者，使之脱离电源。\n（10）如果触电者由于痉挛手指紧握导线缠绕在身上，救护人可先用干燥的木板塞进触电者身下使其与地绝缘来隔断入地电流，然后再采取其它办法把电源切断。\n（11）如果触电者触及断落在地上的带电高压导线，且尚未确证线路无电之前，救护人员不可进入断线落地点8～10米的范围内，防止跨步电压触电。进入该范围的救护人员应穿上绝缘靴或临时双脚并拢跳跃地接近触电者。触电者脱离带电导线后应迅速将其带至8～10米以外立即开始触电急救。只有在确认线路已经无电，才可在触电者离开触电导线后就地急救。\n（12）确定触电人员的伤害状况，根据其体征采取现场急救措施。\n坚信假死状态，在触电人员确实脱离危险或120急救人员到达现场之前坚持进行抢救。\n4.3 触电应急处置\n4.3.1切断电源或使触电人员迅速脱离电源的方法如下：\n（1）高压触电脱离方法：触电者触及高压带电设备，救护人员应迅速断开使触电者带电的开关、刀闸或其他断路设备，或用适合该电压等级的绝缘工具（绝缘手套、穿绝缘鞋、并使用绝缘棒）等方法，将触电者与带电设备脱离。触电者未脱离电源前，现场救护人员不得直接用手触及伤员。救护人员在抢救过程中应注意保持自身与周围带电部分必要的安全距离，保证自己免受电击。\n（2）低压触电脱离方法：低压设备触电，救护人员应设法迅速切断电源，如拉开电源开关、刀闸，拔除电源插头等；或使用绝缘工具、干燥的木棒、木板、绝缘绳子等绝缘材料解脱触电者； 也可抓住触电者干燥而不贴身的衣服，将其拖开，切记要避免碰到金属物体和触电者的裸露身体；也可用绝缘手套或将手用干燥衣物等包起绝缘后解脱触电者；救护人员也可站在绝缘垫上或干木板上，绝缘自己进行救护。为使触电者脱离导电体，最好用一只手进行。\n（3）高处触电脱离方法：高处作业触电，应迅速切断线路电源的开关、刀闸或其他断路设备。对低压带电线路，救护人员应立即攀登至能确保自己安全的位置，系好自己的安全带后，用带绝缘柄钢丝钳、干燥的绝缘体将触电者拉离电源。在完成上述措施后，应立即用绳索迅速将伤员送至地面，或采取迅速有效的措施送至地面或平台上。\n解脱电源后,可能会造成高处坠落而再次伤害的，要迅速采取地面拉网、垫软物等预防措施。\n（4）落地带电导线触电脱离方法：触电者触及断落在地的带电高压导线，在未明确线路是否有电，救护人员在做好安全措施（如穿好绝缘靴、戴好绝缘手套）后，才能用绝缘棒拨离带电导线。救护人员应疏散现场人员在以导线落地点为圆心，8米为半径的范围以外，以防跨步电压伤人。\n4.3.2 现场抢救方法：\n（1）判断触电伤员神志是否清醒\n①触电伤员如神志清醒者，应使其就地仰面平躺，严密观察，暂时不要使其站立或走动。\n②触电伤员如神志不清者，应就地仰面平躺，且确保气道畅通，并呼叫伤员或轻拍其肩部，以判断伤员是否意识丧失，禁止摇动伤员头部呼叫伤员。\n（2）触电后又摔伤的伤员，应就地仰面平躺，保持脊柱在伸直状态，不得弯曲；如需搬运，应用硬模板保持仰面平躺，使伤员身体处于平直状态，避免脊椎受伤。\n（3）对触电伤员呼吸、心跳情况的判定\n①触电伤员如意识丧失，应在10秒内，用看、听、试的方法， 判定伤员呼吸、心跳情况。\n看——看伤员的胸部、腹部有无起伏动作。\n听——用耳贴近伤员的口鼻处，听有无呼气声音。\n试——试测口鼻有无呼气的气流，再用两手指轻试一侧（左或右）喉结旁凹陷处的颈动脉有无搏动。\n②若看、听、试结果，既无呼吸又无颈动脉搏动，则可判定为呼吸、心跳停止。\n（4）触电伤员的呼吸和心跳均已停止时，应立即按心肺复苏法中支持生命的三项基本措施进行抢救。三项基本措施：\n①通畅气道：触电伤员呼吸停止，重要的是应始终确保气道通畅。\n如发现伤员口内有异物，可将其身体及头部同时侧转，并迅速用一个手指或用两手指交叉从口角处插入，取出异物。\n操作中要注意防止将异物推到咽喉深部。\n通畅气道可采用仰头抬颏法。用一只手放在触电者前额，另一只手的手指将其下颌骨向上抬起，两手协同将头部推向后仰，舌根随之抬起，气道即可通畅，严禁用枕头或其他物品垫在伤员头下。头部抬高前倾，会加重气道的阻塞，且使胸外按压时心脏流向脑部的血流减少，甚至消失。如下图：\n![图1 仰头抬颏法 通畅气道](/emergency-plan/electric/airway.png)\n\n②口对口（鼻）人工呼吸：在保持伤员气道通畅的同时，救护人员用放在伤员额头上的手指，捏住伤员的鼻翼，在救护人员深吸气后，与伤员口对口紧合，在不漏气的情况下，先连续大口吹气两次， 每次1秒—5秒。如两次吹气后试测颈动脉仍无搏动，可判断心跳已经停止，要立即同时进行胸外按压。\n除开始时大口吹气两次外，正常口对口（鼻）呼吸的吹气量不宜过大，以免引起胃膨胀。吹气和放松时要注意伤员胸部应有起伏的呼吸动作。吹气时如有较大阻力，可能是头部后仰不够，应及时纠正。\n触电伤员如牙关紧闭，可口对鼻进行人工呼吸。口对鼻人工呼吸吹气时，要将伤员嘴唇紧闭，防止漏气。如下图：\n![图2 口对口人工呼吸法](/emergency-plan/electric/breath.png)\n\n③胸外按压：确定正确按压位置的步骤如下：\n右手的食指和中指沿触电伤员的右侧肋弓下缘向上，找到肋骨和胸骨接合处的中点；\n两手指并齐，中指放在切迹中点（剑突底部），食指平放在胸骨下部；\n另一只手的掌根紧贴食指上缘置于胸骨上，即为按压位置。\n正确的按压姿势如下：\n使触电伤员仰面躺在平硬的地方，救护人员站立或跪在伤员一侧肩旁，两肩位于伤员胸骨正上方，两臂伸直，肘关节固定不屈， 两手掌根相叠，手指翘起，不接触伤员胸壁；\n以髋关节为支点，利用上身的重力，垂直将正常成人胸骨压陷3cm——5cm（瘦弱者酌减） ；\n按压至要求程度后，立即全部放松，但放松时救护人员的掌根不得离开胸壁。\n按压必须有效，其标志是按压过程中可触及到颈动脉搏动。\n操作频率如下：\n外按压要以均匀速度进行，每分钟80次左右，每次按压和放松的时间相等。\n胸外按压与口对口（鼻）人工呼吸同时进行，其节奏为：单人抢救时，每按压15次后吹气2次（15：2）， 反复进行；\n双人抢救时，每按压5次后由另一人吹气1次（5：1），反复进行。\n如下图：\n![图3 胸外心脏按压法](/emergency-plan/electric/compress.png)\n\n（5）抢救过程中的再判定\n①按压吹气1分钟后（相当于单人抢救时做了4个15：2压吹循环），应用看、听、试方法在5秒—7秒时间内完成对伤员呼吸和心跳是否恢复的再判定。\n②若判定颈动脉已有搏动但无呼吸，则暂停胸外按压，再进行2次口对口人工呼吸，接着每5秒时间吹气1次（每分钟12次）。如脉搏和呼吸均未恢复，则继续坚持心肺复苏法抢救。\n（6）抢救过程中伤员的移动与转院\n①心肺复苏应在现场就地坚持进行，不要为方便而随意移动伤员，如确实需要移动时，抢救中断时间不应超过30秒。\n②移动伤员或将伤员送往医院时，应使伤员平躺在担架上，并在其背部垫以平硬阔木板。移动或送医院过程中应继续抢救，心跳呼吸停止者要继续心肺复苏法抢救。\n③应创造条件，用塑料袋装入砸碎了的冰屑做成帽状包绕在伤员头部，露出眼睛，使脑部温度降低，争取心脑完全复苏。\n4.3.3 伤员好转后的处理\n（1）如伤员的心跳和呼吸经抢救后均已恢复，可暂停心肺复苏法操作，但心跳呼吸恢复的早期有可能再次骤停，应严密监护， 不能麻痹，要随时准备再次抢救。\n（2）初期恢复后，伤员可能神志不清或精神恍惚、躁动，应设法使伤员安静。\n（3）现场抢救用药：现场触电抢救，对采用肾上腺素等药物治疗应持慎重态度。如没有必要的诊断设备和条件及足够的把握，不得乱用。在医院内抢救触电者时，由医务人员经医疗仪器设备诊断后，根据诊断结果再决定是否采用。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n4.1 基本原则\n（1）坚持救人第一原则。触电事故发生后，首先要迅速切断电源或使用合理的方法使触电人员脱离电源，并防止二次事故的发生。\n（2）坚持科学施救、动作迅速原则。根据触电人员伤害情况，积极进行现场急救，同时划出隔离带和警戒线，保护好现场， 及时进行事故报告。\n（3）触电急救的要点是动作迅速，救护得法，切不可惊慌失措，束手无策。要贯彻“迅速、就地、正确、坚持”的触电急救八字方针。\n发现有人触电，首先要尽快使触电者脱离电源， 然后根据触电者的具体症状进行对症施救。\n\n4.4 其他要求针对现场应急救援可能出现的技术难点，应急指挥机构及应急专家组应及时研究对策。\n实施应急救援前，应排除救援现场的隐患，确保救援环境安全。\n自身救援力量不足时应及时请求社会力量协助。\n\n附表一\n附表二：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    230,
    231,
    232,
    233,
    234,
    235,
    236,
    237,
    238,
    239,
    240,
    241,
    242
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '触电事故应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立防地震、塌方联动机制",
    "detailDescription": "项目公司、运营项目部与周边临近企业、政府部门等建立防地震、塌方联动机制。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "安装高清视频监控和报警系统",
    "detailDescription": "在重点设备安装地点安装高清晰视频监控系统及报警系统。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配置必要的照明设施",
    "detailDescription": "在视频监控点配置必要的照明设施。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "值班人员24小时值守",
    "detailDescription": "值班人员24小时不间断地值守在视频监控点。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "做好监控记录",
    "detailDescription": "做好监控记录。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "启动本应急预案",
    "detailDescription": "发生突发地震、塌方事件，启动本应急预案。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "1小时内报告上级和公安",
    "detailDescription": "迅速按规定程序向上级应急管理机构和公安部门报告。从事发到报告的时间间隔最多不得超过1个小时。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "报告政府主管部门",
    "detailDescription": "按照国家和政府对突发地震、塌方事件报告的时限要求，报告政府主管部门。"
  },
  {
    "order": 9,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "项目公司、运营项目部与周边临近企业、政府部门建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "当遇到地震时",
          "detailDescription": "(1)当遇到地震时，监控中心应立即通知现场巡检人员检查综合管廊内人员是否安全撤离，若发现尚有人员被困，应立即引导疏散和抢救。"
        },
        {
          "order": 5,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "设立警戒岗位",
          "detailDescription": "(2)设立警戒岗位，保证人员迅速安全离开。"
        },
        {
          "order": 6,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "通知管线用户切断相关系统",
          "detailDescription": "监控中心及时通知综合管廊各管线租用户切断相关系统，减少损失。"
        },
        {
          "order": 7,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "震后检查设施并组织修缮",
          "detailDescription": "地震过后，配合相关部门检查综合管廊设备设施的损坏情况，做好灾后修缮。"
        },
        {
          "order": 8,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "迅速致电政府地震管理部门",
          "detailDescription": "(4)迅速致电政府地震管理部门，查询地震强度及余震情况。"
        },
        {
          "order": 9,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "出现人员伤亡时",
          "detailDescription": "(5)出现人员伤亡时，应立即送至附近医疗点急救。"
        },
        {
          "order": 10,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "留意治安防止趁火打劫",
          "detailDescription": "现场巡检值班人员留意综合管廊治安，防止不法分子趁火打劫。"
        },
        {
          "order": 11,
          "nodeKey": "s9-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "记录地震相关情况",
          "detailDescription": "对地震相关情况做好记录。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "5、应急处置措施\n(1)当遇到地震时，监控中心应立即通知现场巡检人员检查综合管廊内人员是否安全撤离，若发现尚有人员被困，应立即引导疏散和抢救。\n(2)设立警戒岗位，保证人员迅速安全离开。\n(3)监控中心应及时通知综合管廊各管线租用户切断相关系统，减少损失。地震过后，配合相关部门检查综合管廊所有设备设施的损坏情况，做好灾后的修缮工作。\n(4)迅速致电政府地震管理部门，查询地震强度及余震情况。\n(5)出现人员伤亡时，应立即送至附近医疗点急救。\n(6)现场巡检值班人员要留意综合管廊的治安情况，防止不法分子趁火打劫。同时对地震相关情况做好记录。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "4、应急处置原则从思想上充分认识地震、塌方的严重性。建立高效的工作体制和快速反应机制，加强统一领导。做好舆论宣传导向工作，确保项目公司安全与稳定的经营秩序。\n\n7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n附表 1：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    243,
    244,
    245,
    246,
    247,
    248,
    249,
    250,
    251,
    252,
    253,
    254,
    255
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '地震塌方应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET step_tree_json = $json$
{
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "actionId": "",
          "nodeType": "STEP",
          "required": true,
          "dependsOn": [],
          "paramSlots": [],
          "executionMode": "SEQUENTIAL",
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "actionId": "",
          "nodeType": "STEP",
          "required": true,
          "dependsOn": [],
          "paramSlots": [],
          "executionMode": "SEQUENTIAL",
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "actionId": "",
          "nodeType": "STEP",
          "required": true,
          "dependsOn": [],
          "paramSlots": [],
          "executionMode": "SEQUENTIAL",
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "确定情节并报告领导小组",
          "detailDescription": "接到交通事故信息后，确定事故情节，立即报告应急领导小组。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "启动紧急预案",
          "detailDescription": "应急领导小组启动紧急预案。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打120",
          "detailDescription": "迅速拨打120。"
        },
        {
          "order": 7,
          "nodeKey": "s4-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "报告上级领导",
          "detailDescription": "及时报告上级领导。"
        },
        {
          "order": 8,
          "nodeKey": "s5",
          "actionId": "",
          "nodeType": "STEP",
          "required": true,
          "dependsOn": [],
          "paramSlots": [],
          "executionMode": "SEQUENTIAL",
          "parentNodeKey": null,
          "stepDescription": "交通事故事故发生后",
          "detailDescription": "（2）交通事故事故发生后,立即停止现场作业活动,将伤员放置平坦的地方,现场有救护经验的人员立即对伤员按照应急措施实施紧急救护。"
        },
        {
          "order": 9,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "指挥现场救护",
          "detailDescription": "现场最高负责人担任救护指挥员，指挥现场救护。"
        },
        {
          "order": 10,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打120请求救护",
          "detailDescription": "伤员得到急救的同时，用手机或其他通讯设施拨打120，要求紧急救护。"
        },
        {
          "order": 11,
          "nodeKey": "s6-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向项目公司负责人报告",
          "detailDescription": "打电话向项目公司负责人及其他负责人报告。"
        },
        {
          "order": 12,
          "nodeKey": "s6-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保护事故现场",
          "detailDescription": "保护事故现场。"
        },
        {
          "order": 13,
          "nodeKey": "s7",
          "actionId": "",
          "nodeType": "STEP",
          "required": true,
          "dependsOn": [],
          "paramSlots": [],
          "executionMode": "SEQUENTIAL",
          "parentNodeKey": null,
          "stepDescription": "安全负责人接到报告后",
          "detailDescription": "（4）安全负责人接到报告后，问清楚救护中心地点，与救护中心取得联系，落实后勤保障工作，确保伤员能立即得到救护，不因后勤不到位而影响急救。"
        },
        {
          "order": 14,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "按四不放过查处",
          "detailDescription": "交通事故按现场调查处理程序，及时按四不放过原则查处。"
        },
        {
          "order": 15,
          "nodeKey": "s8-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "分析原因并编写调查报告",
          "detailDescription": "事故后分析原因，编写调查报告。"
        },
        {
          "order": 16,
          "nodeKey": "s8-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "采取纠正措施并改进预案",
          "detailDescription": "采取纠正和预防措施，对预案进行评价并改善预案。"
        },
        {
          "order": 17,
          "nodeKey": "s8-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向上级单位报告",
          "detailDescription": "事故情况及时报告上级单位。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '交通事故应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "安装气体监测设备",
    "detailDescription": "对可能产生有毒有害气体的场所安装气体监测设备，实时监测气体浓度。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查通风和照明",
    "detailDescription": "定期对有限空间进行检查和维护，确保通风、照明等设施正常运行。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "检查设备和管道密封",
    "detailDescription": "加强对相关设备和管道的密封性检查，防止有害气体泄漏。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "进入前通风并检测气体",
    "detailDescription": "在有人员工作的管廊分区，提前开启通风设备换气。进入前用气体检测设备检测，合格并经作业负责人同意方可进入。氧气含量应在19.5%至23.5%，二氧化碳每立方米不高于1000PPM。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "作业中持续监测和通风",
    "detailDescription": "作业过程中持续进行气体监测，并开启通风设备换气。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "身体不适时撤离",
    "detailDescription": "作业人员出现身体不适时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "防护失效时撤离",
    "detailDescription": "安全防护设备或个体防护用品失效时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "气体检测报警时撤离",
    "detailDescription": "气体检测设备报警时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 9,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "接到撤离命令时撤离",
    "detailDescription": "监护人员或现场负责人下达撤离命令时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 10,
    "nodeKey": "w10",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "气体超标时撤离",
    "detailDescription": "氧气含量严重不足或有害气体浓度超过安全限值时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 11,
    "nodeKey": "w11",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "通风故障时撤离",
    "detailDescription": "通风设备突然故障、无法保证空气正常流通时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 12,
    "nodeKey": "w12",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "其他危险情况撤离",
    "detailDescription": "出现其他可能危及安全的情况时，立即中断作业，撤离作业分区。"
  },
  {
    "order": 13,
    "nodeKey": "w13",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "中断作业后报告上级",
    "detailDescription": "中断作业应当向上级领导报告。"
  },
  {
    "order": 14,
    "nodeKey": "w14",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "有人受伤时拨打120",
    "detailDescription": "有人员受伤时，立即拨打120急救中心。"
  },
  {
    "order": 15,
    "nodeKey": "w15",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "项目公司、运营项目部建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息畅通。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "立即停止作业",
          "detailDescription": "事故发生后立即停止作业。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向本单位报告",
          "detailDescription": "现场负责人或监护人员及时向本单位报告事故情况。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "开启逃生口或加强通风",
          "detailDescription": "开启设备间上方人员逃生口紧急撤离，或加强通风。"
        },
        {
          "order": 7,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "设立警戒",
          "detailDescription": "6.2设立警戒，无关人员禁入施救现场负责人根据救援需要设置警戒区域和警示标志，严禁无关人员禁入现场。"
        },
        {
          "order": 8,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "穿戴防护并隔离",
          "detailDescription": "救援人员正确穿戴个体防护装备，采取可靠的隔离措施。"
        },
        {
          "order": 9,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向管廊输送清洁空气",
          "detailDescription": "使用通风设备向管廊内输送清洁空气，直至救援结束。"
        },
        {
          "order": 10,
          "nodeKey": "s6-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "转移到空气新鲜处救护",
          "detailDescription": "受困人员脱离有限空间后，迅速转移到安全、空气新鲜处，进行现场救护。"
        },
        {
          "order": 11,
          "nodeKey": "s6-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "不能自主救援时呼叫专业力量",
          "detailDescription": "不具备自主救援条件时，拨打119和120，依靠专业力量救援。不允许强行施救。"
        },
        {
          "order": 12,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保持联络",
          "detailDescription": "6.4保持联络，轮换救援救援过程中，按照事先明确的联络信号与外部人员进行有效联络，并保持通讯畅通，救援人员施救时间较长时，应轮换救援。"
        },
        {
          "order": 13,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "出现危险",
          "detailDescription": "6.5出现危险，及时撤离出现可能危及安全的情况时，救援人员应立即撤离危险区域，安全条件具备后再施救。"
        },
        {
          "order": 14,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "清点人员装备并清理有毒物质",
          "detailDescription": "救援结束后清点、核实现场人员、装备，清理残留的有毒有害物质。"
        },
        {
          "order": 15,
          "nodeKey": "s9-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保护现场以便调查",
          "detailDescription": "尽可能保护事故现场，便于后续事故调查和救援评估。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "4、处置措施\n4.2 救援措施\n（1）发生有限空间作业中毒和窒息事故后，应立即分析事发有限空间环境危害控制情况、应急救援装备配置情况以及现场救援能力等情况，判断可否采取自主救援以及采取何种救援方式。\n（2）若现场具备自主救援条件，应根据实际情况采取非进入式或进入式救援，并确保救援人员人身安全。\n（3）在事故现场设立警示标志，并有专人值守，做好现场保卫警戒工作，以防事态扩大。\n（4）采取进入式救援应在确认安全情况下，使用大功率机械通风设备进行通风；救援人员进入有限空间场所前应进行气体检测，氧气含量在19.5％-23.5%且有毒有害气体含量达到施救条件，确认符合要求后，方可救援。\n（5）救援人员应穿戴专业防护服、佩戴正压式空气呼吸器或高压送风式长管呼吸器、防爆通讯器材等应急设备，配备防爆手电，必要时佩戴全身式安全带、系安全绳。\n（6）进入有限空间场所后应确认伤者状态，用担架移至出入口下方，由其他人员使用救生绳和安全带将其吊出。救生绳和安全带连接处应位于胸前，以便吊起时伤者头部后仰保持呼吸顺畅。\n（7）迅速将伤者转移至安全、空气新鲜处，呼吸困难时给氧，呼吸停止或心搏骤停时立即进行心肺复苏。伤者苏醒后继续对其供氧并保暖，保持平躺状态，等候进一步治疗。\n（8）立即与当地救护中心联系，迅速将伤者转送医院。\n（9）若现场不具备自主救援条件，应及时拨打119和120，借助专业救援力量开展救援工作，决不允许强行施救。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n4.1 处置原则\n（1）事故发生后，事发单位应按照应急预案，第一时间采取有效的处置措施控制事态发展，最大限度减少损失。\n（2）事故现场的应急处置，坚持以人为本的原则，做好应急救援人员的自我保护，防止次生灾害发生。\n（3）快速响应：一旦发现中毒窒息事件，迅速行动，争取黄金救援时间。\n（4）科学施救：依据科学知识和专业方法进行救援，避免盲目行动导致二次伤害。\n（5）现场救援与送医救治结合：在现场进行初步急救的同时，尽快将伤员送往医院。\n（6）防止事故扩大：采取有效措施防止中毒窒息范围进一步扩大。\n\n4.3 其他要求\n（1）针对现场应急救援可能出现的技术难点，应急指挥机构及专家技术组应及时研究对策。\n（2）实施应急救援前，应排除救援现场隐患，确保救援环境安全。应急救援过程中，应注意防止施救不当引起伤亡扩大。\n（3）为救援人员提供的劳动防护用品应齐全、有效。\n（4）应急预案体系中应明确编制物资清单，储备相应的应急救援装备、器材和药品等物资，并确保状态良好。\n（5）科学及时做好信息发布和舆论引导，未经授权任何人不得擅自对外发布、传播与事故相关的信息或接受媒体采访。\n\n附表 1：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    269,
    270,
    271,
    272,
    273,
    274,
    275,
    276,
    277,
    278,
    279,
    280,
    281,
    282,
    283,
    284
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '有限空间中毒窒息应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "专人24小时值班",
    "detailDescription": "防台、防汛、防暴雨应急处理领导小组设专人24小时值班。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "掌握气象和汛情",
    "detailDescription": "与气象部门联系，及时掌握降雨量、上游水位等气象信息和汛情动态。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "监控预警系统监视现场",
    "detailDescription": "依据综合管廊监控预警系统随时监控现场情况。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "危险信号后报告领导小组",
    "detailDescription": "监控预警系统发出危险信号后，当班值守人员立即向项目公司应急领导小组报告。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "按实际情况启动预案",
    "detailDescription": "经过信息反馈和实际情况，启动实施相应的应急救援预案。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "相关人员全部到位",
          "detailDescription": "接到汛情警报后，相关人员全部到位。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "加强值班巡查",
          "detailDescription": "加强值班巡查。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "检查出入口和排水设施",
          "detailDescription": "对控制中心、出入口、通风口及排水设施进行全面检查。"
        },
        {
          "order": 7,
          "nodeKey": "s4-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "疏通排水沟渠",
          "detailDescription": "疏通排水沟渠。"
        },
        {
          "order": 8,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "切断可能被淹设备的电源",
          "detailDescription": "切断可能被水浸淹的设备电源。"
        },
        {
          "order": 9,
          "nodeKey": "s5-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "转移人员物资和设备",
          "detailDescription": "把人员、物资和设备疏散转移到安全地带。"
        },
        {
          "order": 10,
          "nodeKey": "s5-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "准备抢险救灾物资",
          "detailDescription": "准备好各种抢险救灾物资。"
        },
        {
          "order": 11,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "建立防汛器材库",
          "detailDescription": "4.3.3建立防汛器材库，由专人保管，严禁挪作他用，并有详细工具列表，严格岗位责任制，每人有自己的责任范围，经常进行检查，加强汇报制度，如遇到险情及时上报和反馈。"
        },
        {
          "order": 12,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "有积水时到现场排险",
          "detailDescription": "有积水时，防汛值班人员及时赶到现场排险。"
        },
        {
          "order": 13,
          "nodeKey": "s7-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "重点部位重点监控",
          "detailDescription": "对重点部位重点监控。"
        },
        {
          "order": 14,
          "nodeKey": "s7-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "一般部位强化管理",
          "detailDescription": "对其他一般部位进行强化管理。"
        },
        {
          "order": 15,
          "nodeKey": "s7-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "险情及时上报并现场排险",
          "detailDescription": "一旦有险情，及时上报，并在现场主动排险。"
        },
        {
          "order": 16,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "汛期警报期间",
          "detailDescription": "4.3.5汛期警报期间，所有值班人员必须24小时开机，确保通讯畅通。"
        },
        {
          "order": 17,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "现场救治伤员",
          "detailDescription": "有人员受伤时，先清除伤员口鼻杂物，外伤做简易包扎，呼吸心跳停止的做人工呼吸和心脏复苏。"
        },
        {
          "order": 18,
          "nodeKey": "s9-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "联系急救中心并派人接应",
          "detailDescription": "尽快与急救中心联系，并派人到路口接应。"
        },
        {
          "order": 19,
          "nodeKey": "s10",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急结束",
          "detailDescription": "4.3.7应急结束。当遇险人员全部得救，事故现场得以控制，符合有关标准，导致次生、衍生事故隐患消除后，经现场应急救援指挥部确认和批准，现场应急处置工作结束，应急救援队伍撤离现场，应急结束。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "4、处置措施\n4.3应急处置措施\n4.3.1接到汛情警报后，相关人员全部到位，加强值班巡查，对综合管廊的控制中心、出入口、通风口及排水设施等进行全面的检查，疏通排水沟渠。\n4.3.2切断可能被水浸淹的设备电源，人员、物资和设备疏散转移到安全地带。同时准备好各种抢险救灾物质。\n4.3.3建立防汛器材库，由专人保管，严禁挪作他用，并有详细工具列表，严格岗位责任制，每人有自己的责任范围，经常进行检查，加强汇报制度，如遇到险情及时上报和反馈。\n4.3.4根据现场实际情况，做好防范措施。如有积水情况，防汛值班人员必须及时赶到现场进行排险。在对重点部位重点监控的同时，也要对其他一般部位进行强化管理。一旦有险情要及时上报并且现场主动排险。\n4.3.5汛期警报期间，所有值班人员必须24小时开机，确保通讯畅通。\n4.3.6人员救治。事故发生如遇有人员受伤，应首先对人员进行救治，如清除伤员口鼻杂物，以防窒息，对外伤要进行简易包扎，对呼吸心跳停止的伤员予以人工呼吸等心脏复苏救治。同时尽快与急救中心取得联系，并派人至路口接应。\n4.3.7应急结束。当遇险人员全部得救，事故现场得以控制，符合有关标准，导致次生、衍生事故隐患消除后，经现场应急救援指挥部确认和批准，现场应急处置工作结束，应急救援队伍撤离现场，应急结束。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "6.2应急物资注意事项1、专人管理，明确责任，保证物资管理有序，便于随时取用。\n2、定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n3、定期检查：确保物资质量和有效期。\n4、对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n5、过期或损坏的物资妥善处理。\n\n4.1 基本原则认真贯彻落实“以防为主，防抗救结合”的工作方针；全面部署，保证重点；统一指挥，各班组协调配合、科学实施防汛调度、抢险救灾，共同应对汛情，尽可能调动一切积极因素，团结抗洪。\n\n4.2防台、防汛、防暴雨保证措施\n4.2.1成立以总经理为组长的防洪领导小组，同时成立抗洪突击队。\n4.2.2做好前期预防准备工作：\n（1）前检查排水沟，污水井是否有淤泥堵塞，检查排水管道与市政管网接口，保证畅通。\n（2）检查排污水泵自动抽排系统是否灵敏、抽排效果是否正常，特别保证监控中心及连廊段水泵正常运行。\n（3）对引出口、吊装口、换舱口应每日进行巡查，查看堵头是否松动、是否密封不严，特别注意对于低于地面标高的逃生口重点关注，出现异常情况立即上报（及时安排水泵进行抽排）。\n（4）检修备用发电机和控制线路，发电机燃料是否充足，保证应急电源的安全可靠。\n（5）检查手电筒及各类防汛物资的状态完好情况，检查设备机房应急照明。\n（6）在汛期充分优化施工组织安排，针对运营工作特点和要求，汛期前编制防洪应急措施，加强对员工的教育管理，项目固定专人，每天定时收看当地天气预报，充分收集及掌握武汉地区的气象及水文资料，并将重要信息及时上报项目负责人。\n4.2.3配足防洪物资及设备，如沙袋、帆布蓬、大功率抽水机等，并禁止挪用。\n4.2.4配备发电机，以确保汛期突然停电情况下的防排水需要。\n4.2.5根据地形对现场排水系统进行疏通，以保证水流畅通，不积水。\n4.2.6建立与运维项目部、防汛物资供应商、就近医院等单位的联动机制，确保汛情时可以互相协调与帮助，将汛期带给光谷综合管廊的危害及损失降至最低。\n4.2.7防洪领导小组成员及防汛值班人员保持警惕，当达到汛情预警值时，立即向有关方面汇报并启动应急预案。\n\n附表一：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    285,
    286,
    287,
    288,
    289,
    290,
    291,
    292,
    293,
    294,
    295,
    296,
    297
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '防汛防暴雨应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "专人24小时值班掌握管线",
    "detailDescription": "监控中心设专人24小时值班，及时掌握供水、中水、污水管线运行情况。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "监控预警系统监视现场",
    "detailDescription": "依据综合管廊监控预警系统随时监控现场情况。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "危险信号后报告领导小组",
    "detailDescription": "监控预警系统发出危险信号后，当班值守人员立即向光谷管廊应急救援领导小组报告。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "按实际情况启动预案",
    "detailDescription": "经过信息反馈和实际情况，启动实施相应的应急救援预案。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "接到警报后",
          "detailDescription": "(1)接到警报后，相关人员全部到位，切断破损管道供水、可能被水浸淹的设备电源，开启排水泵、通风设备，若排水不畅疏通堵塞排水沟渠。"
        },
        {
          "order": 5,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "重点部位重点监控",
          "detailDescription": "对重点部位重点监控。"
        },
        {
          "order": 6,
          "nodeKey": "s5-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "一般部位强化管理",
          "detailDescription": "对其他一般部位进行强化管理。"
        },
        {
          "order": 7,
          "nodeKey": "s5-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "异常情况上报并现场排险",
          "detailDescription": "一旦有其他异常情况，及时上报，并在现场主动排险。"
        },
        {
          "order": 8,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "抢险期间",
          "detailDescription": "(3)抢险期间，所有值班人员必须24小时开机，确保通讯畅通。"
        },
        {
          "order": 9,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "现场救治伤员",
          "detailDescription": "有人员受伤时，先清除伤员口鼻杂物，外伤做简易包扎，呼吸心跳停止的做人工呼吸和心脏复苏。"
        },
        {
          "order": 10,
          "nodeKey": "s7-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "联系急救中心并派人接应",
          "detailDescription": "尽快与急救中心联系，并派人到路口接应。"
        },
        {
          "order": 11,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急结束",
          "detailDescription": "(5)应急结束。当遇险人员全部得救，事故现场得以控制，符合有关标准，导致次生、衍生事故隐患消除后，经现场应急救援指挥部确认和批准，现场应急处置工作结束，应急救援队伍撤离现场。由项目公司会同有关部门宣布应急结束。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "5、应急处置措施\n5.2应急处置措施\n(1)接到警报后，相关人员全部到位，切断破损管道供水、可能被水浸淹的设备电源，开启排水泵、通风设备，若排水不畅疏通堵塞排水沟渠。\n(2)在对重点部位重点监控的同时，也要对其他一般部位进行强化管理。一旦有其他异常情况要及时上报并且现场主动排险。\n(3)抢险期间，所有值班人员必须24小时开机，确保通讯畅通。\n(4)人员救治。事故发生如遇有人员受伤，应首先对人员进行救治，如清除伤员口鼻杂物，以防窒息，对外伤要进行简易包扎，对呼吸心跳停止的伤员予以人工呼吸等心脏复苏救治。同时尽快与急救中心取得联系，并派人至路口接应。\n(5)应急结束。当遇险人员全部得救，事故现场得以控制，符合有关标准，导致次生、衍生事故隐患消除后，经现场应急救援指挥部确认和批准，现场应急处置工作结束，应急救援队伍撤离现场。由项目公司会同有关部门宣布应急结束。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "4、应急处置原则认真贯彻落实“以防为主，防抢结合”的工作方针；全面部署，保证重点；统一指挥，科学实施防水管破裂、抢险救灾，尽可能调动一切积极因素，团结排渍。\n\n7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n5.1防破排渍保证措施\n（1）定期检查排水沟（管）及抽水设备的可靠性，加强防洪应变能力。\n（2）针对运营工作特点和要求，制定防汛排渍措施，加强对员工的教育管理，充分收集入廊管线单位资料。\n（3）配备发电机、大功率抽水机等，以确保突然停电情况下的排水需要。\n（4）根据地形对现场排水系统进行疏通，以保证水流畅通，不积水。\n\n附表 1：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    298,
    299,
    300,
    301,
    302,
    303,
    304,
    305,
    306,
    307,
    308,
    309,
    310,
    311
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '爆管应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立防恐联动机制",
    "detailDescription": "项目公司、运营项目部与周边临近企业、政府部门等建立防恐联动机制。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "安装高清视频监控和报警系统",
    "detailDescription": "在重点设备安装地点安装高清晰视频监控系统及报警系统。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配置必要的照明设施",
    "detailDescription": "在视频监控点配置必要的照明设施。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "值班人员24小时值守",
    "detailDescription": "值班人员24小时不间断地值守在视频监控点。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "做好监控记录",
    "detailDescription": "做好监控记录。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "启动防恐应急预案",
    "detailDescription": "发生突发恐怖事件，启动本应急预案。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "1小时内报告上级和公安",
    "detailDescription": "迅速按规定程序向上级应急管理机构和公安部门报告。从事发到报告的时间间隔最多不得超过1个小时。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "报告政府主管部门",
    "detailDescription": "按照国家和政府对突发恐怖事件报告的时限要求，报告政府主管部门。"
  },
  {
    "order": 9,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "项目公司、运营项目部与周边临近企业、政府部门建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "实时监控现场",
          "detailDescription": "发生突发恐怖事件后，迅速对事件现场进行实时监控。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "封闭现场",
          "detailDescription": "封闭事件现场。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "追踪事态",
          "detailDescription": "追踪事态。"
        },
        {
          "order": 7,
          "nodeKey": "s4-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "上报事态变化",
          "detailDescription": "上报事态发展变化情况。"
        },
        {
          "order": 8,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打“110”求助",
          "detailDescription": "(2)拨打“110”求助，说明事情经过、参与人数、所携带的器械、事件发生的地点等。"
        },
        {
          "order": 9,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "抢救受伤人员",
          "detailDescription": "抢救受伤人员，进行简单处置。"
        },
        {
          "order": 10,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "送往医院",
          "detailDescription": "简单处置后立即送往医院。"
        },
        {
          "order": 11,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "疏散周边人员",
          "detailDescription": "根据现场情况，把周边人员疏散到安全区域。"
        },
        {
          "order": 12,
          "nodeKey": "s7-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "安置疏散人员",
          "detailDescription": "对疏散人员进行安置。"
        },
        {
          "order": 13,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "设立警戒线",
          "detailDescription": "(5)设立警戒线，封锁和隔离相关区域，控制无关人员进入事发现场。"
        },
        {
          "order": 14,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "根据现场情况按照各专项预案进行抢险救援工",
          "detailDescription": "(6)根据现场情况按照各专项预案进行抢险救援工。"
        },
        {
          "order": 15,
          "nodeKey": "s10",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "运送应急物资",
          "detailDescription": "向事发现场运送应急物资。"
        },
        {
          "order": 16,
          "nodeKey": "s10-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保持现场和外部通讯",
          "detailDescription": "保持现场及外部通讯联络畅通。"
        },
        {
          "order": 17,
          "nodeKey": "s11",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "根据现场指挥部的命令进行处置",
          "detailDescription": "(8)根据现场指挥部的命令进行处置。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "5、处置措施\n(1)发生突发恐怖事件后迅速对事件现场进行实时监控、封闭、追踪，并上报事态发展变化情况。\n(2)拨打“110”求助，说明事情经过、参与人数、所携带的器械、事件发生的地点等。\n(3)抢救受伤人员，进行简单处置后立即送往医院。\n(4)根据现场情况疏散周边人员到安全区域，并进行安置。\n(5)设立警戒线，封锁和隔离相关区域，控制无关人员进入事发现场。\n(6)根据现场情况按照各专项预案进行抢险救援工。\n(7)向事发现场运送应急物资，保持现场及外部通讯联络畅通。\n(8)根据现场指挥部的命令进行处置。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "4、处置原则从思想上充分认识恐怖活动的严重性和防恐工作的紧迫性。建立高效的工作体制和快速反应机制，加强统一领导。做好舆论宣传导向工作，确保项目公司安全与稳定的经营秩序。\n\n7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n附表 1：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    312,
    313,
    314,
    315,
    316,
    317
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '防恐应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立防盗机制",
    "detailDescription": "光谷综合管廊建立防盗机制。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "在重点位置安装监控和报警",
    "detailDescription": "在重点设备安装地点及人员出入口安装高清晰视频监控系统及报警系统。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "配置必要的照明设施",
    "detailDescription": "在上述位置配置必要的照明设施。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "值班人员24小时值守",
    "detailDescription": "值班人员24小时不间断地值守在视频监控点。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "做好监控记录",
    "detailDescription": "做好监控记录。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "启动防盗应急预案",
    "detailDescription": "发生突发盗窃事件，启动本应急预案。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "1小时内报告上级和公安",
    "detailDescription": "迅速按规定程序向上级应急管理机构和公安部门报告。从事发到报告的时间间隔最多不得超过1个小时。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "实时监控现场",
          "detailDescription": "发生突发盗窃事件后，迅速对事件现场进行实时监控。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "封闭现场",
          "detailDescription": "封闭事件现场。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "追踪事态",
          "detailDescription": "追踪事态。"
        },
        {
          "order": 7,
          "nodeKey": "s4-3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "上报事态变化",
          "detailDescription": "上报事态发展变化情况。"
        },
        {
          "order": 8,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打“110”求助",
          "detailDescription": "（2）拨打“110”求助，说明事发时间、地点、事情经过、参与人数、所携带的作案工具等。"
        },
        {
          "order": 9,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "抢救受伤人员",
          "detailDescription": "有伤员时立即抢救，进行简单处置。"
        },
        {
          "order": 10,
          "nodeKey": "s6-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "送往医院",
          "detailDescription": "简单处置后立即送往医院。"
        },
        {
          "order": 11,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "设立警戒线",
          "detailDescription": "（4）设立警戒线，封锁和隔离相关区域，控制无关人员进入事发现场。"
        },
        {
          "order": 12,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "根据现场情况按照各专项预案进行设施",
          "detailDescription": "（5）根据现场情况按照各专项预案进行设施、设备修复工作。"
        },
        {
          "order": 13,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "运送应急物资",
          "detailDescription": "向事发现场运送应急物资。"
        },
        {
          "order": 14,
          "nodeKey": "s9-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保持现场和外部通讯",
          "detailDescription": "保持现场及外部通讯联络畅通。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "5、处置措施\n（1）发生突发盗窃事件后迅速对事件现场进行实时监控、封闭、追踪，并上报事态发展变化情况。\n（2）拨打“110”求助，说明事发时间、地点、事情经过、参与人数、所携带的作案工具等。\n（3）如有伤员，立即抢救受伤人员，进行简单处置后立即送往医院。\n（4）设立警戒线，封锁和隔离相关区域，控制无关人员进入事发现场。\n（5）根据现场情况按照各专项预案进行设施、设备修复工作。\n（6）向事发现场运送应急物资，保持现场及外部通讯联络畅通。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "4、处置原则\n（1）从思想上充分认识偷盗活动的严重性和防盗工作的紧迫性。\n（2）建立高效的工作体制和快速反应机制。\n（3）注重日常的安全防范措施，加强安保工作，降低被盗风险。\n（4）一旦发现盗窃迹象或事件，迅速采取行动，争取在最短时间内进行处理。\n（5）采取有效措施尽力减少被盗造成的财产损失和其它不良影响。\n（6）加强统一领导，做好舆论宣传工作，确保综合管廊安全与稳定的运维秩序。\n\n7.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n（5）过期或损坏的物资妥善处理。\n\n附表 1：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    318,
    319,
    320,
    321,
    322,
    323,
    324,
    325,
    326,
    327,
    328,
    329,
    330,
    331,
    332
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '防盗应急预案';

UPDATE dynamicbusiness.ent_emergentcy_plan_t123
SET warning_step_tree_json = $json$[
  {
    "order": 1,
    "nodeKey": "w1",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "用监控和报警系统24小时监视",
    "detailDescription": "依据管廊内视频监控系统、火灾自动报警系统，进行24小时监控。"
  },
  {
    "order": 2,
    "nodeKey": "w2",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "日常巡视检查",
    "detailDescription": "日常巡视人员进行巡视检查。"
  },
  {
    "order": 3,
    "nodeKey": "w3",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "发现火情先保证人身安全",
    "detailDescription": "发现着火或火灾隐患后，当事人或发现人先确保人身安全。"
  },
  {
    "order": 4,
    "nodeKey": "w4",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "切断电源",
    "detailDescription": "及时切断电源。"
  },
  {
    "order": 5,
    "nodeKey": "w5",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "按火情选用器材灭火",
    "detailDescription": "根据燃烧物及火情确定灭火方法。管廊内设有干粉灭火装置、磷酸铵盐干粉灭火器和防火隔断。"
  },
  {
    "order": 6,
    "nodeKey": "w6",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "火情失控时拨打119",
    "detailDescription": "不能控制火情时，立即拨打119。讲清着火单位名称、详细地址、着火物资、火情大小、报警人姓名和联系电话。"
  },
  {
    "order": 7,
    "nodeKey": "w7",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "向上级领导报告",
    "detailDescription": "同时向上级领导报告。"
  },
  {
    "order": 8,
    "nodeKey": "w8",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "有人伤亡时拨打120",
    "detailDescription": "有人员伤亡时，立即拨打120急救中心。"
  },
  {
    "order": 9,
    "nodeKey": "w9",
    "paramSlots": [],
    "parentNodeKey": null,
    "stepDescription": "建立24小时信息通讯网络",
    "detailDescription": "光谷综合管廊建立信息通讯网络，形成值班电话、值班电台、无线对讲系统、领导和相关人员移动电话组成的通讯联系网，确保信息24小时畅通无阻。"
  }
]$json$::jsonb,
    step_tree_json = $json$
{
  "stages": [
    {
      "name": "事件上报",
      "stageKey": "report"
    },
    {
      "name": "研判",
      "stageKey": "assess"
    },
    {
      "name": "应急预警",
      "stageKey": "warning"
    },
    {
      "name": "应急响应",
      "stageKey": "response"
    }
  ],
  "methods": [
    {
      "label": "应急响应",
      "stepTree": [
        {
          "order": 1,
          "nodeKey": "s1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急会议",
          "detailDescription": "3.1 应急会议由应急领导小组组长主持召开应急会议，研究决策应急响应级别、处置措施、责任人员等内容。"
        },
        {
          "order": 2,
          "nodeKey": "s2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "信息上报",
          "detailDescription": "3.2 信息上报同《生产安全事故综合应急预案》4.2 信息报告。"
        },
        {
          "order": 3,
          "nodeKey": "s3",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "应急响应",
          "detailDescription": "3.3 应急响应项目公司按照《生产安全事故综合应急预案》5.2响应程序，开展应急响应工作。按照职责分工开展资源协调、信息公开、后勤及资金保障工作。"
        },
        {
          "order": 4,
          "nodeKey": "s4",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "大声呼喊示警",
          "detailDescription": "发现火情后，发现人第一时间大声呼喊。"
        },
        {
          "order": 5,
          "nodeKey": "s4-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "报告现场负责人",
          "detailDescription": "报告现场负责人。"
        },
        {
          "order": 6,
          "nodeKey": "s4-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "拨打报警电话",
          "detailDescription": "及时拨打报警电话。"
        },
        {
          "order": 7,
          "nodeKey": "s5",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "轻微着火时立即扑灭",
          "detailDescription": "局部轻微着火、不危及人员安全且可以马上扑灭时，立即使用合适的灭火器灭火。"
        },
        {
          "order": 8,
          "nodeKey": "s5-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "有蔓延趋势时增援并报告",
          "detailDescription": "局部着火可以扑救但有蔓延扩大趋势时，在不危及人员安全的情况下，立即通知周围人员参与灭火，并向现场负责人报告。"
        },
        {
          "order": 9,
          "nodeKey": "s5-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "按火灾类别选用灭火器材",
          "detailDescription": "火灾类别与适用的灭火器材对应表参见下表：\n火灾 易燃物 适用的灭火器材/注意事项类别 类别 灭火步骤对于忌水物质，如布、纸等应尽量A类固 减少水浸所造成的损失对珍贵图棉、麻、纸 水、泡沫、干粉、二氧体物质 书，档案资料等应使用二氧化碳、张、木材 化碳火灾 卤代烷以及干粉灭火剂进行灭火。\n与火源点保持足够的距离一般用泡沫、干粉等灭火器二氧化碳适宜小范围汽油、煤B类液 油类火灾油、柴油、体和可 泡沫扑救容器内的易 禁止使用高压直流水枪直接灭火，原油、油溶的固 燃可燃液体火灾 可以使用喷雾水枪扑救。扑救时要漆、甲醇、体物质 酒精等醇类火灾要使 保持足够距离乙醇、沥火灾 用抗溶性泡沫青、石蜡沙土适宜扑救沥青和地面流散的易燃可燃液体火灾先将气体输送阀门或管道关死，截断气源，进行冷却灭火，可使用直流水枪或喷雾水枪、煤气、天然 二氧化碳或干粉灭火C类气 气、甲烷、 器戴好防护用具，防止烫伤、中毒等体火灾 乙炔、氢气 对一时无法堵漏、封闭火灾 气源的燃烧，不宜立即将火扑灭，可以一边用水冷却保护建筑物和设备，一边让气体自行燃尽钾、钠、镁、一般用沙土掩埋或使 忌用水、泡沫或含水性物质，也不D类金 钛、锆、锂、用7150金属火灾灭火 能用卤代烷、二氧化碳及常用干粉属火灾 铝镁合金剂 灭火器等火灾"
        },
        {
          "order": 10,
          "nodeKey": "s6",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "火势蔓延扩大",
          "detailDescription": "4.2.3 火势蔓延扩大，不可能马上扑灭的，现场负责人应立即组织人员疏散至安全地带，并指定专人清点疏散人数，发现缺少人员时，立即报告现场指挥人员。"
        },
        {
          "order": 11,
          "nodeKey": "s7",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "转移危险物品并派人看管",
          "detailDescription": "将可能引起扩大燃烧的危险物品转移至安全区域，派专人看管。"
        },
        {
          "order": 12,
          "nodeKey": "s7-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "不能移动时采取防护",
          "detailDescription": "不能移动时，采取防护措施，避免造成更大伤亡和损失。"
        },
        {
          "order": 13,
          "nodeKey": "s8",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "火势严重时拨打119",
          "detailDescription": "火势严重时，现场指挥人员立即拨打119。"
        },
        {
          "order": 14,
          "nodeKey": "s8-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "派人迎候消防车",
          "detailDescription": "报警后立即派人迎候消防车，并带领消防车赶到火场。"
        },
        {
          "order": 15,
          "nodeKey": "s8-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向消防人员说明火场情况",
          "detailDescription": "向消防人员说明起火物、被困人员、电气线路、电器设施、易燃易爆物品等情况。"
        },
        {
          "order": 16,
          "nodeKey": "s9",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "划定警戒并维护秩序",
          "detailDescription": "火灾现场及时划定警戒范围，维护秩序。"
        },
        {
          "order": 17,
          "nodeKey": "s9-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "监护重点部位防止趁火打劫",
          "detailDescription": "加强对重点部位、重要设备和重要物资的监护，防止有人趁火打劫。"
        },
        {
          "order": 18,
          "nodeKey": "s9-2",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "保护现场并视情请求公安",
          "detailDescription": "抢险时保护事发现场。必要时请求地方公安机关支持。"
        },
        {
          "order": 19,
          "nodeKey": "s10",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "充分做好抢险人员的人身安全防护",
          "detailDescription": "4.2.7 充分做好抢险人员的人身安全防护，防止造成人员中毒或窒息等次生伤害。火灾事故抢险后，要保护现场，设专人巡视，以防死灰复燃。"
        },
        {
          "order": 20,
          "nodeKey": "s11",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "高层建筑发生火灾",
          "detailDescription": "4.2.8 高层建筑发生火灾，人员疏散时应使用室内楼梯或室外消防梯，切忌使用室内电梯或外用电梯逃生。人员逃生时应保持镇定，根据火势和安全指示标志判断正确方向和方式，不得盲目行动。注意防范有毒有害气体，应用水浇湿毛巾或衣服捂住口鼻，采用弯腰低姿行走，必要时匍匐前进。"
        },
        {
          "order": 21,
          "nodeKey": "s12",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "寻找避难或自救出路",
          "detailDescription": "下行楼梯被阻断时，利用绳索、落水管、浸湿的床单、窗帘、布匹自救，或尽量靠近当街窗户、阳台、楼顶等容易被人看见的地方避难。"
        },
        {
          "order": 22,
          "nodeKey": "s12-1",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "向救援人员发出求救信号",
          "detailDescription": "向救援人员发出求救信号。"
        },
        {
          "order": 23,
          "nodeKey": "s13",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "烧伤急救措施",
          "detailDescription": "4.2.10 烧伤急救措施"
        },
        {
          "order": 1,
          "nodeKey": "s14",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s13",
          "stepDescription": "烧伤急救应先灭火",
          "detailDescription": "（1）烧伤急救应先灭火，使伤员尽快脱离热源，尽量缩短烧伤时间。立即冷却烧伤部位 用冷水冲洗烧伤部位10～30分钟或冷水浸泡直到无痛的感觉为止。"
        },
        {
          "order": 2,
          "nodeKey": "s15",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s13",
          "stepDescription": "冷却后",
          "detailDescription": "（2）冷却后，必须仔细检查身体情况，保持伤口清洁。伤员的衣服鞋袜用剪刀剪开后脱去，伤口全部用清洁布片覆盖，防止污染。\n四肢烧伤时，先用清洁冷水冲洗，然后用清洁布片、消毒纱布覆盖。"
        },
        {
          "order": 3,
          "nodeKey": "s16",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s13",
          "stepDescription": "妥善保护创伤面",
          "detailDescription": "（3）妥善保护创伤面，不可挑破伤处的水泡，不可在伤处乱涂药水或药膏等，对伤员应及时送往医院。"
        },
        {
          "order": 4,
          "nodeKey": "s17",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s13",
          "stepDescription": "搬运时",
          "detailDescription": "（4）搬运时，伤员应取仰卧位，搬运动作应轻缓，行走要稳，并随时观察伤员情况，对途中发生呼吸、心跳停止者，应就地抢救。"
        },
        {
          "order": 24,
          "nodeKey": "s18",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": null,
          "stepDescription": "电气火灾发生后",
          "detailDescription": "4.2.11电气火灾发生后，为保证人身安全，防止人身触电，应立即切断电源。夜间发生电气火灾，切断电源要解决临时照明， 以利扑救。带电灭火要防止扑救人员发生触电事故。带电灭火应注意以下问题："
        },
        {
          "order": 1,
          "nodeKey": "s19",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s18",
          "stepDescription": "应使用允许带电灭火的灭火器",
          "detailDescription": "（1）应使用允许带电灭火的灭火器；"
        },
        {
          "order": 2,
          "nodeKey": "s20",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s18",
          "stepDescription": "扑救人员所使用的消防器材与带电部位应保持足够的安全",
          "detailDescription": "（2）扑救人员所使用的消防器材与带电部位应保持足够的安全距离， 10KV电源不小于0.4米，35KV电源不小于0.5米；"
        },
        {
          "order": 3,
          "nodeKey": "s21",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s18",
          "stepDescription": "对架空线路等高空设施灭火时",
          "detailDescription": "（3）对架空线路等高空设施灭火时，人体与带电体之间的仰角不应大于45度，并站在线路外侧，以防导线断落造成触电；"
        },
        {
          "order": 4,
          "nodeKey": "s22",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s18",
          "stepDescription": "高压电气设备及线路发生接地短路时",
          "detailDescription": "（4）高压电气设备及线路发生接地短路时，在室内扑救人员不得进入距离故障点4米以内，在室外扑救人员不得进入距离故障点8米以内范围。凡是进入上述范围内的扑救人员，必须穿绝缘靴。接触电气设备外壳及架构时，应戴绝缘手套；"
        },
        {
          "order": 5,
          "nodeKey": "s23",
          "stageKey": "response",
          "paramSlots": [],
          "parentNodeKey": "s18",
          "stepDescription": "可用水进行带电灭火",
          "detailDescription": "（5）可用水进行带电灭火。但因水能导电，必须采取适当安全措施后才能进行。灭火人员必须穿戴绝缘手套和绝缘靴，水枪喷嘴必须安装接地线，方可使用喷雾水枪灭火。人应避免与水流接触， 防止地面水渍导电引起触电事故。"
        }
      ],
      "methodKey": "response"
    }
  ],
  "version": 1,
  "readingTabs": [
    {
      "key": "measures",
      "text": "4、处置措施\n4.2 具体要求\n4.2.1 当发现火情后，发现人应第一时间大声呼喊，同时报告现场负责人，及时拨打报警电话。\n4.2.2 局部轻微着火，不危及人员安全时，可以马上扑灭的，应立即使用合适的灭火器灭火。当局部着火，可以扑救但有蔓延扩大趋势的，在不危及人员安全情况下，应一方面立即通知周围人员参与灭火，一方面向现场负责人报告。火灾类别与适用的灭火器材对应表参见下表：\n火灾 易燃物 适用的灭火器材/注意事项类别 类别 灭火步骤对于忌水物质，如布、纸等应尽量A类固 减少水浸所造成的损失对珍贵图棉、麻、纸 水、泡沫、干粉、二氧体物质 书，档案资料等应使用二氧化碳、张、木材 化碳火灾 卤代烷以及干粉灭火剂进行灭火。\n与火源点保持足够的距离一般用泡沫、干粉等灭火器二氧化碳适宜小范围汽油、煤B类液 油类火灾油、柴油、体和可 泡沫扑救容器内的易 禁止使用高压直流水枪直接灭火，原油、油溶的固 燃可燃液体火灾 可以使用喷雾水枪扑救。扑救时要漆、甲醇、体物质 酒精等醇类火灾要使 保持足够距离乙醇、沥火灾 用抗溶性泡沫青、石蜡沙土适宜扑救沥青和地面流散的易燃可燃液体火灾先将气体输送阀门或管道关死，截断气源，进行冷却灭火，可使用直流水枪或喷雾水枪、煤气、天然 二氧化碳或干粉灭火C类气 气、甲烷、 器戴好防护用具，防止烫伤、中毒等体火灾 乙炔、氢气 对一时无法堵漏、封闭火灾 气源的燃烧，不宜立即将火扑灭，可以一边用水冷却保护建筑物和设备，一边让气体自行燃尽钾、钠、镁、一般用沙土掩埋或使 忌用水、泡沫或含水性物质，也不D类金 钛、锆、锂、用7150金属火灾灭火 能用卤代烷、二氧化碳及常用干粉属火灾 铝镁合金剂 灭火器等火灾\n4.2.3 火势蔓延扩大，不可能马上扑灭的，现场负责人应立即组织人员疏散至安全地带，并指定专人清点疏散人数，发现缺少人员时，立即报告现场指挥人员。\n4.2.4 将可能引起扩大燃烧的危险物品转移至安全区域，派专人看管。不能移动时，应采取防护措施，避免造成更大伤亡和损失。\n4.2.5 如火势严重，现场指挥人员应立即拨打119报警。\n报警后立即派人迎候消防车，并带领消防车迅速赶到火场。现场指挥人员应及时、准确地向消防人员说明起火物、被困人员、电气线路、电器设施、易燃易爆物品等情况。\n4.2.6 火灾现场应及时划定警戒范围，维护秩序，加强对重点部位、重要设备和重要物资的监护，防止有人“趁火打劫”。事故抢险时应保护好事发现场，必要时可请求地方公安机关给予支持。\n4.2.7 充分做好抢险人员的人身安全防护，防止造成人员中毒或窒息等次生伤害。火灾事故抢险后，要保护现场，设专人巡视，以防死灰复燃。\n4.2.8 高层建筑发生火灾，人员疏散时应使用室内楼梯或室外消防梯，切忌使用室内电梯或外用电梯逃生。人员逃生时应保持镇定，根据火势和安全指示标志判断正确方向和方式，不得盲目行动。注意防范有毒有害气体，应用水浇湿毛巾或衣服捂住口鼻，采用弯腰低姿行走，必要时匍匐前进。\n4.2.9 高层建筑发生火灾，要利用一切可以利用的条件进行逃生或争取逃生时间。在下行楼梯受到阻断，可利用绳索、落水管、浸湿的床单、窗帘、布匹等进行自救。要尽量靠近当街窗户或阳台、楼顶等容易被人看见的地方寻找避难所，同时向救援人员发出求救信号。\n4.2.10 烧伤急救措施\n（1）烧伤急救应先灭火，使伤员尽快脱离热源，尽量缩短烧伤时间。立即冷却烧伤部位 用冷水冲洗烧伤部位10～30分钟或冷水浸泡直到无痛的感觉为止。\n（2）冷却后，必须仔细检查身体情况，保持伤口清洁。伤员的衣服鞋袜用剪刀剪开后脱去，伤口全部用清洁布片覆盖，防止污染。\n四肢烧伤时，先用清洁冷水冲洗，然后用清洁布片、消毒纱布覆盖。\n（3）妥善保护创伤面，不可挑破伤处的水泡，不可在伤处乱涂药水或药膏等，对伤员应及时送往医院。\n（4）搬运时，伤员应取仰卧位，搬运动作应轻缓，行走要稳，并随时观察伤员情况，对途中发生呼吸、心跳停止者，应就地抢救。\n4.2.11电气火灾发生后，为保证人身安全，防止人身触电，应立即切断电源。夜间发生电气火灾，切断电源要解决临时照明， 以利扑救。带电灭火要防止扑救人员发生触电事故。带电灭火应注意以下问题：\n（1）应使用允许带电灭火的灭火器；\n（2）扑救人员所使用的消防器材与带电部位应保持足够的安全距离， 10KV电源不小于0.4米，35KV电源不小于0.5米；\n（3）对架空线路等高空设施灭火时，人体与带电体之间的仰角不应大于45度，并站在线路外侧，以防导线断落造成触电；\n（4）高压电气设备及线路发生接地短路时，在室内扑救人员不得进入距离故障点4米以内，在室外扑救人员不得进入距离故障点8米以内范围。凡是进入上述范围内的扑救人员，必须穿绝缘靴。接触电气设备外壳及架构时，应戴绝缘手套；\n（5）可用水进行带电灭火。但因水能导电，必须采取适当安全措施后才能进行。灭火人员必须穿戴绝缘手套和绝缘靴，水枪喷嘴必须安装接地线，方可使用喷雾水枪灭火。人应避免与水流接触， 防止地面水渍导电引起触电事故。",
      "label": "处置措施"
    },
    {
      "key": "notes",
      "text": "6.2应急物资注意事项\n（1）专人管理，明确责任，保证物资管理有序，便于随时取用。\n（2）定期更换超过消毒期的敷料和过期药品，每次急救后根据消耗情况，及时补充物资。\n（3）定期检查：确保物资质量和有效期。\n（4）对相关人员进行应急物资使用的培训，确保应急救援时能熟练使用。\n\n4.1 基本原则\n（1）先救人、后灭火。火场上如果有人受到火势威胁，首要任务是把被火围困人员解救出来。\n（2）先控制、后消灭。对于不可能立即扑灭的火灾，要首先控制火势的继续蔓延，具备了扑灭火灾的条件时，展开攻势，扑灭火灾。\n（3）先重点，后一般。全面了解并认真分析整个火场的情况分清重点。\n\n4.3 其他要求针对现场应急救援可能出现的技术难点，应急指挥机构应及时研究对策。\n实施应急救援前，应排除救援现场的隐患，确保救援环境安全。\n自身救援力量不足时应及时请求社会力量协助。\n\n附表一：",
      "label": "其他注意事项"
    }
  ],
  "resourceIds": [
    333,
    334,
    335,
    336,
    337,
    338,
    339,
    340,
    341,
    342,
    343,
    344,
    345,
    346
  ]
}$json$::jsonb,
    updater = 'V283',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '火灾事故应急预案';
