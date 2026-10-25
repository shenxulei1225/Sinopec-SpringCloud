-- 部门负责人属于 department 底座固定列，值只存 user_account 实体 id。
-- Flyway 只迁移表结构和旧行。字段库与基础字段元数据由 platform-import seed 写入。

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
      AND table_name ~ '^ent_department(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS leader_user_id bigint',
      tbl
    );
    EXECUTE format($sql$
      UPDATE dynamicbusiness.%I
      SET leader_user_id = CASE
        WHEN jsonb_typeof(custom_fields->'leader_user_id') = 'number'
             AND custom_fields->>'leader_user_id' ~ '^[0-9]+$'
          THEN (custom_fields->>'leader_user_id')::bigint
        WHEN jsonb_typeof(custom_fields->'leader_user_id') = 'string'
             AND custom_fields->>'leader_user_id' ~ '^[0-9]+$'
          THEN (custom_fields->>'leader_user_id')::bigint
        WHEN jsonb_typeof(custom_fields->'leader_user_id') = 'object'
             AND custom_fields->'leader_user_id'->>'id' ~ '^[0-9]+$'
          THEN (custom_fields->'leader_user_id'->>'id')::bigint
        ELSE NULL
      END
      WHERE leader_user_id IS NULL
        AND custom_fields ? 'leader_user_id'
    $sql$, tbl);
    EXECUTE format(
      'UPDATE dynamicbusiness.%I SET custom_fields = custom_fields - ''leader_user_id'' WHERE custom_fields ? ''leader_user_id''',
      tbl
    );
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (leader_user_id) WHERE deleted = false AND leader_user_id IS NOT NULL',
      'idx_' || tbl || '_leader_user_id',
      tbl
    );
  END LOOP;
END $$;
