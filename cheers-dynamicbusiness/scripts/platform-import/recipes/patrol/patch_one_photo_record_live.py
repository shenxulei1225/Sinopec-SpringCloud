# 推流就是开始直播、停止直播。拍照、录像各只留开始和停止这一条。
# 画面上的另一套停用。机器人拍照对照改挂到开始拍照。
import json
import psycopg2
from psycopg2.extras import Json

conn = psycopg2.connect(
    host="127.0.0.1", port=5432, dbname="sinopec", user="postgres", password="Coolhomer"
)
cur = conn.cursor()

def action_id(code):
    cur.execute(
        "SELECT id, param_slots_json, execution_means FROM dynamicbusiness.ent_action_t1 WHERE code=%s AND deleted=false",
        (code,),
    )
    return cur.fetchone()

take = action_id("takePhoto")
shoot = action_id("act-robot-shoot")
take_id, take_slots, take_means = take
shoot_id, shoot_slots, shoot_means = shoot

kept = []
for method in take_slots.get("methods", []):
    means = method.get("executionMeans")
    if means in ("MANUAL", "ROBOT", "FIXED_CAMERA"):
        continue
    kept.append(method)
for method in shoot_slots.get("methods", []):
    if method.get("executionMeans") == "UAV":
        continue
    kept.append(method)
take_slots["methods"] = kept

means = []
for item in list(take_means or []) + list(shoot_means or []):
    if item not in means:
        means.append(item)
cur.execute(
    """
    UPDATE dynamicbusiness.ent_action_t1
       SET param_slots_json = %s, execution_means = %s
     WHERE id = %s
    """,
    (Json(take_slots), Json(means), take_id),
)
print("takePhoto means", means)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_protocol_mapping_t1
       SET name = '开始拍照 · 机器人对接协议',
           custom_fields = jsonb_set(
             jsonb_set(custom_fields, '{action_ref,id}', %s::jsonb),
             '{action_ref,entityTypeCode}', '"action"'
           )
     WHERE deleted = false
       AND (custom_fields->'action_ref'->>'id')::bigint = %s
    """,
    (json.dumps(take_id), shoot_id),
)
print("robot photo mapping moved", cur.rowcount)

cur.execute(
    """
    INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1
        (entity_id, category_id, entity_type_code, sort, creator, deleted, tenant_id)
    SELECT %s, r.category_id, 'action', 0, '1', false, 1
    FROM dynamicbusiness.dynamic_entity_category_relation_t1 r
    WHERE r.entity_type_code = 'action' AND r.entity_id = %s AND r.deleted = false
      AND NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t1 x
        WHERE x.entity_type_code = 'action' AND x.entity_id = %s
          AND x.category_id = r.category_id AND x.deleted = false
      )
    """,
    (take_id, shoot_id, take_id),
)
print("categories copied", cur.rowcount)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_action_t1
       SET name = '停止拍照'
     WHERE code = 'photoStop' AND deleted = false
    """
)
cur.execute(
    """
    UPDATE dynamicbusiness.ent_protocol_mapping_t1 m
       SET name = replace(m.name, '手动停止拍照', '停止拍照')
      FROM dynamicbusiness.ent_action_t1 a
     WHERE a.id = (m.custom_fields->'action_ref'->>'id')::bigint
       AND a.code = 'photoStop' AND m.deleted = false
    """
)
print("photo stop renamed", cur.rowcount)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_protocol_mapping_t1 m
       SET deleted = true
      FROM dynamicbusiness.ent_action_t1 a
     WHERE m.deleted = false
       AND a.id = (m.custom_fields->'action_ref'->>'id')::bigint
       AND a.code IN ('photoTake', 'recordStart', 'recordStop', 'act-robot-shoot')
    """
)
print("duplicate mappings removed", cur.rowcount)
cur.execute(
    """
    UPDATE dynamicbusiness.ent_action_t1
       SET deleted = true
     WHERE deleted = false
       AND code IN ('photoTake', 'recordStart', 'recordStop', 'act-robot-shoot')
    """
)
print("duplicate actions removed", cur.rowcount)

cur.execute(
    """
    SELECT table_name FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND column_name = 'custom_fields'
    """
)
tables = [
    row[0] for row in cur.fetchall()
    if row[0] not in ("ent_action_t1", "ent_protocol_mapping_t1")
]
replaced = 0
for table in tables:
    cur.execute(
        f"""
        UPDATE dynamicbusiness.{table}
           SET custom_fields = replace(custom_fields::text, 'act-robot-shoot', 'takePhoto')::jsonb
         WHERE custom_fields::text LIKE '%act-robot-shoot%'
        """
    )
    if cur.rowcount:
        print("rewrote", table, cur.rowcount)
        replaced += cur.rowcount
print("references rewritten", replaced)

conn.commit()
conn.close()
