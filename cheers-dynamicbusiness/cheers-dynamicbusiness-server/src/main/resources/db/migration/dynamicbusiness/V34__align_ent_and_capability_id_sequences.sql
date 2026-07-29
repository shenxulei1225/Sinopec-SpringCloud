-- 存量专用表 / 能力投影表：补齐 id 序列默认值，并将序列对齐到 MAX(id)。
-- 背景：
--   1) V1 中个别 ent_*（如 ent_equipment）仅有 id bigint NOT NULL，无 DEFAULT；
--   2) seed / 手工导入显式写入大 id 后序列未推进，插入会 DuplicateKey 或表现为系统异常；
--   3) 运行时新建数据类型用 BIGSERIAL（正常），但「表已存在则跳过 CREATE」不会修复坏表。

DO $$
DECLARE
  r RECORD;
  seq_qual text;
  max_id bigint;
  has_row boolean;
  id_default text;
BEGIN
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    JOIN pg_attribute a
      ON a.attrelid = c.oid AND a.attname = 'id' AND NOT a.attisdropped AND a.attnum > 0
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname LIKE 'ent\_%' ESCAPE '\'
        OR c.relname IN (
          'model_crud_form_definition',
          'business_capability',
          'capability_component_projection'
        )
      )
    ORDER BY c.relname
  LOOP
    seq_qual := 'dynamicbusiness.' || r.tbl || '_id_seq';

    SELECT pg_get_expr(d.adbin, d.adrelid)
      INTO id_default
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    JOIN pg_attribute a
      ON a.attrelid = c.oid AND a.attname = 'id' AND NOT a.attisdropped AND a.attnum > 0
    LEFT JOIN pg_attrdef d ON d.adrelid = c.oid AND d.adnum = a.attnum
    WHERE n.nspname = 'dynamicbusiness' AND c.relname = r.tbl;

    -- 若已有 nextval 默认值，沿用其序列名；否则创建 {table}_id_seq
    IF id_default IS NOT NULL AND id_default LIKE 'nextval(%' THEN
      seq_qual := substring(id_default FROM 'nextval\(''([^'']+)''');
    ELSE
      EXECUTE format('CREATE SEQUENCE IF NOT EXISTS %s', seq_qual);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ALTER COLUMN id SET DEFAULT nextval(%L::regclass)',
        r.tbl, seq_qual
      );
      BEGIN
        EXECUTE format(
          'ALTER SEQUENCE %s OWNED BY dynamicbusiness.%I.id',
          seq_qual, r.tbl
        );
      EXCEPTION
        WHEN OTHERS THEN
          -- 序列归属已存在或无权变更时忽略，不影响 DEFAULT / setval
          NULL;
      END;
    END IF;

    EXECUTE format('SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.%I', r.tbl) INTO max_id;
    EXECUTE format('SELECT EXISTS (SELECT 1 FROM dynamicbusiness.%I)', r.tbl) INTO has_row;
    PERFORM setval(seq_qual::regclass, max_id, has_row);
  END LOOP;
END $$;
