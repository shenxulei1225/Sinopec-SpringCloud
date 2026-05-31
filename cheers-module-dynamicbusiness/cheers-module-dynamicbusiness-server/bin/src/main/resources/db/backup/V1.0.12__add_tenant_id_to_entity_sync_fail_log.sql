-- 给 system_entity_sync_fail_log 表添加租户字段
-- 支持多租户定时任务处理

-- 注意：部分存量环境可能不存在该表（历史迁移未包含/模块未启用）。
-- 为避免 Flyway 在存量库上直接失败,这里在表存在时才执行变更。
DO $
BEGIN
  IF to_regclass('public.system_entity_sync_fail_log') IS NOT NULL THEN

    -- 添加 tenant_id 字段
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0';

    -- 添加 creator 字段
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS creator VARCHAR(64) DEFAULT ''''';

    -- 添加 create_time 字段（替代原来的 created_at）
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP';

    -- 添加 updater 字段
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS updater VARCHAR(64) DEFAULT ''''';

    -- 添加 update_time 字段（替代原来的 updated_at）
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP';

    -- 添加 deleted 字段
    EXECUTE 'ALTER TABLE system_entity_sync_fail_log ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE';

    -- 迁移旧数据：将 created_at 和 updated_at 的值复制到新字段
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'system_entity_sync_fail_log' AND column_name = 'created_at'
    ) THEN
      EXECUTE 'UPDATE system_entity_sync_fail_log SET create_time = created_at WHERE created_at IS NOT NULL AND create_time = CURRENT_TIMESTAMP';
    END IF;

    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'public' AND table_name = 'system_entity_sync_fail_log' AND column_name = 'updated_at'
    ) THEN
      EXECUTE 'UPDATE system_entity_sync_fail_log SET update_time = updated_at WHERE updated_at IS NOT NULL AND update_time = CURRENT_TIMESTAMP';
    END IF;

    -- 添加注释
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.tenant_id IS ''租户编号''';
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.creator IS ''创建者''';
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.create_time IS ''创建时间''';
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.updater IS ''更新者''';
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.update_time IS ''更新时间''';
    EXECUTE 'COMMENT ON COLUMN system_entity_sync_fail_log.deleted IS ''是否删除''';

    -- 创建租户索引
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_entity_sync_fail_log_tenant ON system_entity_sync_fail_log(tenant_id)';

  END IF;
END $;
