"""
回填 ent_equipment_t1.facility_id（Wave 2 列表过滤后空站场导致不可见）。

规则（租户 1）:
- 设备所属站场统一落到金桥厂区 FAC-JINQIAO (44)
- 含原东营油库(1)、空站场、误挂租户1「光谷管廊」的样例
- 武汉光谷管廊属租户 2：禁止在租户 1 建同名站场；误建的软删

幂等：可重复执行。
"""
import psycopg2

DSN = dict(host="127.0.0.1", dbname="sinopec", user="postgres", password="Coolhomer")
JINQIAO_ID = 44
DONGYING_ID = 1


def main():
    c = psycopg2.connect(**DSN)
    c.autocommit = False
    cur = c.cursor()
    cur.execute("SET search_path TO dynamicbusiness")

    cur.execute(
        """
        UPDATE ent_facility_t1
        SET deleted = true,
            updater = 'migrate-facility-owning',
            update_time = NOW()
        WHERE code = 'FAC-CORRIDOR-WH-GGGL'
          AND tenant_id = 1
          AND deleted = false
        """
    )
    print("soft-deleted t1 fake corridor facilities:", cur.rowcount)

    cur.execute(
        """
        UPDATE ent_equipment_t1 e
        SET facility_id = %s,
            updater = 'migrate-facility-owning',
            update_time = NOW()
        WHERE e.deleted = false
          AND (
            e.facility_id IS NULL
            OR e.facility_id = %s
            OR e.facility_id IN (
              SELECT id FROM ent_facility_t1
              WHERE code = 'FAC-CORRIDOR-WH-GGGL' AND tenant_id = 1
            )
          )
        """,
        (JINQIAO_ID, DONGYING_ID),
    )
    print("updated →金桥:", cur.rowcount)

    cur.execute(
        """
        SELECT facility_id, count(*) FROM ent_equipment_t1
        WHERE deleted = false GROUP BY facility_id ORDER BY count(*) DESC
        """
    )
    print("final dist:", cur.fetchall())

    cur.execute(
        """
        SELECT count(*) FROM ent_equipment_t1
        WHERE deleted = false AND facility_id IS NULL
        """
    )
    nulls = cur.fetchone()[0]
    print("still null:", nulls)
    if nulls:
        c.rollback()
        raise SystemExit("abort: still have null facility_id")

    c.commit()
    print("COMMIT OK")
    c.close()


if __name__ == "__main__":
    main()
