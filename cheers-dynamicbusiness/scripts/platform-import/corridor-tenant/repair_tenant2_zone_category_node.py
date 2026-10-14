# -*- coding: utf-8 -*-
"""
给租户 2 的分区补上「点分类节点就是当前对象」。

分区的分类节点和分区实体、一对一绑定都已经在。
目录编排没写 selection_source=CATEGORY_NODE 时，数据页不采用节点上的实体，
详情就显示「当前分类节点尚未绑定实体」。

不新建实体，不改分类树，不改数据页布局。
顶层「管廊空间」是结构根，按规则不绑实体。
"""

from __future__ import annotations

import psycopg2

DST = 2
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
    cur.execute(
        f"""
        SELECT id, selection_source
        FROM {SCHEMA}.dm_catalog_orchestration
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = 'zone'
        """,
        (DST,),
    )
    existing = cur.fetchone()
    if existing:
        if existing[1] == "CATEGORY_NODE":
            print("already", existing)
        else:
            cur.execute(
                f"""
                UPDATE {SCHEMA}.dm_catalog_orchestration
                SET selection_source = 'CATEGORY_NODE',
                    enabled = true,
                    updater = 'repair-category-node',
                    update_time = now()
                WHERE id = %s AND tenant_id = %s
                """,
                (existing[0], DST),
            )
            print("updated", existing[0])
    else:
        cur.execute(
            f"""
            INSERT INTO {SCHEMA}.dm_catalog_orchestration
                (entity_type_code, enabled, creator, updater, deleted, tenant_id, selection_source)
            VALUES ('zone', true, 'repair-category-node', 'repair-category-node', false, %s, 'CATEGORY_NODE')
            """,
            (DST,),
        )
        print("inserted")

    cur.execute(
        f"""
        SELECT id, entity_type_code, enabled, selection_source
        FROM {SCHEMA}.dm_catalog_orchestration
        WHERE tenant_id = %s AND deleted = false AND entity_type_code = 'zone'
        """,
        (DST,),
    )
    print("zone_orchestration", cur.fetchone())

    cur.execute(
        """
        SELECT c.name, l.entity_id, e.name
        FROM dynamicbusiness.dynamic_category c
        JOIN dynamicbusiness.dynamic_category_entity_link_t2 l
          ON l.category_id = c.id AND l.deleted = false AND l.entity_type_code = 'zone'
        JOIN dynamicbusiness.ent_zone_t2 e
          ON e.id = l.entity_id AND e.deleted = false
        WHERE c.tenant_id = 2 AND c.deleted = false AND c.name = '光谷五路北'
        """
    )
    print("sample_binding", cur.fetchone())


if __name__ == "__main__":
    main()
