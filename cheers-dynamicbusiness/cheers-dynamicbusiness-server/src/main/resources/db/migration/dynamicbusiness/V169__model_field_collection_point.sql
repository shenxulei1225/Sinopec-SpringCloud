-- 采集点挂在型号的字段分配上。历史行抄字段编号。
-- 按设备另存的点名单不再使用。

ALTER TABLE dynamicbusiness.dynamic_model_field_assignment
    ADD COLUMN IF NOT EXISTS collection_point boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN dynamicbusiness.dynamic_model_field_assignment.collection_point IS
    '这个字段是该型号的采集点。读数进历史库，按字段编号对上。画面显示字段名称和单位。';

DROP TABLE IF EXISTS dynamicbusiness.device_collection_point;
