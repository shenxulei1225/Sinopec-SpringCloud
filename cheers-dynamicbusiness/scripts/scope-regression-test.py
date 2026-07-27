#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Scope 回归测试（P0–P2）

前置：
  1. dynamicbusiness-server 已启动（默认 http://127.0.0.1:58096）
  2. 已执行 seed：scripts/platform-import/system/seed/dynamic_entity_task_scope_regression.sql

用法：
  python scripts/scope-regression-test.py
  SCOPE_TEST_TOKEN=xxx python scripts/scope-regression-test.py
"""

from __future__ import annotations

import json
import os
import sys
import urllib.error
import urllib.request
from dataclasses import dataclass
from typing import Any, Callable, Optional

# 本机直连，避免系统代理导致 502
for _k in ("HTTP_PROXY", "HTTPS_PROXY", "http_proxy", "https_proxy", "ALL_PROXY", "all_proxy"):
    os.environ.pop(_k, None)
os.environ["NO_PROXY"] = os.environ["no_proxy"] = "*"
urllib.request.install_opener(urllib.request.build_opener(urllib.request.ProxyHandler({})))

BASE_URL = os.environ.get(
    "SCOPE_TEST_BASE_URL",
    "http://127.0.0.1:58096/admin-api/dynamicbusiness",
)
TOKEN = os.environ.get("SCOPE_TEST_TOKEN", "325c3b87f1844aa2b5962d3c803aff83")
TENANT_ID = os.environ.get("SCOPE_TEST_TENANT_ID", "1")

# 与 dynamic_entity_task_scope_regression.sql（油库样例）对齐的期望值
EXPECT = {
    # task storage
    "models_all": 8,
    "models_patrol_scope": 5,
    "models_maint_scope": 2,
    "entities_all": 22,
    "entities_patrol_scope": 15,  # 5+3+3+2+2
    "entities_maint_scope": 5,
    "entities_empty_scope": 0,
    "entities_general_model": 2,
    # task_excution_record storage
    "record_models_all": 3,
    "record_models_patrol_scope": 2,
    "record_models_maint_scope": 1,
    "record_entities_all": 13,
    "record_entities_patrol_scope": 8,
    "record_entities_maint_scope": 5,
}


@dataclass
class Case:
    phase: str
    name: str
    run: Callable[[], tuple[bool, str]]
    known_gap: bool = False


def http(
    method: str,
    path: str,
    *,
    body: Optional[dict] = None,
    params: Optional[dict[str, Any]] = None,
) -> dict:
    url = BASE_URL.rstrip("/") + path
    if params:
        from urllib.parse import urlencode

        qs = urlencode({k: v for k, v in params.items() if v is not None})
        url = f"{url}?{qs}"
    data = None
    headers = {
        "Authorization": f"Bearer {TOKEN}",
        "tenant-id": TENANT_ID,
    }
    if body is not None:
        data = json.dumps(body, ensure_ascii=False).encode("utf-8")
        headers["Content-Type"] = "application/json; charset=utf-8"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            raw = resp.read().decode("utf-8")
    except urllib.error.HTTPError as e:
        raw = e.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"HTTP {e.code} {path}: {raw[:500]}") from e
    except urllib.error.URLError as e:
        raise RuntimeError(f"无法连接 {url}: {e}") from e
    parsed = json.loads(raw)
    if parsed.get("code") not in (0, 200, None):
        raise RuntimeError(f"API code={parsed.get('code')} msg={parsed.get('msg')} path={path}")
    return parsed


def query_total(body: dict) -> int:
    data = http("POST", "/business/entities/query-by-scene", body=body).get("data") or {}
    page = data.get("page")
    if page is not None:
        return int(page.get("total") or 0)
    tree = data.get("tree")
    if tree is not None:
        return len(tree)
    lst = data.get("list")
    if lst is not None:
        return len(lst)
    return 0


def list_models(entity_type: str, domain: Optional[str] = None) -> list:
    params = {"entityTypeCode": entity_type}
    if domain:
        params["domain"] = domain
    return http("GET", "/business/models/list-by-entity-type", params=params).get("data") or []


def find_category_id(tree: list, code: str) -> Optional[int]:
    for node in tree:
        if node.get("code") == code:
            return node.get("id")
        child = find_category_id(node.get("children") or [], code)
        if child is not None:
            return child
    return None


def get_category_tree(category_type: str = "task") -> list:
    return (
        http(
            "GET",
            "/category/tree",
            params={"categoryTypeCode": category_type, "status": "1"},
        ).get("data")
        or []
    )


def assert_eq(actual: Any, expected: Any, detail: str = "") -> tuple[bool, str]:
    ok = actual == expected
    msg = f"expected={expected} actual={actual}"
    if detail:
        msg = f"{detail}: {msg}"
    return ok, msg


def main() -> int:
    cat_tree = get_category_tree("task")
    cat_daily = find_category_id(cat_tree, "task_cat_daily")
    cat_special = find_category_id(cat_tree, "task_cat_special")
    cat_maint = find_category_id(cat_tree, "task_cat_maintenance")
    cat_root = find_category_id(cat_tree, "task_root")

    if not all([cat_daily, cat_special, cat_maint, cat_root]):
        print("FAIL 分类树不完整，请先执行 scope regression seed")
        print("  found", {
            "task_root": cat_root,
            "task_cat_daily": cat_daily,
            "task_cat_special": cat_special,
            "task_cat_maintenance": cat_maint,
        })
        return 1

    patrol_model = next((m for m in list_models("task", "巡检") if m.get("code") == "patrol_task"), None)
    general_model = next((m for m in list_models("task") if m.get("code") == "general_station_task"), None)

    cases: list[Case] = []

    # --- P0 读路径 / query-by-scene ---
    def p0_abc_all_patrol():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["entities_patrol_scope"], "PATTERN_ABC_ALL+巡检")

    def p0_abc_all_maint():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "维修",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["entities_maint_scope"], "PATTERN_ABC_ALL+维修")

    def p0_abc_all_native():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["entities_all"], "PATTERN_ABC_ALL 无 scope")

    def p0_abc_empty_scope():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "空域测试",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["entities_empty_scope"], "空 Scope 哨兵")

    def p0_abc_tree_patrol():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "巡检",
                "resultShape": "TREE",
                "resultDetail": "LIGHT",
                "pageNo": 1,
                "pageSize": 50,
            }
        )
        return assert_eq(total, EXPECT["entities_patrol_scope"], "PATTERN_ABC_ALL TREE+巡检")

    def p0_pattern_b_scope():
        total = query_total(
            {
                "scene": "PATTERN_B_ENTITIES_BY_MODEL",
                "entityTypeCode": "task",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["entities_patrol_scope"], "PATTERN_B+巡检")

    def p0_explicit_model_ids_win():
        if not patrol_model:
            return False, "patrol_task model missing"
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "modelIds": [patrol_model["id"]],
                "domain": "维修",  # 应被忽略
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, 5, "显式 modelIds 优先于 domain")

    def p0_dm_category_scope():
        total = query_total(
            {
                "scene": "DATA_MGMT_ENTITIES_BY_CATEGORY_MODEL",
                "entityTypeCode": "task",
                "categoryTypeCode": "task",
                "categoryIds": [cat_daily],
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        # 日常分类 + 巡检 Scope：分类挂接 8 条 + patrol_task 未挂分类 910004/910005
        return assert_eq(total, 10, "DATA_MGMT 分类∩Scope")

    def p0_dm_uncategorized_scope():
        total = query_total(
            {
                "scene": "DATA_MGMT_ENTITIES_UNCATEGORIZED",
                "entityTypeCode": "task",
                "categoryTypeCode": "task",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        # 未挂分类 + 巡检 Scope：仅 patrol_adhoc 2 条（910106/910107）
        return assert_eq(total, 2, "DATA_MGMT 未分类+巡检")

    def p0_pattern_ac_category_scope():
        total = query_total(
            {
                "scene": "PATTERN_A_C_ENTITIES_BY_CATEGORY",
                "entityTypeCode": "task",
                "categoryTypeCode": "task",
                "categoryIds": [cat_daily],
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        # 当前实现未在 A/C 场景应用 modelIds — 会返回分类下全部 10 条（含未挂分类 patrol_task）
        return assert_eq(total, 10, "PATTERN_A_C+分类+巡检（当前未过滤 Scope）")

    def p0_pattern_b_category():
        total = query_total(
            {
                "scene": "PATTERN_B_ENTITIES_BY_CATEGORY",
                "entityTypeCode": "task",
                "categoryTypeCode": "task",
                "categoryIds": [cat_daily],
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        # Pattern B 按分类下模型汇总，当前未应用 Scope 过滤 → 含 general 共 12 条
        return assert_eq(total, 12, "PATTERN_B 分类+巡检（当前未过滤 Scope）")

    cases += [
        Case("P0", "PATTERN_ABC_ALL + 巡检", p0_abc_all_patrol),
        Case("P0", "PATTERN_ABC_ALL + 维修", p0_abc_all_maint),
        Case("P0", "PATTERN_ABC_ALL 全量", p0_abc_all_native),
        Case("P0", "空 Scope 哨兵", p0_abc_empty_scope),
        Case("P0", "PATTERN_ABC_ALL TREE", p0_abc_tree_patrol),
        Case("P0", "PATTERN_B + 巡检", p0_pattern_b_scope),
        Case("P0", "modelIds 优先于 domain", p0_explicit_model_ids_win),
        Case("P0", "DATA_MGMT 分类∩Scope", p0_dm_category_scope),
        Case("P0", "DATA_MGMT 未分类+Scope", p0_dm_uncategorized_scope),
        Case("P0", "PATTERN_A_C 分类（Scope 缺口）", p0_pattern_ac_category_scope, known_gap=True),
        Case("P0", "PATTERN_B 分类（Scope 缺口）", p0_pattern_b_category, known_gap=True),
    ]

    # --- P0 执行记录（task_excution_record storage + Scope）---
    def p0_record_abc_all():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task_excution_record",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["record_entities_all"], "执行记录全量")

    def p0_record_abc_patrol():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task_excution_record",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["record_entities_patrol_scope"], "执行记录+巡检")

    def p0_record_abc_maint():
        total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task_excution_record",
                "domain": "维修",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, EXPECT["record_entities_maint_scope"], "执行记录+维修")

    def p0_record_models_list():
        all_m = list_models("task_excution_record")
        patrol_m = list_models("task_excution_record", "巡检")
        maint_m = list_models("task_excution_record", "维修")
        ok = (
            len(all_m) == EXPECT["record_models_all"]
            and len(patrol_m) == EXPECT["record_models_patrol_scope"]
            and len(maint_m) == EXPECT["record_models_maint_scope"]
        )
        return ok, f"record all={len(all_m)} patrol={len(patrol_m)} maint={len(maint_m)}"

    def p0_record_scope_isolation():
        patrol_total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task_excution_record",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        maint_total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task_excution_record",
                "domain": "维修",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        ok = (
            patrol_total == EXPECT["record_entities_patrol_scope"]
            and maint_total == EXPECT["record_entities_maint_scope"]
            and patrol_total + maint_total == EXPECT["record_entities_all"]
        )
        return ok, f"patrol={patrol_total} maint={maint_total} sum={patrol_total + maint_total}"

    cases += [
        Case("P0", "执行记录 PATTERN_ABC_ALL 全量", p0_record_abc_all),
        Case("P0", "执行记录 + 巡检 Scope", p0_record_abc_patrol),
        Case("P0", "执行记录 + 维修 Scope", p0_record_abc_maint),
        Case("P0", "执行记录模型 list-by-entity-type", p0_record_models_list),
        Case("P0", "执行记录 Scope 互不重叠", p0_record_scope_isolation),
    ]

    # --- P0 模型 API ---
    def p0_models_list():
        all_m = list_models("task")
        patrol_m = list_models("task", "巡检")
        maint_m = list_models("task", "维修")
        ok = (
            len(all_m) == EXPECT["models_all"]
            and len(patrol_m) == EXPECT["models_patrol_scope"]
            and len(maint_m) == EXPECT["models_maint_scope"]
        )
        return ok, f"all={len(all_m)} patrol={len(patrol_m)} maint={len(maint_m)}"

    def p0_model_get_domain():
        if not patrol_model:
            return False, "patrol_task missing"
        vo = http("GET", "/business/models/get", params={"id": patrol_model["id"]}).get("data") or {}
        return assert_eq(vo.get("domain"), "巡检", "model get domain")

    def p0_page_models():
        page = http(
            "GET",
            "/business/models/page-models",
            params={"entityTypeCode": "task", "domain": "巡检", "pageNo": 1, "pageSize": 50},
        ).get("data") or {}
        total = int(page.get("total") or 0)
        return assert_eq(total, EXPECT["models_patrol_scope"], "page-models+巡检")

    cases += [
        Case("P0", "list-by-entity-type 模型计数", p0_models_list),
        Case("P0", "models/get domain", p0_model_get_domain),
        Case("P0", "page-models+巡检", p0_page_models),
    ]

    # --- P1 域入口 / 组合 ---
    def p1_scoped_type_registry():
        types = http("GET", "/entity-type/list-all").get("data") or []
        codes = {t.get("code") for t in types if t.get("entryKind") == "DOMAIN"}
        need = {
            "task_patrol",
            "task_maintenance",
            "task_record_patrol",
            "task_record_maintenance",
        }
        ok = need.issubset(codes)
        return ok, f"scoped codes={codes & need}"

    def p1_uncategorized_models_scope():
        lst = http(
            "GET",
            "/business/models/list-uncategorized-by-category-type",
            params={"categoryTypeCode": "task", "entityTypeCode": "task", "domain": "巡检"},
        ).get("data") or []
        codes = {m.get("code") for m in lst}
        ok = "patrol_adhoc" in codes and "patrol_task" not in codes
        return ok, f"uncategorized patrol models={codes}"

    def p1_maint_category_entities():
        total = query_total(
            {
                "scene": "PATTERN_A_C_ENTITIES_BY_CATEGORY",
                "entityTypeCode": "task",
                "categoryTypeCode": "task",
                "categoryIds": [cat_maint],
                "domain": "维修",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        return assert_eq(total, 3, "维修分类实体数")

    cases += [
        Case("P1", "DOMAIN 域入口注册", p1_scoped_type_registry),
        Case("P1", "未挂分类模型+Scope", p1_uncategorized_models_scope),
        Case("P1", "维修分类 A/C 实体", p1_maint_category_entities),
    ]

    # --- P2 边界 / 第二业务类型抽样 ---
    def p2_equipment_native_models():
        lst = list_models("equipment")
        ok = len(lst) >= 1
        return ok, f"equipment models={len(lst)}"

    def p2_batch_models_domain():
        ids = [m["id"] for m in list_models("task", "巡检")[:2]]
        batch = http("GET", "/business/models/batch", params={"ids": ",".join(map(str, ids))}).get("data") or []
        ok = len(batch) == len(ids) and all(b.get("domain") == "巡检" for b in batch)
        return ok, f"batch scopes={[b.get('domain') for b in batch]}"

    def p2_general_not_in_patrol_scope():
        total = query_total(
            {
                "scene": "PATTERN_B_ENTITIES_BY_MODEL",
                "entityTypeCode": "task",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        if not general_model:
            return False, "general_station_task missing"
        # 全 patrol scope 不应含 general 的 2 条
        return assert_eq(total, EXPECT["entities_patrol_scope"], "general 不在巡检 Scope")

    def p2_task_scope_isolation():
        patrol_total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "巡检",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        maint_total = query_total(
            {
                "scene": "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE",
                "entityTypeCode": "task",
                "domain": "维修",
                "pageNo": 1,
                "pageSize": 100,
                "resultShape": "PAGE",
            }
        )
        ok = (
            patrol_total == EXPECT["entities_patrol_scope"]
            and maint_total == EXPECT["entities_maint_scope"]
            and patrol_total + maint_total + EXPECT["entities_general_model"] == EXPECT["entities_all"]
        )
        return ok, f"patrol={patrol_total} maint={maint_total} general={EXPECT['entities_general_model']}"

    cases += [
        Case("P2", "equipment 原生入口抽样", p2_equipment_native_models),
        Case("P2", "models/batch 带 domain", p2_batch_models_domain),
        Case("P2", "general 域外", p2_general_not_in_patrol_scope),
        Case("P2", "任务 Scope 与 general 隔离", p2_task_scope_isolation),
    ]

    passed = failed = gaps = 0
    print(f"Scope regression @ {BASE_URL}\n")
    for phase in ("P0", "P1", "P2"):
        phase_cases = [c for c in cases if c.phase == phase]
        if not phase_cases:
            continue
        print(f"## {phase}")
        for c in phase_cases:
            try:
                ok, msg = c.run()
            except Exception as e:
                ok, msg = False, str(e)
            if ok and c.known_gap:
                gaps += 1
                mark = "GAP "
            elif ok:
                passed += 1
                mark = "PASS"
            else:
                failed += 1
                mark = "FAIL"
            suffix = " [known gap]" if c.known_gap else ""
            print(f"  {mark}  {c.name}: {msg}{suffix}")
        print()

    print(f"Summary: PASS={passed} FAIL={failed} KNOWN_GAP={gaps} TOTAL={len(cases)}")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
