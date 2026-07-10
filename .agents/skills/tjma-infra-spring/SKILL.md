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
