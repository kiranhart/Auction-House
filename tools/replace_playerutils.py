from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src"


def main():
    changed = 0
    for p in ROOT.rglob("*.java"):
        raw = p.read_text(encoding="utf-8")
        if "PlayerUtils" not in raw:
            continue

        t = raw
        t = t.replace("import ca.tweetzy.core.utils.PlayerUtils;\r\n", "")
        t = t.replace("import ca.tweetzy.core.utils.PlayerUtils;\n", "")
        t = t.replace("PlayerUtils.giveItem", "PlayerUtil.giveItem")
        t = t.replace("PlayerUtils.findPlayer", "PlayerLookup.findPlayer")

        if "PlayerUtil.giveItem" in t or "PlayerLookup.findPlayer" in t:
            if "import ca.tweetzy.flight.utils.PlayerUtil;" not in t:
                t = insert_after_package(t, "import ca.tweetzy.flight.utils.PlayerUtil;\n")
            if "import ca.tweetzy.auctionhouse.helpers.PlayerLookup;" not in t:
                t = insert_after_package(t, "import ca.tweetzy.auctionhouse.helpers.PlayerLookup;\n")

        if t != raw:
            p.write_text(t, encoding="utf-8")
            changed += 1
            print(p)

    print("changed:", changed)


def insert_after_package(text: str, line: str) -> str:
    lines = text.splitlines(keepends=True)
    for i, x in enumerate(lines):
        if x.startswith("package "):
            pkg_end = i + 1
            while pkg_end < len(lines) and lines[pkg_end].strip() == "":
                pkg_end += 1
            # skip existing import section start - put after blank line following package
            insert_at = pkg_end
            chunk = "".join(lines[:insert_at])
            rest = "".join(lines[insert_at:])
            return chunk + ("" if chunk.endswith("\n") else "\n") + line + rest
    raise ValueError("no package")


if __name__ == "__main__":
    main()
