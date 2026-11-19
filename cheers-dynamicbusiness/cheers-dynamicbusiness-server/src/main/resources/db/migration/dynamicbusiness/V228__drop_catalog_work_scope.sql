-- 删除数据目录上的「工作范围」列（NETWORK 全网 / FACILITY 站场级）。
-- 记录属于哪座站只认数据归属声明（dynamic_catalog_ownership）：声明了所属场站 → 场站的目录，
-- 列表按右上角选中的站收窄。目录本身不再标全网还是站场级。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_entity_type DROP COLUMN IF EXISTS work_scope;
