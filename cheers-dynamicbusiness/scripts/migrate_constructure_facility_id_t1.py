"""
租户1 构筑物（Constructure / ent_constructure_t1）补所属站场并回填金桥。

原因：work_scope=FACILITY 列表按 facility_id 过滤，但类型无基础字段/无列 → 全部被滤掉。
"""
import psycopg2

DSN = dict(host="127.0.0.1", dbname="sinopec", user="postgres", password="Coolhomer")
JINQIAO_ID = 44
ENTITY_TYPE = "Constructure"
TENANT_ID = 1


def main():
    c = psycopg2.connect(**DSN)
    c.autocommit = False
    cur = c.cursor()
    cur.execute("SET search_path TO dynamicbusiness")

    cur.execute(
        """
        SELECT 1 FROM information_schema.columns
        WHERE table_schema='dynamicbusiness' AND table_name='ent_constructure_t1'
          AND column_name='facility_id'
        """
    )
    if cur.fetchone() is None:
        cur.execute(
            "ALTER TABLE ent_constructure_t1 ADD COLUMN facility_id BIGINT"
        )
        print("ADD COLUMN facility_id")
    else:
        print("column facility_id already exists")

    cur.execute(
        """
        UPDATE ent_constructure_t1
        SET facility_id = %s,
            updater = 'migrate-facility-owning',
            update_time = NOW()
        WHERE deleted = false
          AND (facility_id IS NULL OR facility_id <> %s)
        """,
        (JINQIAO_ID, JINQIAO_ID),
    )
    print("backfill facility_id→金桥:", cur.rowcount)

    cur.execute(
        """
        SELECT id FROM dynamic_field
        WHERE deleted=false AND tenant_id=%s AND code='facility_id'
        LIMIT 1
        """,
        (TENANT_ID,),
    )
    lib = cur.fetchone()
    if not lib:
        raise SystemExit("missing dynamic_field facility_id for tenant 1")
    lib_id = lib[0]

    cur.execute(
        """
        INSERT INTO dynamic_entity_type_base_field (
          entity_type_code, library_field_id, field_code, field_name, data_type,
          required, default_value, description, type_config, sort_order, status,
          tenant_id, creator, is_searchable, is_filterable, is_sortable
        )
        SELECT
          %s, %s, 'facility_id', '所属站场', 'REF',
          TRUE, NULL, '站场级归属；创建写入、编辑只读', NULL, 5, 1,
          %s, 'migrate', FALSE, TRUE, TRUE
        WHERE NOT EXISTS (
          SELECT 1 FROM dynamic_entity_type_base_field b
          WHERE b.deleted=false AND b.tenant_id=%s
            AND b.entity_type_code=%s AND b.field_code='facility_id'
        )
        """,
        (ENTITY_TYPE, lib_id, TENANT_ID, TENANT_ID, ENTITY_TYPE),
    )
    print("base field insert rows:", cur.rowcount)

    cur.execute(
        """
        INSERT INTO dynamic_model_field_assignment (
          model_id, field_id, model_code, field_code,
          required, is_searchable, is_filterable, is_sortable, sort,
          default_value, target_entity_type, field_source, tenant_id, creator
        )
        SELECT
          m.id, f.id, m.code, f.code,
          true, false, true, true, 5,
          NULL, 'facility', 'BASE', %s, 'migrate'
        FROM dynamic_model m
        JOIN dynamic_field f
          ON f.deleted=false AND f.tenant_id=%s AND f.code='facility_id'
        WHERE m.deleted=false AND m.tenant_id=%s AND m.entity_type_code=%s
          AND NOT EXISTS (
            SELECT 1 FROM dynamic_model_field_assignment a
            WHERE a.deleted=false AND a.tenant_id=%s
              AND a.model_id=m.id AND a.field_id=f.id
          )
        """,
        (TENANT_ID, TENANT_ID, TENANT_ID, ENTITY_TYPE, TENANT_ID),
    )
    print("model assignment insert rows:", cur.rowcount)

    cur.execute(
        """
        SELECT facility_id, count(*) FROM ent_constructure_t1
        WHERE deleted=false GROUP BY facility_id
        """
    )
    print("dist:", cur.fetchall())

    c.commit()
    print("COMMIT OK")
    c.close()


if __name__ == "__main__":
    main()
