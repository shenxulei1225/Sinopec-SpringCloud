-- 联动方式从 255 字的文本列改成 jsonb。一条联动可以写下全部舱室。
SET search_path TO dynamicbusiness, public;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness'
          AND table_name = 'ent_linkage_t123'
          AND column_name = 'f_471fe2c2cedc4181987e6fcdb5afc47c'
          AND data_type = 'character varying'
    ) THEN
        ALTER TABLE ent_linkage_t123
            ALTER COLUMN f_471fe2c2cedc4181987e6fcdb5afc47c TYPE jsonb
            USING CASE
                WHEN f_471fe2c2cedc4181987e6fcdb5afc47c IS NULL
                    OR btrim(f_471fe2c2cedc4181987e6fcdb5afc47c) = '' THEN NULL
                WHEN btrim(f_471fe2c2cedc4181987e6fcdb5afc47c) LIKE '{%'
                    THEN f_471fe2c2cedc4181987e6fcdb5afc47c::jsonb
                ELSE NULL
            END;
    END IF;
END $$;

UPDATE dynamic_field
SET type = 'JSON',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND deleted = false
  AND code = 'F-471fe2c2cedc4181987e6fcdb5afc47c'
  AND type IS DISTINCT FROM 'JSON';

UPDATE dynamic_entity_type_base_field
SET data_type = 'JSON',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND deleted = false
  AND entity_type_code = 'linkage'
  AND field_code = 'F-471fe2c2cedc4181987e6fcdb5afc47c'
  AND data_type IS DISTINCT FROM 'JSON';
