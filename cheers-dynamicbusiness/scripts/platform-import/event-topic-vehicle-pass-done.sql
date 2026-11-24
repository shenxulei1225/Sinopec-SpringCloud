-- 智慧管廊事件主题：已经进出。
-- 安防图层按名字规则 vehicle/pass/done/{facilityId} 订阅。
-- 没有这一行时不订。本脚本只补这一行，启动和打开页面都不插入。

INSERT INTO dynamicbusiness.ent_event_topic_t123 (
    tenant_id, entity_type_code, model_id, domain, name, code,
    parent_id, sort, status, custom_fields, creator, updater, deleted,
    f_696d23e0541845208db3b3524d219a61, facility_id
)
SELECT
    123, 'event_topic', 2591, '', '已经进出', 'vehicle_pass_done',
    0, 0, 1,
    '{"event_category":"车辆进出","event_payload_schema":[{"code":"plate","type":"TEXT","label":"车牌"},{"code":"direction","type":"TEXT","label":"方向"},{"code":"gate","type":"TEXT","label":"道口"},{"code":"at","type":"TEXT","label":"时间"}]}'::jsonb,
    '1', '1', false,
    'vehicle/pass/done/{facilityId}', 1
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_event_topic_t123
    WHERE code = 'vehicle_pass_done' AND deleted = false
);
