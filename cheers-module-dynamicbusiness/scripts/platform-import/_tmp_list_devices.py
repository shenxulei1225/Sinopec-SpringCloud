import re, pathlib
md = pathlib.Path(r"F:\XProject\参考资料\弱电集成设备清单-武汉理工光科.md").read_text(encoding="utf-8")
names=set()
for line in md.splitlines():
    if not line.startswith("|") or line.startswith("| ---"): continue
    cells=[c.strip() for c in line.strip("|").split("|")]
    if len(cells)<8 or not re.match(r"^\d+$", cells[0]): continue
    if "小计" in cells[1] or cells[1].startswith("**"): continue
    names.add(cells[1].strip())
for n in sorted(names): print(n)
print("---", len(names))
