import ftfy

path = r"C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus\02_Desenvolvimento_e_Planejamento\07_Plano_Desenvolvimento_CSUs.md"
with open(path, encoding="utf-8") as f:
    content = f.read()

fixed = ftfy.fix_text(content)

with open(r"C:\Users\anton\git\Scelus\scratch\plan_doc_fixed_preview.md", "w", encoding="utf-8") as f:
    f.write(fixed)

print("Wrote preview. Sample around 'vinculo':")
idx = fixed.find("Fase 4")
print(fixed[idx:idx+900])
