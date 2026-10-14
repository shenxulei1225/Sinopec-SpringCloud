# -*- coding: utf-8 -*-
"""
按业务编码补租户 2 上缺的记录。

不覆盖租户 2 已经有的管廊设施、分区、运营区域、构筑物。
编码已经在的，沿用那一行。编码没有的，才新增。

关联、所属设施、分区、区域都按编码找到租户 2 的那一行，再写入它的编号。
不把租户 1 的编号原样抄过来。
"""

from __future__ import annotations

import psycopg2

SRC = 1
DST = 2
SCHEMA = "dynamicbusiness"
KEEP_TABLES = ("ent_facility_t2", "ent_zone_t2", "ent_region_t2", "ent_structure_t2")
KEEP_CODES = ("facility", "zone", "region", "structure")
MARKER = "copy-by-code"
NULL_ON_INSERT = ("facility_id", "zone_id", "region_id", "parent_id")


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
        repair(cur)
        conn.commit()
        print("COMMITTED")
    except Exception:
        conn.rollback()
        print("ROLLED BACK")
        raise
    finally:
        cur.close()
        conn.close()


def repair(cur) -> None:
    codes = load_copied_codes(cur)
    print("copied_codes", len(codes))
    bumped = bump_lagging_sequences(cur)
    print("sequences_bumped", bumped)

    tables = copied_physical_tables(cur)
    facility_codes = referenced_entity_codes(cur, tables, "facility_id", "ent_facility_t1")
    zone_codes = referenced_entity_codes(cur, tables, "zone_id", "ent_zone_t1")
    region_codes = referenced_entity_codes(cur, tables, "region_id", "ent_region_t1")
    category_ids = missing_category_ids(cur, codes)
    facility_codes, zone_codes, region_codes = add_linked_entity_codes(
        cur, category_ids, facility_codes, zone_codes, region_codes
    )
    zone_codes = expand_entity_ancestors(cur, "ent_zone_t1", zone_codes)
    region_codes = expand_entity_ancestors(cur, "ent_region_t1", region_codes)
    facility_codes = expand_facility_codes(cur, facility_codes, zone_codes, region_codes)
    print("facility_codes", sorted(facility_codes))
    print("zone_codes", len(zone_codes), "region_codes", len(region_codes))
    print("categories_to_add", len(category_ids))

    ensure_models_by_code(cur, "ent_facility_t1", facility_codes)
    ensure_models_by_code(cur, "ent_region_t1", region_codes)
    ensure_models_by_code(cur, "ent_zone_t1", zone_codes)
    inserted_facility = insert_entities_by_code(cur, "ent_facility_t1", "ent_facility_t2", facility_codes)
    inserted_region = insert_entities_by_code(cur, "ent_region_t1", "ent_region_t2", region_codes)
    inserted_zone = insert_entities_by_code(cur, "ent_zone_t1", "ent_zone_t2", zone_codes)
    print("entities_inserted", inserted_facility, inserted_region, inserted_zone)

    rebound = rebind_foreign_keys_by_code(cur, tables)
    print("rebound_tables", rebound)

    inserted_types = insert_missing_category_types(cur, category_ids)
    inserted_categories = insert_categories_by_code(cur, category_ids)
    relinked_parents = relink_category_parents(cur)
    print("category_types", inserted_types, "categories", inserted_categories, "parents", relinked_parents)

    links = insert_category_entity_links(cur, category_ids)
    relations = insert_entity_category_relations(cur, codes)
    model_categories = insert_model_category_relations(cur, codes)
    model_entities = insert_model_entity_relations(cur, codes)
    field_indexes = insert_field_indexes(cur)
    entity_relations = insert_kept_end_entity_relations(cur, codes)
    base_fields = insert_missing_base_fields(cur)
    print(
        "links", links,
        "entity_categories", relations,
        "model_categories", model_categories,
        "model_entities", model_entities,
        "field_indexes", field_indexes,
        "entity_relations", entity_relations,
        "base_fields", base_fields,
    )
    verify_kept_rows(cur)


def load_copied_codes(cur) -> list[str]:
    cur.execute(
        f"""
        SELECT code FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND creator = 'copy-from-tenant-1'
        ORDER BY code
        """,
        (DST,),
    )
    return [row[0] for row in cur.fetchall()]


def id_sequence(cur, schema: str, table: str) -> str | None:
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


def bump_lagging_sequences(cur) -> int:
    cur.execute(
        """
        SELECT n.nspname, c.relname, pg_get_expr(ad.adbin, ad.adrelid)
        FROM pg_attribute a
        JOIN pg_class c ON c.oid = a.attrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_attrdef ad ON ad.adrelid = a.attrelid AND ad.adnum = a.attnum
        WHERE n.nspname IN ('dynamicbusiness', 'platformresource')
          AND a.attname = 'id'
          AND c.relkind = 'r'
          AND pg_get_expr(ad.adbin, ad.adrelid) LIKE 'nextval%%'
        """
    )
    bumped = 0
    for schema, table, default in cur.fetchall():
        seq = default.split("'")[1]
        cur.execute(f"SELECT last_value FROM {seq}")
        last_value = int(cur.fetchone()[0])
        cur.execute(f'SELECT COALESCE(MAX(id), 0) FROM {schema}."{table}"')
        maximum = int(cur.fetchone()[0])
        if last_value >= maximum:
            continue
        cur.execute("SELECT setval(%s, %s, true)", (seq, maximum))
        bumped += 1
    return bumped


def columns_of(cur, table: str) -> list[str]:
    cur.execute(
        """
        SELECT column_name FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s
        ORDER BY ordinal_position
        """,
        (SCHEMA, table),
    )
    return [row[0] for row in cur.fetchall()]


def table_exists(cur, table: str) -> bool:
    cur.execute("SELECT to_regclass(%s)", (f"{SCHEMA}.{table}",))
    return cur.fetchone()[0] is not None


def copied_physical_tables(cur) -> list[str]:
    cur.execute(
        f"""
        SELECT DISTINCT dedicated_table_name
        FROM {SCHEMA}.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false AND creator = 'copy-from-tenant-1'
          AND dedicated_table_name IS NOT NULL
        """,
        (DST,),
    )
    found = []
    for (name,) in cur.fetchall():
        if name in KEEP_TABLES or not name.endswith("_t2"):
            continue
        if table_exists(cur, name) and table_exists(cur, name[:-1] + "1"):
            found.append(name)
    return sorted(found)


def referenced_entity_codes(cur, tables: list[str], fk: str, entity_table: str) -> set[str]:
    if not table_exists(cur, entity_table):
        return set()
    codes: set[str] = set()
    for table in tables:
        source = table[:-1] + "1"
        if fk not in columns_of(cur, source):
            continue
        cur.execute(
            f"""
            SELECT DISTINCT ent.code
            FROM {SCHEMA}.{source} src
            JOIN {SCHEMA}.{entity_table} ent ON ent.id = src.{fk}
            WHERE src.{fk} IS NOT NULL AND ent.code IS NOT NULL AND ent.deleted = false
            """
        )
        codes.update(row[0] for row in cur.fetchall())
    return codes


def missing_category_ids(cur, codes: list[str]) -> list[int]:
    cur.execute(
        f"""
        SELECT DISTINCT sc.id
        FROM {SCHEMA}.dynamic_entity_category_relation_t1 src
        JOIN {SCHEMA}.dynamic_category sc
          ON sc.id = src.category_id AND sc.tenant_id = %s AND sc.deleted = false
        WHERE src.tenant_id = %s AND src.deleted = false
          AND src.entity_type_code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_category dc
            WHERE dc.tenant_id = %s AND dc.deleted = false AND dc.code = sc.code
          )
        """,
        (SRC, SRC, codes, DST),
    )
    pending = [row[0] for row in cur.fetchall()]
    cur.execute(
        f"""
        SELECT DISTINCT sc.id
        FROM {SCHEMA}.dynamic_model_category_relation_t1 src
        JOIN {SCHEMA}.dynamic_category sc
          ON sc.tenant_id = %s AND sc.deleted = false AND sc.code = src.category_code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND src.entity_type_code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_category dc
            WHERE dc.tenant_id = %s AND dc.deleted = false AND dc.code = sc.code
          )
        """,
        (SRC, SRC, codes, DST),
    )
    pending.extend(row[0] for row in cur.fetchall())
    seen: set[int] = set()
    while pending:
        current = pending.pop()
        if current in seen:
            continue
        seen.add(current)
        cur.execute(
            f"SELECT parent_id FROM {SCHEMA}.dynamic_category WHERE id = %s",
            (current,),
        )
        parent_id = cur.fetchone()[0]
        if not parent_id:
            continue
        cur.execute(
            f"""
            SELECT id, code FROM {SCHEMA}.dynamic_category
            WHERE id = %s AND deleted = false
            """,
            (parent_id,),
        )
        parent = cur.fetchone()
        if parent is None:
            continue
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_category
            WHERE tenant_id = %s AND deleted = false AND code = %s
            """,
            (DST, parent[1]),
        )
        if cur.fetchone() is None:
            pending.append(parent[0])
    if not seen:
        return []
    cur.execute(
        f"""
        SELECT id FROM {SCHEMA}.dynamic_category
        WHERE id = ANY(%s)
        ORDER BY level NULLS FIRST, id
        """,
        (list(seen),),
    )
    return [row[0] for row in cur.fetchall()]


def add_linked_entity_codes(cur, category_ids: list[int], facility_codes, zone_codes, region_codes):
    if not category_ids:
        return facility_codes, zone_codes, region_codes
    buckets = {"facility": facility_codes, "zone": zone_codes, "region": region_codes}
    cur.execute(
        f"""
        SELECT link.entity_type_code, link.entity_id
        FROM {SCHEMA}.dynamic_category_entity_link_t1 link
        WHERE link.deleted = false AND link.tenant_id = %s
          AND link.category_id = ANY(%s)
          AND link.entity_type_code = ANY(%s)
        """,
        (SRC, category_ids, list(KEEP_CODES[:3])),
    )
    for type_code, entity_id in cur.fetchall():
        table = f"ent_{type_code}_t1"
        if not table_exists(cur, table):
            continue
        cur.execute(
            f"SELECT code FROM {SCHEMA}.{table} WHERE id = %s AND deleted = false",
            (entity_id,),
        )
        row = cur.fetchone()
        if row and row[0]:
            buckets[type_code].add(row[0])
    return facility_codes, zone_codes, region_codes


def expand_entity_ancestors(cur, table: str, codes: set[str]) -> set[str]:
    if not codes or not table_exists(cur, table) or "parent_id" not in columns_of(cur, table):
        return codes
    pending = set(codes)
    seen: set[str] = set()
    while pending:
        code = pending.pop()
        if code in seen:
            continue
        seen.add(code)
        cur.execute(
            f"""
            SELECT parent.code
            FROM {SCHEMA}.{table} src
            JOIN {SCHEMA}.{table} parent ON parent.id = src.parent_id AND parent.deleted = false
            WHERE src.code = %s AND src.deleted = false
            """,
            (code,),
        )
        row = cur.fetchone()
        if not row or not row[0]:
            continue
        cur.execute(
            f"SELECT 1 FROM {SCHEMA}.{table[:-1] + '2'} WHERE deleted = false AND code = %s",
            (row[0],),
        )
        if cur.fetchone() is None:
            pending.add(row[0])
    return seen


def expand_facility_codes(cur, facility_codes: set[str], zone_codes: set[str], region_codes: set[str]) -> set[str]:
    found = set(facility_codes)
    for table, codes in (("ent_zone_t1", zone_codes), ("ent_region_t1", region_codes)):
        if not codes or "facility_id" not in columns_of(cur, table):
            continue
        cur.execute(
            f"""
            SELECT DISTINCT facility.code
            FROM {SCHEMA}.{table} src
            JOIN {SCHEMA}.ent_facility_t1 facility
              ON facility.id = src.facility_id AND facility.deleted = false
            WHERE src.deleted = false AND src.code = ANY(%s) AND facility.code IS NOT NULL
            """,
            (list(codes),),
        )
        found.update(row[0] for row in cur.fetchall())
    return found


def ensure_models_by_code(cur, source: str, codes: set[str]) -> None:
    if not codes or "model_id" not in columns_of(cur, source):
        return
    cur.execute(
        f"""
        SELECT DISTINCT model1.code
        FROM {SCHEMA}.{source} src
        JOIN {SCHEMA}.dynamic_model model1 ON model1.id = src.model_id AND model1.deleted = false
        WHERE src.deleted = false AND src.code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_model model2
            WHERE model2.tenant_id = %s AND model2.deleted = false AND model2.code = model1.code
          )
        """,
        (list(codes), DST),
    )
    missing = [row[0] for row in cur.fetchall()]
    if not missing:
        return
    seq = id_sequence(cur, SCHEMA, "dynamic_model")
    shared = columns_of(cur, "dynamic_model")
    for code in missing:
        names = []
        values = []
        for name in shared:
            if name == "id":
                names.append(name)
                values.append(f"nextval('{seq}')")
            elif name == "tenant_id":
                names.append(name)
                values.append(str(DST))
            elif name == "creator":
                names.append(name)
                values.append(f"'{MARKER}'")
            elif name == "origin_facility_id":
                names.append(name)
                values.append("NULL")
            else:
                names.append(name)
                values.append(f"src.{name}")
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dynamic_model ({", ".join(names)})
            SELECT {", ".join(values)}
            FROM {SCHEMA}.dynamic_model src
            WHERE src.deleted = false AND src.tenant_id = %s AND src.code = %s
            """,
            (SRC, code),
        )
        print("model_inserted", code)


def insert_entities_by_code(cur, source: str, target: str, codes: set[str]) -> int:
    if not codes or not table_exists(cur, source) or not table_exists(cur, target):
        return 0
    seq = id_sequence(cur, SCHEMA, target)
    if not seq:
        raise RuntimeError(f"{target} 没有 id 序列")
    shared = [name for name in columns_of(cur, source) if name in columns_of(cur, target)]
    inserted = 0
    for code in sorted(codes):
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.{target}
            WHERE deleted = false AND code = %s
            """,
            (code,),
        )
        if cur.fetchone():
            continue
        values = []
        names = []
        for name in shared:
            if name == "id":
                names.append("id")
                values.append(f"nextval('{seq}')")
                continue
            if name == "tenant_id":
                names.append("tenant_id")
                values.append(str(DST))
                continue
            if name == "creator":
                names.append("creator")
                values.append(f"'{MARKER}'")
                continue
            if name in ("facility_id", "zone_id", "region_id"):
                entity_name = name[: -len("_id")]
                names.append(name)
                values.append(
                    f"""(
                        SELECT ent2.id
                        FROM {SCHEMA}.ent_{entity_name}_t1 ent1
                        JOIN {SCHEMA}.ent_{entity_name}_t2 ent2
                          ON ent2.deleted = false AND ent2.code = ent1.code
                        WHERE ent1.id = src.{name}
                        LIMIT 1
                    )"""
                )
                continue
            if name == "parent_id":
                names.append(name)
                values.append("NULL")
                continue
            if name == "model_id":
                names.append(name)
                values.append(
                    f"""(
                        SELECT model2.id
                        FROM {SCHEMA}.dynamic_model model1
                        JOIN {SCHEMA}.dynamic_model model2
                          ON model2.tenant_id = {DST}
                         AND model2.deleted = false
                         AND model2.code = model1.code
                        WHERE model1.id = src.model_id
                        LIMIT 1
                    )"""
                )
                continue
            names.append(name)
            values.append(f"src.{name}")
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.{target} ({", ".join(names)})
            SELECT {", ".join(values)}
            FROM {SCHEMA}.{source} src
            WHERE src.deleted = false AND src.code = %s
            """,
            (code,),
        )
        inserted += cur.rowcount
    return inserted


def rebind_foreign_keys_by_code(cur, tables: list[str]) -> int:
    changed = 0
    jobs = (
        ("facility_id", "ent_facility_t1", "ent_facility_t2"),
        ("zone_id", "ent_zone_t1", "ent_zone_t2"),
        ("region_id", "ent_region_t1", "ent_region_t2"),
    )
    targets = list(tables)
    for kept in KEEP_TABLES:
        if table_exists(cur, kept):
            targets.append(kept)
    for table in targets:
        source = table[:-1] + "1"
        if not table_exists(cur, source):
            continue
        only_new = table in KEEP_TABLES
        for fk, entity_source, entity_target in jobs:
            if fk not in columns_of(cur, source) or fk not in columns_of(cur, table):
                continue
            # 复制过来的台账编号与租户 1 相同。新补的设施、分区、区域用了新编号，只能按编码对上。
            if only_new:
                match_sql = "dst.creator = %s AND dst.code = src.code"
                match_params: tuple = (MARKER,)
            else:
                match_sql = "dst.id = src.id"
                match_params = ()
            cur.execute(
                f"""
                UPDATE {SCHEMA}.{table} dst
                SET {fk} = ent2.id
                FROM {SCHEMA}.{source} src
                JOIN {SCHEMA}.{entity_source} ent1 ON ent1.id = src.{fk} AND ent1.deleted = false
                JOIN {SCHEMA}.{entity_target} ent2
                  ON ent2.deleted = false AND ent2.code = ent1.code
                WHERE {match_sql}
                  AND src.{fk} IS NOT NULL
                  AND dst.{fk} IS DISTINCT FROM ent2.id
                """,
                match_params,
            )
            changed += cur.rowcount
        if "parent_id" in columns_of(cur, table) and "code" in columns_of(cur, table):
            cur.execute(
                f"""
                UPDATE {SCHEMA}.{table} dst
                SET parent_id = parent2.id
                FROM {SCHEMA}.{source} src
                JOIN {SCHEMA}.{source} parent1
                  ON parent1.id = src.parent_id AND parent1.deleted = false
                JOIN {SCHEMA}.{table} parent2
                  ON parent2.deleted = false AND parent2.code = parent1.code
                WHERE dst.creator = %s
                  AND dst.code = src.code
                  AND src.parent_id IS NOT NULL
                  AND dst.parent_id IS DISTINCT FROM parent2.id
                """,
                (MARKER,),
            )
            changed += cur.rowcount
        if "model_id" in columns_of(cur, table) and "code" in columns_of(cur, table):
            cur.execute(
                f"""
                UPDATE {SCHEMA}.{table} dst
                SET model_id = model2.id
                FROM {SCHEMA}.{source} src
                JOIN {SCHEMA}.dynamic_model model1 ON model1.id = src.model_id AND model1.deleted = false
                JOIN {SCHEMA}.dynamic_model model2
                  ON model2.tenant_id = %s AND model2.deleted = false AND model2.code = model1.code
                WHERE dst.creator = %s
                  AND dst.code = src.code
                  AND src.model_id IS NOT NULL
                  AND dst.model_id IS DISTINCT FROM model2.id
                """,
                (DST, MARKER),
            )
            changed += cur.rowcount
    return changed


def insert_missing_category_types(cur, category_ids: list[int]) -> int:
    if not category_ids:
        return 0
    cur.execute(
        f"""
        SELECT DISTINCT category_type_code
        FROM {SCHEMA}.dynamic_category
        WHERE id = ANY(%s)
        """,
        (category_ids,),
    )
    type_codes = [row[0] for row in cur.fetchall() if row[0]]
    seq = id_sequence(cur, SCHEMA, "dynamic_category_type")
    inserted = 0
    shared = columns_of(cur, "dynamic_category_type")
    for code in type_codes:
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_category_type
            WHERE tenant_id = %s AND deleted = false AND category_type_code = %s
            """,
            (DST, code),
        )
        if cur.fetchone():
            continue
        names = []
        values = []
        for name in shared:
            if name == "id":
                names.append(name)
                values.append(f"nextval('{seq}')")
            elif name == "tenant_id":
                names.append(name)
                values.append(str(DST))
            elif name == "creator":
                names.append(name)
                values.append(f"'{MARKER}'")
            elif name == "top_level_category_id":
                names.append(name)
                values.append("NULL")
            else:
                names.append(name)
                values.append(f"src.{name}")
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dynamic_category_type ({", ".join(names)})
            SELECT {", ".join(values)}
            FROM {SCHEMA}.dynamic_category_type src
            WHERE src.tenant_id = %s AND src.deleted = false AND src.category_type_code = %s
            """,
            (SRC, code),
        )
        inserted += cur.rowcount
    if inserted:
        cur.execute(
            f"""
            UPDATE {SCHEMA}.dynamic_category_type dst
            SET top_level_category_id = dc.id
            FROM {SCHEMA}.dynamic_category_type src
            JOIN {SCHEMA}.dynamic_category sc ON sc.id = src.top_level_category_id
            JOIN {SCHEMA}.dynamic_category dc
              ON dc.tenant_id = %s AND dc.deleted = false AND dc.code = sc.code
            WHERE dst.tenant_id = %s AND dst.creator = %s
              AND src.tenant_id = %s AND src.category_type_code = dst.category_type_code
            """,
            (DST, DST, MARKER, SRC),
        )
    return inserted


def insert_categories_by_code(cur, category_ids: list[int]) -> int:
    if not category_ids:
        return 0
    seq = id_sequence(cur, SCHEMA, "dynamic_category")
    shared = columns_of(cur, "dynamic_category")
    inserted = 0
    for category_id in category_ids:
        cur.execute(
            f"SELECT code, parent_id FROM {SCHEMA}.dynamic_category WHERE id = %s",
            (category_id,),
        )
        code, parent_id = cur.fetchone()
        cur.execute(
            f"""
            SELECT 1 FROM {SCHEMA}.dynamic_category
            WHERE tenant_id = %s AND deleted = false AND code = %s
            """,
            (DST, code),
        )
        if cur.fetchone():
            continue
        parent_code_sql = "src.parent_code"
        params: list = []
        if parent_id:
            cur.execute(
                f"SELECT code FROM {SCHEMA}.dynamic_category WHERE id = %s",
                (parent_id,),
            )
            parent_code = cur.fetchone()[0]
            parent_code_sql = "%s"
            params.append(parent_code)
        names = []
        values = []
        for name in shared:
            if name == "id":
                names.append(name)
                values.append(f"nextval('{seq}')")
            elif name == "tenant_id":
                names.append(name)
                values.append(str(DST))
            elif name == "creator":
                names.append(name)
                values.append(f"'{MARKER}'")
            elif name == "parent_id":
                names.append(name)
                values.append("NULL")
            elif name == "tree_path":
                names.append(name)
                values.append("NULL")
            elif name == "parent_code":
                names.append(name)
                values.append(parent_code_sql)
            else:
                names.append(name)
                values.append(f"src.{name}")
        params.append(category_id)
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dynamic_category ({", ".join(names)})
            SELECT {", ".join(values)}
            FROM {SCHEMA}.dynamic_category src
            WHERE src.id = %s
            """,
            params,
        )
        inserted += cur.rowcount
    return inserted


def relink_category_parents(cur) -> int:
    cur.execute(
        f"""
        UPDATE {SCHEMA}.dynamic_category child
        SET parent_id = parent.id
        FROM {SCHEMA}.dynamic_category parent
        WHERE child.tenant_id = %s
          AND child.creator = %s
          AND child.parent_code IS NOT NULL
          AND parent.tenant_id = %s
          AND parent.deleted = false
          AND parent.code = child.parent_code
          AND child.parent_id IS DISTINCT FROM parent.id
        """,
        (DST, MARKER, DST),
    )
    changed = cur.rowcount
    cur.execute(
        f"""
        SELECT id FROM {SCHEMA}.dynamic_category
        WHERE tenant_id = %s AND creator = %s AND deleted = false
        """,
        (DST, MARKER),
    )
    ids = [row[0] for row in cur.fetchall()]
    for category_id in ids:
        chain = []
        current = category_id
        guard = 0
        while current and guard < 30:
            chain.append(current)
            cur.execute(
                f"SELECT parent_id FROM {SCHEMA}.dynamic_category WHERE id = %s",
                (current,),
            )
            current = cur.fetchone()[0]
            guard += 1
        chain.reverse()
        tree_path = "/" + "/".join(str(item) for item in chain) + "/"
        cur.execute(
            f"UPDATE {SCHEMA}.dynamic_category SET tree_path = %s, level = %s WHERE id = %s",
            (tree_path, len(chain), category_id),
        )
    return changed


def insert_category_entity_links(cur, category_ids: list[int]) -> int:
    if not category_ids:
        return 0
    seq = id_sequence(cur, SCHEMA, "dynamic_category_entity_link_t2")
    inserted = 0
    for type_code in ("facility", "zone", "region"):
        source_entity = f"ent_{type_code}_t1"
        target_entity = f"ent_{type_code}_t2"
        if not table_exists(cur, source_entity):
            continue
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dynamic_category_entity_link_t2 (
                id, category_id, entity_id, entity_model_id, creator, create_time,
                updater, update_time, deleted, tenant_id, entity_type_code, domain
            )
            SELECT nextval(%s), dc.id, ent2.id, src.entity_model_id, %s, src.create_time,
                   src.updater, src.update_time, false, %s, src.entity_type_code, src.domain
            FROM {SCHEMA}.dynamic_category_entity_link_t1 src
            JOIN {SCHEMA}.dynamic_category sc ON sc.id = src.category_id AND sc.deleted = false
            JOIN {SCHEMA}.dynamic_category dc
              ON dc.tenant_id = %s AND dc.deleted = false AND dc.code = sc.code
            JOIN {SCHEMA}.{source_entity} ent1 ON ent1.id = src.entity_id AND ent1.deleted = false
            JOIN {SCHEMA}.{target_entity} ent2 ON ent2.deleted = false AND ent2.code = ent1.code
            WHERE src.deleted = false AND src.tenant_id = %s
              AND src.entity_type_code = %s
              AND src.category_id = ANY(%s)
              AND NOT EXISTS (
                SELECT 1 FROM {SCHEMA}.dynamic_category_entity_link_t2 dst
                WHERE dst.deleted = false AND dst.tenant_id = %s
                  AND dst.category_id = dc.id AND dst.entity_id = ent2.id
                  AND dst.entity_type_code = src.entity_type_code
              )
            """,
            (seq, MARKER, DST, DST, SRC, type_code, category_ids, DST),
        )
        inserted += cur.rowcount
    return inserted


def insert_entity_category_relations(cur, codes: list[str]) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_entity_category_relation_t2")
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.dynamic_entity_category_relation_t2 (
            id, entity_id, category_id, entity_type_code, sort, creator, create_time,
            updater, update_time, deleted, tenant_id, domain
        )
        SELECT nextval(%s), src.entity_id, dc.id, src.entity_type_code, src.sort, %s,
               src.create_time, src.updater, src.update_time, false, %s, src.domain
        FROM {SCHEMA}.dynamic_entity_category_relation_t1 src
        JOIN {SCHEMA}.dynamic_category sc
          ON sc.id = src.category_id AND sc.tenant_id = %s AND sc.deleted = false
        JOIN {SCHEMA}.dynamic_category dc
          ON dc.tenant_id = %s AND dc.deleted = false AND dc.code = sc.code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND src.entity_type_code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_entity_category_relation_t2 dst
            WHERE dst.tenant_id = %s AND dst.deleted = false
              AND dst.entity_type_code = src.entity_type_code
              AND dst.entity_id = src.entity_id
              AND dst.category_id = dc.id
          )
        """,
        (seq, MARKER, DST, SRC, DST, SRC, codes, DST),
    )
    return cur.rowcount


def insert_model_category_relations(cur, codes: list[str]) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_model_category_relation_t2")
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.dynamic_model_category_relation_t2 (
            id, model_id, category_id, entity_type_code, sort, creator, create_time,
            updater, update_time, deleted, tenant_id, model_code, category_code
        )
        SELECT nextval(%s), model2.id, dc.id, src.entity_type_code, src.sort, %s,
               src.create_time, src.updater, src.update_time, false, %s,
               src.model_code, src.category_code
        FROM {SCHEMA}.dynamic_model_category_relation_t1 src
        JOIN {SCHEMA}.dynamic_model model2
          ON model2.tenant_id = %s AND model2.deleted = false AND model2.code = src.model_code
        JOIN {SCHEMA}.dynamic_category dc
          ON dc.tenant_id = %s AND dc.deleted = false AND dc.code = src.category_code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND src.entity_type_code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_model_category_relation_t2 dst
            WHERE dst.tenant_id = %s AND dst.deleted = false
              AND dst.entity_type_code = src.entity_type_code
              AND dst.model_code = src.model_code
              AND dst.category_code = src.category_code
          )
        """,
        (seq, MARKER, DST, DST, DST, SRC, codes, DST),
    )
    return cur.rowcount


def insert_model_entity_relations(cur, codes: list[str]) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_model_entity_relation_t2")
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.dynamic_model_entity_relation_t2 (
            id, model_id, model_entity_type_code, entity_id, entity_type_code, domain,
            sort, creator, create_time, updater, update_time, deleted, tenant_id
        )
        SELECT nextval(%s), model2.id, src.model_entity_type_code, src.entity_id,
               src.entity_type_code, src.domain, src.sort, %s, src.create_time,
               src.updater, src.update_time, false, %s
        FROM {SCHEMA}.dynamic_model_entity_relation_t1 src
        JOIN {SCHEMA}.dynamic_model model1 ON model1.id = src.model_id AND model1.deleted = false
        JOIN {SCHEMA}.dynamic_model model2
          ON model2.tenant_id = %s AND model2.deleted = false AND model2.code = model1.code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND src.entity_type_code = ANY(%s)
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_model_entity_relation_t2 dst
            WHERE dst.tenant_id = %s AND dst.deleted = false
              AND dst.entity_type_code = src.entity_type_code
              AND dst.entity_id = src.entity_id
              AND dst.model_id = model2.id
          )
        """,
        (seq, MARKER, DST, DST, SRC, codes, DST),
    )
    return cur.rowcount


def insert_field_indexes(cur) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_entity_field_index_t2")
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.dynamic_entity_field_index_t2 (
            id, entity_id, model_id, field_code, value_string, value_number, value_date,
            value_datetime, value_boolean, creator, create_time, updater, update_time,
            deleted, tenant_id
        )
        SELECT nextval(%s), src.entity_id, model2.id, src.field_code, src.value_string,
               src.value_number, src.value_date, src.value_datetime, src.value_boolean,
               %s, src.create_time, src.updater, src.update_time, false, %s
        FROM {SCHEMA}.dynamic_entity_field_index_t1 src
        JOIN {SCHEMA}.dynamic_model model1 ON model1.id = src.model_id AND model1.deleted = false
        JOIN {SCHEMA}.dynamic_model model2
          ON model2.tenant_id = %s AND model2.deleted = false AND model2.code = model1.code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_entity_field_index_t2 dst
            WHERE dst.tenant_id = %s AND dst.deleted = false
              AND dst.entity_id = src.entity_id
              AND dst.field_code = src.field_code
          )
        """,
        (seq, MARKER, DST, DST, SRC, DST),
    )
    return cur.rowcount


def insert_kept_end_entity_relations(cur, codes: list[str]) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_entity_relation_t2")
    inserted = 0
    for type_code in ("facility", "zone", "region"):
        source_entity = f"ent_{type_code}_t1"
        target_entity = f"ent_{type_code}_t2"
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dynamic_entity_relation_t2 (
                id, source_entity_id, target_entity_id, relation_type, relation_name,
                description, relation_attributes, status, field_code, source_model_code,
                target_model_code, source_entity_type_code, target_entity_type_code,
                creator, create_time, updater, update_time, deleted, tenant_id
            )
            SELECT nextval(%s), src.source_entity_id, ent2.id, src.relation_type, src.relation_name,
                   src.description, src.relation_attributes, src.status, src.field_code,
                   src.source_model_code, src.target_model_code, src.source_entity_type_code,
                   src.target_entity_type_code, %s, src.create_time, src.updater, src.update_time,
                   false, %s
            FROM {SCHEMA}.dynamic_entity_relation_t1 src
            JOIN {SCHEMA}.{source_entity} ent1
              ON ent1.id = src.target_entity_id AND ent1.deleted = false
            JOIN {SCHEMA}.{target_entity} ent2
              ON ent2.deleted = false AND ent2.code = ent1.code
            WHERE src.tenant_id = %s AND src.deleted = false
              AND src.target_entity_type_code = %s
              AND src.source_entity_type_code = ANY(%s)
              AND NOT EXISTS (
                SELECT 1 FROM {SCHEMA}.dynamic_entity_relation_t2 dst
                WHERE dst.tenant_id = %s AND dst.deleted = false
                  AND dst.source_entity_type_code = src.source_entity_type_code
                  AND dst.source_entity_id = src.source_entity_id
                  AND dst.target_entity_type_code = src.target_entity_type_code
                  AND dst.target_entity_id = ent2.id
                  AND dst.field_code IS NOT DISTINCT FROM src.field_code
              )
            """,
            (seq, MARKER, DST, SRC, type_code, codes, DST),
        )
        inserted += cur.rowcount
    return inserted


def insert_missing_base_fields(cur) -> int:
    seq = id_sequence(cur, SCHEMA, "dynamic_entity_type_base_field")
    cur.execute(
        f"""
        INSERT INTO {SCHEMA}.dynamic_entity_type_base_field (
            id, entity_type_code, field_code, field_name, data_type, required, default_value,
            description, type_config, sort_order, status, creator, create_time, updater,
            update_time, deleted, tenant_id, library_field_id, is_searchable, is_filterable,
            is_sortable
        )
        SELECT nextval(%s), src.entity_type_code, src.field_code, src.field_name, src.data_type,
               src.required, src.default_value, src.description, src.type_config, src.sort_order,
               src.status, %s, CURRENT_TIMESTAMP, src.updater, CURRENT_TIMESTAMP, false, %s,
               field2.id, src.is_searchable, src.is_filterable, src.is_sortable
        FROM {SCHEMA}.dynamic_entity_type_base_field src
        JOIN {SCHEMA}.dynamic_entity_type et
          ON et.tenant_id = %s AND et.deleted = false AND et.code = src.entity_type_code
        JOIN {SCHEMA}.dynamic_field field1 ON field1.id = src.library_field_id AND field1.deleted = false
        JOIN {SCHEMA}.dynamic_field field2
          ON field2.tenant_id = %s AND field2.deleted = false AND field2.code = field1.code
        WHERE src.tenant_id = %s AND src.deleted = false
          AND NOT EXISTS (
            SELECT 1 FROM {SCHEMA}.dynamic_entity_type_base_field dst
            WHERE dst.tenant_id = %s AND dst.deleted = false
              AND dst.entity_type_code = src.entity_type_code
              AND dst.field_code = src.field_code
          )
        """,
        (seq, MARKER, DST, DST, DST, SRC, DST),
    )
    return cur.rowcount


def verify_kept_rows(cur) -> None:
    cur.execute(
        f"""
        SELECT id, code, name FROM {SCHEMA}.ent_facility_t2
        WHERE id = 2000001
        """
    )
    print("corridor_facility", cur.fetchone())
    cur.execute(
        f"""
        SELECT COUNT(*) FROM {SCHEMA}.ent_zone_t2
        WHERE creator IS DISTINCT FROM %s
        """,
        (MARKER,),
    )
    print("zone_rows_not_from_this_repair", cur.fetchone()[0])


if __name__ == "__main__":
    main()
