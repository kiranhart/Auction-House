import re
from pathlib import Path

root = Path(r"D:/Development/Spigot Plugins/Active/Auction House/src/main/java")
keys = set()
for p in root.rglob("*.java"):
    t = p.read_text(encoding="utf-8")
    for m in re.finditer(r'getMessage\("([^"]+)"\)', t):
        keys.add(m.group(1))

tr = Path(
    r"D:/Development/Spigot Plugins/Active/Auction House/src/main/java/ca/tweetzy/auctionhouse/settings/Translations.java"
).read_text(encoding="utf-8")
defined = set(re.findall(r'create\("([^"]+)"', tr))

def lk(s):
    return s.lower()

dlow = {lk(x) for x in defined}
missing = sorted(k for k in keys if lk(k) not in dlow)
print("usage keys", len(keys))
print("missing count", len(missing))
for x in missing:
    print(" ", repr(x))
