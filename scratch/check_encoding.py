path = r"C:\Users\anton\OneDrive\Documentos\TJMA\obsidian\scelus\02_Desenvolvimento_e_Planejamento\07_Plano_Desenvolvimento_CSUs.md"
with open(path, encoding="utf-8") as f:
    lines = f.readlines()
for i in range(57, 64):
    print(i + 1, lines[i], end="")

print("\n--- scan whole file for remaining mojibake markers ---")
with open(path, encoding="utf-8") as f:
    content = f.read()
markers = ["Ã", "â€", "�"]
for m in markers:
    print(m, "->", content.count(m))
