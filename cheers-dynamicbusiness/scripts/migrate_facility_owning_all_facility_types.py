#!/usr/bin/env python3
"""
为所有已创建的站场级（work_scope=FACILITY）NATIVE/CATEGORY 类型补齐：
1) 物理表 facility_id 列（含 _t{tenant} 与遗留无后缀表）
2) dynamic_entity_type_base_field 基础字段行
3) dynamic_model_field_assignment 型号分配

不回填业务数据行（避免误把空表/跨站数据写成某一站）。
幂等可重复执行。
"""
from __future__ import annotations

import psycopg2

DSN = dict(host="127.0.0.1", dbname="sinopec", user="postgres", password="Coolhomer")
SKIP_CODES = {"facility"}


def physical_candidates(code: str, tenant_id: int, dedicated: str | None) -> list[str]:
    base = "ent_" + code.lower().replace("-", "_")
    names = []
    if dedicated and dedicated.strip():
        names.append(dedicated.strip().lower())
    names.append(f"{base}_t{tenant_id}")
    names.append(base)
    # unique preserve order
    seen = set()
    out = []
    for n in names:
        if n not in seen:
            seen.add(n)
            out.append(n)
    return out


def table_exists(cur, name: str) -> bool:
    cur.execute(
        """
        SELECT 1 FROM information_schema.tables
        WHERE table_schema='dynamicbusiness' AND table_name=%s
        """,
        (name,),
    )
    return cur.fetchone() is not None


def has_column(cur, table: str, col: str) -> bool:
    cur.execute(
        """
        SELECT 1 FROM information_schema.columns
        WHERE table_schema='dynamicbusiness' AND table_name=%s AND column_name=%s
        """,
        (table, col),
    )
    return cur.fetchone() is not None


def main() -> None:
    c = psycopg2.connect(**DSN)
    c.autocommit = False
    cur = c.cursor()
    cur.execute("SET search_path TO dynamicbusiness")

    cur.execute(
        """
        SELECT code, tenant_id, storage_type, dedicated_table_name
        FROM dynamic_entity_type
        WHERE deleted = false
          AND COALESCE(NULLIF(work_scope, ''), 'FACILITY') = 'FACILITY'
          AND entry_kind IN ('NATIVE', 'CATEGORY')
        ORDER BY tenant_id, code
        """
    )
    types = cur.fetchall()
    added_cols = 0
    added_bf = 0
    added_asg = 0

    for code, tenant_id, storage_type, dedicated in types:
        if code.lower() in SKIP_CODES:
            continue
        if (storage_type or "").upper() != "DEDICATED":
            print(f"skip non-DEDICATED {code} tenant={tenant_id} storage={storage_type}")
            continue

        cur.execute(
            """
            SELECT id FROM dynamic_field
            WHERE deleted=false AND tenant_id=%s AND code='facility_id'
            LIMIT 1
            """,
            (tenant_id,),
        )
        lib = cur.fetchone()
        if not lib:
            print(f"WARN missing library facility_id tenant={tenant_id}, skip type={code}")
            continue
        lib_id = lib[0]

        for table in physical_candidates(code, tenant_id, dedicated):
            if not table_exists(cur, table):
                continue
            if has_column(cur, table, "facility_id"):
                continue
            cur.execute(f'ALTER TABLE "{table}" ADD COLUMN facility_id BIGINT')
            added_cols += 1
            print(f"ADD COLUMN {table}.facility_id")

        cur.execute(
            """
            INSERT INTO dynamic_entity_type_base_field (
              entity_type_code, library_field_id, field_code, field_name, data_type,
              required, default_value, description, type_config, sort_order, status,
              tenant_id, creator, is_searchable, is_filterable, is_sortable
            )
            SELECT
              %s, %s, 'facility_id', '所属场站', 'REF',
              TRUE, NULL, '站场级归属；创建写入、编辑只读', NULL, 5, 1,
              %s, 'migrate-facility-owning-all', FALSE, TRUE, TRUE
            WHERE NOT EXISTS (
              SELECT 1 FROM dynamic_entity_type_base_field b
              WHERE b.deleted=false AND b.tenant_id=%s
                AND b.entity_type_code=%s AND b.field_code='facility_id'
            )
            """,
            (code, lib_id, tenant_id, tenant_id, code),
        )
        if cur.rowcount:
            added_bf += cur.rowcount
            print(f"base field {code} tenant={tenant_id}")

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
              NULL, 'facility', 'SYSTEM', %s, 'migrate-facility-owning-all'
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
            (tenant_id, tenant_id, tenant_id, code, tenant_id),
        )
        if cur.rowcount:
            added_asg += cur.rowcount
            print(f"model asg {code} tenant={tenant_id} rows={cur.rowcount}")

    cur.execute(
        """
        UPDATE dynamic_model_field_assignment
        SET field_source = 'SYSTEM', updater = 'migrate-facility-owning-all'
        WHERE deleted = false AND field_code = 'facility_id' AND field_source = 'BASE'
        """
    )
    fixed_asg = cur.rowcount
    c.commit()
    print(f"COMMIT OK cols={added_cols} base_fields={added_bf} assignments={added_asg} fixed_field_source={fixed_asg}")
    c.close()


if __name__ == "__main__":
    main()
