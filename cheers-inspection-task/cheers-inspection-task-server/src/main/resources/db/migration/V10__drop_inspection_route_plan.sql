-- V10: 路线权威收口至动态业务总任务 plannedRoute（FLD-TSK-027）
-- 退役 inspection_route_plan 台账及 inspection_task 上的路线双写列（V2 快照列）

ALTER TABLE inspection_task DROP COLUMN IF EXISTS route_plan_id;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS network_ref;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS planned_route;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS duration_estimate_minutes;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS inspection_type;

DROP TABLE IF EXISTS inspection_route_plan;
DROP SEQUENCE IF EXISTS inspection_route_plan_seq;
