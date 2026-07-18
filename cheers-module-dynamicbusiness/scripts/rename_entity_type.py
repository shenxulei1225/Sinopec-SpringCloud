#!/usr/bin/env python3
"""One-off: ent_ → ent_, EntityType* → EntityType*, dynamic_entity_type* → dynamic_entity_type*."""
from __future__ import annotations

import os
from pathlib import Path

ROOTS = [
    Path("/Users/kevin/Documents/Sinopec/Sinopec-SpringCloud/cheers-module-dynamicbusiness"),
    Path("/Users/kevin/Documents/Sinopec/Sinopec-SpringCloud/cheers-module-business-middle-platform"),
    Path("/Users/kevin/Documents/Sinopec/ecs-react"),
    Path("/Users/kevin/Documents/Sinopec/docs"),
]
SKIP_DIRS = {".git", "node_modules", "target", "__pycache__", ".next", "dist", "build", "db/backup"}
EXTS = {
    ".java", ".ts", ".tsx", ".sql", ".json", ".yaml", ".yml", ".xml", ".py", ".md",
    ".mjs", ".js", ".properties", ".csv", ".sh", ".ps1",
}

REPLACEMENTS = [
    ("dynamic_entity_type_base_field", "dynamic_entity_type_base_field"),
    ("dynamic_entity_type_config", "dynamic_entity_type_config"),
    ("dynamic_entity_type_relation", "dynamic_entity_type_relation"),
    ("dynamic_entity_type", "dynamic_entity_type"),
    ("EntityTypeBaseField", "EntityTypeBaseField"),
    ("EntityTypeRelation", "EntityTypeRelation"),
    ("RelatableEntityType", "RelatableEntityType"),
    ("EntityType", "EntityType"),
    ("target_entity_type", "target_entity_type"),
    ("targetEntityTypeName", "targetEntityTypeName"),
    ("targetEntityType", "targetEntityType"),
    ("refEntityType", "refEntityType"),
    ("RefEntityType", "RefEntityType"),
    ("list-by-entity-type", "list-by-entity-type"),
    ("by-entity-type", "by-entity-type"),
    ("rebuild/entity-type", "rebuild/entity-type"),
    ("system:business-type", "system:entity-type"),
    ("/entity-type", "/entity-type"),
    ("controller.admin.entitytype", "controller.admin.entitytype"),
    ("service.entitytype", "service.entitytype"),
    ("dataobject.entitytype", "dataobject.entitytype"),
    ("mysql.entitytype", "mysql.entitytype"),
    ("convert.entitytype", "convert.entitytype"),
    ("enums.entitytype", "enums.entitytype"),
    ("ent_", "ent_"),
    ("fetchEntityTypeModelOptions", "fetchEntityTypeModelOptions"),
    ("parseEntityTypeCodeFromPath", "parseEntityTypeCodeFromPath"),
    ("parseEntityType", "parseEntityType"),
    ("entityType.ts", "entityType.ts"),
    ("entityTypeSelectId", "entityTypeSelectId"),
    ("configurator-tree-entity-type", "configurator-tree-entity-type"),
    ("configurator-entity-type", "configurator-entity-type"),
    ("setEntityTypeCode", "setEntityTypeCode"),
    ("applyEntityTypeSelection", "applyEntityTypeSelection"),
]


def should_skip(path: Path) -> bool:
    parts = set(path.parts)
    if "db" in parts and "backup" in parts:
        return True
    if any(part.startswith("_tmp_") for part in path.parts):
        return True
    return False


def main() -> None:
    changed = 0
    for root in ROOTS:
        if not root.exists():
            continue
        for dirpath, dirnames, filenames in os.walk(root):
            dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
            for fn in filenames:
                p = Path(dirpath) / fn
                if should_skip(p):
                    continue
                if p.suffix not in EXTS and fn not in ("Dockerfile", "CLAUDE.md", "AGENTS.md"):
                    continue
                try:
                    text = p.read_text(encoding="utf-8")
                except (UnicodeDecodeError, OSError):
                    continue
                new = text
                for old, rep in REPLACEMENTS:
                    new = new.replace(old, rep)
                if new != text:
                    p.write_text(new, encoding="utf-8")
                    changed += 1
    print(f"Updated {changed} files")


if __name__ == "__main__":
    main()
