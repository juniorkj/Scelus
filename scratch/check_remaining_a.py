path = r"C:\Users\anton\git\Scelus\scratch\plan_doc_fixed_preview.md"
with open(path, encoding="utf-8") as f:
    content = f.read()

with open(r"C:\Users\anton\git\Scelus\scratch\remaining_a_report.txt", "w", encoding="utf-8") as out:
    idx = 0
    while True:
        idx = content.find("Ã", idx)
        if idx == -1:
            break
        out.write(repr(content[max(0, idx - 40):idx + 40]) + "\n\n")
        idx += 1
