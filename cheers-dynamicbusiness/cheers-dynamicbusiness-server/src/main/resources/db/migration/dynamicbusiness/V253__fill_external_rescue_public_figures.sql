-- 用公开资料补外部救援单位的可接收人数和电话。
-- 可接收人数只写开放床位或编制床位。急诊床位、坐标、消防队伍人数没有单独公开数字，本脚本不写。
-- 同一家医院数字互相矛盾或来源过时的，不在这里更新。

UPDATE dynamicbusiness.ent_external_rescue_resource_t1 AS e
SET receivable_count = v.receivable_count,
    contact_phone = COALESCE(v.contact_phone, e.contact_phone),
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    ('福建医科大学附属协和医院', 2500, '0591-83357896'),
    ('福建医科大学附属第二医院', 2000, '0595-22791140'),
    ('泉州市第一医院', 2743, '0595-28073120'),
    ('厦门大学附属第一医院', 3500, '0592-2132222'),
    ('厦门大学附属中山医院', 3500, '0592-2292201'),
    ('漳州市医院', 2000, '0596-2082950'),
    ('宁德市闽东医院', 1700, '0593-6336086'),
    ('徐州市中心医院', 4500, NULL),
    ('徐州医科大学附属医院', 4150, NULL),
    ('洛阳市中心医院', 2157, NULL),
    ('河南科技大学第一附属医院', 3752, NULL),
    ('龙岩市第二医院', 1580, NULL),
    ('莆田学院附属医院', 2200, '0594-2293910')
) AS v(name, receivable_count, contact_phone)
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND e.name = v.name;

UPDATE dynamicbusiness.ent_external_rescue_resource_t1
SET contact_phone = '0597-2294813',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND name = '龙岩市第一医院'
  AND contact_phone IS NULL;

UPDATE dynamicbusiness.ent_external_rescue_resource_t1
SET contact_phone = '0595-86394100',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND name = '南安市医院'
  AND contact_phone IS NULL;

UPDATE dynamicbusiness.ent_external_rescue_resource_t1
SET contact_phone = '0595-27799119',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND name = '泉州市泉港石化工业区消防大队'
  AND contact_phone IS NULL;
