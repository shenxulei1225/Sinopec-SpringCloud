import re
from pathlib import Path

p = Path(__file__).resolve().parent / "platform-import/system/seed/dynamic_entity_type_base_field.sql"
text = p.read_text(encoding="utf-8")
text = re.sub(
    r"\(SELECT f\.id FROM dynamic_field f WHERE f\.deleted = false AND f\.tenant_id = 1 AND f\.code = '[^']+' LIMIT 1\)",
    "NULL",
    text,
)
p.write_text(text, encoding="utf-8")
print("done")
