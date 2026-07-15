fixed_path = r"C:\Users\anton\git\Scelus\scratch\plan_doc_fixed_preview.md"
target_path = r"C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus\02_Desenvolvimento_e_Planejamento\07_Plano_Desenvolvimento_CSUs.md"

with open(fixed_path, encoding="utf-8") as f:
    content = f.read()

with open(target_path, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print("Written.")
