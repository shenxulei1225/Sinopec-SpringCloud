-- equipment models from weak-current inventory → standard category library
-- Source MD: F:/XProject/参考资料/弱电集成设备清单-武汉理工光科.md
-- Regenerate: python export_equipment_models_from_inventory_md.py
-- Models: 97; unmapped device names: 0

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-b1442741c47f926f', '罗克韦尔（AB） AB 2080-LC70-24QBB 2085-IQ16 2085-OW16 2085-IF8', 'equipment',
  '品类：ACU柜；规格：AB 2080-LC70-24QBB 2085-IQ16 2085-OW16 2085-IF8；品牌：罗克韦尔（AB）；分类：ACU控制柜；出处：一、监控中心设备清单', 1, 1, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6c57190b28a46057', '西米 XM-QT-CO2', 'equipment',
  '品类：CO2传感器；规格：西米 XM-QT-CO2；品牌：西米；分类：二氧化碳传感器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 2, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-d264f01e700c7d20', 'DELL PowerEdge R540', 'equipment',
  '品类：GIS服务器；规格：DELL PowerEdge R540；品牌：DELL；分类：GIS服务器；出处：一、监控中心设备清单', 1, 3, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-b070a5657ad133ab', '畅电 CDU-HOS', 'equipment',
  '品类：IP电话；规格：畅电 CDU-HOS；品牌：畅电；分类：IP电话终端；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 4, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-16de91ca89897431', '畅电 CDU-HOS-FIB', 'equipment',
  '品类：IP电话控制主机；规格：畅电 CDU-HOS-FIB；品牌：畅电；分类：IP电话控制主机；出处：一、监控中心设备清单', 1, 5, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1fa3e2fd0c5fb3dd', '朗视 S1000', 'equipment',
  '品类：IP电话机；规格：朗视 S1000；品牌：朗视；分类：IP电话终端；出处：一、监控中心设备清单', 1, 6, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-80350db0b05b9a4d', '畅电 CD-V5.0', 'equipment',
  '品类：IP电话系统客户端管理软件；规格：畅电 CD-V5.0；品牌：畅电；分类：IP电话系统软件；出处：一、监控中心设备清单', 1, 7, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-bbdd7bd0d7cfbc3a', '畅电 CD-V4.0', 'equipment',
  '品类：IP电话系统服务器管理软件；规格：畅电 CD-V4.0；品牌：畅电；分类：IP电话系统软件；出处：一、监控中心设备清单', 1, 8, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-cb1269157176295b', '畅电 CDU-HOS-SIP 500S', 'equipment',
  '品类：IP电话软交换服务器；规格：畅电 CDU-HOS-SIP 500S；品牌：畅电；分类：IP语音交换服务器；出处：一、监控中心设备清单', 1, 9, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-9597b8ece068f6d9', '蓝宝 AS-9108DU,8口KVM切换器', 'equipment',
  '品类：KVM多电脑切换器；规格：AS-9108DU,8口KVM切换器；品牌：蓝宝；分类：KVM切换器；出处：一、监控中心设备清单', 1, 10, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-195cbd1d1d421043', '宇视 UNV MW7218-A-FL-U', 'equipment',
  '品类：LED全彩显示屏；规格：UNV MW7218-A-FL-U；品牌：宇视；分类：LED显示屏；出处：一、监控中心设备清单', 1, 11, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-804736552fb88c8d', '山西高科 室外P4全彩（模组规格320mm*160mm）', 'equipment',
  '品类：LED屏；规格：室外P4全彩（模组规格320mm*160mm）；品牌：山西高科；分类：LED显示屏；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单', 1, 12, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6ccef78e4f0c5091', '西米 XM-QT-O2', 'equipment',
  '品类：O2传感器；规格：西米 XM-QT-O2；品牌：西米；分类：氧气传感器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 13, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-e550ca3e1829c5fa', '软派 SOFTPX-UPS', 'equipment',
  '品类：UPS智能设备监控器；规格：软派 SOFTPX-UPS；品牌：软派；分类：UPS监控模块；出处：一、监控中心设备清单', 1, 14, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-32bb4b9b4d571db1', '科士达 YMK3330-RT', 'equipment',
  '品类：UPS电源柜；规格：科士达 YMK3330-RT；品牌：科士达；分类：UPS电源柜；出处：一、监控中心设备清单', 1, 15, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-70c02f4b3027abe3', '软派 雅达 ET903', 'equipment',
  '品类：三相电量仪；规格：雅达 ET903；品牌：软派；分类：电量仪；出处：一、监控中心设备清单', 1, 16, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-35bc92a7327667f0', '烽火 FSG10000-A-2300', 'equipment',
  '品类：上网行为管理主机；规格：烽火 FSG10000-A-2300；品牌：烽火；分类：上网行为管理；出处：一、监控中心设备清单', 1, 17, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-d0e9abcf3626c59f', '锐河 RF50-5', 'equipment',
  '品类：中心射频接口缆线；规格：锐河 RF50-5；品牌：—；分类：对讲天线；出处：一、监控中心设备清单', 1, 18, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-aa98fa746b322feb', '二氧化碳传感器 · 西米 XM-QT-CO2', 'equipment',
  '品类：二氧化碳传感器；规格：西米 XM-QT-CO2；品牌：西米；分类：二氧化碳传感器；出处：一、监控中心设备清单', 1, 19, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-d918458c7a03f584', '软派 国产', 'equipment',
  '品类：交流电互感器；规格：国产；品牌：软派；分类：电流互感器；出处：一、监控中心设备清单', 1, 20, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-93a4f69c7cc31077', '三旺 IAP2600-4A25-PD', 'equipment',
  '品类：人员定位主机；规格：三旺 IAP2600-4A25-PD；品牌：三旺；分类：定位读写主机；出处：一、监控中心设备清单', 1, 21, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-53cdf81bf0f216c6', '人员定位主机（全向读写器含增益天线） · 三旺 IAP2600-4A25-PD', 'equipment',
  '品类：人员定位主机（全向读写器含增益天线）；规格：三旺 IAP2600-4A25-PD；品牌：三旺；分类：定位读写主机；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 22, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-aefd22c278a2fba0', '三旺 SW-MA6-WF6', 'equipment',
  '品类：人员定位系统客户端软件；规格：三旺 SW-MA6-WF6；品牌：三旺；分类：定位系统软件；出处：一、监控中心设备清单', 1, 23, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-017cdd4b1ecacfb3', 'Dell I7-9700 16G 128GSSD+2T DVDRW W10 SE2218HV/LED 22"含win10操作系统', 'equipment',
  '品类：人员定位系统工作站；规格：Dell I7-9700 16G 128GSSD+2T DVDRW W10 SE2218HV/LED 22"含win10操作系统；品牌：DELL；分类：定位系统工作站；出处：一、监控中心设备清单', 1, 24, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-871afc1fefcafea9', '三旺 IAC8500-1GC4GT', 'equipment',
  '品类：人员定位系统服务器；规格：三旺 IAC8500-1GC4GT；品牌：三旺；分类：定位系统服务器；出处：一、监控中心设备清单', 1, 25, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-b40633b2751387be', '三旺 SW-MA2-WF5', 'equipment',
  '品类：人员定位系统服务器管理软件；规格：三旺 SW-MA2-WF5；品牌：三旺；分类：定位系统软件；出处：一、监控中心设备清单', 1, 26, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-9f09370988167c1e', '烽火 S7800-03-CBA-AC', 'equipment',
  '品类：企业级核心交换机；规格：烽火 S7800-03-CBA-AC；品牌：烽火；分类：核心交换机；出处：一、监控中心设备清单', 1, 27, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-dba95e5328a788f2', '希捷 HD Seagate ST6000VX0003', 'equipment',
  '品类：企业级硬盘；规格：HD Seagate ST6000VX0003；品牌：希捷；分类：监控存储硬盘；出处：一、监控中心设备清单', 1, 28, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-bc68232ca7da9c99', 'DELL', 'equipment',
  '品类：便携式电脑；规格：DELL；品牌：DELL；分类：便携式计算机；出处：一、监控中心设备清单', 1, 29, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-45866bf269164142', '锐河 RHET-BL-400', 'equipment',
  '品类：信号剥离器；规格：锐河 RHET-BL-400；品牌：锐河；分类：对讲功分器；出处：一、监控中心设备清单', 1, 30, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-2896d6f3e19d0d25', '慧谷 千兆单模（10KM）', 'equipment',
  '品类：光模块；规格：千兆单模（10KM）；品牌：慧谷；分类：光模块；出处：一、监控中心设备清单', 1, 31, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-9393cff74f6ec775', '慧谷 HG-614GS-EM-25 千兆单模1光4电接口', 'equipment',
  '品类：光纤收发器；规格：HG-614GS-EM-25 千兆单模1光4电接口；品牌：慧谷；分类：光纤收发器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 32, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-65a25f8ecf1df71f', '摩托罗拉 锐河 OMTApp', 'equipment',
  '品类：光纤直放站网管软件；规格：锐河 OMTApp；品牌：摩托罗拉；分类：对讲网管软件；出处：一、监控中心设备清单', 1, 33, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6b0399b9bd9513cf', '锐河 RHET-PA-400J-Z', 'equipment',
  '品类：光纤直放站近端机；规格：锐河 RHET-PA-400J-Z；品牌：—；分类：对讲远端设备；出处：一、监控中心设备清单', 1, 34, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-ce93046f5041439c', 'DELL optiplex-3070-spec-sheet', 'equipment',
  '品类：入侵报警工作站；规格：optiplex-3070-spec-sheet；品牌：DELL；分类：入侵报警工作站；出处：一、监控中心设备清单', 1, 35, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-7ea0c802548b19d4', '精华隆 EP8100', 'equipment',
  '品类：入侵报警系统客户端软件；规格：精华隆 EP8100；品牌：精华隆；分类：入侵报警软件；出处：一、监控中心设备清单', 1, 36, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-755078aa7dfb541b', '分区ACU柜 · 罗克韦尔（AB） AB 2080-LC70-24QBB 2085-IQ16 2085-OW16 2085-IF8', 'equipment',
  '品类：分区ACU柜；规格：AB 2080-LC70-24QBB 2085-IQ16 2085-OW16 2085-IF8；品牌：罗克韦尔（AB）；分类：ACU控制柜；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 37, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-94b81b84ca2b7b9b', '锐河 RHET-PA-GF3', 'equipment',
  '品类：功分器；规格：锐河 RHET-PA-GF3；品牌：锐河；分类：对讲功分器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 38, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-637a9cfb3b25c85c', '宇视 包含：门禁控制器ER-SR41、开门按钮EA721、单门磁力锁EA-SM11-280/读卡器EC-S11H-M等', 'equipment',
  '品类：单门门禁控制系统；规格：包含：门禁控制器ER-SR41、开门按钮EA721、单门磁力锁EA-SM11-280/读卡器EC-S11H-M等；品牌：宇视；分类：单门门禁系统；出处：一、监控中心设备清单', 1, 39, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1afd687e6bc21755', '锐河 RHET-SG-400', 'equipment',
  '品类：双工器；规格：锐河 RHET-SG-400；品牌：锐河；分类：对讲双工器；出处：一、监控中心设备清单', 1, 40, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-41c311cc67bed2ba', '宇视 包含：门禁控制器ER-SR41、开门按钮EA721、双门磁力锁EA-SM21-280/读卡器EC-S11H-M等', 'equipment',
  '品类：双门门禁控制系统；规格：包含：门禁控制器ER-SR41、开门按钮EA721、双门磁力锁EA-SM21-280/读卡器EC-S11H-M等；品牌：宇视；分类：双门门禁系统；出处：一、监控中心设备清单', 1, 41, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6959c7a26d9608fb', '发送卡 · 宇视 UNV MW7218-A-FL-U', 'equipment',
  '品类：发送卡；规格：UNV MW7218-A-FL-U；品牌：宇视；分类：视频发送卡；出处：一、监控中心设备清单', 1, 42, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-a290cbb76ad097b6', '声光报警器 · 精华隆 EP8100', 'equipment',
  '品类：声光报警器；规格：精华隆 EP8100；品牌：精华隆；分类：声光报警器；出处：一、监控中心设备清单', 1, 43, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-dabc2c853e32865f', 'UNV MW7218-A-FL-U', 'equipment',
  '品类：大屏支架；规格：UNV MW7218-A-FL-U；品牌：定制；分类：大屏安装支架；出处：一、监控中心设备清单', 1, 44, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-04fca772459212a2', '烽火 FSG10000-S-2300', 'equipment',
  '品类：安全路由器；规格：烽火 FSG10000-S-2300；品牌：烽火；分类：路由器；出处：一、监控中心设备清单', 1, 45, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1530b29ad576c094', '锐河 RHET-PA-N2', 'equipment',
  '品类：定向天线；规格：锐河 RHET-PA-N2；品牌：锐河；分类：对讲天线；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 46, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-100542002dec8b72', '室内定向天线 · 锐河 RHET-PA-N2', 'equipment',
  '品类：室内定向天线；规格：锐河 RHET-PA-N2；品牌：—；分类：对讲天线；出处：一、监控中心设备清单', 1, 47, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-377d7ee3350e9b66', '工作站 · DELL optiplex-3070-spec-sheet', 'equipment',
  '品类：工作站；规格：optiplex-3070-spec-sheet；品牌：DELL；分类：工作站；出处：一、监控中心设备清单', 1, 48, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-58eec4087a92e5eb', '应用服务器 · DELL PowerEdge R540', 'equipment',
  '品类：应用服务器；规格：DELL PowerEdge R540；品牌：DELL；分类：应用服务器；出处：一、监控中心设备清单', 1, 49, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-9b56903df958dfdc', '惠普 HP7720', 'equipment',
  '品类：彩色打印机；规格：HP7720；品牌：惠普；分类：彩色打印机；出处：一、监控中心设备清单', 1, 50, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6dc4abc3b89d2bac', '报警主机 · 精华隆 EP8100', 'equipment',
  '品类：报警主机；规格：精华隆 EP8100；品牌：精华隆；分类：入侵报警主机；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单', 1, 51, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-b80d7b8c5adc4eaa', '报警器控制盒 · 精华隆 EP8100', 'equipment',
  '品类：报警器控制盒；规格：精华隆 EP8100；品牌：精华隆；分类：入侵报警主机；出处：一、监控中心设备清单', 1, 52, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-87ff40a56ac3f461', '宇视 UNV DMC8000', 'equipment',
  '品类：拼接控制器；规格：UNV DMC8000；品牌：宇视；分类：拼接控制器；出处：一、监控中心设备清单', 1, 53, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-0445b371f7401e3b', '烽火 S4820-28T-GF-AC', 'equipment',
  '品类：接入交换机；规格：烽火 S4820-28T-GF-AC；品牌：烽火；分类：接入交换机；出处：一、监控中心设备清单', 1, 54, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-d7bf0fd8e0801a9d', '支架 · 三旺 IAP2600-4A25-PD', 'equipment',
  '品类：支架；规格：三旺 IAP2600-4A25-PD；品牌：三旺；分类：定位配套附件；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 55, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-fc1a506efbe6b9de', '畅电 TE200', 'equipment',
  '品类：数字中继网关；规格：畅电 TE200；品牌：畅电；分类：语音网关；出处：一、监控中心设备清单', 1, 56, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-518bc7240d12e8ae', '摩托罗拉 SLR 5300', 'equipment',
  '品类：数字无线中继调度主机；规格：摩托罗拉 SLR 5300；品牌：—；分类：对讲中继主机；出处：一、监控中心设备清单', 1, 57, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-f2dda8570a79ce1a', '数据库服务器 · DELL PowerEdge R540', 'equipment',
  '品类：数据库服务器；规格：DELL PowerEdge R540；品牌：DELL；分类：数据库服务器；出处：一、监控中心设备清单', 1, 58, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-acf9ca5f8079e490', '软派 SOFTPX-FAN', 'equipment',
  '品类：新风智能设备监控器；规格：软派 SOFTPX-FAN；品牌：软派；分类：新风监控模块；出处：一、监控中心设备清单', 1, 59, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-2f7f90648ff9a2ee', '摩托罗拉 XIR C1200', 'equipment',
  '品类：无线对讲手持机；规格：摩托罗拉 XIR C1200；品牌：摩托罗拉；分类：对讲手持机；出处：一、监控中心设备清单', 1, 60, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-e8c7a010de171113', '锐河 RHET-PA-403Y-Z', 'equipment',
  '品类：无线远端机（光纤直放远端机）；规格：锐河 RHET-PA-403Y-Z；品牌：锐河；分类：对讲远端设备；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 61, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-d1349f3314122c0b', '图腾 K36042', 'equipment',
  '品类：服务器机柜；规格：图腾 K36042；品牌：图腾；分类：服务器机柜；出处：一、监控中心设备清单', 1, 62, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-97c5d88b468cdb63', '图腾 K36242', 'equipment',
  '品类：服务器机柜；规格：图腾 K36242；品牌：图腾；分类：服务器机柜；出处：一、监控中心设备清单', 1, 63, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-844a4dcc1c3de7c9', '软派 航嘉 SPWEB', 'equipment',
  '品类：机房环境监测系统服务器；规格：航嘉 SPWEB；品牌：软派；分类：机房监控服务器；出处：一、监控中心设备清单', 1, 64, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-91013dd60872d3fc', '蓝宝 AS-9100ULS,1U标准机架式19寸一体化显示器、键盘', 'equipment',
  '品类：机架式一体化键盘显示器；规格：AS-9100ULS,1U标准机架式19寸一体化显示器、键盘；品牌：蓝宝；分类：机架式控制台；出处：一、监控中心设备清单', 1, 65, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-bad6ce090800b5fe', '氧气传感器 · 西米 XM-QT-O2', 'equipment',
  '品类：氧气传感器；规格：西米 XM-QT-O2；品牌：西米；分类：氧气传感器；出处：一、监控中心设备清单', 1, 66, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-2ee0687b44b35798', '宇视 UNV VS-MS8800', 'equipment',
  '品类：流媒体服务器；规格：UNV VS-MS8800；品牌：宇视；分类：流媒体服务器；出处：一、监控中心设备清单', 1, 67, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-e6900cea94d17e7c', '西米 XM-310-TH', 'equipment',
  '品类：温湿度传感器；规格：西米 XM-310-TH；品牌：西米；分类：温湿度传感器；出处：一、监控中心设备清单；七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 68, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6f06efcfd1cd2635', '软派 SOFTPX-T/H', 'equipment',
  '品类：温湿度传感器；规格：软派 SOFTPX-T/H；品牌：软派；分类：温湿度传感器；出处：一、监控中心设备清单', 1, 69, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1f2c95093caf10e9', '软派 SOFTPX-LEAK', 'equipment',
  '品类：漏水监测器；规格：软派 SOFTPX-LEAK；品牌：软派；分类：漏水监测器；出处：一、监控中心设备清单', 1, 70, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-850626138d6f6e1f', '软派 SOFTPX-V1.2', 'equipment',
  '品类：环境监测服务器管理软件；规格：软派 SOFTPX-V1.2；品牌：软派；分类：动环监控软件；出处：一、监控中心设备清单', 1, 71, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-75aef1bb4f2200b6', '东土 KIEN7009-2S4T', 'equipment',
  '品类：环网交换机；规格：东土 KIEN7009-2S4T；品牌：东土；分类：环网交换机；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 72, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-10c9c5766f9b9dba', '环网交换机（安防交换机） · 东土 KIEN7009-2S4T', 'equipment',
  '品类：环网交换机（安防交换机）；规格：东土 KIEN7009-2S4T；品牌：东土；分类：环网交换机；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单', 1, 73, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-99202b60c398d30e', '电源适配器 · 三旺 IAP2600-4A25-PD', 'equipment',
  '品类：电源适配器；规格：三旺 IAP2600-4A25-PD；品牌：三旺；分类：定位配套附件；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 74, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-c2108b63b671fd8e', '监控控制柜（19"机柜）（定制）', 'equipment',
  '品类：监控控制柜（19"机柜）；规格：定制；品牌：国产；分类：监控控制柜；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 75, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-3afd58393cf95b5d', '软派 SOFTPX-SMS', 'equipment',
  '品类：短信系统；规格：软派 SOFTPX-SMS；品牌：软派；分类：动环短信模块；出处：一、监控中心设备清单', 1, 76, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-28e52efbecda00f1', '移动终端 · 三旺 IAP2600-4A25-PD', 'equipment',
  '品类：移动终端；规格：三旺 IAP2600-4A25-PD；品牌：三旺；分类：定位移动终端；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 77, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-4ae41b3638a16f5d', '软派 SOFTPX-KT', 'equipment',
  '品类：空调智能设备监控器；规格：软派 SOFTPX-KT；品牌：软派；分类：空调监控模块；出处：一、监控中心设备清单', 1, 78, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6f9364efaed005fa', '管理终端 · 软派 国产', 'equipment',
  '品类：管理终端；规格：国产；品牌：软派；分类：动环管理终端；出处：一、监控中心设备清单', 1, 79, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-e19263288192561d', '精密配电柜（定制）', 'equipment',
  '品类：精密配电柜；规格：定制；品牌：国标定制；分类：精密配电柜；出处：一、监控中心设备清单', 1, 80, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-75d60b77b50eb489', '红外入侵报警主机 · 精华隆 EP8100', 'equipment',
  '品类：红外入侵报警主机；规格：精华隆 EP8100；品牌：精华隆；分类：入侵报警主机；出处：一、监控中心设备清单', 1, 81, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-829d372b286ad175', '宇视 UNV IPC-S362-IR', 'equipment',
  '品类：红外半球摄像机；规格：UNV IPC-S362-IR；品牌：宇视；分类：半球摄像机；出处：一、监控中心设备清单', 1, 82, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-6b2c34aedd0144d7', '精华隆 ED690', 'equipment',
  '品类：红外双鉴探测器；规格：精华隆 ED690；品牌：精华隆；分类：红外探测器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 83, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1d60b35024bbdcec', '宇视 UNV IPC-B2A2-IR', 'equipment',
  '品类：网络摄像机；规格：UNV IPC-B2A2-IR；品牌：宇视；分类：网络摄像机；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单；四、神墩五路设备清单', 1, 84, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-a5bf96067dbed1ba', '图腾 G36642', 'equipment',
  '品类：网络机柜；规格：图腾 G36642；品牌：图腾；分类：网络机柜；出处：一、监控中心设备清单', 1, 85, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-797f0230493e2614', '锐河 RHET-PA-OH9', 'equipment',
  '品类：耦合器；规格：锐河 RHET-PA-OH9；品牌：—；分类：对讲耦合器；出处：一、监控中心设备清单', 1, 86, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-dcbe6b4940b488f3', '理士 12V200AH,每组32节,共2组,包含电池柜及电池开关箱', 'equipment',
  '品类：蓄电池柜；规格：12V200AH,每组32节,共2组,包含电池柜及电池开关箱；品牌：理士；分类：蓄电池组；出处：一、监控中心设备清单', 1, 87, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-7acb830b705688c6', '宇视 UNV VMS-B230', 'equipment',
  '品类：视频存储服务器；规格：UNV VMS-B230；品牌：宇视；分类：视频存储服务器；出处：一、监控中心设备清单', 1, 88, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-e72bf355dd09e036', '视频监控工作站 · DELL optiplex-3070-spec-sheet', 'equipment',
  '品类：视频监控工作站；规格：optiplex-3070-spec-sheet；品牌：DELL；分类：视频监控工作站；出处：一、监控中心设备清单', 1, 89, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-93cd38235ea76a82', '宇视 UNV VS_VM5800', 'equipment',
  '品类：视频管理服务器；规格：UNV VS_VM5800；品牌：宇视；分类：视频管理服务器；出处：一、监控中心设备清单', 1, 90, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-1c4ccb99d132c84b', '语音通信工作站 · DELL optiplex-3070-spec-sheet', 'equipment',
  '品类：语音通信工作站；规格：optiplex-3070-spec-sheet；品牌：DELL；分类：语音通信工作站；出处：一、监控中心设备清单', 1, 91, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-7e617f072030935b', '宇视 网络型,配套双门磁力锁EA-SM21-280、出门按钮EA721、读卡器EC-S11H-M、磁力锁支架EA-SB11-280U', 'equipment',
  '品类：门禁控制器；规格：网络型,配套双门磁力锁EA-SM21-280、出门按钮EA721、读卡器EC-S11H-M、磁力锁支架EA-SB11-280U；品牌：宇视；分类：门禁控制器；出处：七、光谷六路北（高新大道-神墩五路）设备清单；三、神墩三路设备清单；九、高科园路南（高新三路-虎山东街）设备清单；二、神墩一路设备清单；五、神墩五路-清湾路设备清单；八、高科园路北（神墩三路-高新三路）设备清单；六、托月路设备清单；十、高新大道设备清单；十一、南新街/虎山东街设备清单', 1, 92, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-cb364c23bb0d58e9', '宇视 optiplex-3070-spec-sheet', 'equipment',
  '品类：门禁系统服务器；规格：optiplex-3070-spec-sheet；品牌：宇视；分类：门禁管理服务器；出处：一、监控中心设备清单', 1, 93, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-0d535dee7940828b', '宇视 集成了员工管理、访客管理、权限管理以及考勤统计等业务系统,并提供一体化的操作体验,支持按通道绑定的设备下发权限；支持按时间模板配置权限；支持远程开门；支持对识别模块、闸机状态统一管理,呈现运行状态；', 'equipment',
  '品类：门禁系统服务器管理软件；规格：集成了员工管理、访客管理、权限管理以及考勤统计等业务系统,并提供一体化的操作体验,支持按通道绑定的设备下发权限；支持按时间模板配置权限；支持远程开门；支持对识别模块、闸机状态统一管理,呈现运行状态；；品牌：宇视；分类：门禁管理软件；出处：一、监控中心设备清单', 1, 94, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-c7e516234b0ecc91', '软派 航嘉 S-400', 'equipment',
  '品类：集中监控管理主机；规格：航嘉 S-400；品牌：软派；分类：动环监控主机；出处：一、监控中心设备清单', 1, 95, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-2dbf59aaf008127a', '高清视频解码器 · 宇视 UNV MW7218-A-FL-U', 'equipment',
  '品类：高清视频解码器；规格：UNV MW7218-A-FL-U；品牌：宇视；分类：视频解码器；出处：一、监控中心设备清单', 1, 96, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'eqm-inv-bdc506ede65534ff', '惠普 HP-M1136', 'equipment',
  '品类：黑色打印机；规格：HP-M1136；品牌：惠普；分类：黑白打印机；出处：一、监控中心设备清单', 1, 97, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

