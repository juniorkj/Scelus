orig_path = r"C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus\02_Desenvolvimento_e_Planejamento\07_Plano_Desenvolvimento_CSUs.md"
fixed_path = r"C:\Users\anton\git\Scelus\scratch\plan_doc_fixed_preview.md"

orig = open(orig_path, encoding="utf-8").read()
fixed = open(fixed_path, encoding="utf-8").read()

with open(r"C:\Users\anton\git\Scelus\scratch\diff_report.txt", "w", encoding="utf-8") as out:
    out.write(f"orig len: {len(orig)}\n")
    out.write(f"fixed len: {len(fixed)}\n")
    out.write(f"remaining U+FFFD in fixed: {fixed.count(chr(0xFFFD))}\n")
    out.write(f"remaining 'Ã' in fixed: {fixed.count('Ã')}\n")
    out.write(f"remaining 'â€' in fixed: {fixed.count('â€')}\n\n")

    import difflib

    orig_lines = orig.splitlines()
    fixed_lines = fixed.splitlines()
    sm = difflib.SequenceMatcher(None, orig_lines, fixed_lines, autojunk=False)
    changed_lines = 0
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag != "equal":
            changed_lines += max(i2 - i1, j2 - j1)
    out.write(f"changed lines (line-level diff): {changed_lines} / {len(orig_lines)}\n\n")

    out.write("--- sample of changed line pairs (first 40) ---\n")
    shown = 0
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal" or shown >= 40:
            continue
        for k in range(max(i2 - i1, j2 - j1)):
            oi = i1 + k
            fj = j1 + k
            oline = orig_lines[oi] if oi < i2 else "<no orig line>"
            fline = fixed_lines[fj] if fj < j2 else "<no fixed line>"
            if oline != fline:
                out.write(f"L{oi+1} ORIG:  {oline}\n")
                out.write(f"L{fj+1} FIXED: {fline}\n\n")
                shown += 1
                if shown >= 40:
                    break

print("done")
