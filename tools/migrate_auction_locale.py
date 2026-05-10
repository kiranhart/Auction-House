#!/usr/bin/env python3
"""Replace AuctionLocale with TranslationManager + Translations.* (balanced parens)."""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TRANS = ROOT / "src/main/java/ca/tweetzy/auctionhouse/settings/Translations.java"
SRC = ROOT / "src/main/java"


def load_key_to_field() -> dict[str, str]:
    text = TRANS.read_text(encoding="utf-8")
    pat = re.compile(r'public static TranslationEntry (\w+) = create\("([^"]+)"')
    m: dict[str, str] = {}
    for field, key in pat.findall(text):
        lk = key.lower()
        if lk in m and m[lk] != field:
            print(f"WARN duplicate key {key!r}: {m[lk]} vs {field}", file=sys.stderr)
        m[lk] = field
    return m


def lookup(key: str, k2f: dict[str, str]) -> str:
    lk = key.lower()
    if lk not in k2f:
        raise KeyError(f"No Translations field for key {key!r}")
    return k2f[lk]


def extract_paren_content(s: str, open_idx: int) -> tuple[str, int]:
    """s[open_idx] == '(', return inner including nested, and index after closing ')'."""
    assert s[open_idx] == "("
    depth = 0
    i = open_idx
    while i < len(s):
        c = s[i]
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
            if depth == 0:
                return s[open_idx + 1 : i], i + 1
        i += 1
    raise ValueError("unbalanced")


def split_first_arg(inner: str) -> tuple[str, str]:
    """Split 'a, rest' where a is first comma-separated token respecting parens/strings."""
    depth = 0
    in_str = False
    esc = False
    for i, c in enumerate(inner):
        if in_str:
            if esc:
                esc = False
            elif c == "\\":
                esc = True
            elif c == '"':
                in_str = False
            continue
        if c == '"':
            in_str = True
            continue
        if c == "(":
            depth += 1
        elif c == ")":
            depth -= 1
        elif c == "," and depth == 0:
            return inner[:i].strip(), inner[i + 1 :].strip()
    return inner.strip(), ""


def parse_msg_call(inner: str, k2f: dict[str, str]) -> str:
    first, rest = split_first_arg(inner)
    rest = rest.strip()
    if not rest.startswith('"'):
        raise ValueError(f"expected string key after first arg, got: {rest[:80]}")
    end_quote = rest.index('"', 1)
    key = rest[1:end_quote]
    tail = rest[end_quote + 1 :].strip()
    field = lookup(key, k2f)
    if tail.startswith(","):
        tail = tail[1:].strip()
    if first == "null":
        if tail:
            return f"TranslationManager.string(Translations.{field}, {tail})"
        return f"TranslationManager.string(Translations.{field})"
    if tail:
        return f"TranslationManager.string({first}, Translations.{field}, {tail})"
    return f"TranslationManager.string({first}, Translations.{field})"


def parse_msglist_call(inner: str, k2f: dict[str, str]) -> str:
    return parse_msg_call(inner, k2f).replace(
        "TranslationManager.string", "TranslationManager.list", 1
    )


def parse_tell_call(inner: str, k2f: dict[str, str]) -> str:
    target, rest = split_first_arg(inner)
    rest = rest.strip()
    if not rest.startswith('"'):
        raise ValueError(f"tell: expected key string: {rest[:80]}")
    end_quote = rest.index('"', 1)
    key = rest[1:end_quote]
    tail = rest[end_quote + 1 :].strip()
    field = lookup(key, k2f)
    if tail.startswith(","):
        tail = tail[1:].strip()
    if tail:
        return (
            f"Common.tell({target}, TranslationManager.string({target} instanceof Player pl ? pl : null, "
            f"Translations.{field}, {tail}))"
        )
    return (
        f"Common.tell({target}, TranslationManager.string({target} instanceof Player pl ? pl : null, "
        f"Translations.{field}))"
    )


def replace_method_calls(text: str, method: str, parser, k2f: dict[str, str]) -> str:
    needle = f"AuctionLocale.{method}("
    out = []
    i = 0
    while True:
        j = text.find(needle, i)
        if j == -1:
            out.append(text[i:])
            break
        out.append(text[i:j])
        open_paren = j + len(needle) - 1
        inner, after = extract_paren_content(text, open_paren)
        try:
            repl = parser(inner, k2f)
        except Exception as e:
            raise RuntimeError(f"At offset {j}: {e}\ninner={inner[:200]}") from e
        out.append(repl)
        i = after
    return "".join(out)


def strip_auction_locale_import(content: str) -> str:
    content = re.sub(
        r"\nimport ca\.tweetzy\.auctionhouse\.lang\.AuctionLocale;\s*\n",
        "\n",
        content,
    )
    content = re.sub(
        r"import ca\.tweetzy\.auctionhouse\.lang\.AuctionLocale;\s*\n",
        "",
        content,
    )
    return content


def ensure_imports(content: str) -> str:
    needs_tm = "TranslationManager." in content
    needs_tr = "Translations." in content
    needs_common = "Common." in content
    needs_player = " instanceof Player " in content
    if not (needs_tm or needs_tr or needs_common):
        return content
    lines_to_add = []
    if needs_tm and "import ca.tweetzy.flight.settings.TranslationManager;" not in content:
        lines_to_add.append("import ca.tweetzy.flight.settings.TranslationManager;")
    if needs_tr and "import ca.tweetzy.auctionhouse.settings.Translations;" not in content:
        lines_to_add.append("import ca.tweetzy.auctionhouse.settings.Translations;")
    if needs_common and "import ca.tweetzy.flight.utils.Common;" not in content:
        lines_to_add.append("import ca.tweetzy.flight.utils.Common;")
    if needs_player and "import org.bukkit.entity.Player;" not in content:
        lines_to_add.append("import org.bukkit.entity.Player;")
    if not lines_to_add:
        return content
    # Insert after package line only (must not prepend before copyright header).
    m = re.search(r"^package\s+[^;]+;\s*\n", content, re.MULTILINE)
    if m:
        insert_pos = m.end()
        return content[:insert_pos] + "\n".join(lines_to_add) + "\n" + content[insert_pos:]
    return content


def process_file(path: Path, k2f: dict[str, str]) -> bool:
    raw = path.read_text(encoding="utf-8")
    if "AuctionLocale" not in raw:
        return False
    text = raw
    text = replace_method_calls(text, "msg", parse_msg_call, k2f)
    text = replace_method_calls(text, "msgList", parse_msglist_call, k2f)
    text = replace_method_calls(text, "tell", parse_tell_call, k2f)
    text = strip_auction_locale_import(text)
    text = ensure_imports(text)
    if text != raw:
        path.write_text(text.replace("\r\n", "\n"), encoding="utf-8")
        return True
    return False


def main() -> None:
    k2f = load_key_to_field()
    print(f"Loaded {len(k2f)} translation keys")
    changed = 0
    for p in sorted(SRC.rglob("*.java")):
        if p.name == "AuctionLocale.java":
            continue
        try:
            if process_file(p, k2f):
                changed += 1
                print(p.relative_to(ROOT))
        except Exception as e:
            print(f"FAIL {p}: {e}", file=sys.stderr)
            sys.exit(1)
    print(f"Modified {changed} files")


if __name__ == "__main__":
    main()
