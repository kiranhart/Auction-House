# One-off: convert ConfigSetting -> FlightSettings.create (ConfigEntry)
import re
from pathlib import Path

PATH = Path(__file__).resolve().parent.parent / "src/main/java/ca/tweetzy/auctionhouse/settings/Settings.java"


def skip_ws(s, i):
    while i < len(s) and s[i] in " \t\n\r":
        i += 1
    return i


def parse_string_literal(s, i):
    if i >= len(s) or s[i] != '"':
        return None, i
    i += 1
    out = []
    while i < len(s):
        c = s[i]
        if c == "\\":
            if i + 1 < len(s):
                out.append(c + s[i + 1])
                i += 2
                continue
        if c == '"':
            return "".join(out), i + 1
        out.append(c)
        i += 1
    raise ValueError("unterminated string")


def parse_value_expr(s, i):
    """Parse Java expression until top-level comma or closing paren of ConfigSetting call."""
    i = skip_ws(s, i)
    start = i
    paren = bracket = brace = 0
    in_str = False
    esc = False
    while i < len(s):
        c = s[i]
        if in_str:
            if esc:
                esc = False
            elif c == "\\":
                esc = True
            elif c == '"':
                in_str = False
            i += 1
            continue
        if c == '"':
            in_str = True
            i += 1
            continue
        if c == "(":
            paren += 1
        elif c == ")":
            if paren == 0 and bracket == 0 and brace == 0:
                break
            paren -= 1
        elif c == "[":
            bracket += 1
        elif c == "]":
            bracket -= 1
        elif c == "{":
            brace += 1
        elif c == "}":
            brace -= 1
        elif c == "," and paren == 0 and bracket == 0 and brace == 0:
            break
        i += 1
    return s[start:i].strip(), i


def convert_file(text):
    prefix = "new ConfigSetting(config, "
    out = []
    pos = 0
    while True:
        idx = text.find(prefix, pos)
        if idx == -1:
            out.append(text[pos:])
            break
        out.append(text[pos:idx])
        i = idx + len(prefix)
        i = skip_ws(text, i)
        key, i = parse_string_literal(text, i)
        if key is None:
            raise ValueError(f"expected key string at {idx}")
        i = skip_ws(text, i)
        if i >= len(text) or text[i] != ",":
            raise ValueError(f"expected comma after key {key!r}")
        i += 1
        val, i = parse_value_expr(text, i)
        comments = []
        while True:
            i = skip_ws(text, i)
            if i < len(text) and text[i] == ",":
                i += 1
                i = skip_ws(text, i)
                if i < len(text) and text[i] == '"':
                    cmt, i = parse_string_literal(text, i)
                    comments.append(cmt)
                    continue
            break
        i = skip_ws(text, i)
        if i >= len(text) or text[i] != ")":
            raise ValueError(f"expected ) after config setting {key!r} at {i}")
        i += 1
        if i < len(text) and text[i] == ";":
            i += 1
        # build create(...)
        args = [java_string_literal(key), val]
        for c in comments:
            args.append(java_string_literal(c))
        line = "create(" + ", ".join(args) + ");"
        out.append(line)
        pos = i
    return "".join(out)


def java_string_literal(content):
    esc = content.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n").replace("\r", "\\r")
    return '"' + esc + '"'


# Fix: use simple replacement for whole file structure
def main():
    text = PATH.read_text(encoding="utf-8")
    # header
    text = text.replace(
        "import ca.tweetzy.auctionhouse.AuctionHouse;\nimport ca.tweetzy.core.configuration.Config;\nimport ca.tweetzy.core.configuration.ConfigSetting;",
        "import ca.tweetzy.flight.config.ConfigEntry;\nimport ca.tweetzy.flight.settings.FlightSettings;",
    )
    text = text.replace("public class Settings {", "public final class Settings extends FlightSettings {")
    text = text.replace(
        "\tstatic final Config config = AuctionHouse.getInstance().getCoreConfig();\n\n",
        "",
    )
    text = text.replace("public static final ConfigSetting ", "public static final ConfigEntry ")
    converted = convert_file(text)
    # setup() -> init()
    converted = converted.replace(
        "\tpublic static void setup() {\n\t\tconfig.load();\n\t\tconfig.setAutoremove(false).setAutosave(true);\n\t\tconfig.saveChanges();\n\t}",
        '\tpublic static void init() {\n\t\tca.tweetzy.flight.FlightPlugin.getCoreConfig().init();\n\t}',
    )
    PATH.write_text(converted, encoding="utf-8")
    print("done")


if __name__ == "__main__":
    main()
