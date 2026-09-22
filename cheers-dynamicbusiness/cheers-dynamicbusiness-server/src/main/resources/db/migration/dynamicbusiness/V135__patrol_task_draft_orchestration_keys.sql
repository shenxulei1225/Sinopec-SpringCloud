-- V135: 总任务草稿袋（FLD-TSK-033）编排键一次性重命名，退役 scheduleEnabled / arrangePreview*
--
-- 写路径与读路径只认 orchestrationCommitted / orchestrationPreview*；本脚本收口存量 JSON。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_task(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      $upd$
      UPDATE dynamicbusiness.%I e
      SET custom_fields = sub.new_cf,
          updater = COALESCE(e.updater, 'flyway-v135'),
          update_time = CURRENT_TIMESTAMP
      FROM (
        SELECT id,
          jsonb_set(
            custom_fields,
            '{FLD-TSK-033}',
            (
              (custom_fields -> 'FLD-TSK-033')
              - 'scheduleEnabled'
              - 'arrangePreviewSlots'
              - 'arrangePreviewPlainSummary'
              || CASE
                   WHEN (custom_fields -> 'FLD-TSK-033') ? 'scheduleEnabled'
                     AND NOT ((custom_fields -> 'FLD-TSK-033') ? 'orchestrationCommitted')
                   THEN jsonb_build_object(
                     'orchestrationCommitted', custom_fields -> 'FLD-TSK-033' -> 'scheduleEnabled')
                   ELSE '{}'::jsonb
                 END
              || CASE
                   WHEN (custom_fields -> 'FLD-TSK-033') ? 'arrangePreviewSlots'
                     AND NOT ((custom_fields -> 'FLD-TSK-033') ? 'orchestrationPreviewSlots')
                   THEN jsonb_build_object(
                     'orchestrationPreviewSlots', custom_fields -> 'FLD-TSK-033' -> 'arrangePreviewSlots')
                   ELSE '{}'::jsonb
                 END
              || CASE
                   WHEN (custom_fields -> 'FLD-TSK-033') ? 'arrangePreviewPlainSummary'
                     AND NOT ((custom_fields -> 'FLD-TSK-033') ? 'orchestrationPreviewPlainSummary')
                   THEN jsonb_build_object(
                     'orchestrationPreviewPlainSummary',
                     custom_fields -> 'FLD-TSK-033' -> 'arrangePreviewPlainSummary')
                   ELSE '{}'::jsonb
                 END
            ),
            true
          ) AS new_cf
        FROM dynamicbusiness.%I
        WHERE deleted = FALSE
          AND custom_fields ? 'FLD-TSK-033'
          AND (
            (custom_fields -> 'FLD-TSK-033') ? 'scheduleEnabled'
            OR (custom_fields -> 'FLD-TSK-033') ? 'arrangePreviewSlots'
            OR (custom_fields -> 'FLD-TSK-033') ? 'arrangePreviewPlainSummary'
          )
      ) sub
      WHERE e.id = sub.id
      $upd$,
      tbl,
      tbl
    );
  END LOOP;
END $$;
