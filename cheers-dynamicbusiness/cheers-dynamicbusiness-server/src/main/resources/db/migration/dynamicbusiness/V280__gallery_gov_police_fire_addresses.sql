-- 只写入政务网站已经公布门牌的单位。
-- 江岸区政府、武昌区政府便民查询、洪山区 2024 年行政执法主体公告。
-- 洪山街派出所用湖北省公安厅 2026-06 公布的新址。
-- 市公安局名录只有分局地址和所名，没有各所门牌的不写入。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.ent_external_rescue_resource_t123
SET contact_phone = '027-85391717', updater = 'V280', update_time = now()
WHERE tenant_id = 123 AND deleted = false AND code = 'ex-guandong-police'
  AND (contact_phone IS NULL OR btrim(contact_phone) = '');

UPDATE dynamicbusiness.ent_external_rescue_resource_t123
SET current_location = '武汉东湖新技术开发区高新五路333号',
    contact_phone = '027-50777250',
    updater = 'V280', update_time = now()
WHERE tenant_id = 123 AND deleted = false AND code = 'ex-baoxie-police';

INSERT INTO dynamicbusiness.ent_external_rescue_resource_t123 (
    tenant_id, entity_type_code, model_id, name, code, status, facility_id,
    current_location, contact_phone, creator, updater, deleted
)
SELECT 123, 'external_rescue_resource', m.id, v.name, v.code, 1, 1,
       v.place, v.phone, 'V280', 'V280', false
FROM (
    VALUES
    ('ex-police', 'ex-pol-ja-01', '大智派出所', '武汉市江岸区保成路泰宁街23号', '027-85391000'),
    ('ex-police', 'ex-pol-ja-02', '上海派出所', '武汉市江岸区胜利街100号', '027-85391005'),
    ('ex-police', 'ex-pol-ja-03', '一元派出所', '武汉市江岸区胜利街166号', '027-85391008'),
    ('ex-police', 'ex-pol-ja-04', '车站派出所', '武汉市江岸区天声街59号', '027-85391012'),
    ('ex-police', 'ex-pol-ja-05', '四唯派出所', '武汉市江岸区新兴街34号', '027-85391016'),
    ('ex-police', 'ex-pol-ja-06', '永清派出所', '武汉市江岸区胜利街306号', '027-85391020'),
    ('ex-police', 'ex-pol-ja-07', '西马派出所', '武汉市江岸区模范路59号', '027-85391026'),
    ('ex-police', 'ex-pol-ja-08', '球场派出所', '武汉市江岸区解放南路421号', '027-85391028'),
    ('ex-police', 'ex-pol-ja-09', '劳动派出所', '武汉市江岸区解放公园路47号', '027-85391033'),
    ('ex-police', 'ex-pol-ja-10', '二七派出所', '武汉市江岸区黄埔路16号', '027-82885766'),
    ('ex-police', 'ex-pol-ja-11', '新村派出所', '武汉市江岸区连城中路666号', '027-85391040'),
    ('ex-police', 'ex-pol-ja-12', '丹水派出所', '武汉市江岸区解放大道2056号', '027-82343686'),
    ('ex-police', 'ex-pol-ja-13', '台北派出所', '武汉市江岸区台北二村8号', '027-85391050'),
    ('ex-police', 'ex-pol-ja-14', '花桥派出所', '武汉市江岸区江大路119号', '027-85391066'),
    ('ex-police', 'ex-pol-ja-15', '谌家矶派出所', '武汉市江岸区谌家矶大道75号', '027-82329926'),
    ('ex-police', 'ex-pol-ja-16', '百步亭派出所', '武汉市江岸区温馨路9号', '027-82354669'),
    ('ex-police', 'ex-pol-ja-17', '后湖派出所', '武汉市江岸区兴业路177号', '027-82910186'),
    ('ex-police', 'ex-pol-ja-18', '塔子湖派出所', '武汉市江岸区石桥小路特1号', '027-85391074'),
    ('ex-police', 'ex-pol-ja-19', '竹叶山派出所', '武汉市江岸区黄孝河路123号', '027-85391057'),
    ('ex-police', 'ex-pol-wc-01', '粮道街派出所', '武汉市武昌区公正路1号', '027-88085591'),
    ('ex-police', 'ex-pol-wc-02', '紫阳街派出所', '武汉市武昌武巡回街100号', '027-88085642'),
    ('ex-police', 'ex-pol-wc-03', '梅苑派出所', '武汉市武昌区丁字桥路110号', '027-88085544'),
    ('ex-police', 'ex-pol-wc-04', '南湖街派出所', '武汉市武昌区恒安路247号', '027-88085789'),
    ('ex-police', 'ex-pol-wc-05', '徐家棚街派出所', '武汉市武昌区惠誉路6号', '027-88085520'),
    ('ex-police', 'ex-pol-wc-06', '杨园街派出所', '武汉市武昌区林茂路12号', '027-88085512'),
    ('ex-police', 'ex-pol-wc-07', '余家头派出所', '武汉市武昌区团结路12号', '027-88738505'),
    ('ex-police', 'ex-pol-wc-08', '中华路派出所', '武汉市武昌区中华路76号', '027-88085611'),
    ('ex-police', 'ex-pol-wc-09', '白沙洲街派出所', '武汉市武昌区陆家街234号', '027-88085671'),
    ('ex-police', 'ex-pol-wc-10', '黄鹤楼街派出所', '武汉市武昌区彭刘杨路37号', '027-88085631'),
    ('ex-police', 'ex-pol-wc-11', '积玉桥街派出所', '武汉市武昌区尚隆路23号', '027-88085569'),
    ('ex-police', 'ex-pol-wc-12', '珞珈山街派出所', '武汉市武昌区八一路301号', '027-68766117'),
    ('ex-police', 'ex-pol-wc-13', '首义路街派出所', '武汉市武昌首义路63号', '027-88085693'),
    ('ex-police', 'ex-pol-wc-14', '中南路街派出所', '武汉市武昌武中南二路77号', '027-88085711'),
    ('ex-police', 'ex-pol-wc-15', '石洞街派出所', '武汉市武昌区白云洞二路1号', '027-88085468'),
    ('ex-police', 'ex-pol-hs-01', '洪山街派出所', '武汉市洪山区治维路18号', '027-87153110'),
    ('ex-police', 'ex-pol-hs-02', '北港街派出所', '武汉市洪山区文盛街99号', '027-88021397'),
    ('ex-police', 'ex-pol-hs-03', '青菱街派出所', '武汉市洪山区黄家湖西路', '027-88713100'),
    ('ex-police', 'ex-pol-hs-04', '张家湾街派出所', '武汉市洪山区张家湾39号', '027-86820006'),
    ('ex-police', 'ex-pol-hs-05', '狮子山街派出所', '武汉市洪山区狮子山街马湖村8号', '027-88033414'),
    ('ex-police', 'ex-pol-hs-06', '珞南街派出所', '武汉市洪山区桂元路170号', '027-87740019'),
    ('ex-police', 'ex-pol-hs-07', '卓刀泉街派出所', '武汉市洪山区商学路66号', '027-87801110'),
    ('ex-police', 'ex-pol-hs-08', '关山街派出所', '武汉市洪山区光谷创业街26号', '027-87803329'),
    ('ex-police', 'ex-pol-hs-09', '和平街派出所', '武汉市洪山区团结大道1400号', '027-86368900'),
    ('ex-police', 'ex-pol-hs-10', '梨园街派出所', '武汉市洪山区杨园南路28号', '027-86822866'),
    ('ex-police', 'ex-pol-hs-11', '白沙洲市场派出所', '武汉市洪山区建和路建和生鲜市场斜对面', '027-88155551'),
    ('ex-police', 'ex-pol-hs-12', '武汉火车站派出所', '武汉市洪山区白云路特1号', '027-86689847'),
    ('ex-police', 'ex-pol-hs-13', '天兴派出所', '武汉市洪山区天兴村130号', '13971110129'),
    ('ex-police', 'ex-pol-hs-14', '马房山派出所', '武汉市洪山区雄楚大道199号', '027-87666692'),
    ('ex-police', 'ex-pol-hs-15', '喻家山派出所', '武汉市洪山区珞瑜东路华中科技大学内', '027-87450110'),
    ('ex-police', 'ex-pol-hs-16', '南湖派出所', '武汉市武昌区恒安路247号', '027-88038869'),
    ('ex-fire', 'ex-fire-hs-brigade', '武汉市洪山区消防救援大队', '武汉市洪山区珞桂路60号', '027-87519119'),
    ('ex-fire', 'ex-fire-wc-zhongshan', '中山路消防救援站', '武汉市武昌区巡司河路25号', NULL),
    ('ex-fire', 'ex-fire-hs-nanhu', '南湖消防救援站', '武汉市洪山区文昌路111号', NULL)
) AS v(model_code, code, name, place, phone)
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 123 AND m.deleted = false AND m.code = v.model_code
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t123 e
    WHERE e.tenant_id = 123 AND e.deleted = false AND e.code = v.code
);

INSERT INTO dynamicbusiness.dynamic_category_entity_link_t123 (
    category_id, entity_id, entity_model_id, creator, tenant_id, entity_type_code
)
SELECT c.id, e.id, e.model_id, 'V280', 123, 'external_rescue_resource'
FROM dynamicbusiness.ent_external_rescue_resource_t123 e
JOIN dynamicbusiness.dynamic_model m ON m.id = e.model_id
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 123 AND c.deleted = false AND c.code = m.code
WHERE e.tenant_id = 123 AND e.deleted = false AND e.creator = 'V280'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category_entity_link_t123 l
      WHERE l.tenant_id = 123 AND l.deleted = false
        AND l.entity_id = e.id AND l.category_id = c.id
        AND l.entity_type_code = 'external_rescue_resource'
  );
