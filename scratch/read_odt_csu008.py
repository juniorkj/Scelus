import zipfile
import xml.etree.ElementTree as ET

odt_path = r"c:\Users\anton\git\Scelus\docs\SCELUS_CSU008 - Cadastrar Medidas Protetivas de Urgência.odt"

try:
    with zipfile.ZipFile(odt_path) as z:
        content_xml = z.read("content.xml")
        root = ET.fromstring(content_xml)

        text_content = []
        for paragraph in root.iter('{urn:oasis:names:tc:opendocument:xmlns:text:1.0}p'):
            text = "".join(paragraph.itertext())
            if text.strip():
                text_content.append(text)

        # Também captura tabelas (regras de negócio, telas), que <text:p> sozinho pode não pegar bem
        ns_table = '{urn:oasis:names:tc:opendocument:xmlns:table:1.0}'
        print("=== PARÁGRAFOS ===")
        print("\n".join(text_content))

        print("\n\n=== TABELAS ===")
        for table in root.iter(f'{ns_table}table'):
            print("\n--- Tabela ---")
            for row in table.iter(f'{ns_table}table-row'):
                cells = []
                for cell in row.iter(f'{ns_table}table-cell'):
                    cell_text = " ".join(
                        "".join(p.itertext())
                        for p in cell.iter('{urn:oasis:names:tc:opendocument:xmlns:text:1.0}p')
                    )
                    cells.append(cell_text)
                print(" | ".join(cells))
except Exception as e:
    print("Erro ao ler ODT:", e)
