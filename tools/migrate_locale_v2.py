"""Replace Tweety locale usage with AuctionLocale. Run: cd \"Auction House\" && python tools/migrate_locale_v2.py"""
from pathlib import Path
from typing import Optional

JAVA_ROOT = Path(__file__).resolve().parent.parent / "src/main/java/ca/tweetzy/auctionhouse"
LOC_MSG = '.getLocale().getMessage("'


def read_string_lit(text: str, i: int) -> tuple[str, int]:
    if text[i] != '"':
        raise ValueError
    i += 1
    parts = []
    while i < len(text):
        c = text[i]
        if c == "\\":
            parts.append("\\" + text[i + 1])
            i += 2
            continue
        if c == '"':
            return "".join(parts), i + 1
        parts.append(c)
        i += 1


def esc_java(s: str) -> str:
    return (
        s.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n").replace("\r", "\\r")
    )


def collapsed(text: str) -> str:
    return "".join(text.split())


def skip_ws(text, i):
    while i < len(text) and text[i] in " \t\r\n":
        i += 1
    return i


def read_expression(text: str, i: int) -> tuple[str, int]:
    i = skip_ws(text, i)
    start = i
    parens = brack = brace = 0
    instr = False
    backslash = False
    while i < len(text):
        c = text[i]
        if instr:
            if backslash:
                backslash = False
            elif c == "\\":
                backslash = True
            elif c == '"':
                instr = False
            i += 1
            continue
        if c == '"':
            instr = True
            i += 1
            continue
        if c == "(":
            parens += 1
        elif c == ")":
            if parens == 0 and brack == 0 and brace == 0:
                break
            parens -= 1
        elif c == "[":
            brack += 1
        elif c == "]":
            brack -= 1
        elif c == "{":
            brace += 1
        elif c == "}":
            brace -= 1
        elif parens == 0 and brack == 0 and brace == 0 and c == ".":
            lookahead = text[i : i + 26]
            if (
                lookahead.startswith(".processPlaceholder(")
                or lookahead.startswith(".sendPrefixedMessage(")
                or lookahead.startswith(".getMessage(")
            ):
                break
        elif c == "," and parens == 0 and brack == 0 and brace == 0:
            break
        i += 1
    return text[start:i].strip(), i


def find_receiver_start(text: str, gm: int) -> Optional[int]:
    ah = text.rfind("AuctionHouse.getInstance()", 0, gm)
    if ah != -1 and collapsed(text[ah:gm]) == collapsed("AuctionHouse.getInstance()"):
        return ah
    ws = max(0, gm - 200)
    while True:
        ins = text.rfind("instance", ws, gm)
        if ins == -1:
            break
        if ins > 0 and (text[ins - 1].isalnum() or text[ins - 1] == "_"):
            ws = ins - 1
            continue
        if collapsed(text[ins:gm]) == "instance":
            return ins
        ws = ins - 1
    return None


def transform_locale_calls(text: str):
    parts = []
    i = 0
    modified = False
    while True:
        gm = text.find(LOC_MSG, i)
        if gm == -1:
            parts.append(text[i:])
            break
        rs = find_receiver_start(text, gm)
        if rs is None:
            parts.append(text[i : gm + len(LOC_MSG)])
            i = gm + len(LOC_MSG)
            continue
        qu = gm + len(LOC_MSG) - 1
        key, qi = read_string_lit(text, qu)
        qi = skip_ws(text, qi)
        if qi >= len(text) or text[qi] != ")":
            parts.append(text[i : gm + len(LOC_MSG)])
            i = gm + len(LOC_MSG)
            continue
        qi += 1

        plist = []
        while True:
            qi = skip_ws(text, qi)
            if qi >= len(text) or not text[qi:].startswith('.processPlaceholder("'):
                break
            qk = qi + len('.processPlaceholder("') - 1
            pk, qk = read_string_lit(text, qk)
            qk = skip_ws(text, qk)
            if qk >= len(text) or text[qk] != ",":
                raise RuntimeError("bad comma after placeholder")
            qk += 1
            ex, qk = read_expression(text, qk)
            qk = skip_ws(text, qk)
            if qk >= len(text) or text[qk] != ")":
                raise RuntimeError("closing processPlaceholder")
            qk += 1
            plist.append((pk, ex))
            qi = qk

        qi = skip_ws(text, qi)
        pk_java = ",".join(f'"{esc_java(pk)}",{ex}' for pk, ex in plist)
        ek = esc_java(key)
        replaced = False
        if qi < len(text) and text[qi:].startswith(".sendPrefixedMessage("):
            q2 = qi + len(".sendPrefixedMessage(")
            tgt, q2 = read_expression(text, q2)
            q2 = skip_ws(text, q2)
            if q2 >= len(text) or text[q2] != ")":
                raise RuntimeError("sendPrefixed closing")
            q2 += 1
            if pk_java:
                rep = f'AuctionLocale.tell({tgt}, "{ek}", {pk_java});'
            else:
                rep = f'AuctionLocale.tell({tgt}, "{ek}");'
            parts.append(text[i:rs])
            parts.append(rep)
            i = q2
            qi2 = skip_ws(text, i)
            if qi2 < len(text) and text[qi2] == ";":
                i = qi2 + 1
            else:
                i = qi2
            modified = True
            replaced = True

        elif qi < len(text) and text[qi:].startswith(".getMessage()"):
            end = qi + len(".getMessage()")
            if pk_java:
                rep = f'AuctionLocale.msg(null, "{ek}", {pk_java})'
            else:
                rep = f'AuctionLocale.msg(null, "{ek}")'
            parts.append(text[i:rs])
            parts.append(rep)
            i = end
            modified = True
            replaced = True

        if not replaced:
            parts.append(text[i : gm + len(LOC_MSG)])
            i = gm + len(LOC_MSG)

    step1 = "".join(parts)

    parts2 = []
    i = 0
    while True:
        nm = step1.find(".getLocale().newMessage(", i)
        if nm == -1:
            parts2.append(step1[i:])
            break

        gm = nm
        rs = None
        ah = step1.rfind("AuctionHouse.getInstance()", 0, gm)
        if ah != -1 and collapsed(step1[ah:gm]) == collapsed("AuctionHouse.getInstance()"):
            rs = ah
        if rs is None:
            ix = gm
            ws = max(0, gm - 200)
            while True:
                ins = step1.rfind("instance", ws, gm)
                if ins == -1:
                    break
                if ins > 0 and (
                    step1[ins - 1].isalnum() or step1[ins - 1] == "_"
                ):
                    ws = ins - 1
                    continue
                if collapsed(step1[ins:gm]) == "instance":
                    rs = ins
                    break
                ws = ins - 1

        if rs is None:
            parts2.append(step1[i : nm + 1])
            i = nm + 1
            continue

        k = nm + len(".getLocale().newMessage(")
        ex, k = read_expression(step1, k)
        k = skip_ws(step1, k)
        if k >= len(step1) or step1[k] != ")":
            raise RuntimeError("newMessage")
        k += 1
        k = skip_ws(step1, k)
        if not step1[k:].startswith(".sendPrefixedMessage("):
            parts2.append(step1[i : nm + 1])
            i = nm + 1
            continue
        k2 = k + len(".sendPrefixedMessage(")
        tgt, k2 = read_expression(step1, k2)
        k2 = skip_ws(step1, k2)
        if k2 >= len(step1) or step1[k2] != ")":
            raise RuntimeError("sendPrefixed newMessage")
        k2 += 1
        parts2.append(step1[i:rs])
        parts2.append(f"Common.tell({tgt}, {ex});")
        i = k2
        k3 = skip_ws(step1, i)
        if k3 < len(step1) and step1[k3] == ";":
            i = k3 + 1
        modified = True

    return "".join(parts2), modified


def main():
    for fp in sorted(JAVA_ROOT.rglob("*.java")):
        if fp.name in ("AuctionLocale.java", "LocaleSettings.java"):
            continue
        t = fp.read_text(encoding="utf-8")
        if ".getLocale()" not in t:
            continue
        nt, mod = transform_locale_calls(t)
        if mod:
            fp.write_text(nt, encoding="utf-8")
            print("updated", fp.relative_to(fp.parent))


if __name__ == "__main__":
    main()
