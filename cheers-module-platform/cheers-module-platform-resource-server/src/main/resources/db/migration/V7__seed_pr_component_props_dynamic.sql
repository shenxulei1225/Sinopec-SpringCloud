SET search_path TO platformresource;

-- 为 list 演示模板绑定动态业务数据来源（equipment / entity）。
-- 使用单引号 JSON 字面量，避免 Flyway 占位符与 PostgreSQL dollar-quote 冲突。

UPDATE pr_component_props
SET data_source = '{"businessCategory":"dynamic","businessTypeCode":"equipment","dataKind":"entity"}',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE id IN (1001, 1002)
  AND deleted = FALSE;
