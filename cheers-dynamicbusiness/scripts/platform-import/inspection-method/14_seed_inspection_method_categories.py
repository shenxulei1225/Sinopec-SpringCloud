#!/usr/bin/env python3
"""
检查方法分类树 + 模板关联（租户1，幂等）。

分类按检查手段分组（与 07/08 方法模板库一致），挂在 category_type_code=inspection_method 根下。
关联写入 dynamic_entity_category_relation_t1（简单分类 MULTI）。
"""
from __future__ import annotations

import psycopg2

DSN = dict(host="127.0.0.1", dbname="sinopec", user="postgres", password="Coolhomer")
TENANT = 1
CTYPE = "inspection_method"
ROOT_CODE = "inspection_method_root"

# code, name, sort
CATEGORIES = [
    ("IM-CAT-GENERAL", "通用外观与记录", 10),
    ("IM-CAT-GAUGE", "仪表读数", 20),
    ("IM-CAT-MECH", "机泵阀门与附件", 30),
    ("IM-CAT-VIDEO", "视频安防", 40),
    ("IM-CAT-COMM", "通信与网络", 50),
    ("IM-CAT-ACCESS", "门禁与通道", 60),
    ("IM-CAT-FIRE", "消防报警", 70),
    ("IM-CAT-ELEC", "电气与电源", 80),
    ("IM-CAT-SOFT", "软件与系统", 90),
]

# method.code -> category.code
METHOD_TO_CAT = {
    "IM-TPL-VISUAL-01": "IM-CAT-GENERAL",
    "IM-TPL-PHOTO-RECORD": "IM-CAT-GENERAL",
    "IM-TPL-MARKING-READ": "IM-CAT-GENERAL",
    "IM-TPL-GAUGE-READ": "IM-CAT-GAUGE",
    "IM-TPL-LEVEL-TEMP-READ": "IM-CAT-GAUGE",
    "IM-TPL-PRESSURE-READ": "IM-CAT-GAUGE",
    "IM-TPL-LEAK-CHECK": "IM-CAT-MECH",
    "IM-TPL-VALVE-POSITION": "IM-CAT-MECH",
    "IM-TPL-VALVE-OPERATE": "IM-CAT-MECH",
    "IM-TPL-VIBRATION-LISTEN": "IM-CAT-MECH",
    "IM-TPL-MOTOR-TEMP": "IM-CAT-MECH",
    "IM-TPL-LUBE-LEVEL": "IM-CAT-MECH",
    "IM-TPL-GROUNDING-VISUAL": "IM-CAT-MECH",
    "IM-TPL-FIRE-DIKE": "IM-CAT-MECH",
    "IM-TPL-RELIEF-VALVE": "IM-CAT-MECH",
    "IM-TPL-CAM-LENS": "IM-CAT-VIDEO",
    "IM-TPL-CAM-IMAGE": "IM-CAT-VIDEO",
    "IM-TPL-CAM-PTZ": "IM-CAT-VIDEO",
    "IM-TPL-CAM-IR": "IM-CAT-VIDEO",
    "IM-TPL-CAM-OSD": "IM-CAT-VIDEO",
    "IM-TPL-INTERCOM": "IM-CAT-COMM",
    "IM-TPL-NETWORK-LINK": "IM-CAT-COMM",
    "IM-TPL-LINK-COMM": "IM-CAT-COMM",
    "IM-TPL-RADIO-RF": "IM-CAT-COMM",
    "IM-TPL-ACCESS-CARD": "IM-CAT-ACCESS",
    "IM-TPL-DOOR-LOCK": "IM-CAT-ACCESS",
    "IM-TPL-PASSAGE-CLEAR": "IM-CAT-ACCESS",
    "IM-TPL-EMERGENCY-LIGHT": "IM-CAT-FIRE",
    "IM-TPL-SOUND-LIGHT": "IM-CAT-FIRE",
    "IM-TPL-DETECTOR": "IM-CAT-FIRE",
    "IM-TPL-ALARM-ZONE": "IM-CAT-FIRE",
    "IM-TPL-POWER-SELFTEST": "IM-CAT-ELEC",
    "IM-TPL-BATTERY-STATUS": "IM-CAT-ELEC",
    "IM-TPL-COOLING-FAN": "IM-CAT-ELEC",
    "IM-TPL-ELECTRICAL-PANEL": "IM-CAT-ELEC",
    "IM-TPL-INDICATOR-LIGHT": "IM-CAT-ELEC",
    "IM-TPL-CABLE-JOINT": "IM-CAT-ELEC",
    "IM-TPL-SOFTWARE-LOGIN": "IM-CAT-SOFT",
}


def main() -> None:
    c = psycopg2.connect(**DSN)
    c.autocommit = False
    cur = c.cursor()
    cur.execute("SET search_path TO dynamicbusiness")

    cur.execute(
        """
        SELECT id FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code=%s AND code=%s
        """,
        (TENANT, CTYPE, ROOT_CODE),
    )
    root = cur.fetchone()
    if not root:
        raise SystemExit(f"missing root category {ROOT_CODE} for {CTYPE}")
    root_id = root[0]
    print("root", root_id)

    cat_ids: dict[str, int] = {}
    for code, name, sort in CATEGORIES:
        cur.execute(
            """
            SELECT id FROM dynamic_category
            WHERE deleted=false AND tenant_id=%s
              AND category_type_code=%s AND code=%s
            """,
            (TENANT, CTYPE, code),
        )
        row = cur.fetchone()
        if row:
            cat_id = row[0]
            cur.execute(
                """
                UPDATE dynamic_category
                SET name=%s, parent_id=%s, parent_code=%s, sort=%s, level=2,
                    tree_path=%s, status=1, updater='seed', update_time=NOW()
                WHERE id=%s
                """,
                (name, root_id, ROOT_CODE, sort, f"/{root_id}/{cat_id}/", cat_id),
            )
            print("update cat", code, cat_id)
        else:
            cur.execute(
                """
                INSERT INTO dynamic_category (
                  parent_id, name, code, category_type_code, tree_path, level, sort,
                  status, description, creator, tenant_id, parent_code, deleted
                ) VALUES (
                  %s, %s, %s, %s, NULL, 2, %s,
                  1, %s, 'seed', %s, %s, false
                ) RETURNING id
                """,
                (
                    root_id,
                    name,
                    code,
                    CTYPE,
                    sort,
                    f"检查方法手段分类：{name}",
                    TENANT,
                    ROOT_CODE,
                ),
            )
            cat_id = cur.fetchone()[0]
            cur.execute(
                "UPDATE dynamic_category SET tree_path=%s WHERE id=%s",
                (f"/{root_id}/{cat_id}/", cat_id),
            )
            print("insert cat", code, cat_id)
        cat_ids[code] = cat_id

    # 清掉本类型旧关联后按映射重挂（整理口径）
    cur.execute(
        """
        UPDATE dynamic_entity_category_relation_t1
        SET deleted=true, updater='seed', update_time=NOW()
        WHERE deleted=false AND tenant_id=%s AND entity_type_code=%s
        """,
        (TENANT, CTYPE),
    )
    print("soft-deleted old links:", cur.rowcount)

    cur.execute(
        """
        SELECT id, code FROM ent_inspection_method_t1
        WHERE deleted=false AND tenant_id=%s
        """,
        (TENANT,),
    )
    methods = cur.fetchall()
    linked = 0
    skipped = 0
    for mid, mcode in methods:
        cat_code = METHOD_TO_CAT.get(mcode)
        if not cat_code or cat_code not in cat_ids:
            print("WARN no category for method", mcode)
            skipped += 1
            continue
        cat_id = cat_ids[cat_code]
        cur.execute(
            """
            INSERT INTO dynamic_entity_category_relation_t1 (
              entity_id, category_id, entity_type_code, sort,
              creator, tenant_id, deleted
            ) VALUES (%s, %s, %s, 10, 'seed', %s, false)
            """,
            (mid, cat_id, CTYPE, TENANT),
        )
        linked += 1

    c.commit()
    print(f"COMMIT OK linked={linked} skipped={skipped} cats={len(cat_ids)}")

    cur.execute(
        """
        SELECT c.code, c.name, count(r.id)
        FROM dynamic_category c
        LEFT JOIN dynamic_entity_category_relation_t1 r
          ON r.category_id=c.id AND r.deleted=false AND r.entity_type_code=%s
        WHERE c.deleted=false AND c.tenant_id=%s AND c.category_type_code=%s
          AND c.parent_id IS NOT NULL
        GROUP BY c.code, c.name, c.sort
        ORDER BY c.sort
        """,
        (CTYPE, TENANT, CTYPE),
    )
    print("dist:")
    for r in cur.fetchall():
        print(r)
    c.close()


if __name__ == "__main__":
    main()
