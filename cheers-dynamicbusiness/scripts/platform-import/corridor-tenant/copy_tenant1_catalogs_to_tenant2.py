# -*- coding: utf-8 -*-
"""
把租户 1 已配置、租户 2 还没有的数据目录复制到租户 2。

保留租户 2 已有的四本目录：管廊设施、分区、运营区域、构筑物。
不改这四本的专用表、布局、型号和台账。

复制目录注册、分组、字段、布局、栏位、型号、分类、表单、组件配置，
以及这些目录专用表里的台账行。

站场级台账的 facility_id 改挂租户 2 现有的那一座管廊设施。
zone_id、region_id 清空：它们指向租户 1 的分区和区域。
"""

from __future__ import annotations

import psycopg2
from psycopg2.extras import Json

SRC = 1
DST = 2
KEEP_CODES = ("facility", "zone", "region", "structure")
SCHEMA = "dynamicbusiness"


def main() -> None:
    conn = psycopg2.connect(
        host="127.0.0.1",
        dbname="sinopec",
        user="postgres",
        password="Coolhomer",
    )
    conn.autocommit = False
    cur = conn.cursor()
    cur.execute("SET statement_timeout = '15min'")
    cur.execute("SET lock_timeout = '30s'")
    try:
        copy_catalogs(cur)
        conn.commit()
        print("COMMITTED")
    except Exception:
        conn.rollback()
        print("ROLLED BACK")
        raise
    finally:
        cur.close()
        conn.close()


def copy_catalogs(cur) -> None:
    cur.execute("SET session_replication_role = replica")
    cur.execute(
        """
        CREATE TEMP TABLE copy_idmap (
            kind text NOT NULL,
            old_id bigint NOT NULL,
            new_id bigint NOT NULL,
            PRIMARY KEY (kind, old_id)
        ) ON COMMIT DROP
        """
    )

    copy_codes = load_copy_codes(cur)
    print("copy_codes", len(copy_codes))
    facility_id = load_destination_facility_id(cur)
    print("destination_facility_id", facility_id)

    copy_groups(cur)
    copy_fields(cur)
    copy_props(cur, copy_codes)
    layout_ids = collect_layout_ids(cur, copy_codes)
    copy_layouts(cur, layout_ids)
    copy_entity_types(cur, copy_codes)
    copy_by_entity_codes(
        cur,
        "dynamic_entity_type_base_field",
        copy_codes,
        rewrites={"library_field_id": "field"},
    )
    copy_by_entity_codes(cur, "dynamic_entity_type_config", copy_codes, rewrites={})
    rewrite_dedicated_names(cur, "dynamic_entity_type_config")
    copy_by_entity_codes(cur, "dynamic_entity_type_capability", copy_codes, rewrites={})
    copy_type_relations(cur, copy_codes)
    copy_by_entity_codes(cur, "dm_catalog_orchestration", copy_codes, rewrites={})
    copy_data_tab_layouts(cur, copy_codes)
    copy_column_relations(cur, copy_codes)
    copy_models(cur, copy_codes)
    copy_by_entity_codes(
        cur,
        "dm_model_tab_category",
        copy_codes,
        rewrites={"model_id": "model"},
        optional=True,
    )
    copy_model_assignments(cur, copy_codes)
    copy_category_types(cur, copy_codes)
    copy_categories(cur, copy_codes)
    fix_category_parents(cur)
    copy_crud_forms(cur)
    copy_scopes(cur, copy_codes)
    copied_tables = copy_physical_tables(cur, copy_codes, facility_id)
    copy_association_tables(cur, copy_codes)
    bump_sequences(cur, copied_tables)
    verify(cur, copy_codes)
    cur.execute("SET session_replication_role = origin")


def load_copy_codes(cur) -> list[str]:
    cur.execute(
        f"""
        SELECT code FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false
          AND code <> ALL(%s)
          AND code NOT IN (
            SELECT code FROM {SCHEMA}.dynamic_entity_type
            WHERE tenant_id = %s AND deleted = false
          )
        ORDER BY code
        """,
        (SRC, list(KEEP_CODES), DST),
    )
    return [row[0] for row in cur.fetchall()]


def load_destination_facility_id(cur) -> int:
    cur.execute(
        f"""
        SELECT id FROM {SCHEMA}.ent_facility_t2
        WHERE deleted = false
        ORDER BY id
        LIMIT 1
        """
    )
    row = cur.fetchone()
    if row is None:
        raise RuntimeError("租户 2 没有管廊设施，无法挂站场级台账")
    return int(row[0])


def columns_of(cur, table: str, schema: str = SCHEMA) -> list[tuple[str, str]]:
    cur.execute(
        """
        SELECT column_name, data_type
        FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s
        ORDER BY ordinal_position
        """,
        (schema, table),
    )
    return [(name, data_type) for name, data_type in cur.fetchall()]


def id_sequence(cur, schema: str, table: str) -> str | None:
    """主键序列以列默认值里的 nextval 为准。没有 OWNER 的序列，pg_get_serial_sequence 会返回空。"""
    cur.execute(
        """
        SELECT pg_get_expr(ad.adbin, ad.adrelid)
        FROM pg_attrdef ad
        JOIN pg_attribute a ON a.attrelid = ad.adrelid AND a.attnum = ad.adnum
        JOIN pg_class c ON c.oid = ad.adrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = %s AND c.relname = %s AND a.attname = 'id'
        """,
        (schema, table),
    )
    row = cur.fetchone()
    if not row or not row[0] or "nextval('" not in row[0]:
        return None
    return row[0].split("'")[1]


def next_id(cur, table: str, schema: str = SCHEMA) -> int:
    seq = id_sequence(cur, schema, table)
    if not seq:
        raise RuntimeError(f"{schema}.{table} 没有 id 序列，不能用手写编号迁移")
    cur.execute("SELECT nextval(%s)", (seq,))
    return int(cur.fetchone()[0])


def remember(cur, kind: str, old_id, new_id) -> None:
    if old_id is None or new_id is None:
        return
    cur.execute(
        """
        INSERT INTO copy_idmap (kind, old_id, new_id)
        VALUES (%s, %s, %s)
        ON CONFLICT (kind, old_id) DO NOTHING
        """,
        (kind, int(old_id), int(new_id)),
    )


def mapped(cur, kind: str, old_id):
    if old_id is None:
        return None
    cur.execute(
        "SELECT new_id FROM copy_idmap WHERE kind = %s AND old_id = %s",
        (kind, int(old_id)),
    )
    row = cur.fetchone()
    return int(row[0]) if row else None


def adapt(value, data_type: str):
    if value is None:
        return None
    if data_type in ("jsonb", "json"):
        if isinstance(value, (dict, list)):
            return Json(value)
        return value
    return value


def insert_copy(cur, table: str, row: dict, col_types: dict, schema: str = SCHEMA) -> None:
    cols = list(row.keys())
    values = [adapt(row[col], col_types[col]) for col in cols]
    collist = ", ".join(cols)
    placeholders = ", ".join(["%s"] * len(cols))
    cur.execute(
        f"INSERT INTO {schema}.{table} ({collist}) VALUES ({placeholders})",
        values,
    )


def rewrite_dedicated_table_name(name: str | None, code: str) -> str | None:
    if name is None or not str(name).strip():
        safe = code.strip().lower().replace("-", "_")
        return f"ent_{safe}_t{DST}"
    text = str(name).strip()
    if text.endswith(f"_t{SRC}"):
        return text[: -len(str(SRC))] + str(DST)
    if text.endswith(f"_t{DST}"):
        return text
    return text


def copy_groups(cur) -> None:
    col_types = dict(columns_of(cur, "dynamic_group"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_group
        WHERE tenant_id = %s AND deleted = false
        ORDER BY level NULLS FIRST, id
        """,
        (SRC,),
    )
    src_cols = [desc[0] for desc in cur.description]
    rows = [dict(zip(src_cols, row)) for row in cur.fetchall()]
    copied = 0
    for row in rows:
        cur.execute(
            f"""
            SELECT id FROM {SCHEMA}.dynamic_group
            WHERE tenant_id = %s AND deleted = false
              AND group_type = %s AND name = %s
            LIMIT 1
            """,
            (DST, row["group_type"], row["name"]),
        )
        existing = cur.fetchone()
        if existing:
            remember(cur, "group", row["id"], existing[0])
            continue
        new_id = next_id(cur, "dynamic_group")
        code = row["code"]
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_group
            WHERE tenant_id = %s AND deleted = false
              AND group_type = %s AND code = %s
            """,
            (DST, row["group_type"], code),
        )
        if cur.fetchone():
            code = f"{code}-from-t1"
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["code"] = code
        payload["parent_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_group", payload, col_types)
        remember(cur, "group", row["id"], new_id)
        copied += 1
    for row in rows:
        new_id = mapped(cur, "group", row["id"])
        parent = mapped(cur, "group", row["parent_id"])
        if new_id and parent:
            cur.execute(
                f"UPDATE {SCHEMA}.dynamic_group SET parent_id = %s WHERE id = %s",
                (parent, new_id),
            )
    print("groups_inserted", copied)


def copy_fields(cur) -> None:
    col_types = dict(columns_of(cur, "dynamic_field"))
    cur.execute(
        f"SELECT * FROM {SCHEMA}.dynamic_field WHERE tenant_id = %s AND deleted = false ORDER BY id",
        (SRC,),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        cur.execute(
            f"""
            SELECT id FROM {SCHEMA}.dynamic_field
            WHERE tenant_id = %s AND deleted = false AND code = %s
            LIMIT 1
            """,
            (DST, row["code"]),
        )
        existing = cur.fetchone()
        if existing:
            remember(cur, "field", row["id"], existing[0])
            continue
        new_id = next_id(cur, "dynamic_field")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["origin_facility_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_field", payload, col_types)
        remember(cur, "field", row["id"], new_id)
        copied += 1
    print("fields_inserted", copied)


def copy_props(cur, copy_codes: list[str]) -> None:
    """
    展示配置表不按租户过滤读取。
    数据目录栏位上的 props_id 经常指向 tenant_id=0 的配置。
    只复制 tenant_id=1 会让栏位编号对不上，打开数据页就显示缺少展示配置。
    这里把租户 1 目录栏位引用到的配置一并复制，不论它记在哪个租户上。
    """
    col_types = dict(columns_of(cur, "pr_component_props", "platformresource"))
    cur.execute(
        """
        SELECT DISTINCT p.*
        FROM platformresource.pr_component_props p
        WHERE p.deleted = false
          AND (
            p.tenant_id = %s
            OR p.id IN (
                SELECT props_id
                FROM dynamicbusiness.dm_data_tab_layout
                WHERE tenant_id = %s
                  AND deleted = false
                  AND entity_type_code = ANY(%s)
                  AND props_id IS NOT NULL
                  AND props_id > 0
            )
          )
        ORDER BY p.id
        """,
        (SRC, SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    rows = [dict(zip(src_cols, raw)) for raw in cur.fetchall()]
    copied = 0
    for row in rows:
        new_id = next_id(cur, "pr_component_props", "platformresource")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "pr_component_props", payload, col_types, "platformresource")
        remember(cur, "props", row["id"], new_id)
        copied += 1
    for row in rows:
        new_id = mapped(cur, "props", row["id"])
        template = mapped(cur, "props", row["template_id"])
        if new_id and template:
            cur.execute(
                "UPDATE platformresource.pr_component_props SET template_id = %s WHERE id = %s",
                (template, new_id),
            )
    print("props_inserted", copied)


def collect_layout_ids(cur, copy_codes: list[str]) -> list[int]:
    cur.execute(
        f"""
        SELECT data_layout_id FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s) AND data_layout_id IS NOT NULL
        UNION
        SELECT model_layout_id FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s) AND model_layout_id IS NOT NULL
        UNION
        SELECT layout_id FROM {SCHEMA}.dm_data_tab_layout
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = ANY(%s) AND layout_id IS NOT NULL
        UNION
        SELECT layout_id FROM {SCHEMA}.dm_data_tab_column_relation
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = ANY(%s) AND layout_id IS NOT NULL
        """,
        (SRC, copy_codes, SRC, copy_codes, SRC, copy_codes, SRC, copy_codes),
    )
    return sorted({int(row[0]) for row in cur.fetchall()})


def copy_layouts(cur, layout_ids: list[int]) -> None:
    if not layout_ids:
        print("layouts_inserted", 0)
        return
    col_types = dict(columns_of(cur, "dm_workbench_layout"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dm_workbench_layout
        WHERE tenant_id = %s AND deleted = false AND id = ANY(%s)
        ORDER BY id
        """,
        (SRC, layout_ids),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        new_id = next_id(cur, "dm_workbench_layout")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["source_template_id"] = mapped(cur, "layout", row["source_template_id"])
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dm_workbench_layout", payload, col_types)
        remember(cur, "layout", row["id"], new_id)
        copied += 1
    print("layouts_inserted", copied)


def copy_entity_types(cur, copy_codes: list[str]) -> None:
    col_types = dict(columns_of(cur, "dynamic_entity_type"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s)
        ORDER BY id
        """,
        (SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        new_id = next_id(cur, "dynamic_entity_type")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["parent_id"] = None
        payload["data_layout_id"] = mapped(cur, "layout", row["data_layout_id"])
        payload["model_layout_id"] = mapped(cur, "layout", row["model_layout_id"])
        payload["dedicated_table_name"] = rewrite_dedicated_table_name(
            row["dedicated_table_name"], row["code"]
        )
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_entity_type", payload, col_types)
        remember(cur, "entity_type", row["id"], new_id)
        copied += 1
    print("entity_types_inserted", copied)


def copy_by_entity_codes(cur, table: str, copy_codes: list[str], rewrites: dict, optional: bool = False) -> None:
    names = [name for name, _ in columns_of(cur, table)]
    if not names:
        if optional:
            print(table, "missing")
            return
        raise RuntimeError(f"表不存在: {table}")
    if "entity_type_code" not in names:
        if optional:
            print(table, "no entity_type_code")
            return
        raise RuntimeError(f"{table} 没有 entity_type_code")
    col_types = dict(columns_of(cur, table))
    deleted_sql = "AND deleted = false" if "deleted" in names else ""
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.{table}
        WHERE tenant_id = %s {deleted_sql} AND entity_type_code = ANY(%s)
        ORDER BY id
        """,
        (SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        new_id = next_id(cur, table)
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        if "creator" in payload:
            payload["creator"] = "copy-from-tenant-1"
        if "layout_id" in payload:
            payload["layout_id"] = mapped(cur, "layout", row["layout_id"])
        if "props_id" in payload and row.get("props_id"):
            mapped_props = mapped(cur, "props", row["props_id"])
            if mapped_props is None:
                raise RuntimeError(
                    f"{table} id={row['id']} 的展示配置 {row['props_id']} 没有复制到租户 {DST}"
                )
            payload["props_id"] = mapped_props
        for column, kind in rewrites.items():
            if column in payload:
                payload[column] = mapped(cur, kind, row[column])
        if "dedicated_table_name" in payload:
            payload["dedicated_table_name"] = rewrite_dedicated_table_name(
                row["dedicated_table_name"], row["entity_type_code"]
            )
        insert_copy(cur, table, payload, col_types)
        copied += 1
    print(table, copied)


def rewrite_dedicated_names(cur, table: str) -> None:
    cur.execute(
        f"""
        UPDATE {SCHEMA}.{table}
        SET dedicated_table_name = regexp_replace(dedicated_table_name, '_t{SRC}$', '_t{DST}')
        WHERE tenant_id = %s
          AND creator = 'copy-from-tenant-1'
          AND dedicated_table_name ~ '_t{SRC}$'
        """,
        (DST,),
    )


def copy_type_relations(cur, copy_codes: list[str]) -> None:
    available = list(copy_codes) + list(KEEP_CODES)
    col_types = dict(columns_of(cur, "dynamic_entity_type_relation"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_entity_type_relation
        WHERE tenant_id = %s AND deleted = false
          AND source_entity_type_code = ANY(%s)
          AND target_entity_type_code = ANY(%s)
        ORDER BY id
        """,
        (SRC, available, available),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        if row["source_entity_type_code"] not in copy_codes and row["target_entity_type_code"] not in copy_codes:
            continue
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_entity_type_relation
            WHERE tenant_id = %s AND deleted = false
              AND source_entity_type_code = %s
              AND target_entity_type_code = %s
              AND relation_name IS NOT DISTINCT FROM %s
            """,
            (DST, row["source_entity_type_code"], row["target_entity_type_code"], row["relation_name"]),
        )
        if cur.fetchone():
            continue
        new_id = next_id(cur, "dynamic_entity_type_relation")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_entity_type_relation", payload, col_types)
        copied += 1
    print("entity_type_relations", copied)


def copy_data_tab_layouts(cur, copy_codes: list[str]) -> None:
    copy_by_entity_codes(cur, "dm_data_tab_layout", copy_codes, rewrites={})


def copy_column_relations(cur, copy_codes: list[str]) -> None:
    copy_by_entity_codes(cur, "dm_data_tab_column_relation", copy_codes, rewrites={})


def copy_models(cur, copy_codes: list[str]) -> None:
    col_types = dict(columns_of(cur, "dynamic_model"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_model
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = ANY(%s)
        ORDER BY id
        """,
        (SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        cur.execute(
            f"""
            SELECT id FROM {SCHEMA}.dynamic_model
            WHERE tenant_id = %s AND deleted = false AND code = %s
            LIMIT 1
            """,
            (DST, row["code"]),
        )
        existing = cur.fetchone()
        if existing:
            remember(cur, "model", row["id"], existing[0])
            continue
        new_id = next_id(cur, "dynamic_model")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["origin_facility_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_model", payload, col_types)
        remember(cur, "model", row["id"], new_id)
        copied += 1
    print("models_inserted", copied)


def copy_model_assignments(cur, copy_codes: list[str]) -> None:
    col_types = dict(columns_of(cur, "dynamic_model_field_assignment"))
    cur.execute(
        f"""
        SELECT a.*
        FROM {SCHEMA}.dynamic_model_field_assignment a
        JOIN {SCHEMA}.dynamic_model m ON m.id = a.model_id
        WHERE a.tenant_id = %s AND a.deleted = false
          AND m.entity_type_code = ANY(%s)
          AND m.deleted = false
        ORDER BY a.id
        """,
        (SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    skipped = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        model_id = mapped(cur, "model", row["model_id"])
        field_id = mapped(cur, "field", row["field_id"])
        if model_id is None or field_id is None:
            skipped += 1
            continue
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_model_field_assignment
            WHERE tenant_id = %s AND deleted = false
              AND model_code = %s AND field_code = %s
            """,
            (DST, row["model_code"], row["field_code"]),
        )
        if cur.fetchone():
            skipped += 1
            continue
        new_id = next_id(cur, "dynamic_model_field_assignment")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["model_id"] = model_id
        payload["field_id"] = field_id
        payload["field_group_id"] = mapped(cur, "group", row["field_group_id"])
        payload["model_relation_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_model_field_assignment", payload, col_types)
        copied += 1
    print("assignments_inserted", copied, "skipped", skipped)


def category_type_codes(cur, copy_codes: list[str]) -> list[str]:
    cur.execute(
        f"""
        SELECT DISTINCT category_type_code
        FROM {SCHEMA}.dynamic_category
        WHERE tenant_id = %s AND deleted = false
          AND category_type_code = ANY(%s)
        """,
        (SRC, copy_codes),
    )
    found = {row[0] for row in cur.fetchall() if row[0]}
    found.update(copy_codes)
    found.difference_update(KEEP_CODES)
    return sorted(found)


def copy_category_types(cur, copy_codes: list[str]) -> None:
    codes = category_type_codes(cur, copy_codes)
    col_types = dict(columns_of(cur, "dynamic_category_type"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_category_type
        WHERE tenant_id = %s AND deleted = false AND category_type_code = ANY(%s)
        ORDER BY id
        """,
        (SRC, codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        cur.execute(
            f"""
            SELECT id FROM {SCHEMA}.dynamic_category_type
            WHERE tenant_id = %s AND deleted = false AND category_type_code = %s
            LIMIT 1
            """,
            (DST, row["category_type_code"]),
        )
        existing = cur.fetchone()
        if existing:
            remember(cur, "category_type", row["id"], existing[0])
            continue
        new_id = next_id(cur, "dynamic_category_type")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["top_level_category_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_category_type", payload, col_types)
        remember(cur, "category_type", row["id"], new_id)
        copied += 1
    print("category_types_inserted", copied)


def copy_categories(cur, copy_codes: list[str]) -> None:
    codes = category_type_codes(cur, copy_codes)
    col_types = dict(columns_of(cur, "dynamic_category"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_category
        WHERE tenant_id = %s AND deleted = false AND category_type_code = ANY(%s)
        ORDER BY level NULLS FIRST, id
        """,
        (SRC, codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    rows = [dict(zip(src_cols, raw)) for raw in cur.fetchall()]
    copied = 0
    for row in rows:
        cur.execute(
            f"""
            SELECT id FROM {SCHEMA}.dynamic_category
            WHERE tenant_id = %s AND deleted = false AND code = %s
            LIMIT 1
            """,
            (DST, row["code"]),
        )
        existing = cur.fetchone()
        if existing:
            remember(cur, "category", row["id"], existing[0])
            continue
        new_id = next_id(cur, "dynamic_category")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["parent_id"] = None
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_category", payload, col_types)
        remember(cur, "category", row["id"], new_id)
        copied += 1
    print("categories_inserted", copied, "source", len(rows))


def fix_category_parents(cur) -> None:
    cur.execute(
        f"""
        UPDATE {SCHEMA}.dynamic_category AS child
        SET parent_id = parent_map.new_id
        FROM copy_idmap AS self_map
        JOIN {SCHEMA}.dynamic_category AS src
          ON src.id = self_map.old_id
        JOIN copy_idmap AS parent_map
          ON parent_map.kind = 'category' AND parent_map.old_id = src.parent_id
        WHERE self_map.kind = 'category'
          AND child.id = self_map.new_id
          AND child.tenant_id = %s
          AND child.creator = 'copy-from-tenant-1'
          AND src.parent_id IS NOT NULL
        """,
        (DST,),
    )
    cur.execute(
        f"""
        UPDATE {SCHEMA}.dynamic_category_type AS dst_type
        SET top_level_category_id = cat_map.new_id
        FROM copy_idmap AS type_map
        JOIN {SCHEMA}.dynamic_category_type AS src
          ON src.id = type_map.old_id
        JOIN copy_idmap AS cat_map
          ON cat_map.kind = 'category' AND cat_map.old_id = src.top_level_category_id
        WHERE type_map.kind = 'category_type'
          AND dst_type.id = type_map.new_id
          AND dst_type.tenant_id = %s
        """,
        (DST,),
    )


def copy_crud_forms(cur) -> None:
    col_types = dict(columns_of(cur, "model_crud_form_definition"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.model_crud_form_definition
        WHERE tenant_id = %s AND deleted = false
        ORDER BY id
        """,
        (SRC,),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        model_id = mapped(cur, "model", row["model_id"])
        if model_id is None:
            continue
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.model_crud_form_definition
            WHERE tenant_id = %s AND deleted = false AND model_id = %s
            """,
            (DST, model_id),
        )
        if cur.fetchone():
            continue
        new_id = next_id(cur, "model_crud_form_definition")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["model_id"] = model_id
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "model_crud_form_definition", payload, col_types)
        copied += 1
    print("crud_forms_inserted", copied)


def copy_scopes(cur, copy_codes: list[str]) -> None:
    col_types = dict(columns_of(cur, "dynamic_entity_type_scope"))
    cur.execute(
        f"""
        SELECT * FROM {SCHEMA}.dynamic_entity_type_scope
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = ANY(%s)
        ORDER BY id
        """,
        (SRC, copy_codes),
    )
    src_cols = [desc[0] for desc in cur.description]
    copied = 0
    for raw in cur.fetchall():
        row = dict(zip(src_cols, raw))
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_entity_type_scope
            WHERE tenant_id = %s AND deleted = false
              AND entity_type_code = %s AND entity_id = %s
            """,
            (DST, row["entity_type_code"], row["entity_id"]),
        )
        if cur.fetchone():
            continue
        new_id = next_id(cur, "dynamic_entity_type_scope")
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["creator"] = "copy-from-tenant-1"
        insert_copy(cur, "dynamic_entity_type_scope", payload, col_types)
        copied += 1
    print("scopes_inserted", copied)


def resolve_source_table(cur, code: str, stored: str | None) -> str | None:
    candidates = []
    if stored and stored.endswith(f"_t{SRC}"):
        candidates.append(stored)
    safe = code.strip().lower().replace("-", "_")
    candidates.append(f"ent_{safe}_t{SRC}")
    if stored:
        candidates.append(stored)
    seen = set()
    for name in candidates:
        if name in seen:
            continue
        seen.add(name)
        cur.execute("SELECT to_regclass(%s)", (f"{SCHEMA}.{name}",))
        if cur.fetchone()[0]:
            return name
    return None


def target_table_name(source: str, code: str) -> str:
    if source.endswith(f"_t{SRC}"):
        return source[: -len(str(SRC))] + str(DST)
    safe = code.strip().lower().replace("-", "_")
    return f"ent_{safe}_t{DST}"


def copy_physical_tables(cur, copy_codes: list[str], facility_id: int) -> list[str]:
    cur.execute(
        f"""
        SELECT code, entry_kind, base_entity_type_code, dedicated_table_name
        FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s)
        """,
        (DST, copy_codes),
    )
    owners = {}
    for code, kind, base, stored in cur.fetchall():
        owner = base if kind in ("DOMAIN", "REUSE", "SCOPE") and base else code
        if owner in KEEP_CODES:
            continue
        owners.setdefault(owner, stored if code == owner else None)
    # dedicated name on the native row is authoritative
    cur.execute(
        f"""
        SELECT code, dedicated_table_name
        FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s)
          AND entry_kind = 'NATIVE'
        """,
        (SRC, list(owners)),
    )
    stored_by_owner = {code: name for code, name in cur.fetchall()}
    copied_targets = []
    for owner in sorted(owners):
        source = resolve_source_table(cur, owner, stored_by_owner.get(owner))
        if source is None:
            print("physical_missing", owner)
            continue
        target = target_table_name(source, owner)
        if target in ("ent_facility_t2", "ent_zone_t2", "ent_region_t2", "ent_structure_t2"):
            print("physical_skip_kept", target)
            continue
        ensure_target_table(cur, source, target)
        inserted = insert_physical_rows(cur, source, target, facility_id)
        copied_targets.append(target)
        print("physical", source, "->", target, inserted)
    return copied_targets


def ensure_target_table(cur, source: str, target: str) -> None:
    cur.execute("SELECT to_regclass(%s)", (f"{SCHEMA}.{target}",))
    if cur.fetchone()[0] is None:
        cur.execute(
            f"CREATE TABLE {SCHEMA}.{target} (LIKE {SCHEMA}.{source} INCLUDING DEFAULTS)"
        )
        cur.execute(
            """
            SELECT 1 FROM pg_constraint
            WHERE conrelid = %s::regclass AND contype = 'p'
            """,
            (f"{SCHEMA}.{target}",),
        )
        if cur.fetchone() is None:
            cur.execute(f"ALTER TABLE {SCHEMA}.{target} ADD PRIMARY KEY (id)")
    add_missing_columns(cur, source, target)


def add_missing_columns(cur, source: str, target: str) -> None:
    cur.execute(
        """
        SELECT a.attname, format_type(a.atttypid, a.atttypmod)
        FROM pg_attribute a
        JOIN pg_class c ON c.oid = a.attrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = %s AND c.relname = %s AND a.attnum > 0 AND NOT a.attisdropped
        ORDER BY a.attnum
        """,
        (SCHEMA, source),
    )
    source_cols = cur.fetchall()
    cur.execute(
        """
        SELECT a.attname
        FROM pg_attribute a
        JOIN pg_class c ON c.oid = a.attrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = %s AND c.relname = %s AND a.attnum > 0 AND NOT a.attisdropped
        """,
        (SCHEMA, target),
    )
    have = {row[0] for row in cur.fetchall()}
    for name, col_type in source_cols:
        if name in have:
            continue
        cur.execute(f"ALTER TABLE {SCHEMA}.{target} ADD COLUMN {name} {col_type}")


def insert_physical_rows(cur, source: str, target: str, facility_id: int) -> int:
    cur.execute(f"SELECT COUNT(*) FROM {SCHEMA}.{target}")
    if cur.fetchone()[0]:
        return 0
    source_cols = [name for name, _ in columns_of(cur, source)]
    target_cols = [name for name, _ in columns_of(cur, target)]
    common = [name for name in source_cols if name in target_cols]
    collist = ", ".join(common)
    deleted_sql = "WHERE COALESCE(deleted, false) = false" if "deleted" in source_cols else ""
    cur.execute(
        f"INSERT INTO {SCHEMA}.{target} ({collist}) SELECT {collist} FROM {SCHEMA}.{source} {deleted_sql}"
    )
    inserted = cur.rowcount
    if "tenant_id" in target_cols:
        cur.execute(f"UPDATE {SCHEMA}.{target} SET tenant_id = %s", (DST,))
    if "facility_id" in target_cols:
        cur.execute(
            f"UPDATE {SCHEMA}.{target} SET facility_id = %s WHERE facility_id IS NOT NULL",
            (facility_id,),
        )
    for column in ("zone_id", "region_id"):
        if column in target_cols:
            cur.execute(f"UPDATE {SCHEMA}.{target} SET {column} = NULL WHERE {column} IS NOT NULL")
    return inserted


def copy_association_tables(cur, copy_codes: list[str]) -> None:
    pairs = [
        ("dynamic_category_entity_link_t1", "dynamic_category_entity_link_t2"),
        ("dynamic_entity_category_relation_t1", "dynamic_entity_category_relation_t2"),
        ("dynamic_model_category_relation_t1", "dynamic_model_category_relation_t2"),
        ("dynamic_model_entity_relation_t1", "dynamic_model_entity_relation_t2"),
        ("dynamic_model_model_relation_t1", "dynamic_model_model_relation_t2"),
        ("dynamic_category_category_relation_t1", "dynamic_category_category_relation_t2"),
        ("dynamic_entity_relation_t1", "dynamic_entity_relation_t2"),
        ("dynamic_entity_field_index_t1", "dynamic_entity_field_index_t2"),
    ]
    for source, target in pairs:
        cur.execute("SELECT to_regclass(%s)", (f"{SCHEMA}.{source}",))
        if cur.fetchone()[0] is None:
            print("assoc_missing", source)
            continue
        cur.execute("SELECT to_regclass(%s)", (f"{SCHEMA}.{target}",))
        if cur.fetchone()[0] is None:
            cur.execute(
                f"CREATE TABLE {SCHEMA}.{target} (LIKE {SCHEMA}.{source} INCLUDING DEFAULTS)"
            )
            cur.execute(f"ALTER TABLE {SCHEMA}.{target} ADD PRIMARY KEY (id)")
        inserted = insert_association(cur, source, target, copy_codes)
        print("assoc", target, inserted)


def insert_association(cur, source: str, target: str, copy_codes: list[str]) -> int:
    source_cols = [name for name, _ in columns_of(cur, source)]
    target_cols = [name for name, _ in columns_of(cur, target)]
    common = [name for name in source_cols if name in target_cols and name != "id"]
    if not common:
        return 0
    cur.execute(f"SELECT COALESCE(MAX(id), 0) FROM {SCHEMA}.{target}")
    id_base = int(cur.fetchone()[0])
    select_bits = [f"{id_base} + row_number() OVER ()"]
    common = ["id", *common]
    joins = []
    where = ["TRUE"]
    if "deleted" in source_cols:
        where.append("src.deleted = false")
    if "tenant_id" in common:
        where.append("src.tenant_id = %s")
        params: list = [SRC]
    else:
        params = []
    if "entity_type_code" in source_cols:
        where.append("src.entity_type_code = ANY(%s)")
        params.append(copy_codes)
    if "source_entity_type_code" in source_cols and "target_entity_type_code" in source_cols:
        where.append("src.source_entity_type_code = ANY(%s)")
        where.append("src.target_entity_type_code = ANY(%s)")
        params.extend([copy_codes, copy_codes])
    remap = {
        "category_id": "category",
        "model_id": "model",
        "source_model_id": "model",
        "target_model_id": "model",
        "field_id": "field",
        "parent_category_id": "category",
        "child_category_id": "category",
    }
    for column in common:
        if column == "id":
            continue
        if column == "tenant_id":
            select_bits.append(str(DST))
            continue
        kind = remap.get(column)
        if kind:
            alias = f"map_{column}"
            joins.append(
                f"JOIN copy_idmap {alias} ON {alias}.kind = '{kind}' AND {alias}.old_id = src.{column}"
            )
            select_bits.append(f"{alias}.new_id")
            continue
        select_bits.append(f"src.{column}")
    collist = ", ".join(common)
    select_list = ", ".join(select_bits)
    join_sql = " ".join(joins)
    where_sql = " AND ".join(where)
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.{target} ({collist})
        SELECT {select_list}
        FROM {SCHEMA}.{source} src
        {join_sql}
        WHERE {where_sql}
        """,
        params,
    )
    return cur.rowcount


def bump_sequences(cur, tables: list[str]) -> None:
    extra = [
        "dynamic_entity_type",
        "dynamic_entity_type_base_field",
        "dynamic_field",
        "dynamic_model",
        "dynamic_model_field_assignment",
        "dynamic_category",
        "dynamic_category_type",
        "dynamic_group",
        "dm_workbench_layout",
        "dm_data_tab_layout",
        "pr_component_props",
    ]
    for table in list(dict.fromkeys(tables + extra)):
        schema = "platformresource" if table == "pr_component_props" else SCHEMA
        seq = id_sequence(cur, schema, table)
        if not seq:
            continue
        cur.execute(f"SELECT COALESCE(MAX(id), 1) FROM {schema}.{table}")
        maximum = int(cur.fetchone()[0] or 1)
        cur.execute(f"SELECT last_value FROM {seq}")
        last_value = int(cur.fetchone()[0] or 1)
        # 只向前拨。已经写过的编号不能把序列拨回去。
        cur.execute("SELECT setval(%s, %s, true)", (seq, max(maximum, last_value)))


def verify(cur, copy_codes: list[str]) -> None:
    cur.execute(
        f"""
        SELECT COUNT(*) FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false
        """,
        (DST,),
    )
    print("tenant2_catalogs", cur.fetchone()[0])
    cur.execute(
        f"""
        SELECT code, dedicated_table_name
        FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND code = ANY(%s)
          AND dedicated_table_name LIKE '%%_t1'
        """,
        (DST, copy_codes),
    )
    leaked = cur.fetchall()
    if leaked:
        raise RuntimeError(f"仍指向租户 1 专用表: {leaked[:10]}")
    cur.execute(
        f"""
        SELECT
          (SELECT COUNT(*) FROM {SCHEMA}.ent_facility_t2),
          (SELECT COUNT(*) FROM {SCHEMA}.ent_zone_t2),
          (SELECT COUNT(*) FROM {SCHEMA}.ent_structure_t2),
          (SELECT COUNT(*) FROM {SCHEMA}.ent_equipment_t2),
          (SELECT COUNT(*) FROM {SCHEMA}.ent_inspection_item_t2)
        """
    )
    print("kept_and_copied_counts", cur.fetchone())


if __name__ == "__main__":
    main()
