SET search_path TO work_order;

-- 标准流程挂在设备或事件上。这一次没挂上时，工单仍可创建，这三列为空。
ALTER TABLE wo_work_order
    ALTER COLUMN standard_id DROP NOT NULL,
    ALTER COLUMN standard_version_no DROP NOT NULL,
    ALTER COLUMN standard_snapshot_json DROP NOT NULL;

COMMENT ON COLUMN wo_work_order.standard_id IS '这一次处理内容上已挂的标准流程。没挂上则为空，不由派单人挑选。';
