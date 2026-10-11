# 飞行中的探照灯和喊话。只加官网实时下行里有的指令，不写进航线文件。
# 适用飞机是机场 3 的 M4D、M4TD。已有同编码则改内容，不另插一条。
import psycopg2
from psycopg2.extras import Json

M4D = 1874
M4TD = 1875
CAT_MANUAL = 2104823
CAT_DRONE = 2104822
ACTION_MODEL = 1774
PROTOCOL_MODEL = 1856
MAPPING_MODEL = 1850
TOPIC = "thing/product/{gateway_sn}/drc/down"


def light_fields():
    return [
        {"fieldCode": "selected_searchlight", "valueSource": "USER", "required": True, "defaultValue": None},
        {"fieldCode": "psdk_index", "valueSource": "EXECUTOR_MOUNT", "required": True, "defaultValue": None},
    ]


def fine_fields():
    return [
        {
            "fieldCode": "searchlight_fine_value",
            "valueSource": "INTERACTION",
            "required": True,
            "defaultValue": 0,
            "min": -3,
            "max": 3,
        },
        {
            "fieldCode": "searchlight_fine_saved",
            "valueSource": "INTERACTION",
            "required": True,
            "defaultValue": False,
        },
    ]


def speaker_fields():
    return [
        {"fieldCode": "selected_speaker", "valueSource": "USER", "required": True, "defaultValue": None},
        {"fieldCode": "psdk_index", "valueSource": "EXECUTOR_MOUNT", "required": True, "defaultValue": None},
    ]


def slots(fields):
    return {
        "version": 2,
        "methods": [
            {
                "fields": [],
                "executionMeans": "UAV",
                "applicableModelIds": [M4D, M4TD],
                "applicableCategoryIds": [CAT_DRONE],
                "paramsPerEquipmentModel": True,
                "paramsSharedAcrossModels": False,
            },
            {"fields": fields, "executionMeans": "UAV", "equipmentModelId": M4D},
            {"fields": fields, "executionMeans": "UAV", "equipmentModelId": M4TD},
        ],
    }


def desc(code, path, label, description, mapping_required=True):
    return {
        "code": code,
        "path": path,
        "type": "integer",
        "label": label,
        "required": True,
        "mappingRequired": mapping_required,
        "description": description,
    }


def protocol_bag(method, data, descriptions):
    return {
        "opcode": 0,
        "mqtt_topic": TOPIC,
        "command_json": {"outbound": {"method": method, "data": data}},
        "protocol_version": ["dji-dock-1.16.1"],
        "command_direction": "downlink",
        "communication_protocol": "mqtt",
        "field_description_json": {"outbound": descriptions},
    }


PSDK = desc("psdk_index", "data.psdk_index", "负载序号", "机场推上来的 PSDK 序号。不是相机云台位置。")
GROUP = desc("group", "data.group", "主探照灯", "官网固定 0，主探照灯。", mapping_required=False)

ACTIONS = [
    {
        "name": "手动关闭探照灯",
        "code": "lightModeOff",
        "method": "drc_light_mode_set",
        "data": {"psdk_index": 0, "group": 0, "mode": 0},
        "descriptions": [PSDK, GROUP, desc("mode", "data.mode", "模式", "0 关闭。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动开启探照灯",
        "code": "lightModeOn",
        "method": "drc_light_mode_set",
        "data": {"psdk_index": 0, "group": 0, "mode": 1},
        "descriptions": [PSDK, GROUP, desc("mode", "data.mode", "模式", "1 常亮。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动探照灯爆闪",
        "code": "lightModeStrobe",
        "method": "drc_light_mode_set",
        "data": {"psdk_index": 0, "group": 0, "mode": 2},
        "descriptions": [PSDK, GROUP, desc("mode", "data.mode", "模式", "2 爆闪。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动探照灯快速爆闪",
        "code": "lightModeFastStrobe",
        "method": "drc_light_mode_set",
        "data": {"psdk_index": 0, "group": 0, "mode": 3},
        "descriptions": [PSDK, GROUP, desc("mode", "data.mode", "模式", "3 快速爆闪。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动探照灯交替爆闪",
        "code": "lightModeAlternate",
        "method": "drc_light_mode_set",
        "data": {"psdk_index": 0, "group": 0, "mode": 4},
        "descriptions": [PSDK, GROUP, desc("mode", "data.mode", "模式", "4 交替爆闪。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动设置探照灯亮度",
        "code": "lightBrightnessSet",
        "method": "drc_light_brightness_set",
        "data": {"psdk_index": 0, "group": 0, "brightness": 1},
        "descriptions": [
            PSDK,
            GROUP,
            desc("brightness", "data.brightness", "亮度", "1 到 100。"),
        ],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.brightness", "slot": "searchlightBrightness", "sourceKind": "INTERACTION"},
        ],
        "fields": light_fields()
        + [
            {
                "fieldCode": "searchlightBrightness",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 50,
                "min": 1,
                "max": 100,
            }
        ],
    },
    {
        "name": "手动停止喊话",
        "code": "speakerStop",
        "method": "drc_speaker_play_stop",
        "data": {"psdk_index": 0},
        "descriptions": [PSDK],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": speaker_fields(),
    },
    {
        "name": "手动重新喊话",
        "code": "speakerReplay",
        "method": "drc_speaker_replay",
        "data": {"psdk_index": 0},
        "descriptions": [PSDK],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": speaker_fields(),
    },
    {
        "name": "手动设置喊话音量",
        "code": "speakerVolumeSet",
        "method": "drc_speaker_play_volume_set",
        "data": {"psdk_index": 0, "play_volume": 60},
        "descriptions": [PSDK, desc("play_volume", "data.play_volume", "音量", "1 到 100。")],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.play_volume", "slot": "megaphone_volume", "sourceKind": "INTERACTION"},
        ],
        "fields": speaker_fields()
        + [
            {
                "fieldCode": "megaphone_volume",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 60,
                "min": 1,
                "max": 100,
            }
        ],
    },
    {
        "name": "手动喊话单次播放",
        "code": "speakerPlayOnce",
        "method": "drc_speaker_play_mode_set",
        "data": {"psdk_index": 0, "play_mode": 0},
        "descriptions": [PSDK, desc("play_mode", "data.play_mode", "播放方式", "0 单次播放。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": speaker_fields(),
    },
    {
        "name": "手动喊话循环播放",
        "code": "speakerPlayLoop",
        "method": "drc_speaker_play_mode_set",
        "data": {"psdk_index": 0, "play_mode": 1},
        "descriptions": [PSDK, desc("play_mode", "data.play_mode", "播放方式", "1 循环播放。", mapping_required=False)],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": speaker_fields(),
    },
    {
        "name": "手动播放喊话文本",
        "code": "speakerTtsPlay",
        "method": "drc_speaker_tts_play_start",
        "data": {"psdk_index": 0, "tts": {"name": "tts", "text": "", "md5": ""}},
        "descriptions": [
            PSDK,
            {
                "code": "text",
                "path": "data.tts.text",
                "type": "string",
                "label": "喊话文本",
                "required": True,
                "description": "当时要念出来的文字。",
            },
            {
                "code": "md5",
                "path": "data.tts.md5",
                "type": "string",
                "label": "文本校验",
                "required": True,
                "description": "这段文字的校验和。由系统按文字算出，不手填。",
            },
        ],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.tts.text", "slot": "speaker_tts_text", "sourceKind": "INTERACTION"},
            {"path": "data.tts.md5", "slot": "speaker_tts_md5", "sourceKind": "SYSTEM"},
        ],
        "fields": speaker_fields()
        + [
            {
                "fieldCode": "speaker_tts_text",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": None,
            }
        ],
    },
    {
        "name": "手动校准探照灯",
        "code": "lightCalibrate",
        "method": "drc_light_calibration",
        "data": {"psdk_index": 0},
        "descriptions": [PSDK],
        "maps": [{"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"}],
        "fields": light_fields(),
    },
    {
        "name": "手动微调左探照灯",
        "code": "lightFineLeft",
        "method": "drc_light_fine_tuning_set",
        "data": {"psdk_index": 0, "position": 0, "value": 0, "saved": False},
        "descriptions": [
            PSDK,
            desc("position", "data.position", "灯", "0 左灯。", mapping_required=False),
            desc("value", "data.value", "微调角度", "-3 到 3，单位度。"),
            {
                "code": "saved",
                "path": "data.saved",
                "type": "boolean",
                "label": "是否保存",
                "required": True,
                "description": "真表示把这次微调保存下来。",
            },
        ],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.value", "slot": "searchlight_fine_value", "sourceKind": "INTERACTION"},
            {"path": "data.saved", "slot": "searchlight_fine_saved", "sourceKind": "INTERACTION"},
        ],
        "fields": light_fields() + fine_fields(),
    },
    {
        "name": "手动微调右探照灯",
        "code": "lightFineRight",
        "method": "drc_light_fine_tuning_set",
        "data": {"psdk_index": 0, "position": 1, "value": 0, "saved": False},
        "descriptions": [
            PSDK,
            desc("position", "data.position", "灯", "1 右灯。", mapping_required=False),
            desc("value", "data.value", "微调角度", "-3 到 3，单位度。"),
            {
                "code": "saved",
                "path": "data.saved",
                "type": "boolean",
                "label": "是否保存",
                "required": True,
                "description": "真表示把这次微调保存下来。",
            },
        ],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.value", "slot": "searchlight_fine_value", "sourceKind": "INTERACTION"},
            {"path": "data.saved", "slot": "searchlight_fine_saved", "sourceKind": "INTERACTION"},
        ],
        "fields": light_fields() + fine_fields(),
    },
    {
        "name": "手动设置喊话语音",
        "code": "speakerTtsSet",
        "method": "drc_speaker_tts_set",
        "data": {"psdk_index": 0, "volume": 60, "type": 0, "language": 0, "speed": 50},
        "descriptions": [
            PSDK,
            desc("volume", "data.volume", "音量", "1 到 100。"),
            desc("type", "data.type", "声音", "0 男声，1 女声。"),
            desc("language", "data.language", "语言", "0 中文，1 英文。"),
            desc("speed", "data.speed", "语速", "1 到 100。"),
        ],
        "maps": [
            {"path": "data.psdk_index", "slot": "psdk_index", "sourceKind": "ACTION_PARAM"},
            {"path": "data.volume", "slot": "megaphone_volume", "sourceKind": "INTERACTION"},
            {"path": "data.type", "slot": "speaker_voice_type", "sourceKind": "INTERACTION"},
            {"path": "data.language", "slot": "speaker_tts_language", "sourceKind": "INTERACTION"},
            {"path": "data.speed", "slot": "speaker_tts_speed", "sourceKind": "INTERACTION"},
        ],
        "fields": speaker_fields()
        + [
            {
                "fieldCode": "megaphone_volume",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 60,
                "min": 1,
                "max": 100,
            },
            {
                "fieldCode": "speaker_voice_type",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 0,
                "min": 0,
                "max": 1,
            },
            {
                "fieldCode": "speaker_tts_language",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 0,
                "min": 0,
                "max": 1,
            },
            {
                "fieldCode": "speaker_tts_speed",
                "valueSource": "INTERACTION",
                "required": True,
                "defaultValue": 50,
                "min": 1,
                "max": 100,
            },
        ],
    },
]


def upsert_action(cur, spec, sort):
    cur.execute(
        "SELECT id FROM dynamicbusiness.ent_action_t1 WHERE code = %s AND deleted = false",
        (spec["code"],),
    )
    row = cur.fetchone()
    payload = Json(slots(spec["fields"]))
    if row:
        action_id = row[0]
        cur.execute(
            """
            UPDATE dynamicbusiness.ent_action_t1
               SET name = %s, execution_means = %s, param_slots_json = %s, deleted = false
             WHERE id = %s
            """,
            (spec["name"], Json(["UAV"]), payload, action_id),
        )
        return action_id
    cur.execute(
        """
        INSERT INTO dynamicbusiness.ent_action_t1
            (tenant_id, entity_type_code, model_id, name, code, parent_id, status, sort,
             execution_means, param_slots_json, deleted)
        VALUES (1, 'action', %s, %s, %s, 0, 1, %s, %s, %s, false)
        RETURNING id
        """,
        (ACTION_MODEL, spec["name"], spec["code"], sort, Json(["UAV"]), payload),
    )
    return cur.fetchone()[0]


def upsert_protocol(cur, spec):
    code = "proto-dji-dock-drc-" + spec["code"]
    bag = protocol_bag(spec["method"], spec["data"], spec["descriptions"])
    cur.execute(
        "SELECT id FROM dynamicbusiness.ent_data_protocol_t1 WHERE code = %s AND deleted = false",
        (code,),
    )
    row = cur.fetchone()
    if row:
        protocol_id = row[0]
        cur.execute(
            """
            UPDATE dynamicbusiness.ent_data_protocol_t1
               SET name = %s, custom_fields = %s, command_json = %s,
                   protocol_version = %s, communication_protocol = 'mqtt',
                   command_direction = 'downlink', deleted = false
             WHERE id = %s
            """,
            (spec["name"], Json(bag), Json(bag["command_json"]), Json(["dji-dock-1.16.1"]), protocol_id),
        )
        return protocol_id
    cur.execute(
        """
        INSERT INTO dynamicbusiness.ent_data_protocol_t1
            (tenant_id, entity_type_code, model_id, name, code, parent_id, status,
             custom_fields, command_json, command_direction, communication_protocol,
             protocol_version, deleted)
        VALUES (1, 'data_protocol', %s, %s, %s, 0, 1, %s, %s, 'downlink', 'mqtt', %s, false)
        RETURNING id
        """,
        (PROTOCOL_MODEL, spec["name"], code, Json(bag), Json(bag["command_json"]), Json(["dji-dock-1.16.1"])),
    )
    return cur.fetchone()[0]


def upsert_mapping(cur, action_id, protocol_id, spec):
    code = "map-drc-" + spec["code"] + "-dji-dock"
    bag = {
        "action_ref": {"id": action_id, "entityTypeCode": "action"},
        "mapped_protocol_version": "dji-dock-1.16.1",
        "param_slot_mapping_json": spec["maps"],
        "protocol_instruction_ref": {"id": protocol_id, "entityTypeCode": "data_protocol"},
    }
    cur.execute(
        "SELECT id FROM dynamicbusiness.ent_protocol_mapping_t1 WHERE code = %s AND deleted = false",
        (code,),
    )
    row = cur.fetchone()
    name = spec["name"] + " · 大疆机场"
    if row:
        cur.execute(
            """
            UPDATE dynamicbusiness.ent_protocol_mapping_t1
               SET name = %s, custom_fields = %s, deleted = false
             WHERE id = %s
            """,
            (name, Json(bag), row[0]),
        )
        return
    cur.execute(
        """
        INSERT INTO dynamicbusiness.ent_protocol_mapping_t1
            (tenant_id, entity_type_code, model_id, name, code, parent_id, status, custom_fields, deleted)
        VALUES (1, 'protocol_mapping', %s, %s, %s, 0, 1, %s, false)
        """,
        (MAPPING_MODEL, name, code, Json(bag)),
    )


def ensure_category(cur, action_id):
    cur.execute(
        """
        SELECT id FROM dynamicbusiness.dynamic_entity_category_relation_t1
        WHERE entity_type_code = 'action' AND entity_id = %s AND category_id = %s
          AND deleted = false
        """,
        (action_id, CAT_MANUAL),
    )
    if cur.fetchone():
        return
    cur.execute(
        """
        INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1
            (entity_id, category_id, entity_type_code, sort, creator, deleted, tenant_id)
        VALUES (%s, %s, 'action', 0, '1', false, 1)
        """,
        (action_id, CAT_MANUAL),
    )


def ensure_field(cur, code, name, field_type, description):
    cur.execute(
        "SELECT id FROM dynamicbusiness.dynamic_field WHERE code = %s AND deleted = false",
        (code,),
    )
    if cur.fetchone():
        return
    cur.execute(
        """
        INSERT INTO dynamicbusiness.dynamic_field
            (code, name, type, description, source, status, tenant_id, deleted)
        VALUES (%s, %s, %s, %s, 'LIBRARY', 1, 1, false)
        """,
        (code, name, field_type, description),
    )


def ensure_tts_field(cur):
    ensure_field(cur, "speaker_tts_text", "喊话文本", "STRING", "当时要念出来的文字。")
    ensure_field(cur, "searchlight_fine_value", "探照灯微调角度", "INTEGER", "-3 到 3，单位度。")
    ensure_field(cur, "searchlight_fine_saved", "保存探照灯微调", "BOOLEAN", "真表示把这次微调保存下来。")
    ensure_field(cur, "speaker_voice_type", "喊话声音", "INTEGER", "0 男声，1 女声。")
    ensure_field(cur, "speaker_tts_language", "喊话语言", "INTEGER", "0 中文，1 英文。")
    ensure_field(cur, "speaker_tts_speed", "喊话语速", "INTEGER", "1 到 100。")


def main():
    conn = psycopg2.connect(
        host="127.0.0.1", port=5432, dbname="sinopec", user="postgres", password="Coolhomer"
    )
    cur = conn.cursor()
    ensure_tts_field(cur)
    for index, spec in enumerate(ACTIONS):
        action_id = upsert_action(cur, spec, 36100 + index)
        protocol_id = upsert_protocol(cur, spec)
        upsert_mapping(cur, action_id, protocol_id, spec)
        ensure_category(cur, action_id)
        print(spec["name"], action_id, protocol_id)
    cur.execute(
        "SELECT id FROM dynamicbusiness.ent_action_t1 WHERE code = 'megaphone' AND deleted = false"
    )
    existing = cur.fetchone()
    if existing:
        ensure_category(cur, existing[0])
        print("喊话器-开始播放音频 also in 手动操作", existing[0])
    conn.commit()
    conn.close()


if __name__ == "__main__":
    main()
