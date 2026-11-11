-- 派单班组（人员编组分类 id）；执行人可空表示班组待领
SET search_path TO work_order;

ALTER TABLE wo_work_order
    ADD COLUMN IF NOT EXISTS assignee_team_id BIGINT;

CREATE INDEX IF NOT EXISTS ix_wo_team_unassigned
    ON wo_work_order (tenant_id, assignee_team_id)
    WHERE deleted = FALSE AND assignee_user_id IS NULL;
