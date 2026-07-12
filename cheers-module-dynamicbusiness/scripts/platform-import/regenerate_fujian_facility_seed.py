#!/usr/bin/env python3
"""Generate Fujian pipeline facility dev sample from 工作范围及技术要求.docx scope."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
OUT = (
    ROOT
    / "Sinopec-SpringCloud/cheers-module-dynamicbusiness/scripts/platform-import/system/seed"
    / "dynamic_entity_facility_fujian_dev_sample.sql"
)

PIPELINES = {
    "W3": {
        "region_id": 100114,
        "region_code": "REG-PIPE-FJ-W3",
        "label": "西三线",
        "stations": [
            "福州末站",
            "福清分输站",
            "莆田分输清管站",
            "仙游站",
            "泉州分输清管站",
            "同安分输站",
            "海沧分输站",
            "漳州分输清管站",
            "龙岩分输清管站",
            "长汀站",
            "南安站",
            "东田站",
            "角美站",
        ],
    },
    "HX2": {
        "region_id": 100115,
        "region_code": "REG-PIPE-FJ-HX2",
        "label": "海西二期",
        "stations": [
            "旧镇分输站",
            "常山分输站",
            "诏安末站",
            "天宝站",
            "南靖站",
            "龙岩站",
            "德化站",
            "琅岐分输站",
            "连江分输站",
            "罗源分输站",
            "宁德分输站",
            "双木洋清管站",
            "福安分输站",
            "柘荣分输站",
            "福鼎末站",
            "安溪分输站",
            "南安分输站",
            "永春分输站",
        ],
    },
}


def infer_model(name: str) -> str:
    if name.endswith("油库"):
        return "MODEL-FACILITY-REFINED-DEPOT"
    if name.endswith("末站"):
        return "MODEL-FACILITY-NG-RECEIVING"
    if "分输" in name or "清管" in name:
        return "MODEL-FACILITY-NG-DISTRIBUTION"
    if name.endswith("阀室"):
        return "MODEL-FACILITY-VALVE-CHAMBER"
    return "MODEL-FACILITY-STATION"


def slug(name: str) -> str:
    return re.sub(r"[^0-9A-Za-z\u4e00-\u9fff]+", "", name)


def facility_type(model_code: str) -> str:
    return {
        "MODEL-FACILITY-REFINED-DEPOT": "refined_depot",
        "MODEL-FACILITY-NG-RECEIVING": "ng_receiving",
        "MODEL-FACILITY-NG-DISTRIBUTION": "ng_distribution",
        "MODEL-FACILITY-VALVE-CHAMBER": "valve_chamber",
        "MODEL-FACILITY-STATION": "station",
    }[model_code]


def main() -> None:
    rows: list[str] = []
    facility_id = 3
    for pipe_key, pipe in PIPELINES.items():
        for idx, name in enumerate(pipe["stations"], start=1):
            model = infer_model(name)
            code = f"FAC-FJ-{pipe_key}-{idx:03d}"
            rows.append(
                "        (\n"
                f"            {facility_id},\n"
                f"            '{code}',\n"
                f"            '{name}',\n"
                f"            '/{facility_id}/',\n"
                f"            {idx},\n"
                f"            '{model}',\n"
                f"            {pipe['region_id']},\n"
                f"            '福建省',\n"
                f"            NULL::numeric,\n"
                f"            NULL::numeric,\n"
                f"            '{facility_type(model)}'\n"
                "        )"
            )
            facility_id += 1

    max_id = facility_id - 1
    values_sql = ",\n".join(rows)
    sql = """-- 开发联调 · 福建管道设施样例（来源：设备分类参考资料/工作范围及技术要求.docx）
-- 西三线 / 海西二期站场；REF_REGION 挂对应管道运营区域节点
-- 幂等：按 code upsert

SET search_path TO dynamicbusiness;

INSERT INTO ent_facility (
    id,
    entity_type_code,
    model_id,
    name,
    code,
    tenant_id,
    creator,
    tree_path,
    sort,
    status,
    deleted,
    region_id,
    address,
    longitude,
    latitude,
    facility_type,
    custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
    v.id,
    'facility',
    m.id,
    v.name,
    v.code,
    1,
    'seed',
    v.tree_path,
    v.sort,
    1,
    false,
    v.region_id,
    v.address,
    v.longitude,
    v.latitude,
    v.facility_type,
    '{}'::jsonb
FROM (
    VALUES
""" + values_sql + """
) AS v(
    id, code, name, tree_path, sort, model_code, region_id,
    address, longitude, latitude, facility_type
)
JOIN dynamic_model m
  ON m.code = v.model_code
 AND m.entity_type_code = 'facility'
 AND m.deleted = false
ON CONFLICT (id) DO UPDATE SET
    model_id = EXCLUDED.model_id,
    name = EXCLUDED.name,
    code = EXCLUDED.code,
    tenant_id = EXCLUDED.tenant_id,
    tree_path = EXCLUDED.tree_path,
    sort = EXCLUDED.sort,
    status = EXCLUDED.status,
    deleted = false,
    region_id = EXCLUDED.region_id,
    address = EXCLUDED.address,
    longitude = EXCLUDED.longitude,
    latitude = EXCLUDED.latitude,
    facility_type = EXCLUDED.facility_type,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

SELECT setval(
    pg_get_serial_sequence('dynamicbusiness.ent_facility', 'id'),
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility),
        """ + str(max_id) + """
    )
);
"""
    OUT.write_text(sql, encoding="utf-8")
    print(f"wrote {OUT} ({len(rows)} facilities)")


if __name__ == "__main__":
    main()
