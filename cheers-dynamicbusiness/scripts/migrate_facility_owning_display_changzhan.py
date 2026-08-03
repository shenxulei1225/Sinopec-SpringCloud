#!/usr/bin/env python3
"""本地立即统一 facility_id 展示名为「所属场站」（与 V49 一致，幂等）。"""
import psycopg2

c = psycopg2.connect(host="127.0.0.1", dbname="sinopec", user="postgres", password="Coolhomer")
c.autocommit = False
cur = c.cursor()
cur.execute("SET search_path TO dynamicbusiness")

cur.execute(
    """
    UPDATE dynamic_field
    SET name = '所属场站',
        description = COALESCE(NULLIF(btrim(description), ''), '站场级业务归属设施；列存 facility 实体 id'),
        provider_code = COALESCE(NULLIF(btrim(provider_code), ''), 'dynamic-entity:facility'),
        type = 'ENTITY_REF',
        updater = 'migrate-display-changzhan',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND code = 'facility_id'
      AND (name IS DISTINCT FROM '所属场站'
           OR COALESCE(provider_code, '') <> 'dynamic-entity:facility'
           OR type IS DISTINCT FROM 'ENTITY_REF')
    """
)
print("library rows:", cur.rowcount)

cur.execute(
    """
    UPDATE dynamic_entity_type_base_field
    SET field_name = '所属场站',
        updater = 'migrate-display-changzhan',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND field_code = 'facility_id'
      AND field_name IS DISTINCT FROM '所属场站'
    """
)
print("base field rows:", cur.rowcount)

cur.execute(
    """
    SELECT tenant_id, name, type, provider_code
    FROM dynamic_field
    WHERE deleted=false AND code='facility_id'
    ORDER BY tenant_id
    """
)
print("library after:", cur.fetchall())
cur.execute(
    """
    SELECT DISTINCT field_name, count(*)
    FROM dynamic_entity_type_base_field
    WHERE deleted=false AND field_code='facility_id'
    GROUP BY 1
    """
)
print("base names:", cur.fetchall())
c.commit()
print("COMMIT OK")
c.close()
