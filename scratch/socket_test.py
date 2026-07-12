import socket

target = "172.19.35.62"
port = 5432

try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(3)
    s.connect((target, port))
    print(f"Conexão socket TCP na porta {port} do host {target} realizada com SUCESSO!")
    s.close()
except Exception as e:
    print(f"Erro ao conectar via socket na porta {port} do host {target}:", e)
