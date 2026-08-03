#!/usr/bin/env python3
"""将曾含 FLD-BASE 脏列的 displayContent 收束为 ["name"]，并全库扫描残留。"""

from __future__ import annotations

import sys

import psycopg2
from psycopg2.extras import Json

DSN = "host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer"
DIRTY_PROPS = (8599, 8750, 8755)


def main() -> int:
    conn = psycopg2.connect(DSN)
    cur = conn.cursor()
    cur.execute("SET search_path TO platformresource, dynamicbusiness")

    for pid in DIRTY_PROPS:
        cur.execute("SELECT props::jsonb FROM pr_component_props WHERE id = %s", (pid,))
        row = cur.fetchone()
        if not row:
            continue
        props = row[0]
        props["displayContent"] = ["name"]
        cur.execute(
            """
            UPDATE pr_component_props
            SET props = %s, update_time = NOW()
            WHERE id = %s
            """,
            (Json(props), pid),
        )
        print(f"props#{pid} displayContent -> {props['displayContent']}")

    conn.commit()

    cur.execute(
        """
        SELECT table_schema, table_name, column_name, data_type
        FROM information_schema.columns
        WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
          AND data_type IN ('jsonb', 'json', 'text', 'character varying')
        ORDER BY 1, 2, 3
        """
    )
    cols = cur.fetchall()
    hits = []
    for sch, tbl, col, _dt in cols:
        sql = f'SELECT count(*) FROM "{sch}"."{tbl}" WHERE "{col}"::text ILIKE %s'
        try:
            cur.execute(sql, ("%FLD-BASE%",))
            n = cur.fetchone()[0]
            if n:
                hits.append((sch, tbl, col, n))
        except Exception:
            conn.rollback()
            cur.execute("SET search_path TO platformresource, dynamicbusiness")

    print("REMAINING_HITS", len(hits))
    for h in sorted(hits, key=lambda x: -x[3])[:50]:
        print(h)

    cur.execute(
        """
        SELECT id, (props::jsonb)->'displayContent'
        FROM pr_component_props WHERE id IN (8599, 8750, 8755)
        """
    )
    print("verify", cur.fetchall())
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
