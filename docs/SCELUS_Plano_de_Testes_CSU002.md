# Plano de Testes — Caso de Uso CSU002 (Scelus)

Este plano descreve o roteiro estruturado para testar a tela de cadastro de crimes (**CSU002**) do projeto **Scelus** após a refatoração para a arquitetura de passos (wizard) utilizando o componente `TjStepper`.

---

## 📋 Roteiro de Teste do Assistente (Stepper)

### **Passo 1: Processo (CNJ)**
1. Acesse o sistema na rota `/crimes/new`.
2. No campo **Número do Processo (CNJ)**, informe um processo de teste válido com dblink ativo no PJe (ex: `0836084-52.2016.8.10.0001` ou `0806372-60.2017.8.10.0040`).
3. Clique no botão **Buscar no PJe**.
4. O sistema deve retornar um banner informativo em azul identificando as partes localizadas e o status de sentença. Os campos de detalhamento do processo devem ficar preenchidos em modo leitura.
5. Clique em **Avançar**.

---

### **Passo 2: Benefícios & Configuração Familiar**
1. **Benefícios:**
   * No dropdown **Parte**, selecione um dos envolvidos importados.
   * No dropdown **Tipo**, selecione um benefício assistencial (ex: *Bolsa Família*).
   * Escolha uma data no date picker de **Início**.
   * Clique em **Adicionar**. O benefício será listado na tabela local abaixo.
2. **Configuração Familiar:**
   * No dropdown **Configuração Familiar**, selecione um tipo (ex: *Nuclear*).
   * Escolha uma data de **Data da Declaração**.
   * Adicione alguma observação no campo de texto e clique em **Adicionar**. A configuração será listada na tabela abaixo.
3. Clique em **Avançar**.

---

### **Passo 3: Fato Ocorrido**
1. Preencha o **Código do Assunto** (ex: `14234`).
2. Insira a **Data do Fato** no calendário.
3. Selecione a opção **Possui Medida Protetiva?** (*Sim* ou *Não*).
4. No campo **Pesquisar CEP**, insira um CEP de teste (ex: `65010-000`) e clique em **Buscar CEP**. Selecione a opção correspondente no dropdown **CEP do Local do Fato**.
5. No bloco de **Comunicante do Fato**, insira um nome fictício e selecione a relação do comunicante (ex: *Testemunha*).
6. Clique em **Avançar**.

---

### **Passo 4: Vítima**
1. Selecione a vítima no dropdown **Parte (PJe)**. O **Polo Processual** será pré-selecionado automaticamente (ex: *ATIVO*).
2. Escolha o CEP correspondente da residência no dropdown.
3. Preencha os campos opcionais do perfil social (Raça, Estado Civil, Religião, Escolaridade, Renda, Ocupação).
4. No bloco **Drogas Utilizadas (Vítima)**, selecione uma substância e clique em **Add**. O item surgirá na tabela local.
5. No bloco **Escutas Judiciais (Vítima)**, selecione uma data, escreva um relato rápido do depoimento e clique em **Adicionar**.
6. Clique em **Avançar**.

---

### **Passo 5: Acusado**
1. Selecione o acusado no dropdown **Parte (PJe)**. O polo será mapeado (ex: *PASSIVO*).
2. Marque se possui antecedentes e se é reincidente.
3. Selecione e adicione substâncias químicas consumidas por ele no bloco de drogas (se houver).
4. Cadastre uma escuta judicial com data e relato de depoimento e clique em **Adicionar**.
5. Clique em **Avançar**.

---

### **Passo 6: Vínculo**
1. No dropdown **Vítima**, selecione o nome correspondente.
2. No dropdown **Acusado**, selecione o nome correspondente.
3. Selecione o **Tipo de Vínculo** (ex: *Cônjuge*).
4. Digite uma observação rápida e clique em **Adicionar**. A relação será listada na tabela.
5. Clique em **Avançar para Resumo**.

---

### **Passo 7: Resumo & Finalização**
1. Revise os dados de cabeçalho (Processo, Fato, Comunicante) consolidados na tela.
2. Verifique se as quantidades de vínculos, benefícios, drogas e escutas correspondem aos itens que você adicionou nos passos anteriores.
3. Clique em **Finalizar Cadastro**.
4. O formulário enviará o payload estruturado para o backend Java e, após a resposta positiva da procedure do banco, você será redirecionado para a lista de crimes (`/crimes`) com o banner de sucesso.
