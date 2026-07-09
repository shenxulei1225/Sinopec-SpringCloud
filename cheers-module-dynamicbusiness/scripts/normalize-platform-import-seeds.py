#!/usr/bin/env python3
"""One-shot: normalize legacy pinyin codes in platform-import seed SQL files."""

from __future__ import annotations

from pathlib import Path

from entity_type_canonical import (
    LEGACY_DEDICATED_TABLE_TO_CANONICAL,
    LEGACY_ENTITY_TYPE_CODE_TO_CANONICAL,
)

ROOT = Path(__file__).resolve().parent / "platform-import"

# SQL string literals: ', 'code',
ENTITY_REPLACEMENTS = {
    f", '{legacy}',": f", '{canonical}',"
    for legacy, canonical in LEGACY_ENTITY_TYPE_CODE_TO_CANONICAL.items()
}
# Also match trailing entity_type_code in VALUES tuples
for legacy, canonical in LEGACY_ENTITY_TYPE_CODE_TO_CANONICAL.items():
    ENTITY_REPLACEMENTS[f"'{legacy}'"] = f"'{canonical}'"

TABLE_REPLACEMENTS = {
    f"'{legacy}'": f"'{canonical}'"
    for legacy, canonical in LEGACY_DEDICATED_TABLE_TO_CANONICAL.items()
}


def apply_replacements(text: str, mapping: dict[str, str]) -> str:
    out = text
    for old, new in mapping.items():
        out = out.replace(old, new)
    return out


def main() -> None:
    sql_files = list(ROOT.rglob("*.sql"))
    changed = 0
    for path in sql_files:
        original = path.read_text(encoding="utf-8")
        updated = apply_replacements(original, ENTITY_REPLACEMENTS)
        updated = apply_replacements(updated, TABLE_REPLACEMENTS)
        # Fix description text still saying spare_part
        updated = updated.replace(
            "'spare_part业务类型存储配置'",
            "'spare_parts业务类型存储配置'",
        )
        if updated != original:
            path.write_text(updated, encoding="utf-8")
            changed += 1
            print(f"updated: {path.relative_to(ROOT.parent)}")
    print(f"done: {changed} file(s)")


if __name__ == "__main__":
    main()
