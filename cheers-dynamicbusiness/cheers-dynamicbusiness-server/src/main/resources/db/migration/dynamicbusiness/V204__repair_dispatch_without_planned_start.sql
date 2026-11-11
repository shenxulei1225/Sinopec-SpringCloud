SET search_path TO dynamicbusiness;

-- 当班派单只记班组。预计开始不再是派工必填。实际开始记在开工那一行进度上。
UPDATE dynamic_ledger_transition
SET required_fields = 'teamId,title',
    trace_template = '已安排给{班组} {执行人}',
    updater = 'flyway-v204',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND from_state = 'accepted'
  AND to_state = 'dispatched'
  AND required_fields LIKE '%plannedStart%';
