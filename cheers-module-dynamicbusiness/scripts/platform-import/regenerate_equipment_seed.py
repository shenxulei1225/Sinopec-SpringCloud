#!/usr/bin/env python3
"""一键重生成标准设备分类库 + 弱电清单型号 seed。"""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def run(script: str) -> None:
    path = ROOT / script
    print(f">> python {script}")
    subprocess.run([sys.executable, str(path)], check=True)


def main() -> None:
    run("export_equipment_category_library.py")
    run("export_equipment_models_from_inventory_md.py")
    print("done: category library + equipment models")


if __name__ == "__main__":
    main()
