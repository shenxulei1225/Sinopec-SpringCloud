from pathlib import Path

p = Path(__file__).resolve().parent.parent / (
    "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V5__create_dedicated_entity_tables.sql"
)
lines = p.read_text(encoding="utf-8").splitlines()
out: list[str] = []
i = 0
while i < len(lines):
    line = lines[i]
    stripped = line.strip()
    if stripped.startswith("ALTER TABLE") and i + 1 < len(lines) and "ADD CONSTRAINT" in lines[i + 1]:
        i += 1
        while i < len(lines) and not lines[i].strip().endswith(";"):
            i += 1
        i += 1
        continue
    if stripped.startswith("ALTER TABLE") and "ADD CONSTRAINT" in line:
        while i < len(lines) and not lines[i].strip().endswith(";"):
            i += 1
        i += 1
        continue
    out.append(line)
    i += 1

p.write_text("\n".join(out) + "\n", encoding="utf-8")
remaining = sum(1 for l in out if "ADD CONSTRAINT" in l)
print(f"V5 cleaned, {len(out)} lines, ADD CONSTRAINT remaining: {remaining}")
