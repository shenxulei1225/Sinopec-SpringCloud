# 把协议版本和通讯方式写到指令的正式列上。
# 对照指向另一份协议的指令时，删掉这条对照，不改成猜一条。
import psycopg2

conn = psycopg2.connect(
    host="127.0.0.1", port=5432, dbname="sinopec", user="postgres", password="Coolhomer"
)
cur = conn.cursor()

cur.execute(
    """
    UPDATE dynamicbusiness.ent_data_protocol_t1
       SET protocol_version = custom_fields->'protocol_version'
     WHERE deleted = false
       AND protocol_version IS NULL
       AND jsonb_typeof(custom_fields->'protocol_version') = 'array'
    """
)
print("protocol_version filled", cur.rowcount)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_data_protocol_t1
       SET communication_protocol = custom_fields->>'communication_protocol'
     WHERE deleted = false
       AND (communication_protocol IS NULL OR communication_protocol = '')
       AND COALESCE(custom_fields->>'communication_protocol', '') <> ''
    """
)
print("communication_protocol filled", cur.rowcount)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_protocol_mapping_t1 m
       SET deleted = true
      FROM dynamicbusiness.ent_action_t1 a
     WHERE m.deleted = false
       AND a.id = (m.custom_fields->'action_ref'->>'id')::bigint
       AND m.custom_fields->>'mapped_protocol_version' = 'dji-pilot-1.16.1'
       AND a.code IN ('searchlightOn', 'searchlightOff', 'searchlightStrobe')
    """
)
print("pilot searchlight mappings removed", cur.rowcount)

cur.execute(
    """
    UPDATE dynamicbusiness.ent_protocol_mapping_t1 m
       SET deleted = true
      FROM dynamicbusiness.ent_data_protocol_t1 p
     WHERE m.deleted = false
       AND p.id = (m.custom_fields->'protocol_instruction_ref'->>'id')::bigint
       AND m.custom_fields->>'mapped_protocol_version' = 'robot-ws'
       AND NOT (p.protocol_version ? 'robot-ws')
    """
)
print("robot mappings pointed at another protocol removed", cur.rowcount)

conn.commit()
conn.close()
