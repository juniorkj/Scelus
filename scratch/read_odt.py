import zipfile
import xml.etree.ElementTree as ET
import os

odt_path = r"c:\Users\anton\git\Scelus\docs\SCELUS_CSU002 - Cadastrar Crimes do Processo.odt"

try:
    with zipfile.ZipFile(odt_path) as z:
        content_xml = z.read("content.xml")
        root = ET.fromstring(content_xml)
        
        # ODT usa namespaces xml. Vamos extrair o texto de elementos com tags de parágrafo e texto
        namespaces = {
            'text': 'urn:oasis:names:tc:opendocument:xmlns:text:1.0',
            'office': 'urn:oasis:names:tc:opendocument:xmlns:office:1.0'
        }
        
        text_content = []
        for paragraph in root.iter('{urn:oasis:names:tc:opendocument:xmlns:text:1.0}p'):
            # Junta todos os textos dentro do parágrafo
            text = "".join(paragraph.itertext())
            if text.strip():
                text_content.append(text)
                
        print("\n".join(text_content))
except Exception as e:
    print("Erro ao ler ODT:", e)
