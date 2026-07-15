import os

vault = r"C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus"
markers = ["â€", chr(0xFFFD), chr(0x00C3) + chr(0x00AD)]

report = []
for root, dirs, files in os.walk(vault):
    for name in files:
        if not name.endswith(".md"):
            continue
        path = os.path.join(root, name)
        try:
            with open(path, encoding="utf-8") as f:
                content = f.read()
        except Exception as e:
            report.append(f"{path}: ERROR {e}")
            continue
        counts = {repr(m): content.count(m) for m in markers}
        if any(counts.values()):
            report.append(f"{path}: {counts}")

with open(r"C:\Users\anton\git\Scelus\scratch\vault_mojibake_report.txt", "w", encoding="utf-8") as out:
    out.write("\n".join(report) if report else "No mojibake markers found in any .md file.")
