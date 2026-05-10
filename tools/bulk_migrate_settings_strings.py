# -*- coding: utf-8 -*-
"""
Replace Settings.<IDENT>.getString() / getStringList() used for migrated display keys
with AuctionLocale.msg / msgList.

Listing lore under keys like "auction stack.*" is resolved via Translations / AuctionLocale;
only Settings.AUCTION_STACK_INFO_LAYOUT (layout token order) remains in config.yml.

See should_migrate_ident() for which idents the script targets.
"""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java"
SETTINGS_PATH = ROOT / "ca" / "tweetzy" / "auctionhouse" / "settings" / "Settings.java"


def parse_settings_keys(text: str) -> dict[str, str]:
    """Map IDENT -> yaml key from: public ... ConfigEntry IDENT = create("KEY", ...)"""
    d: dict[str, str] = {}
    # Single-line captures (covers vast majority including multi-arg create trailing comment)
    for m in re.finditer(
        r"public\s+static\s+final\s+ConfigEntry\s+(\w+)\s*=\s*create\(\s*\"([^\"]+)\"\s*,",
        text,
    ):
        d[m.group(1)] = m.group(2)
    return d


def should_migrate_ident(ident: str) -> bool:
    if ident == "CMD_ERROR_DESC":
        return True
    if ident in ("ITEM_BUNDLE_NAME", "ITEM_BUNDLE_LORE"):
        return True
    if ident.startswith("DISCORD_TITLE_"):
        return True
    if ident in ("DISCORD_MSG_USERNAME", "DISCORD_MSG_PFP"):
        return True
    if ident.startswith("DISCORD_MSG_FIELD_") and (
        ident.endswith("_NAME") or ident.endswith("_VALUE")
    ):
        return True
    if ident.startswith("GUI_") and (
        ident.endswith("_NAME")
        or ident.endswith("_LORE")
        or ident.endswith("_TITLE")
        or ident.endswith("_TITLE_ALL")
    ):
        return True
    return False


def player_expr(java_path: str, line: str) -> str:
    line_l = line
    if "super(" in line_l and "Settings.GUI_" in line_l:
        mm = re.search(
            r"super\(\s*[^,]+\s*,\s*([^,]+?)\s*,\s*Settings\.(?:GUI_\w+)\.getString",
            line_l,
        )
        if mm:
            return mm.group(1).strip()
    pkg = java_path.replace("\\", "/")
    if "/guis/" in pkg:
        return "this.player"
    return "null"


def migrate_file(java_path: Path, ident_to_key: dict[str, str]) -> bool:
    text = java_path.read_text(encoding="utf-8")
    orig = text

    lines = text.splitlines(keepends=True)
    out: list[str] = []
    for line in lines:
        new_line = line

        for m in list(
            re.finditer(r"Settings\.(\w+)\.getStringList\(\)", new_line)
        ):
            ident = m.group(1)
            if not should_migrate_ident(ident) or ident not in ident_to_key:
                continue
            key = ident_to_key[ident]
            pe = player_expr(str(java_path), new_line)
            repl = f'AuctionLocale.msgList({pe}, "{key}")'
            new_line = new_line.replace(m.group(0), repl)

        for m in list(re.finditer(r"Settings\.(\w+)\.getString\(\)", new_line)):
            ident = m.group(1)
            if not should_migrate_ident(ident) or ident not in ident_to_key:
                continue
            key = ident_to_key[ident]
            pe = player_expr(str(java_path), new_line)
            repl = f'AuctionLocale.msg({pe}, "{key}")'
            new_line = new_line.replace(m.group(0), repl)

        out.append(new_line)

    text = "".join(out)
    if text == orig:
        return False

    need_import = (
        "AuctionLocale.msg(" in text
        or "AuctionLocale.msgList(" in text
    ) and (
        java_path.parts[-1] != "AuctionLocale.java"
        and (
            'import ca.tweetzy.auctionhouse.lang.AuctionLocale;'
            not in text
        )
    )

    if need_import:
        m = re.search(r"^(package\s+[^\n]+\n)", text)
        if m:
            insert_at = m.end()
            text = (
                text[:insert_at]
                + "\nimport ca.tweetzy.auctionhouse.lang.AuctionLocale;\n"
                + text[insert_at:]
            )

    java_path.write_text(text, encoding="utf-8")
    return True


def main() -> None:
    stext = SETTINGS_PATH.read_text(encoding="utf-8")
    ident_to_key = parse_settings_keys(stext)

    migrated = []
    missing = []

    changed = 0
    for p in ROOT.rglob("*.java"):
        rel = str(p.relative_to(ROOT))
        if rel.replace("\\", "/").endswith("settings/Settings.java"):
            continue
        if migrate_file(p, ident_to_key):
            migrated.append(rel)
            changed += 1

    print("files changed:", changed)
    for sample in migrated[:40]:
        print("  ", sample)


if __name__ == "__main__":
    main()
