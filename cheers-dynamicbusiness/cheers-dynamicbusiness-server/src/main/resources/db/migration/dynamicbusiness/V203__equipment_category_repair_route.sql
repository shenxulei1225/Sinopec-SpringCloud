-- 设备分类上的派工预填：默认班组、默认作业标准。
-- 只给设备分类用。其它分类这两列空着。
-- 作业标准 id 是工单服务里已发布标准的 id，派工时按它收窄选项。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_category
    ADD COLUMN IF NOT EXISTS repair_team_id BIGINT,
    ADD COLUMN IF NOT EXISTS repair_standard_id BIGINT;

COMMENT ON COLUMN dynamic_category.repair_team_id IS '设备分类的默认派工班组（人员编组分类 id）';
COMMENT ON COLUMN dynamic_category.repair_standard_id IS '设备分类的默认作业标准（工单已发布标准 id）';
