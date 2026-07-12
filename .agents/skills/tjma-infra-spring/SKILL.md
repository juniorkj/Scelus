---
name: tjma-infra-spring
description: Infraestrutura Spring Boot do TJMA (Spring Boot 4.0.3 + Java 21) com integração ao Sentinela e suporte a Procedures Oracle.
---
# SKILL: tjma-infra-spring — Guia de Uso para o scelus-api

A `infra-spring` é a infraestrutura backend do TJMA, migrada de **Java EE (JEE + CDI + JBoss)** para **Spring Boot 4.0.3 + Java 21**. Ela provê:

- Autenticação/autorização via **Sentinela** (SSO Oracle TJMA)
- Execução segura de **Stored Procedures Oracle** (tratamento REF_CURSOR)
- Controle de acesso declarativo com `@AcessoLivre`
- Classes base para CRUD (`SuporteCrud`, `AbstractNegocioService`)
- Utilitários de persistência, validação e relatório

**O `scelus-api` usa a infra-spring como dependência Maven.**

---

## 2. Integração com Sentinela (SSO)

### 2.1 Fluxo de Autenticação

```
Usuário → Sentinela Login → callback /?token=XXX
→ SentinelaServletFilter intercepta todas as rotas /api/* e /rest/*
→ Chama DBA_SENTINELA.PKG_VALIDACAO.sp_validar_token via ExecutorProcedureJpa
→ Popula UsuarioContextContainer (ThreadLocal)
→ Controller recebe request com contexto preenchido
```

### 2.2 Configuração Obrigatória no `application.yml`

```yaml
sentinela:
  enabled: true
  id-sistema: scelus               # CHAR(20) — deve ter exatamente 20 chars (padding automático)
  url-base: https://sistemasd.tjma.jus.br/sentinela
  frontend-url: http://localhost:4200

id_sistema: scelus                 # lido diretamente pela infra (seguranca.properties)
```

> ⚠️ **Padding CHAR(20):** O Oracle armazena `STR_SISTEMA_ID` como `CHAR(20)`. A infra aplica `Strings.rightPad(idSistema, 20)` automaticamente antes de enviar para a procedure. Se o ID do sistema não for encontrado, verifique esse padding.

### 2.3 Beans que devem ser importados no Application

```java
@SpringBootApplication
@ComponentScan(
    basePackages = {"br.jus.tjma.scelus", "br.jus.tjma.infraspring.seguranca"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = br.jus.tjma.infraspring.seguranca.WebConfig.class)
)
@Import({
    br.jus.tjma.infraspring.config.ServletConfig.class,           // registra LoginSentinelaServlet
    br.jus.tjma.infraspring.sentinela.SentinelaConfig.class,      // registra SentinelaServletFilter
    br.jus.tjma.infraspring.sentinela.authz.UsuarioContextService.class,
    br.jus.tjma.infraspring.sentinela.impl.ClienteSentinela.class
})
public class ScelusApiApplication { ... }
```

### 2.4 UsuarioContext — Contexto do Usuário Autenticado

```java
// Wrapper injetável sobre o ThreadLocal da infra
@Component
@RequestScope
public class UsuarioContext {
    // Delega para UsuarioContextContainer.get() (preenchido pelo SentinelaServletFilter)
    public String getLogin()       // matrícula TJMA (ex: "129965")
    public String getNome()        // nome completo
    public String getEmail()       // email institucional
    public Set<String> getGrupos() // grupos Sentinela ("Administradores", "Transporte", ...)
    public boolean isAdmin()
}
```

### 2.5 `@AcessoLivre` — Rotas Públicas

Por padrão, **todas as rotas são bloqueadas** pelo `SentinelaServletFilter`. Para liberar endpoints específicos:

```java
@RestController
@RequestMapping("/api/teste-conexao")
public class ConexaoTesteController {

    @AcessoLivre                    // ← libera esta rota sem token
    @GetMapping("/livre")
    public String rotaPublica() {
        return "rota não autenticada";
    }
}
```

---

## 3. Stored Procedures Oracle — ExecutorProcedureJpa

O Hibernate 6 (Spring Boot 3.x) é **mais rigoroso** com cursores Oracle:
- Parâmetros `OUT` do tipo `ORACLE_CURSOR` exigem mapeamento explícito de posição e tipo.
- O cursor deve permanecer aberto durante toda a iteração.

```java
@Service
@RequiredArgsConstructor
public class MeuService {

    private final ExecutorProcedureJpa executorProcedure;

    public List<Map<String, Object>> buscarPorProcedure(String parametro) {
        List<ProcedureParam> params = List.of(
            ProcedureParam.in("P_PARAMETRO", parametro),
            ProcedureParam.in("P_ID_SISTEMA", "scelus")
        );

        ProcedureExecutada resultado = executorProcedure.executar(
            "DBA_FROTTAS.PKG_VEICULO.SP_LISTAR",
            params,
            new ExtratorCursor()
        );

        return resultado.getResultados();
    }
}
```

---

## 4. Build e Publicação no Nexus

A `infra-spring` é publicada como artefato Maven (`br.jus.tjma.infra:tjma-infra-spring:0.0.1`) no
Nexus TJMA (`tjma-nexus-releases`), consumida pelo `scelus-api` e outros projetos via
`maven-public`.

### 4.1 Pré-requisitos

- **JDK 21** instalado na máquina (ex: `C:\Program Files\Java\jdk-21`).
- Variáveis de ambiente `TJMA_NEXUS_USR` / `TJMA_NEXUS_PWD` — usadas pelos servers
  `tjma-nexus-releases` e `tjma-nexus-snapshots` no `~/.m2/settings.xml`.

### 4.2 Comando de build + deploy

⚠️ **`mvn` nesta máquina pode ter `JAVA_HOME` apontando para um JDK 8 por padrão.** Se isso
acontecer, o `maven-compiler-plugin` ignora o `<maven.compiler.release>21</maven.compiler.release>`
herdado do `spring-boot-starter-parent` e cai silenciosamente para `-source 1.8 -target 1.8`,
quebrando a compilação de qualquer `record` do projeto (`Credenciais`, `DadosComplementaresUsuario`)
com o erro `class, interface, or enum expected`. **Sempre force o `JAVA_HOME`/`PATH` para o JDK 21**
ao rodar o build:

```bash
cd infra-spring
JAVA_HOME="/c/Program Files/Java/jdk-21" PATH="/c/Program Files/Java/jdk-21/bin:$PATH" \
  mvn -B clean deploy
```

Para diagnosticar qual JDK o `mvn` está usando: `mvn -version` (campo "Java version").

### 4.3 Cache local do `.m2` (evitar artefato antigo)

Ao republicar uma versão (ex: depois de apagar do Nexus e corrigir algo), remova o cache local
**antes** do build, para o `scelus-api` (ou outro consumidor) não continuar usando o jar antigo já
resolvido:

```bash
rm -rf ~/.m2/repository/br/jus/tjma/infra/tjma-infra-spring
```

### 4.4 Erro `PKIX path building failed` no deploy (Avast/antivírus com proxy TLS)

Em máquinas onde o Avast intercepta HTTPS (`SSL/TLS scanning`), o Nexus responde com um certificado
re-assinado pela CA local `Avast Web/Mail Shield Root`. Se essa CA não estiver no `cacerts` do JDK
usado no build, o `deploy` falha com:

```
PKIX path building failed: ... unable to find valid certification path to requested target
```

Correção (uma vez por JDK/máquina — precisa de terminal elevado/Administrador, pois
`Program Files` não é gravável sem elevação):

```powershell
# 1. Exportar a CA da Avast do repositório de certificados do Windows
$cert = Get-ChildItem Cert:\LocalMachine\Root | Where-Object { $_.Subject -like "*Avast*" }
[System.IO.File]::WriteAllBytes("$env:TEMP\avast-root.cer", $cert.Export('Cert'))

# 2. Importar no cacerts do JDK 21 (rodar em terminal ELEVADO)
& "C:\Program Files\Java\jdk-21\bin\keytool.exe" -importcert -noprompt -trustcacerts `
  -alias avastwebmailshieldroot `
  -keystore "C:\Program Files\Java\jdk-21\lib\security\cacerts" `
  -storepass changeit `
  -file "$env:TEMP\avast-root.cer"
```

Verificar se já está importado: `keytool -list -keystore "<cacerts>" -storepass changeit -alias avastwebmailshieldroot`.

### 4.5 Checklist rápido de deploy

1. `JAVA_HOME`/`PATH` apontando para JDK 21 (não JDK 8).
2. `TJMA_NEXUS_USR`/`TJMA_NEXUS_PWD` definidos no ambiente.
3. Se for republicar uma versão já existente no Nexus: apagar antes o cache em
   `~/.m2/repository/br/jus/tjma/infra/tjma-infra-spring`.
4. `mvn -B clean deploy` — deve terminar com `BUILD SUCCESS` e uploads de `.pom`/`.jar` para
   `tjma-nexus-releases`.
5. No projeto consumidor (`scelus-api`), rodar `mvn clean install` (ou reiniciar a aplicação) para
   puxar a versão nova.

---

## 💡 Diretrizes e Padrões de Desenvolvimento (Mandatório)

1. **Priorizar funções de banco:** Priorizar a utilização de funções e procedures existentes na base principal PostgreSQL.
2. **Consultas JPA vs Funções:** Utilizar JPA apenas para consultas simples com no máximo 3 joins. Sempre preferir ou oferecer o uso de funções do banco caso existam para a mesma finalidade.
3. **Preferência por Records Java:** Dar preferência para a utilização de classes `record` do Java 21 para DTOs, payloads e estruturas de dados imutáveis de transferência.
4. **Nomenclatura e Comentários:** Comentários de código e Javadocs devem ser escritos em português. Dê preferência para nomes de métodos e variáveis em português.
5. **Template Obrigatório de Javadoc para Classes:**
   ```java
   /**
    * javadoc Descrição da classe
    * 
    * @author Fco Antonio S. Júnior
    * @email fjunior.pdcase@gmail.com
    * @empresa Pd case
    * @version 1.0
    * @since [data de criação]
    */
   ```
6. **Alertas de Permissão:** Sempre avisar no log/output quando for identificado que um novo objeto precisa de concessão de permissão no Sentinela.

