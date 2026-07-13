#!/usr/bin/env python3
"""Generate Fujian facility + Pattern C category seeds (pipeline line → stations under facility)."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
SEED_DIR = (
    ROOT
    / "Sinopec-SpringCloud/cheers-module-dynamicbusiness/scripts/platform-import/system/seed"
)
FACILITY_OUT = SEED_DIR / "dynamic_entity_facility_fujian_dev_sample.sql"
CATEGORY_OUT = SEED_DIR / "dynamic_category_facility_fujian.sql"

# 站场 REF_REGION 统一挂福建省管网（region Pattern C 实体）
REG_PROV_FJ_ENTITY_ID = 100024

PIPELINES = {
    "CPY": {
        "pipeline_entity_id": 42,
        "pipeline_code": "FAC-PIPE-FJ-CPY",
        "pipeline_model": "MODEL-FACILITY-PIPELINE-CP",
        "category_code": "FAC-CAT-PIPE-FJ-CPY",
        "label": "福建成品油管道",
        "medium": "cp",
        "sort": 1,
        "stations": [
            "泉港油库",
            "兴闽站",
            "石湖山站",
            "东孚站",
        ],
    },
    "W3": {
        "pipeline_entity_id": 40,
        "pipeline_code": "FAC-PIPE-FJ-W3",
        "pipeline_model": "MODEL-FACILITY-PIPELINE-NG",
        "category_code": "FAC-CAT-PIPE-FJ-W3",
        "label": "西三线",
        "medium": "ng",
        "sort": 2,
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
        "pipeline_entity_id": 41,
        "pipeline_code": "FAC-PIPE-FJ-HX2",
        "pipeline_model": "MODEL-FACILITY-PIPELINE-NG",
        "category_code": "FAC-CAT-PIPE-FJ-HX2",
        "label": "海西二期",
        "medium": "ng",
        "sort": 3,
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
    "LNGL": {
        "pipeline_entity_id": 43,
        "pipeline_code": "FAC-PIPE-FJ-LNGL",
        "pipeline_model": "MODEL-FACILITY-PIPELINE-NG",
        "category_code": "FAC-CAT-PIPE-FJ-LNGL",
        "label": "漳州LNG联络线",
        "medium": "ng",
        "sort": 4,
        "stations": [
            "程溪分输站",
            "港尾分输站",
        ],
    },
}

STATION_ID_START = {
    "CPY": 34,
    "W3": 3,
    "HX2": 16,
    "LNGL": 38,
}

CENTER_STATIONS = {
    "泉港油库",
    "福州末站",
    "莆田分输清管站",
    "安溪分输站",
    "海沧分输站",
    "漳州分输清管站",
    "龙岩分输清管站",
    "宁德分输站",
    "常山分输站",
}

MEDIUM_MODEL_SUFFIX = {
    "cr": "CR",
    "cp": "CP",
    "ng": "NG",
}


def infer_model(name: str, medium: str) -> str:
    medium = medium if medium in MEDIUM_MODEL_SUFFIX else "ng"
    suffix = MEDIUM_MODEL_SUFFIX[medium]

    if name.endswith("油库"):
        return (
            "MODEL-FACILITY-REFINED-DEPOT"
            if medium == "cp"
            else "MODEL-FACILITY-DEPOT"
        )
    if "分输清管" in name or ("分输" in name and "清管" in name):
        return f"MODEL-FACILITY-{suffix}-DISTRIBUTION-PIGGING"
    if "清管" in name:
        return f"MODEL-FACILITY-{suffix}-PIGGING"
    if "分输" in name:
        return f"MODEL-FACILITY-{suffix}-DISTRIBUTION"
    if name.endswith("末站"):
        return f"MODEL-FACILITY-{suffix}-TERMINAL"
    if name.endswith("首站"):
        return f"MODEL-FACILITY-{suffix}-HEAD"
    if "接收" in name:
        return f"MODEL-FACILITY-{suffix}-RECEIVING"
    if name.endswith("阀室"):
        return f"MODEL-FACILITY-{suffix}-VALVE-CHAMBER"
    if name.endswith("站"):
        return f"MODEL-FACILITY-{suffix}-DISTRIBUTION"
    return f"MODEL-FACILITY-{suffix}-DISTRIBUTION"


def facility_type(model_code: str) -> str:
    mapping = {
        "MODEL-FACILITY-DEPOT": "depot",
        "MODEL-FACILITY-REFINED-DEPOT": "refined_depot",
        "MODEL-FACILITY-NG-RECEIVING": "ng_receiving",
        "MODEL-FACILITY-NG-TERMINAL": "ng_terminal",
        "MODEL-FACILITY-NG-HEAD": "ng_head",
        "MODEL-FACILITY-NG-DISTRIBUTION-PIGGING": "ng_distribution_pigging",
        "MODEL-FACILITY-NG-PIGGING": "ng_pigging",
        "MODEL-FACILITY-NG-DISTRIBUTION": "ng_distribution",
        "MODEL-FACILITY-NG-VALVE-CHAMBER": "ng_valve_chamber",
        "MODEL-FACILITY-CR-DISTRIBUTION-PIGGING": "cr_distribution_pigging",
        "MODEL-FACILITY-CR-PIGGING": "cr_pigging",
        "MODEL-FACILITY-CR-DISTRIBUTION": "cr_distribution",
        "MODEL-FACILITY-CR-TERMINAL": "cr_terminal",
        "MODEL-FACILITY-CR-HEAD": "cr_head",
        "MODEL-FACILITY-CR-RECEIVING": "cr_receiving",
        "MODEL-FACILITY-CR-VALVE-CHAMBER": "cr_valve_chamber",
        "MODEL-FACILITY-CP-DISTRIBUTION-PIGGING": "cp_distribution_pigging",
        "MODEL-FACILITY-CP-PIGGING": "cp_pigging",
        "MODEL-FACILITY-CP-DISTRIBUTION": "cp_distribution",
        "MODEL-FACILITY-CP-TERMINAL": "cp_terminal",
        "MODEL-FACILITY-CP-HEAD": "cp_head",
        "MODEL-FACILITY-CP-RECEIVING": "cp_receiving",
        "MODEL-FACILITY-CP-VALVE-CHAMBER": "cp_valve_chamber",
        "MODEL-FACILITY-VALVE-CHAMBER": "valve_chamber",
        "MODEL-FACILITY-STATION": "station",
        "MODEL-FACILITY-PIPELINE-CR": "pipeline_cr",
        "MODEL-FACILITY-PIPELINE-CP": "pipeline_cp",
        "MODEL-FACILITY-PIPELINE-NG": "pipeline_ng",
    }
    return mapping.get(model_code, "station")


def station_category_code(pipe_key: str, idx: int) -> str:
    return f"FAC-CAT-ST-FJ-{pipe_key}-{idx:03d}"


def build_station_rows() -> tuple[list[str], list[dict], int]:
    rows: list[str] = []
    stations_meta: list[dict] = []
    max_id = 43

    for pipe_key, pipe in PIPELINES.items():
        facility_id = STATION_ID_START[pipe_key]
        for idx, name in enumerate(pipe["stations"], start=1):
            medium = pipe.get("medium", "ng")
            model = infer_model(name, medium)
            code = f"FAC-FJ-{pipe_key}-{idx:03d}"
            rows.append(
                "        (\n"
                f"            {facility_id},\n"
                f"            '{code}',\n"
                f"            '{name}',\n"
                f"            '/{facility_id}/',\n"
                f"            {idx},\n"
                f"            '{model}',\n"
                f"            {REG_PROV_FJ_ENTITY_ID},\n"
                f"            '福建省',\n"
                f"            NULL::numeric,\n"
                f"            NULL::numeric,\n"
                f"            '{facility_type(model)}'\n"
                "        )"
            )
            stations_meta.append(
                {
                    "pipe_key": pipe_key,
                    "entity_id": facility_id,
                    "entity_code": code,
                    "name": name,
                    "model": model,
                    "category_code": station_category_code(pipe_key, idx),
                    "sort": idx,
                }
            )
            max_id = max(max_id, facility_id)
            facility_id += 1

    return rows, stations_meta, max_id


def write_facility_sql(rows: list[str], max_id: int) -> None:
    center_names = "、".join(sorted(CENTER_STATIONS))
    values_sql = ",\n".join(rows)
    sql = f"""-- 开发联调 · 福建管道设施样例（来源：设备分类参考资料/工作范围及技术要求.docx）
-- 管道线路与站场均为 facility；站场 REF_REGION 挂福建省管网（entity id {REG_PROV_FJ_ENTITY_ID}）
-- 管道 ↔ 站场层级见 dynamic_category_facility_fujian.sql（Pattern C）
-- 中心站（实施参考）：{center_names}
-- 幂等：按 id upsert

SET search_path TO dynamicbusiness;

INSERT INTO ent_facility (
    id, entity_type_code, model_id, name, code, tenant_id, creator,
    tree_path, sort, status, deleted, region_id, address, longitude, latitude,
    facility_type, custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
    v.id, 'facility', m.id, v.name, v.code, 1, 'seed',
    v.tree_path, v.sort, 1, false, v.region_id, v.address, v.longitude, v.latitude,
    v.facility_type, '{{}}'::jsonb
FROM (
    VALUES
{values_sql}
) AS v(
    id, code, name, tree_path, sort, model_code, region_id,
    address, longitude, latitude, facility_type
)
JOIN dynamic_model m
  ON m.code = v.model_code AND m.entity_type_code = 'facility' AND m.deleted = false
ON CONFLICT (id) DO UPDATE SET
    model_id = EXCLUDED.model_id,
    name = EXCLUDED.name,
    code = EXCLUDED.code,
    region_id = EXCLUDED.region_id,
    facility_type = EXCLUDED.facility_type,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

SELECT setval(
    pg_get_serial_sequence('dynamicbusiness.ent_facility', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility), {max_id})
);
"""
    FACILITY_OUT.write_text(sql, encoding="utf-8")
    print(f"wrote {FACILITY_OUT} ({len(rows)} stations)")


def write_category_sql(stations_meta: list[dict]) -> None:
    pattern_c_rows = []

    for pipe_key, pipe in PIPELINES.items():
        pattern_c_rows.append(
            "("
            f"'{pipe['category_code']}', '{pipe['label']}', "
            f"{pipe['pipeline_entity_id']}, '{pipe['pipeline_code']}', "
            f"'{pipe['pipeline_model']}', {pipe['sort']}, 'facility_root'"
            ")"
        )

    for meta in stations_meta:
        pipe = PIPELINES[meta["pipe_key"]]
        pattern_c_rows.append(
            "("
            f"'{meta['category_code']}', '{meta['name']}', "
            f"{meta['entity_id']}, '{meta['entity_code']}', "
            f"'{meta['model']}', {meta['sort']}, '{pipe['category_code']}'"
            ")"
        )

    values_block = ",\n".join(f"  {row}" for row in pattern_c_rows)

    sql = f"""-- 福建设施 Pattern C：管道线路（facility 管道模型）→ 站场（facility 站型模型）
-- 前置：dynamic_entity_facility_fujian_dev_sample.sql、dynamic_model_facility.sql（含 PIPELINE 模型）
-- 组织维度：站场 region_id 已在设施 seed 挂 REG-PROV-FJ（{REG_PROV_FJ_ENTITY_ID}）

SET search_path TO dynamicbusiness;

DROP TABLE IF EXISTS tmp_facility_pattern_c;
CREATE TEMP TABLE tmp_facility_pattern_c AS
SELECT *
FROM (VALUES
{values_block}
) AS v(
  category_code, display_name, entity_id, entity_code, model_code, sort_order, parent_code
);

-- 1) 管道线路实体（facility · 管道模型）
INSERT INTO ent_facility (
  id, entity_type_code, model_id, name, code, tenant_id, creator,
  tree_path, sort, status, deleted, region_id, facility_type, custom_fields
)
SELECT
  n.entity_id, 'facility', m.id, n.display_name,
  COALESCE(n.entity_code, 'FAC-PIPE-' || n.entity_id::text),
  1, 'seed', '/' || n.entity_id::text || '/', n.sort_order, 1, false,
  {REG_PROV_FJ_ENTITY_ID},
  CASE m.code
    WHEN 'MODEL-FACILITY-PIPELINE-CP' THEN 'pipeline_cp'
    WHEN 'MODEL-FACILITY-PIPELINE-CR' THEN 'pipeline_cr'
    ELSE 'pipeline_ng'
  END,
  '{{}}'::jsonb
FROM tmp_facility_pattern_c n
JOIN dynamic_model m ON m.deleted = false AND m.code = n.model_code
WHERE n.model_code LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  region_id = EXCLUDED.region_id,
  facility_type = EXCLUDED.facility_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2a) 管道线路分类（挂 facility_root）
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'facility', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_facility_pattern_c n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1
 AND p.code = n.parent_code AND p.category_type_code = 'facility'
WHERE n.model_code LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2b) 站场分类（挂管道线路分类）
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'facility', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_facility_pattern_c n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1
 AND p.code = n.parent_code AND p.category_type_code = 'facility'
WHERE n.model_code NOT LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 3) 模型 ↔ 分类
INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'facility', m.code, c.code, 1, 1, 'seed'
FROM tmp_facility_pattern_c n
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN dynamic_model m ON m.deleted = false AND m.code = n.model_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 4) Pattern C 绑定（清理旧 facility 绑定后重建福建节点）
UPDATE dynamic_category_entity_link l
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE l.category_id = c.id
  AND c.deleted = false AND c.tenant_id = 1
  AND c.category_type_code = 'facility'
  AND c.code LIKE 'FAC-CAT-%'
  AND l.deleted = false;

INSERT INTO dynamic_category_entity_link (category_id, entity_id, entity_model_id, tenant_id, creator)
SELECT c.id, n.entity_id, e.model_id, 1, 'seed'
FROM tmp_facility_pattern_c n
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN ent_facility e ON e.id = n.entity_id AND e.deleted = false;

-- 5) 废弃 region 下误挂的管道节点与 ent_region pipeline 行
UPDATE dynamic_category
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code LIKE 'REG-CAT-PIPE-%';

UPDATE ent_region
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND region_type = 'pipeline';

UPDATE dynamic_model
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'MODEL-REGION-PIPELINE';

-- 6) 废弃独立 pipeline 数据类型及 ent_pipeline 演示数据
UPDATE dynamic_entity_type
SET status = 'inactive', deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE code = 'pipeline' AND tenant_id = 1;

UPDATE ent_pipeline SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1;

-- 7) 重建 facility 分类 tree_path / level（本 seed 在 import 全量 repair 之后执行）
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND c.category_type_code = 'facility'
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.category_type_code = 'facility'
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;

-- 8) Pattern C：facility 分类种类说明（数据管理读 ORG_RECORD 语义）
UPDATE dynamic_category_type
SET
  description = '管道线路 → 站场层级；Pattern C 分类即实体（与运营区域相同建立方式）',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'facility' AND tenant_id = 1 AND deleted = false;

-- 9) 数据管理布局：关闭型号列，与 region Pattern C 一致（分类 → 实体）
UPDATE dm_entity_dimension
SET enabled = false, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility' AND dimension_kind = 'MODEL' AND deleted = false;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_facility', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility), 43)
);
"""
    CATEGORY_OUT.write_text(sql, encoding="utf-8")
    print(f"wrote {CATEGORY_OUT} ({len(stations_meta) + len(PIPELINES)} Pattern C nodes)")


def main() -> None:
    rows, stations_meta, max_id = build_station_rows()
    write_facility_sql(rows, max_id)
    write_category_sql(stations_meta)


if __name__ == "__main__":
    main()
