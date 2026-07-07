#!/usr/bin/env python3
"""按行业常用类别为 dynamic_field 生成字段池分组 seed（每组内字段互斥）。

输出：platform-import/system/04_field_groups_industry.sql
用法：
  PGPASSWORD=... python3 generate-field-group-seed.py
  PGPASSWORD=... python3 generate-field-group-seed.py --dry-run
"""
from __future__ import annotations

import argparse
import re
import subprocess
from dataclasses import dataclass
from pathlib import Path

TENANT_ID = 1
OUT = Path(__file__).resolve().parent / "platform-import/system/04_field_groups_industry.sql"

PSQL = [
    "psql",
    "-h",
    "127.0.0.1",
    "-U",
    "postgres",
    "-d",
    "sinopec",
    "-t",
    "-A",
    "-F",
    "\t",
]


@dataclass(frozen=True)
class FieldGroupDef:
    code: str
    name: str
    sort: int
    description: str


# 行业常用字段池分组（综合管廊 / 设施运维 / 石化园区通用）
GROUPS: list[FieldGroupDef] = [
    FieldGroupDef("FG-IND-COMMON", "通用信息", 10, "名称、编码、描述、状态、类型等基础属性"),
    FieldGroupDef("FG-IND-EQUIPMENT", "设备类", 20, "设备台账、资产、型号、维护与备件相关"),
    FieldGroupDef("FG-IND-INSPECTION", "巡检类", 30, "巡检、点检、检测项与巡检结果"),
    FieldGroupDef("FG-IND-TASK", "任务", 40, "任务、工单、优先级与进度"),
    FieldGroupDef("FG-IND-MEASURE", "单位", 50, "长度、面积、重量、浓度等带计量含义的数值字段"),
    FieldGroupDef("FG-IND-DATETIME", "时间日期", 60, "日期、时间、期限类字段"),
    FieldGroupDef("FG-IND-LOCATION", "位置空间", 70, "区域、设施、分区、坐标、桩号与位置"),
    FieldGroupDef("FG-IND-PERSONNEL", "人员组织", 80, "人员、联系人、部门与组织"),
    FieldGroupDef("FG-IND-SAFETY", "告警安全", 90, "告警、气体监测、消防与安全"),
    FieldGroupDef("FG-IND-BIZ", "合同客户", 100, "合同、客户、收费与商务"),
    FieldGroupDef("FG-IND-GALLERY", "管廊业务", 110, "管廊、管线、入廊等专项业务"),
    FieldGroupDef("FG-IND-RELATION", "关联引用", 120, "关联其他业务类型的引用字段"),
    FieldGroupDef("FG-IND-OTHER", "其他", 999, "暂未归入以上分类的字段"),
]

# 与历史 zhgl 四组的 code 映射（复用已有行，避免孤儿分组）
LEGACY_CODE_MAP = {
    "FG-IND-EQUIPMENT": "FG-2011499868945776640",
    "FG-IND-INSPECTION": "FG-2034036042340347904",
    "FG-IND-TASK": "FG-2034125864576856064",
    "FG-IND-MEASURE": "FG-2034035819643777024",
}


def sql_literal(value: str | None) -> str:
    if value is None:
        return "NULL"
    return "'" + str(value).replace("'", "''") + "'"


def fetch_fields() -> list[dict]:
    query = """
SELECT code, name, type, COALESCE(unit,''), COALESCE(description,''), COALESCE(source,'')
FROM dynamicbusiness.dynamic_field
WHERE deleted = false AND tenant_id = 1 AND status = 1
ORDER BY name;
"""
    env = None
    proc = subprocess.run(
        PSQL + ["-c", query],
        capture_output=True,
        text=True,
        check=True,
    )
    rows: list[dict] = []
    for line in proc.stdout.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) < 6:
            continue
        rows.append(
            {
                "code": parts[0],
                "name": parts[1],
                "type": parts[2].upper(),
                "unit": parts[3],
                "description": parts[4],
                "source": parts[5],
            }
        )
    return rows


REF_TYPES = {"ENTITY_REF", "REF_MULTI", "BATCH_ENTITY_REF", "REFERENCE", "LINK"}
NUM_TYPES = {"NUMBER", "DECIMAL", "FLOAT", "INTEGER"}
DATE_TYPES = {"DATE", "DATETIME"}


def classify(field: dict) -> str:
    name = field["name"]
    typ = field["type"]
    unit = (field.get("unit") or "").strip()
    desc = field.get("description") or ""
    text = f"{name} {desc}"

    if typ in REF_TYPES or name.startswith("关联"):
        return "FG-IND-RELATION"

    if re.search(r"巡检|点检|检测项|检查项|巡更|检查内容|检测范围|质量检查|检查项完成|完成率", text):
        return "FG-IND-INSPECTION"

    if re.search(r"任务|工单|派工|进度|优先级", text) or name in {"任务名称", "任务标题", "任务编号", "任务状态", "任务类型", "任务进度"}:
        return "FG-IND-TASK"

    if re.search(r"管廊|入廊|管线|舱室|廊道|管道", text):
        return "FG-IND-GALLERY"

    if re.search(
        r"设备|资产|备件|维护|保养|维修|厂家|制造商|型号|序列|出厂|投运|台账|机柜|交换机|探测器|传感器|泵|阀|排风|停机|风机",
        text,
    ):
        return "FG-IND-EQUIPMENT"

    if re.search(r"告警|报警|气体|消防|安全|浓度|泄漏|可燃|有毒|烟雾|火焰", text):
        return "FG-IND-SAFETY"

    if re.search(r"合同|客户|收费|账单|账期|供应商|乙方|甲方|付款|发票|商务", text):
        return "FG-IND-BIZ"

    if re.search(r"人员|联系人|负责|部门|组织|岗位|角色|用户|操作员|巡检员", text):
        return "FG-IND-PERSONNEL"

    if re.search(r"区域|分区|设施|站点|坐标|桩号|位置|地址|里程|经纬|楼层|房间|防火区|站点", text):
        return "FG-IND-LOCATION"

    if unit or re.search(
        r"长度|宽度|高度|深度|厚度|直径|半径|面积|体积|重量|质量|流量|压力|温度|湿度|浓度|里程|间距|标高|高程|米|毫米|千克|吨|兆帕|摄氏度|ppm",
        text,
    ):
        if typ in NUM_TYPES or unit:
            return "FG-IND-MEASURE"

    if typ in DATE_TYPES or re.search(r"日期|时间|期限|截止|生效|失效|开始|结束|创建|更新|安装|投产|建成|购买|维护日", text):
        return "FG-IND-DATETIME"

    if re.search(
        r"名称|描述|备注|编码|编号|代码|状态|类型|排序|是否|来源|图片|附件|上传|照片|说明|摘要|标题|等级|级别|版本",
        text,
    ) or typ in {"STRING", "TEXT", "LONG_TEXT", "ENUM", "BOOLEAN", "IMAGE", "UPLOAD", "COORDINATE"}:
        return "FG-IND-COMMON"

    return "FG-IND-OTHER"


def group_code_for_seed(group_key: str) -> str:
    return LEGACY_CODE_MAP.get(group_key, group_key)


def render_sql(assignments: dict[str, list[dict]]) -> str:
    lines = [
        "-- ============================================================================",
        "-- 字段池分组 · 行业常用分类（互斥：每个字段仅归属一个分组）",
        f"-- Generated by scripts/generate-field-group-seed.py",
        "-- 依赖：dynamic_field 已导入；dynamic_group / dynamic_group_relation 表已存在",
        "-- ============================================================================",
        "",
        "SET search_path TO dynamicbusiness;",
        "",
        "-- 1) 清除旧 FIELD 分组关联（保留字段与模型分配）",
        "UPDATE dynamic_group_relation SET deleted = true, updater = 'field-group-industry', update_time = CURRENT_TIMESTAMP",
        "WHERE tenant_id = 1 AND group_type = 'FIELD' AND deleted = false;",
        "",
        "-- 2) 停用历史空分组（非本次目录内的 legacy code）",
        """UPDATE dynamic_group SET status = 0, updater = 'field-group-industry', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND group_type = 'FIELD' AND deleted = false
  AND code NOT IN (
    'FG-IND-COMMON','FG-IND-EQUIPMENT','FG-IND-INSPECTION','FG-IND-TASK','FG-IND-MEASURE',
    'FG-IND-DATETIME','FG-IND-LOCATION','FG-IND-PERSONNEL','FG-IND-SAFETY','FG-IND-BIZ',
    'FG-IND-GALLERY','FG-IND-RELATION','FG-IND-OTHER',
    'FG-2011499868945776640','FG-2034036042340347904','FG-2034125864576856064','FG-2034035819643777024'
  );""",
        "",
        "-- 3) upsert 分组",
    ]

    for group in GROUPS:
        code = group_code_for_seed(group.code)
        lines.append(
            f"""
INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', {sql_literal(code)}, {sql_literal(group.name)}, {sql_literal(group.description)},
  NULL, NULL, 1, {group.sort}, 1, {TENANT_ID}, 'field-group-industry'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = {TENANT_ID}
    AND g.group_type = 'FIELD' AND g.code = {sql_literal(code)}
);

UPDATE dynamic_group g SET
  name = {sql_literal(group.name)},
  description = {sql_literal(group.description)},
  sort = {group.sort},
  status = 1,
  updater = 'field-group-industry',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = {TENANT_ID}
  AND g.group_type = 'FIELD' AND g.code = {sql_literal(code)};
""".strip()
        )

    lines.append("")
    lines.append("-- 4) 字段 → 分组（按 code 解析，每组内 sort 递增）")

    for group in GROUPS:
        code = group_code_for_seed(group.code)
        fields = assignments.get(group.code, [])
        lines.append(f"\n-- {group.name}: {len(fields)} field(s)")
        for idx, field in enumerate(fields, start=1):
            lines.append(
                f"""
INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, {idx * 10}, {TENANT_ID}, 'field-group-industry'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID}
WHERE g.deleted = false AND g.tenant_id = {TENANT_ID} AND g.group_type = 'FIELD'
  AND g.code = {sql_literal(code)}
  AND f.code = {sql_literal(field['code'])}
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = {TENANT_ID}
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );""".strip()
            )

    lines.append("")
    return "\n".join(lines) + "\n"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--dry-run", action="store_true", help="Print stats only")
    args = parser.parse_args()

    fields = fetch_fields()
    assignments: dict[str, list[dict]] = {g.code: [] for g in GROUPS}
    for field in fields:
        key = classify(field)
        assignments[key].append(field)

    print("Field group assignment summary:")
    for group in GROUPS:
        n = len(assignments[group.code])
        print(f"  {group.name:8s} ({group_code_for_seed(group.code):30s}): {n:3d}")

    unassigned = sum(1 for g in GROUPS if g.code == "FG-IND-OTHER" for _ in assignments["FG-IND-OTHER"])
    print(f"  Total fields: {len(fields)}, Other bucket: {unassigned}")

    if args.dry_run:
        others = [f["name"] for f in assignments["FG-IND-OTHER"]]
        if others:
            print("\nOther fields sample:", others[:20])
        return

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(render_sql(assignments), encoding="utf-8")
    print(f"\nWrote {OUT} ({OUT.stat().st_size // 1024} KB)")


if __name__ == "__main__":
    main()
