# Plano de Desenvolvimento — Implementação dos CSUs (Scelus)

Este plano detalha as etapas de desenvolvimento necessárias para implementar os casos de uso **CSU001**, **CSU002** e **CSU008** no ecossistema Scelus (Angular 21 + Spring Boot 4 + Postgres).

---

## 🎯 Escopo dos Casos de Uso
1. **CSU001 - Consultar Crimes do Processo:** Consulta dinâmica de crimes associados a processos com filtros simples e avançados.
2. **CSU002 - Cadastrar Crimes do Processo:** Consulta de dados no PJe por número do processo e cadastro de crimes, vítimas, acusados e vínculos.
3. **CSU008 - Cadastrar Medidas Protetivas de Urgência (MPU):** Cadastro de novas MPUs ou associação de medidas anteriores a um fato ocorrido com justificativa.

---

## 🏗️ Fase 1: Mapeamento de Entidades JPA (Backend)
Antes de iniciar os fluxos, precisamos mapear as tabelas existentes no banco PostgreSQL (`dba_scelus`) como entidades JPA.

- [ ] **Mapear entidades core em `br.jus.tjma.scelus.comum.model` (ou pacotes específicos):**
  - `ProcessoCrime` (`tb_processo_crime`)
  - `FatoOcorrido` (`tb_fato_ocorrido`)
  - `Vitima` (`tb_vitima`)
  - `Acusado` (`tb_acusado`)
  - `Vinculo` (`tb_vinculo`)
  - `MedidaProtetivaUrgencia` (`tb_medida_protetiva_urgencia`)
- [ ] **Criar Repositórios Spring Data JPA (`JpaRepository`):**
  - `ProcessoCrimeRepository`, `FatoOcorridoRepository`, etc.

---

## 🔍 Fase 2: Implementação do CSU001 (Consulta de Crimes)
Módulo de busca com filtros dinâmicos.

- [ ] **Backend (scelus-api):**
  - Criar DTO de filtro `FiltroConsultaCrimes` estendendo `FiltroPesquisa` da `infra-spring`.
  - Criar `CrimeSpecification` usando JPA Criteria API para suportar filtros avançados (crime, fato, vítima, acusado, MPU, etc.).
  - Criar endpoint `GET /api/crimes` retornando `ResultList<CrimeDTO>`.
- [ ] **Frontend (scelus-web):**
  - Gerar feature via schematics: `ng g @tjma/angular:feature --name=consultar-crimes`.
  - Configurar filtros no HTML usando componentes `@tjma/angular` (`TjInput`, `TjDatePicker`, etc.).
  - Configurar a listagem usando o componente `TjTable`.

---

## 📝 Fase 3: Implementação do CSU002 (Cadastro de Crimes & PJe)
Módulo de cadastro integrando com a consulta de dados do PJe.

- [ ] **Backend (scelus-api):**
  - Criar cliente/integração mockada para busca de processos no PJe (simulando a resposta da procedure/serviço do TJMA).
  - Implementar lógica de persistência transacional (`@Transactional`) para salvar o processo, o fato ocorrido e os acusados/vítimas relacionados.
  - Criar endpoint `POST /api/crimes`.
- [ ] **Frontend (scelus-web):**
  - Criar tela de seleção de processo (informa o número e clica em Buscar).
  - Exibir dados retornados do PJe e liberar o preenchimento de sentença.
  - Formulário dinâmico para cadastrar/associar crimes baseado nos assuntos importados do PJe.

---

## 🛡️ Fase 4: Implementação do CSU008 (Medidas Protetivas - MPU)
Manutenção de MPUs a partir do contexto de um fato ocorrido.

- [ ] **Backend (scelus-api):**
  - Criar endpoint `GET /api/fatos-ocorridos/{id}/mpus` para listar medidas prévias.
  - Criar endpoint `POST /api/fatos-ocorridos/{id}/mpus` para vincular/cadastrar uma MPU com justificativa.
  - Validar regras de negócio (ex: obrigatoriedade do fato, verificação de par acusado-vítima).
- [ ] **Frontend (scelus-web):**
  - Criar o componente seletor/cadastro de MPU em modal usando `@tjma/angular:selector`.
  - Integrar o seletor no fluxo de cadastro do CSU002.

---

## 🧪 Fase 5: Testes e Validação
- [ ] Criar testes unitários no backend mockando o `UsuarioContext` do Sentinela.
- [ ] Executar build completo integrado para validar o classpath.
