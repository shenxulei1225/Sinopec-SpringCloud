-- 任务管理状态属于任务底座。巡检、维修等任务共用草稿/已启用/已停用。
-- 旧 FLD-TSK-022 只参与一次迁移。运行时只读写 manage_status。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_type = 'BASE TABLE'
      AND table_name ~ '^ent_task(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS manage_status varchar(16)',
      tbl
    );
    EXECUTE format($sql$
      UPDATE dynamicbusiness.%I
      SET manage_status = CASE
        WHEN custom_fields->>'FLD-TSK-022' IN ('draft', 'enabled', 'disabled')
          THEN custom_fields->>'FLD-TSK-022'
        WHEN lower(COALESCE(custom_fields->'FLD-TSK-033'->>'orchestrationCommitted', 'false')) = 'true'
          THEN 'enabled'
        WHEN NULLIF(custom_fields->'FLD-TSK-033'->>'runtimeJobId', '') IS NOT NULL
          THEN 'disabled'
        ELSE 'draft'
      END
      WHERE manage_status IS NULL
         OR manage_status NOT IN ('draft', 'enabled', 'disabled')
    $sql$, tbl);
    EXECUTE format(
      'UPDATE dynamicbusiness.%I SET custom_fields = custom_fields - ''FLD-TSK-022'' WHERE custom_fields ? ''FLD-TSK-022''',
      tbl
    );
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN manage_status SET DEFAULT ''draft''',
      tbl
    );
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN manage_status SET NOT NULL',
      tbl
    );
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (tenant_id, model_id, manage_status) WHERE deleted = false',
      'idx_' || tbl || '_manage_status',
      tbl
    );
  END LOOP;
END $$;
