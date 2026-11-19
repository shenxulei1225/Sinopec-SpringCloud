-- 智慧管廊站内应急资源改读本租户的表之后，列表按已登记字段选列。
-- 这五列只加在巡检租户那张表上。本租户的表没有，读列表会报缺列。
-- 类型与巡检租户同一目录上已经存在的列一致。不从巡检租户拷数据。

ALTER TABLE dynamicbusiness.ent_emergency_resource_t123
    ADD COLUMN IF NOT EXISTS resource_category text,
    ADD COLUMN IF NOT EXISTS safety_stock numeric,
    ADD COLUMN IF NOT EXISTS plate_no text,
    ADD COLUMN IF NOT EXISTS vehicle_load text,
    ADD COLUMN IF NOT EXISTS license_valid_until date;
