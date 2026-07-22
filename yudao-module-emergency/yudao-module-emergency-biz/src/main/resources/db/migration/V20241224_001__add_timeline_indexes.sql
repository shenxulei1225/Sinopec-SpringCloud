-- 时间线聚合查询性能优化：创建复合索引
-- 日期：2024-12-24
-- 说明：为时间线查询涉及的字段创建复合索引，提升查询性能

-- 1. 事件表：按事件ID和时间查询
CREATE INDEX IF NOT EXISTS idx_event_timeline ON emergency_event(id, update_time) 
WHERE deleted = FALSE;

-- 2. 任务表：按事件ID和创建时间查询
CREATE INDEX IF NOT EXISTS idx_task_timeline ON emergency_task(event_id, create_time) 
WHERE deleted = FALSE;

-- 3. 资源调度表：按事件ID和创建时间查询
CREATE INDEX IF NOT EXISTS idx_dispatch_timeline ON resource_dispatch(event_id, create_time) 
WHERE deleted = FALSE;

-- 4. 内部上报表：按事件ID和上报时间查询
CREATE INDEX IF NOT EXISTS idx_report_internal_timeline ON emergency_event_report_internal(event_id, report_time) 
WHERE deleted = FALSE;

-- 5. 外部上报表：按事件ID和上报时间查询
CREATE INDEX IF NOT EXISTS idx_report_external_timeline ON emergency_event_report_external(event_id, report_time) 
WHERE deleted = FALSE;

-- 6. 评估表：按事件ID和评估时间查询
CREATE INDEX IF NOT EXISTS idx_assess_timeline ON emergency_event_assess(event_id, assess_time) 
WHERE deleted = FALSE;

-- 7. 处置表：按事件ID和处置时间查询
CREATE INDEX IF NOT EXISTS idx_handling_timeline ON emergency_event_handling(event_id, handling_time) 
WHERE deleted = FALSE;

-- 添加索引注释
COMMENT ON INDEX idx_event_timeline IS '事件表时间线查询索引：用于优化事件创建和状态变更查询';
COMMENT ON INDEX idx_task_timeline IS '任务表时间线查询索引：用于优化任务记录查询';
COMMENT ON INDEX idx_dispatch_timeline IS '资源调度表时间线查询索引：用于优化资源调度记录查询';
COMMENT ON INDEX idx_report_internal_timeline IS '内部上报表时间线查询索引：用于优化内部上报记录查询';
COMMENT ON INDEX idx_report_external_timeline IS '外部上报表时间线查询索引：用于优化外部上报记录查询';
COMMENT ON INDEX idx_assess_timeline IS '评估表时间线查询索引：用于优化评估记录查询';
COMMENT ON INDEX idx_handling_timeline IS '处置表时间线查询索引：用于优化处置记录查询';








