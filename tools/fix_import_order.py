#!/usr/bin/env python3
"""Move stray leading imports before package/copyright back below package line."""
from pathlib import Path
from typing import Optional

ROOT = Path(__file__).resolve().parents[1] / "src/main/java"


def fix_content(text: str) -> Optional[str]:
    lines = text.split("\n")
    i = 0
    stray = []
    while i < len(lines):
        s = lines[i].strip()
        if s.startswith("import ") or s.startswith("import\t"):
            stray.append(lines[i])
            i += 1
            continue
        break
    if not stray:
        return None
    rest_lines = lines[i:]
    rest = "\n".join(rest_lines)
    idx = rest.find("package ")
    if idx == -1:
        print("No package in file with stray imports")
        return None
    # package statement ends at first ';' newline
    semi = rest.find(";", idx)
    if semi == -1:
        return None
    pkg_end = semi + 1
    while pkg_end < len(rest) and rest[pkg_end] in "\r\n":
        pkg_end += 1
    head = rest[:pkg_end]
    tail = rest[pkg_end:]
    existing = {s.strip() for s in tail.split("\n") if s.strip().startswith("import ")}
    add = [s for s in stray if s.strip() not in existing]
    if not add:
        return head + tail
    insert = "\n".join(add) + "\n"
    return head + "\n" + insert + tail.lstrip("\n")


def main() -> None:
    n = 0
    for p in sorted(ROOT.rglob("*.java")):
        raw = p.read_text(encoding="utf-8")
        if not raw.startswith("import "):
            continue
        fixed = fix_content(raw)
        if fixed is not None and fixed != raw:
            p.write_text(fixed)
            print(p.relative_to(ROOT.parent.parent))
            n += 1
    print(f"fixed {n} files")


if __name__ == "__main__":
    main()
