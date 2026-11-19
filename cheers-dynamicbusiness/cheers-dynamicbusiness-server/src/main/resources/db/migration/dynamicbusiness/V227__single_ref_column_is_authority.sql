-- 单值引用存在记录的专用列上时，引用只认那一列（例：设备.所属场站、管道.所属管线段）。
-- 按场站筛记录、反查谁引用了某座站、删除前计数都直接查这一列，不再经实体关系表或分类-实体关联表。
-- 多选引用、没有专用列的引用不变：仍写实体关系表，指向分类即实体时仍写分类-实体关联行。
--
-- 本迁移做的事（所有租户，一次性）：
--   1. 每个单值引用专用列建索引（WHERE deleted = false），保证按列筛选、反查不扫全表。
--   2. 删除这些字段在实体关系表里的镜像行（列为准；与列不一致的少量旧行是创建时的残留）。
--   3. 删除列已能说明的分类-实体关联行：列指向的记录绑了分类时，记录本来就靠这一列落在该分类下。
--   4. 删除指向已物理不存在记录的分类-实体关联行。
--
-- 物理表名与运行时同一规则：dedicated_table_name 已带 _t{租户} 后缀就原样用，否则补当前租户后缀。
-- 元数据指到别的租户表的目录（历史配置错误）跳过，不据此判断该租户的数据。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    r record;
    t record;
    subject_codes text[];
    target_codes text[];
    der_table text;
    ecr_table text;
    link_table text;
    n_mismatch bigint;
    n_der bigint;
    n_ecr bigint;
BEGIN
    FOR r IN
        SELECT bf.tenant_id,
               bf.entity_type_code AS storage_code,
               bf.field_code,
               lower(replace(bf.field_code, '-', '_')) AS col,
               (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END) AS tbl,
               split_part(f.provider_code, ':', 2) AS target_code
        FROM dynamicbusiness.dynamic_entity_type_base_field bf
        JOIN dynamicbusiness.dynamic_entity_type et
          ON et.code = bf.entity_type_code AND et.tenant_id = bf.tenant_id AND et.deleted = false
        JOIN dynamicbusiness.dynamic_field f ON f.id = bf.library_field_id
        WHERE bf.deleted = false
          AND bf.status = 1
          AND upper(bf.data_type) IN ('REF', 'ENTITY_REF', 'REFERENCE')
          AND f.provider_code LIKE 'dynamic-entity:%'
          AND coalesce(et.dedicated_table_name, '') <> ''
          AND (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END) ~ ('_t' || et.tenant_id || '$')
          AND lower(replace(bf.field_code, '-', '_')) ~ '^[a-z][a-z0-9_]*$'
          AND EXISTS (SELECT 1 FROM information_schema.columns c
                      WHERE c.table_schema = 'dynamicbusiness'
                        AND c.table_name = (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END)
                        AND c.column_name = lower(replace(bf.field_code, '-', '_')))
        ORDER BY bf.tenant_id, bf.entity_type_code, bf.field_code
    LOOP
        der_table := 'dynamic_entity_relation_t' || r.tenant_id;
        ecr_table := 'dynamic_entity_category_relation_t' || r.tenant_id;
        link_table := 'dynamic_category_entity_link_t' || r.tenant_id;
        IF to_regclass('dynamicbusiness.' || der_table) IS NULL
           OR to_regclass('dynamicbusiness.' || ecr_table) IS NULL
           OR to_regclass('dynamicbusiness.' || link_table) IS NULL THEN
            CONTINUE;
        END IF;

        SELECT array_agg(DISTINCT code) INTO subject_codes
        FROM (SELECT r.storage_code AS code
              UNION SELECT code FROM dynamicbusiness.dynamic_entity_type
                    WHERE tenant_id = r.tenant_id AND deleted = false AND base_entity_type_code = r.storage_code) s;
        SELECT array_agg(DISTINCT code) INTO target_codes
        FROM (SELECT r.target_code AS code
              UNION SELECT code FROM dynamicbusiness.dynamic_entity_type
                    WHERE tenant_id = r.tenant_id AND deleted = false AND base_entity_type_code = r.target_code) s;

        -- 1. 单值引用列建反查索引
        EXECUTE format('CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (%I) WHERE deleted = false',
                       'idx_' || replace(r.tbl, '-', '_') || '_' || r.col, r.tbl, r.col);

        -- 2. 实体关系表里与列不一致的镜像行（列为准，只报数）
        EXECUTE format(
            'SELECT count(*) FROM dynamicbusiness.%I d JOIN dynamicbusiness.%I e ON e.id = d.source_entity_id
             WHERE d.field_code = $1 AND d.relation_type IN (''ENTITY_REF'', ''ENTITY_REF_MULTI'')
               AND d.source_entity_type_code = ANY($2) AND e.%I IS DISTINCT FROM d.target_entity_id',
            der_table, r.tbl, r.col)
        INTO n_mismatch USING r.field_code, subject_codes;

        -- 3. 删除该字段在实体关系表的镜像行
        EXECUTE format(
            'DELETE FROM dynamicbusiness.%I d
             WHERE d.field_code = $1 AND d.relation_type IN (''ENTITY_REF'', ''ENTITY_REF_MULTI'')
               AND d.source_entity_type_code = ANY($2)',
            der_table)
        USING r.field_code, subject_codes;
        GET DIAGNOSTICS n_der = ROW_COUNT;

        -- 4. 删除列已能说明的分类-实体关联行（列指向的分类即实体所绑的分类）
        EXECUTE format(
            'DELETE FROM dynamicbusiness.%I ecr
             USING dynamicbusiness.%I e, dynamicbusiness.%I l
             WHERE ecr.entity_type_code = ANY($1)
               AND e.id = ecr.entity_id AND e.%I IS NOT NULL
               AND l.entity_id = e.%I AND l.entity_type_code = ANY($2) AND l.deleted = false
               AND l.category_id = ecr.category_id',
            ecr_table, r.tbl, link_table, r.col, r.col)
        USING subject_codes, target_codes;
        GET DIAGNOSTICS n_ecr = ROW_COUNT;

        IF n_mismatch > 0 OR n_der > 0 OR n_ecr > 0 THEN
            RAISE NOTICE 't% %.% -> %: mismatch=% der_deleted=% ecr_deleted=%',
                r.tenant_id, r.storage_code, r.field_code, r.target_code, n_mismatch, n_der, n_ecr;
        END IF;
    END LOOP;

    -- 5. 删除指向已不存在记录的分类-实体关联行（记录行已物理不存在）
    FOR t IN
        SELECT et.tenant_id, et.code, (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END) AS tbl
        FROM dynamicbusiness.dynamic_entity_type et
        WHERE et.deleted = false AND coalesce(et.dedicated_table_name, '') <> ''
          AND (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END) ~ ('_t' || et.tenant_id || '$')
          AND to_regclass('dynamicbusiness.' || (CASE WHEN et.dedicated_table_name ~ '_t[0-9]+$' THEN et.dedicated_table_name ELSE et.dedicated_table_name || '_t' || et.tenant_id END)) IS NOT NULL
          AND to_regclass('dynamicbusiness.dynamic_entity_category_relation_t' || et.tenant_id) IS NOT NULL
    LOOP
        EXECUTE format(
            'DELETE FROM dynamicbusiness.%I ecr
             WHERE ecr.entity_type_code = $1
               AND NOT EXISTS (SELECT 1 FROM dynamicbusiness.%I e WHERE e.id = ecr.entity_id)',
            'dynamic_entity_category_relation_t' || t.tenant_id, t.tbl)
        USING t.code;
        GET DIAGNOSTICS n_ecr = ROW_COUNT;
        IF n_ecr > 0 THEN
            RAISE NOTICE 't% % dangling ecr_deleted=%', t.tenant_id, t.code, n_ecr;
        END IF;
    END LOOP;
END $$;
