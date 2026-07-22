-- 启动响应 500：history 缺序列 + task 序列落后导致重复 id

CREATE SEQUENCE IF NOT EXISTS emergency_response_history_seq;
SELECT setval(
  'emergency_response_history_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM emergency_response_history), 1), 1)
);
ALTER TABLE emergency_response_history
  ALTER COLUMN id SET DEFAULT nextval('emergency_response_history_seq');

CREATE SEQUENCE IF NOT EXISTS emergency_task_history_id_seq;
SELECT setval(
  'emergency_task_history_id_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM emergency_task_history), 1), 1)
);
ALTER TABLE emergency_task_history
  ALTER COLUMN id SET DEFAULT nextval('emergency_task_history_id_seq');

CREATE SEQUENCE IF NOT EXISTS emergency_event_status_history_seq;
SELECT setval(
  'emergency_event_status_history_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM emergency_event_status_history), 1), 1)
);
ALTER TABLE emergency_event_status_history
  ALTER COLUMN id SET DEFAULT nextval('emergency_event_status_history_seq');

-- task / response 序列对齐到 MAX(id)，避免无主键时插出重复 id
SELECT setval(
  'emergency_task_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM emergency_task), 1), 1)
);
SELECT setval(
  'emergency_response_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM emergency_response), 1), 1)
);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conrelid = 'emergency_task'::regclass AND contype = 'p'
  ) AND NOT EXISTS (
    SELECT 1 FROM emergency_task GROUP BY id HAVING COUNT(*) > 1
  ) THEN
    ALTER TABLE emergency_task ADD PRIMARY KEY (id);
  END IF;
END $$;
