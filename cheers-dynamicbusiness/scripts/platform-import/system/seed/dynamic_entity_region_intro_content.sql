-- ============================================================================
-- 运营区域介绍内容补全（联调样例）
-- 目的：为已有 Pattern C region 实体写入介绍页所需 custom_fields，避免详情空白
-- 原则：只写入公开可核对资料；无公开里程则留空；禁止编造管辖范围多边形
-- 头图与驻地 Point GeoJSON 见同目录 dynamic_entity_region_intro_cover_geo.sql
-- 来源摘要：
--   · 集团规模：pipechina.com.cn 集团简介 / 业务领域（公开页 2025 口径）
--   · 二级单位简介：公开转载的二级单位介绍（廊坊北方、徐州东部储运、西气东输、西部/西南/北京管道、华南/华中/华东/华北、LNG 接收站管理等）
--   · 山东省公司：鲁网/青岛新闻网报道（管道总里程超 8200 公里等）
-- 幂等：jsonb || 合并；可重复执行覆盖同键
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 工具：合并介绍字段（仅 FLD-BASE-region-* 键，与 model-crud-form / EntityIntro 约定一致）
CREATE OR REPLACE FUNCTION tmp_region_intro_merge(p_id bigint, p_fields jsonb)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE ent_region e
  SET
    custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || p_fields,
    updater = 'seed-intro',
    update_time = CURRENT_TIMESTAMP
  WHERE e.id = p_id
    AND e.deleted = false
    AND e.tenant_id = 1;
END;
$$;

-- ---------- 集团 ----------
SELECT tmp_region_intro_merge(100001, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '服务国家战略、服务人民需要、服务行业发展',
  'FLD-BASE-region-description',
    '国家石油天然气管网集团有限公司于2019年12月9日挂牌成立，总部北京；2020年10月1日起全面转入实质性运营。主要从事油气干线管网及储气调峰等基础设施投资建设与运营，负责干线管网互联互通、全国油气管网运行调度，并向用户公平开放基础设施。',
  'FLD-BASE-region-hq_location', '北京市',
  'FLD-BASE-region-pipeline_km_total', 112900,
  'FLD-BASE-region-pipeline_km_ng', 63800,
  'FLD-BASE-region-pipeline_km_cr', 22100,
  'FLD-BASE-region-pipeline_km_cp', 27000,
  'FLD-BASE-region-storage_count', 8,
  'FLD-BASE-region-lng_terminal_count', 10,
  'FLD-BASE-region-coverage_note', '管网覆盖全国30个省市区及香港特别行政区',
  'FLD-BASE-region-org_mode_note', '集团 → 省公司/区域公司 → 作业区（对标国家管网运维组织）',
  'FLD-BASE-region-establish_date', '2019-12-09',
  'FLD-BASE-region-centroid_lng', 116.4074,
  'FLD-BASE-region-centroid_lat', 39.9042,
  'FLD-BASE-region-remark',
    '规模数据来源：国家管网官网「集团简介」「业务领域」公开页（油气管道11.29万公里等，页面口径含截至2025年底表述）。未配置管辖边界 GeoJSON。'
));

-- ---------- 省公司 / 区域公司 ----------
SELECT tmp_region_intro_merge(100011, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '东北与华北油气能源战略通道运营主体',
  'FLD-BASE-region-description',
    '国家管网集团北方管道有限责任公司是境内成立较早的管道运营专业化公司，注册地河北廊坊。管输业务立足北方、辐射相关省份，运营管理中俄原油管道、中俄东线天然气管道等跨国能源通道，输送介质涵盖天然气、原油、成品油等。',
  'FLD-BASE-region-hq_location', '河北省廊坊市',
  'FLD-BASE-region-pipeline_km_total', 25000,
  'FLD-BASE-region-coverage_note', '所辖管道主要分布在东北三省、京津冀和鲁豫等，部分延伸至陕甘宁、湘鄂',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如黑河作业区）',
  'FLD-BASE-region-centroid_lng', 116.6838,
  'FLD-BASE-region-centroid_lat', 39.5380,
  'FLD-BASE-region-remark', '简介与约2.5万公里口径来自公开二级单位介绍转载；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100012, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '区域性原油储运与成品油管输保障',
  'FLD-BASE-region-description',
    '国家管网集团东部原油储运有限公司基地位于江苏徐州，前身可追溯至1975年华东输油管线指挥部，2020年划转国家管网集团。形成覆盖华北、华东、华中、华南主要炼化企业的区域性管道储运网络，承担油田及进口原油输送，并参与华东区域成品油管输任务。',
  'FLD-BASE-region-hq_location', '江苏省徐州市',
  'FLD-BASE-region-pipeline_km_total', 5974,
  'FLD-BASE-region-pipeline_km_cr', 5974,
  'FLD-BASE-region-coverage_note', '覆盖华北、华东、华中、华南主要炼化企业；公开口径在用输油管道约29条、站库数十座',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如洛阳、金桥作业区）',
  'FLD-BASE-region-centroid_lng', 117.2838,
  'FLD-BASE-region-centroid_lat', 34.2044,
  'FLD-BASE-region-remark', '里程约5973.54公里等公开企业信息口径；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100013, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '西气东输骨干枢纽与多气源互联互通',
  'FLD-BASE-region-description',
    '西气东输分公司隶属于国家管网集团，主要从事所辖区域天然气干（支）线管网投资建设、运营与公平开放服务。管网横亘西东、纵贯南北，形成多气源、多通道互联互通供气格局，是「全国一张网」的骨干枢纽之一。',
  'FLD-BASE-region-hq_location', '上海市',
  'FLD-BASE-region-pipeline_km_total', 16636,
  'FLD-BASE-region-pipeline_km_ng', 16636,
  'FLD-BASE-region-coverage_note', '公开介绍称途经约19个省市区及香港特别行政区；供气覆盖西北东部、中原、华东、华中、华南并向华北、西南转供',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如高陵作业区）',
  'FLD-BASE-region-centroid_lng', 121.4737,
  'FLD-BASE-region-centroid_lat', 31.2304,
  'FLD-BASE-region-remark', '里程等来自公开二级单位介绍转载；不同年份招聘公告等口径可能不一致。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100014, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '西油东送、西气东输战略通道运营',
  'FLD-BASE-region-description',
    '国家管网集团西部管道有限责任公司位于丝绸之路经济带核心区域，运营双兰线及西气东输一、二、三线西段等重点工程，是覆盖新疆、甘肃、青海的专业化地区管道企业。',
  'FLD-BASE-region-hq_location', '新疆乌鲁木齐市',
  'FLD-BASE-region-pipeline_km_total', 16000,
  'FLD-BASE-region-coverage_note', '业务覆盖新、甘、青三省区；保障西油东送、西气东输通道',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如兰州作业区）',
  'FLD-BASE-region-centroid_lng', 87.6168,
  'FLD-BASE-region-centroid_lat', 43.8256,
  'FLD-BASE-region-remark', '约1.6万公里口径来自公开二级单位介绍；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100015, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '陕京管道系统运营，保障首都供气',
  'FLD-BASE-region-description',
    '国家管网集团北京管道有限公司主要负责陕京管道输配气系统运营管理，包括陕京一线至四线、永唐秦、唐山LNG外输管线、大唐煤制气北京段等。北京市超过95%的天然气由陕京管道输送。',
  'FLD-BASE-region-hq_location', '北京市',
  'FLD-BASE-region-pipeline_km_total', 5387,
  'FLD-BASE-region-pipeline_km_ng', 5387,
  'FLD-BASE-region-coverage_note', '供气范围覆盖陕西、内蒙古、山西、河北、北京、天津',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如安平、永清作业区）',
  'FLD-BASE-region-centroid_lng', 116.4074,
  'FLD-BASE-region-centroid_lat', 39.9042,
  'FLD-BASE-region-remark', '陕京系统总里程5387公里等来自公开二级单位介绍；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100016, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '西南油气干支线管网与中缅通道运营',
  'FLD-BASE-region-description',
    '国家管网集团西南管道有限责任公司注册地四川成都，管理中缅天然气管道（国内段）、中卫—贵阳天然气管道等骨干管道，是西南能源进口通道与川气出川主通道的重要运营主体。',
  'FLD-BASE-region-hq_location', '四川省成都市',
  'FLD-BASE-region-pipeline_km_total', 11300,
  'FLD-BASE-region-coverage_note', '管线途经川、渝、滇、黔、桂、粤、陕、甘、宁等九省区市',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区（如江津、内江作业区）',
  'FLD-BASE-region-centroid_lng', 104.0665,
  'FLD-BASE-region-centroid_lat', 30.5723,
  'FLD-BASE-region-remark', '约11300余公里口径来自公开二级单位介绍；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100017, jsonb_build_object(
  'FLD-BASE-region-mission_summary', 'LNG 接收站专业化集中管理',
  'FLD-BASE-region-description',
    '国家管网集团液化天然气接收站管理分公司于2020年12月26日成立，注册于天津滨海新区，对所辖 LNG 接收站业务实行集中统一管理，覆盖环渤海、珠三角、北部湾等沿海经济圈。',
  'FLD-BASE-region-hq_location', '天津市滨海新区',
  'FLD-BASE-region-lng_terminal_count', 7,
  'FLD-BASE-region-coverage_note', '下辖多家沿海 LNG 接收站，由北向南分布；公开介绍称成立初期下辖七家接收站',
  'FLD-BASE-region-org_mode_note', '专业化分公司集中管理接收站',
  'FLD-BASE-region-establish_date', '2020-12-26',
  'FLD-BASE-region-centroid_lng', 117.7010,
  'FLD-BASE-region-centroid_lat', 39.0410,
  'FLD-BASE-region-remark', '接收站数量取公开二级单位介绍中的成立初期口径；集团层面 LNG 接收站总数以官网业务领域页为准。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100018, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '华南成品油骨干管网运营',
  'FLD-BASE-region-description',
    '国家管网集团华南分公司总部广州，在广东、广西、贵州、云南、四川、重庆等六省区市设有分支机构，公开介绍称其成品油管道规模居国内前列。',
  'FLD-BASE-region-hq_location', '广东省广州市',
  'FLD-BASE-region-pipeline_km_total', 6103,
  'FLD-BASE-region-pipeline_km_cp', 6103,
  'FLD-BASE-region-coverage_note', '分支机构覆盖粤桂黔滇川渝；以成品油长输管道为主',
  'FLD-BASE-region-org_mode_note', '公司 — 输油部（分公司）— 输油站',
  'FLD-BASE-region-centroid_lng', 113.2644,
  'FLD-BASE-region-centroid_lat', 23.1291,
  'FLD-BASE-region-remark', '成品油管道约6103公里来自公开二级单位介绍；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100019, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '华中区域成品油长输管道运营',
  'FLD-BASE-region-description',
    '国家管网集团华中分公司于2020年9月注册、10月正式运营，位于武汉江汉区，主要负责管辖范围内成品油长输管道运输及相关业务。',
  'FLD-BASE-region-hq_location', '湖北省武汉市',
  'FLD-BASE-region-coverage_note', '公开介绍：在役成品油管道分布于湖北、湖南、江西、安徽四省',
  'FLD-BASE-region-org_mode_note', '区域分公司运维体制',
  'FLD-BASE-region-establish_date', '2020-09-14',
  'FLD-BASE-region-centroid_lng', 114.3055,
  'FLD-BASE-region-centroid_lat', 30.5928,
  'FLD-BASE-region-remark', '未检索到权威总里程公开数，故不填里程字段。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100020, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '华东区域成品油管输与区域经营探索',
  'FLD-BASE-region-description',
    '国家管网集团华东分公司于2020年9月注册，总部上海，承担江苏、浙江、上海、福建等华东区域成品油管输任务；后续改革中曾将成品油管道管理权移交东部原油储运公司运维，并探索转型为区域经营主体。',
  'FLD-BASE-region-hq_location', '上海市',
  'FLD-BASE-region-coverage_note', '华东区域：江苏、浙江、上海、福建',
  'FLD-BASE-region-org_mode_note', '区域分公司 / 改革试点经营主体',
  'FLD-BASE-region-establish_date', '2020-09-11',
  'FLD-BASE-region-centroid_lng', 121.4737,
  'FLD-BASE-region-centroid_lat', 31.2304,
  'FLD-BASE-region-remark', '组织沿革来自公开二级单位介绍；里程未单独公开故不填。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100021, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '华北成品油管网与北油南运枢纽',
  'FLD-BASE-region-description',
    '国家管网集团华北分公司由原销售华北分公司管道、油库等业务重组设立，2020年9月注册于天津。管辖多条长输成品油管道及油库，在全国成品油「一张网」中处于北油南运枢纽地位。',
  'FLD-BASE-region-hq_location', '天津市',
  'FLD-BASE-region-pipeline_km_total', 3111,
  'FLD-BASE-region-pipeline_km_cp', 3111,
  'FLD-BASE-region-coverage_note', '业务覆盖天津、河北、河南、山西、山东、安徽、江苏等省市',
  'FLD-BASE-region-org_mode_note', '区域分公司运维体制',
  'FLD-BASE-region-establish_date', '2020-09-30',
  'FLD-BASE-region-centroid_lng', 117.2008,
  'FLD-BASE-region-centroid_lat', 39.0842,
  'FLD-BASE-region-remark', '管道总长约3111公里来自公开二级单位介绍；未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100022, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '山东境内油气管网「全省一张网」运营',
  'FLD-BASE-region-description',
    '国家管网集团山东公司负责运营管理集团在山东境内的油气管网、油库及附属设施，持续推进管网互联互通与公平开放。公开报道称其管辖多条输油气管道与工艺站场，并推进北干线等省级管网融入国家管网。',
  'FLD-BASE-region-hq_location', '山东省济南市',
  'FLD-BASE-region-pipeline_km_total', 8200,
  'FLD-BASE-region-coverage_note', '公开报道：约50条输油气管道、110座工艺站场、大型油库；服务山东油气保供',
  'FLD-BASE-region-org_mode_note', '省公司 — 作业区（东营、德州、枣庄、泰安）',
  'FLD-BASE-region-centroid_lng', 117.1205,
  'FLD-BASE-region-centroid_lat', 36.6519,
  'FLD-BASE-region-remark', '管道总里程超8200公里等来自2025年鲁网/青岛新闻网公开报道。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100023, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '广东省天然气主干管网建设与运营',
  'FLD-BASE-region-description',
    '国家管网集团广东省管网有限公司前身为广东省天然气管网有限公司，注册广州，是国内较早采用纯管输模式的省级管网公司，2020年10月以市场化方式融入国家管网集团，负责省内天然气主干管网建设运营，推进「市市通」「县县通」。',
  'FLD-BASE-region-hq_location', '广东省广州市',
  'FLD-BASE-region-coverage_note', '公开介绍：已实现广东省21个地市通达天然气主干管道',
  'FLD-BASE-region-org_mode_note', '省级管网公司',
  'FLD-BASE-region-centroid_lng', 113.2644,
  'FLD-BASE-region-centroid_lat', 23.1291,
  'FLD-BASE-region-remark', '组织沿革来自公开二级单位介绍；总里程未单独核对故不填。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100024, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '福建省天然气主干管网与海西通道运营',
  'FLD-BASE-region-description',
    '国家管网集团福建省管网有限公司位于福州，主要从事陆地管道运输等业务，运营省内天然气管网及海西相关通道，是福建站场样例数据的主要归属运营区域节点。',
  'FLD-BASE-region-hq_location', '福建省福州市',
  'FLD-BASE-region-coverage_note', '福建省内天然气管网及沿海通道相关设施',
  'FLD-BASE-region-org_mode_note', '省级管网公司',
  'FLD-BASE-region-centroid_lng', 119.2965,
  'FLD-BASE-region-centroid_lat', 26.0745,
  'FLD-BASE-region-remark', '驻地等信息来自公开企业登记资料；总里程未单独核对故不填。未配置边界 GeoJSON。'
));

SELECT tmp_region_intro_merge(100025, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '浙江省天然气管网投资建设与运营',
  'FLD-BASE-region-description',
    '浙江省天然气管网有限公司为国家管网体系内的省级天然气管网运营主体，服务浙江天然气主干管网互联互通与公平开放。',
  'FLD-BASE-region-hq_location', '浙江省',
  'FLD-BASE-region-coverage_note', '浙江省内天然气管网',
  'FLD-BASE-region-org_mode_note', '省级管网公司',
  'FLD-BASE-region-centroid_lng', 120.1551,
  'FLD-BASE-region-centroid_lat', 30.2741,
  'FLD-BASE-region-remark', '公开细目较少，仅填定位与覆盖说明；未编造里程与边界。'
));

SELECT tmp_region_intro_merge(100026, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '湖南区域油气管网运维与保供',
  'FLD-BASE-region-description',
    '湖南分公司为国家管网集团区域运维组织之一，承担湖南境内相关油气管网运行维护与保供协同职责。',
  'FLD-BASE-region-hq_location', '湖南省长沙市',
  'FLD-BASE-region-coverage_note', '湖南省境内相关管网运维范围',
  'FLD-BASE-region-org_mode_note', '区域分公司',
  'FLD-BASE-region-centroid_lng', 112.9388,
  'FLD-BASE-region-centroid_lat', 28.2282,
  'FLD-BASE-region-remark', '公开细目较少，仅填定位与覆盖说明；未编造里程与边界。'
));

SELECT tmp_region_intro_merge(100027, jsonb_build_object(
  'FLD-BASE-region-mission_summary', '新疆煤制气外输通道运营',
  'FLD-BASE-region-description',
    '国家管网集团新疆煤制天然气外输管道有限责任公司成立于2011年，2020年整建制划转国家管网集团；注册地乌鲁木齐高新区，公司机关公开资料曾记载位于北京朝阳。',
  'FLD-BASE-region-hq_location', '新疆乌鲁木齐 / 北京（机关）',
  'FLD-BASE-region-coverage_note', '新疆煤制天然气外输及相关支干线、工程建设单位分布',
  'FLD-BASE-region-org_mode_note', '专业管道公司',
  'FLD-BASE-region-establish_date', '2011-11-01',
  'FLD-BASE-region-centroid_lng', 87.6168,
  'FLD-BASE-region-centroid_lat', 43.8256,
  'FLD-BASE-region-remark', '组织沿革来自公开二级单位介绍；总里程未单独核对故不填。未配置边界 GeoJSON。'
));

-- ---------- 作业区（有值说明 + 驻地城市质心；不编造里程） ----------
SELECT tmp_region_intro_merge(100101, jsonb_build_object(
  'FLD-BASE-region-description', '山东省公司下属东营作业区，负责属地管线巡护、站场运行与应急协同。',
  'FLD-BASE-region-hq_location', '山东省东营市',
  'FLD-BASE-region-coverage_note', '东营及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '省公司 — 作业区',
  'FLD-BASE-region-mission_summary', '东营属地运维单元',
  'FLD-BASE-region-centroid_lng', 118.6747,
  'FLD-BASE-region-centroid_lat', 37.4346,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100102, jsonb_build_object(
  'FLD-BASE-region-description', '山东省公司下属德州作业区，负责属地管线巡护、站场运行与应急协同。',
  'FLD-BASE-region-hq_location', '山东省德州市',
  'FLD-BASE-region-coverage_note', '德州及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '省公司 — 作业区',
  'FLD-BASE-region-mission_summary', '德州属地运维单元',
  'FLD-BASE-region-centroid_lng', 116.3575,
  'FLD-BASE-region-centroid_lat', 37.4341,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100103, jsonb_build_object(
  'FLD-BASE-region-description', '山东省公司下属枣庄作业区，负责属地管线巡护、站场运行与应急协同。',
  'FLD-BASE-region-hq_location', '山东省枣庄市',
  'FLD-BASE-region-coverage_note', '枣庄及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '省公司 — 作业区',
  'FLD-BASE-region-mission_summary', '枣庄属地运维单元',
  'FLD-BASE-region-centroid_lng', 117.3237,
  'FLD-BASE-region-centroid_lat', 34.8109,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100104, jsonb_build_object(
  'FLD-BASE-region-description', '山东省公司下属泰安作业区，负责属地管线巡护、站场运行与应急协同。',
  'FLD-BASE-region-hq_location', '山东省泰安市',
  'FLD-BASE-region-coverage_note', '泰安及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '省公司 — 作业区',
  'FLD-BASE-region-mission_summary', '泰安属地运维单元',
  'FLD-BASE-region-centroid_lng', 117.0880,
  'FLD-BASE-region-centroid_lat', 36.1946,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100105, jsonb_build_object(
  'FLD-BASE-region-description', '北方管道公司下属黑河作业区，服务东北方向跨国/跨省通道属地运维。',
  'FLD-BASE-region-hq_location', '黑龙江省黑河市',
  'FLD-BASE-region-coverage_note', '黑河及周边管段运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '黑河属地运维单元',
  'FLD-BASE-region-centroid_lng', 127.5286,
  'FLD-BASE-region-centroid_lat', 50.2452,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100106, jsonb_build_object(
  'FLD-BASE-region-description', '西气东输系统下属高陵作业区，负责关中方向相关干支线属地运维。',
  'FLD-BASE-region-hq_location', '陕西省西安市高陵区',
  'FLD-BASE-region-coverage_note', '高陵及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '高陵属地运维单元',
  'FLD-BASE-region-centroid_lng', 109.0889,
  'FLD-BASE-region-centroid_lat', 34.5351,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100107, jsonb_build_object(
  'FLD-BASE-region-description', '西部管道公司下属兰州作业区，服务甘青方向战略通道属地运维。',
  'FLD-BASE-region-hq_location', '甘肃省兰州市',
  'FLD-BASE-region-coverage_note', '兰州及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '兰州属地运维单元',
  'FLD-BASE-region-centroid_lng', 103.8343,
  'FLD-BASE-region-centroid_lat', 36.0611,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100108, jsonb_build_object(
  'FLD-BASE-region-description', '北京管道公司下属安平作业区，服务陕京系统河北段属地运维。',
  'FLD-BASE-region-hq_location', '河北省衡水市安平县',
  'FLD-BASE-region-coverage_note', '安平及周边陕京相关管段运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '安平属地运维单元',
  'FLD-BASE-region-centroid_lng', 115.5190,
  'FLD-BASE-region-centroid_lat', 38.2345,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100109, jsonb_build_object(
  'FLD-BASE-region-description', '北京管道公司下属永清作业区，服务陕京系统冀中段属地运维。',
  'FLD-BASE-region-hq_location', '河北省廊坊市永清县',
  'FLD-BASE-region-coverage_note', '永清及周边陕京相关管段运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '永清属地运维单元',
  'FLD-BASE-region-centroid_lng', 116.4987,
  'FLD-BASE-region-centroid_lat', 39.3208,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100110, jsonb_build_object(
  'FLD-BASE-region-description', '西南管道公司下属江津作业区，服务川渝方向干支线属地运维。',
  'FLD-BASE-region-hq_location', '重庆市江津区',
  'FLD-BASE-region-coverage_note', '江津及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '江津属地运维单元',
  'FLD-BASE-region-centroid_lng', 106.2593,
  'FLD-BASE-region-centroid_lat', 29.2831,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100111, jsonb_build_object(
  'FLD-BASE-region-description', '西南管道公司下属内江作业区，服务川南方向干支线属地运维。',
  'FLD-BASE-region-hq_location', '四川省内江市',
  'FLD-BASE-region-coverage_note', '内江及周边管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '内江属地运维单元',
  'FLD-BASE-region-centroid_lng', 105.0584,
  'FLD-BASE-region-centroid_lat', 29.5802,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100112, jsonb_build_object(
  'FLD-BASE-region-description', '东部原油储运公司下属洛阳作业区，服务豫西原油管输属地运维。',
  'FLD-BASE-region-hq_location', '河南省洛阳市',
  'FLD-BASE-region-coverage_note', '洛阳及周边原油管输相关站场/管段',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '洛阳属地运维单元',
  'FLD-BASE-region-centroid_lng', 112.4539,
  'FLD-BASE-region-centroid_lat', 34.6197,
  'FLD-BASE-region-remark', '作业区级公开里程稀缺，仅填组织定位与驻地；未编造边界。'
));
SELECT tmp_region_intro_merge(100113, jsonb_build_object(
  'FLD-BASE-region-description', '东部原油储运公司下属金桥作业区，服务相关原油管输属地运维。',
  'FLD-BASE-region-hq_location', '金桥作业区驻地',
  'FLD-BASE-region-coverage_note', '金桥作业区属地管段/站场运维范围',
  'FLD-BASE-region-org_mode_note', '区域公司 — 作业区',
  'FLD-BASE-region-mission_summary', '金桥属地运维单元',
  'FLD-BASE-region-remark', '公开驻地坐标未核实，不填质心；未编造里程与边界。'
));

DROP FUNCTION tmp_region_intro_merge(bigint, jsonb);

-- 自检：介绍字段非空实体数
DO $$
DECLARE filled integer;
BEGIN
  SELECT COUNT(*) INTO filled
  FROM ent_region
  WHERE deleted = false
    AND tenant_id = 1
    AND id BETWEEN 100001 AND 100113
    AND custom_fields ? 'FLD-BASE-region-description';
  IF filled < 28 THEN
    RAISE EXCEPTION 'region intro seed incomplete: only % entities have description', filled;
  END IF;
  RAISE NOTICE 'region intro seed ok: % entities with description', filled;
END $$;
