"""Replace Tweety locale chains with AuctionLocale / Common.tell."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "src/main/java/ca/tweetzy/auctionhouse"


def parse_string_literal(s: str, i: int):
    assert s[i] == '"'
    i += 1
    out = []
    while i < len(s):
        c = s[i]
        if c == "\\" and i + 1 < len(s):
            out.append(c + s[i + 1])
            i += 2
            continue
        if c == '"':
            return "".join(out), i + 1
        out.append(c)
        i += 1
    raise ValueError("unterminated string")


def skip_ws(s, i):
    while i < len(s) and s[i] in " \t\n\r":
        i += 1
    return i


def parse_balanced_java_expr(s: str, i: int) -> tuple[str, int]:
    i = skip_ws(s, i)
    start = i
    paren = brack = brace = 0
    instr = False
    esc = False
    while i < len(s):
        c = s[i]
        if instr:
            if esc:
                esc = False
            elif c == "\\":
                esc = True
            elif c == '"':
                instr = False
            i += 1
            continue
        if c == '"':
            instr = True
            i += 1
            continue
        if c == "(":
            paren += 1
        elif c == ")":
            if paren == 0 and brack == 0 and brace == 0:
                break
            paren -= 1
        elif c == "[":
            brack += 1
        elif c == "]":
            brack -= 1
        elif c == "{":
            brace += 1
        elif c == "}":
            brace -= 1
        elif c == "." and paren == 0 and brack == 0 and brace == 0:
            la = s[i : i + 22]
            if la.startswith(".processPlaceholder("):
                break
            if la.startswith(".sendPrefixedMessage("):
                break
            if la.startswith(".getMessage()"):
                break
        elif c == "," and paren == 0 and brack == 0 and brace == 0:
            break
        i += 1
    return s[start:i].strip(), i


def escape_java(key: str) -> str:
    return key.replace("\\", "\\\\").replace('"', '\\"')


MESSAGE_CALL = '.getLocale().getMessage("'


def find_receiver_start(text: str, locale_idx: int) -> tuple[str, int] | None:
    """Return receiver Java expression and index where it starts, or None."""
    before = text[:locale_idx]
    if before.rstrip().endswith("AuctionHouse.getInstance())"):
        i = locale_idx - len("AuctionHouse.getInstance())")
        if i >= 0 and text.startswith("AuctionHouse.getInstance()", i):
            pass
    needle = before.rstrip()
    markers = []
    ix = needle.rfind("AuctionHouse.getInstance()")
    if ix != -1 and ix + len("AuctionHouse.getInstance())") == locale_idx:
        markers.append(("AuctionHouse.getInstance()", ix))
    ix = needle.rfind("instance")
    if ix != -1:
        ln = needle[ix + 8 :] if ix + 8 <= len(needle) else ""
        ok = ix == 0 or not (
            needle[max(0, ix - 1)].isalnum() or needle[max(0, ix - 1)] == "_"
        )
        if ok and ix + len("instance") == locale_idx:
            markers.append(("instance", ix))
    if markers:
        expr, sx = markers[-1]
        return expr, sx
    ix = needle.rfind("AuctionHouse.getInstance().getLocale")
    return None


def convert_get_message_chain(text: str, sx: int) -> tuple[str, int] | None:
    msg_start = text.find(MESSAGE_CALL, sx)
    if msg_start == -1:
        return None
    q = msg_start + len(MESSAGE_CALL) - 1
    key, k = parse_string_literal(text, q)
    k = skip_ws(text, k)
    if k >= len(text) or text[k] != ")":
        return None
    k += 1
    placeholders: list[tuple[str, str]] = []
    while True:
        k = skip_ws(text, k)
        if k < len(text) and text.startswith(".processPlaceholder(\"", k):
            nk = k + len('.processPlaceholder("')
            nk = nk - 1
            pname, nk = parse_string_literal(text, nk)
            nk = skip_ws(text, nk)
            if nk >= len(text) or text[nk] != ",":
                return None
            nk += 1
            expr, nk = parse_balanced_java_expr(text, nk)
            nk = skip_ws(text, nk)
            if nk >= len(text) or text[nk] != ")":
                return None
            nk += 1
            placeholders.append((pname, expr))
            k = nk
            continue
        break
    k = skip_ws(text, k)

    pk = ",".join(f'"{escape_java(n)}",{e}' for n, e in placeholders)

    def finish_sendpref():
        nonlocal k
        if k >= len(text) or not text.startswith(".sendPrefixedMessage(", k):
            return None
        nk = k + len(".sendPrefixedMessage(")
        targ, nk = parse_balanced_java_expr(text, nk)
        nk = skip_ws(text, nk)
        if nk >= len(text) or text[nk] != ")":
            return None
        nk += 1
        if pk:
            rep = f'AuctionLocale.tell({targ}, "{escape_java(key)}", {pk});'
        else:
            rep = f'AuctionLocale.tell({targ}, "{escape_java(key)}");'
        return rep, nk

    def finish_getmessage_only():
        nonlocal k
        if k >= len(text) or not text.startswith(".getMessage()", k):
            return None
        nk = k + len(".getMessage()")
        if pk:
            rep = f'AuctionLocale.msg(null, "{escape_java(key)}", {pk})'
        else:
            rep = f'AuctionLocale.msg(null, "{escape_java(key)}")'
        return rep, nk

    fm = finish_sendpref()
    if fm:
        return fm
    return finish_getmessage_only()


def convert_new_message(text: str, sx: int) -> tuple[str, int] | None:
    new_start = text.find(".getLocale().newMessage(", sx)
    if new_start == -1:
        return None
    if new_start - sx > 30:
        return None
    k = new_start + len(".getLocale().newMessage(")
    expr, k = parse_balanced_java_expr(text, k)
    k = skip_ws(text, k)
    if k >= len(text) or text[k] != ")":
        return None
    k += 1
    k = skip_ws(text, k)
    if k >= len(text) or not text.startswith(".sendPrefixedMessage(", k):
        return None
    nk = k + len(".sendPrefixedMessage(")
    targ, nk = parse_balanced_java_expr(text, nk)
    nk = skip_ws(text, nk)
    if nk >= len(text) or text[nk] != ")":
        return None
    nk += 1
    return f"Common.tell({targ}, {expr});", nk


def migrate_file(content: str) -> str:
    out = []
    i = 0
    changed = False
    while True:
        j = content.find(".getLocale()", i)
        if j == -1:
            out.append(content[i:])
            break
        gm = content.find(MESSAGE_CALL, j)
        if gm != j:
            gm = content.find(MESSAGE_CALL, i)
            if gm == -1:
                nm = convert_new_message(content, i)
                if nm:
                    sx = content.find("AuctionHouse.getInstance()", i)
                    inst = sx if sx != -1 and sx < j else None
                    if inst is None:
                        inst_local = content.rfind("instance", i, j)
                        if (
                            inst_local != -1
                            and content[inst_local : inst_local + 8] == "instance"
                            and (
                                inst_local == 0
                                or not (
                                    content[inst_local - 1].isalnum()
                                    or content[inst_local - 1] == "_"
                                )
                            )
                        ):
                            inst = inst_local
                    if inst is not None:
                        rep, nk = nm
                        out.append(content[i:inst])
                        out.append(rep)
                        ee = nk
                        if ee < len(content) and content[ee] == ";":
                            ee += 1
                        else:
                            out.append("")  # no-op
                            if ee < len(content) and content[ee : ee + 1].isspace():
                                ee = skip_ws(content, ee)
                                if ee < len(content) and content[ee] == ";":
                                    ee += 1
                        i = nk
                        if i < len(content) and content[i] == ";":
                            i += 1
                        changed = True
                        continue

            out.append(content[i : max(j, gm) if gm != -1 else j + 13])
            i = max(j, gm) + 13 if gm == -1 else j + len(".getLocale")
            continue

        sx = gm
        recv = None
        if content[sx : sx + len(MESSAGE_CALL) - 1].endswith(")") is False:
            pass

        ah = content.rfind("AuctionHouse.getInstance()", i, gm)
        inst = content.rfind("instance", max(i, gm - 120), gm)
        start = -1
        if ah != -1 and ah + len("AuctionHouse.getInstance()") + len(".getLocale().getMessage(") <= gm:
            if content[gm - len(".getLocale().getMessage") : gm].endswith("getLocale().getMessage"):
                start = ah
            else:
                start = ah
        if start == -1 and inst != -1:
            if content[gm - len(".getLocale().getMessage") : gm].endswith(""):
                prev = "" if inst == 0 else content[inst - 1]
                if inst == 0 or not (
                    prev.isalnum() or prev == "_"
                ):
                    if content[inst:gm].replace(" ", "").startswith("instance.getLocale"):
                        start = inst
        if start == -1:
            out.append(content[i : gm + 1])
            i = gm + 1
            continue

        res = convert_get_message_chain(content, start)
        if res is None:
            out.append(content[i : gm + 13])
            i = gm + 13
            continue
        rep, end = res
        out.append(content[i:start])
        out.append(rep)
        i = end
        if i < len(content) and content[i : skip_ws(content, i) + 1].strip():
            jj = skip_ws(content, end)
            if jj < len(content) and content[jj] == ";":
                end = jj + 1
                i = end
        elif i < len(content) and content[i] == ";":
            i += 1
        changed = True
        if not changed:
            pass
    joined = "".join(out)
    return joined if joined != content else content


def main():
    targets = sorted(ROOT.rglob("*.java"))
    for fp in targets:
        if fp.name in ("AuctionLocale.java", "LocaleSettings.java"):
            continue
        t = fp.read_text(encoding="utf-8")
        if ".getLocale()" not in t:
            continue
        from tools.migrate_locale_v2 import apply_migrations


if __name__ == "__main__":
    raise SystemExit("Use migrate_locale_v2.py")
