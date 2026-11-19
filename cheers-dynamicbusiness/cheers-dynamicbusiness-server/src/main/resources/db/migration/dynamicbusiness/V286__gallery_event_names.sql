-- 管廊现有三起事件的名称是设备故障，和应急预案对不上。改成预案对应的事故。
-- 只改名称。预案、状态和脉络不动。

UPDATE dynamicbusiness.ent_emergency_t123
SET name = '巡检车辆在管廊出入口发生交通事故',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '液压井盖未关到位';

UPDATE dynamicbusiness.ent_emergency_t123
SET name = '综合舱电缆接头起火',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '舱室可燃气体升高';

UPDATE dynamicbusiness.ent_emergency_t123
SET name = '有限空间作业人员中毒窒息',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND name = '综合舱风机停转';
