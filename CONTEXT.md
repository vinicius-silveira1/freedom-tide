# Contexto de Desenvolvimento - Freedom Tide

Este documento serve como um "save state" do nosso processo de desenvolvimento. Ele é atualizado a cada nova funcionalidade implementada para facilitar a continuidade e o onboarding.

---

### **Stack Tecnológica:**
- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL (via Supabase)
- Maven

### **Arquitetura:**
- Camadas: `controller`, `service`, `repository`, `model`, `dto`, `config`, `mapper`.
- Foco em APIs RESTful.
- Uso de DTOs para desacoplar a API da camada de persistência.
- **Nota:** A porta do servidor foi alterada para `8090` em `application.properties` para evitar conflitos no ambiente de desenvolvimento.

---

### **Funcionalidades Implementadas:**

1.  **Estrutura Inicial do Projeto**
2.  **Modelagem de Dados (Entidades JPA)**
3.  **API de Gerenciamento do Jogo**
4.  **API de Recrutamento**
5.  **Sistema de Eventos Narrativos**
6.  **Mecânica de Moral da Tripulação**
7.  **DTOs de Resposta para a API**
8.  **Sistema de Contratos (Completo)**
9.  **Refatoração do DataSeeder (Upsert)**
10. **Disponibilidade de Contratos Condicional:**
    - A entidade `Contract` agora possui campos para pré-requisitos (ex: `requiredReputation`).
    - O `DataSeeder` foi atualizado para definir esses pré-requisitos e garantir que os contratos no banco de dados sempre reflitam as definições do código a cada inicialização.
    - O endpoint de listagem de contratos foi movido para `GET /api/games/{gameId}/contracts`.
    - A lógica de serviço agora filtra os contratos disponíveis com base nos valores da Bússola do Capitão do jogador, fazendo o mundo reagir à sua identidade.

---

## v1.2: Ciclo de Fim de Turno (Consumo e Salários)

**Funcionalidade:** Um ciclo de fim de turno foi integrado ao final da resolução de contratos e eventos, aprofundando o gerenciamento de recursos e tripulação.

---

## v1.3: Fundações do Mundo Interativo (Concluído)

- **Fase 1: Modelagem de Portos**: Introduzido o conceito de Portos.
- **Fase 2: API de Viagem e Estado do Porto**: Permitido ao jogador se mover pelo mundo.
- **Fase 3: Contratos por Porto**: Contratos passaram a ser específicos de cada porto.
- **Fase 4: Ações de Porto**: Endpoint de descoberta para ações possíveis em um porto.
- **Fase 5: Encontros no Mar (Fundação)**: Viagem passou a gerar encontros em alto mar.
- **Fase 6: Ações de Encontro no Mar**: Endpoint de descoberta para ações possíveis em um encontro.
- **Fase 7: Resolução de Encontros (Fugir)**: Fechado o primeiro loop de gameplay (Viajar -> Encontro -> Fugir -> Chegar).

---

## v1.4: Aprofundando Consequências

### Fase 1: Fuga Baseada em Habilidade (Concluído)

- **Descrição**: Alinhada a ação "Fugir" com o pilar de design "Consequência".
- **Nota de Manutenção**: Durante testes subsequentes, foi identificado e corrigido um bug onde a dificuldade de fuga era excessivamente alta. A fórmula foi rebalanceada para ser mais justa.

### Fase 2: Resolução de Encontros (Investigar) (Concluído)

- **Descrição**: Implementada a funcionalidade da ação "Investigar" para o encontro `MYSTERIOUS_WRECK`, adicionando uma camada de exploração e recompensa ao jogo.
- **Status**: **Concluído e Verificado.**

---

## v1.5: Combate Naval (Concluído)

### Fase 1: Resolução de Encontros (Atacar)

- **Descrição**: Implementado um sistema de combate naval simplificado para a ação "Atacar", com trocas de dano baseadas em atributos.
- **Status**: **Concluído e Verificado.**

### Fase 2: Resolução de Encontros (Abordar)

- **Descrição**: Adicionada a ação "Abordar" como uma alternativa tática ao combate. A abordagem é um evento único e decisivo, baseado na habilidade de `Combate` da tripulação.
- **Mudanças**:
    1.  **Escolha Tática**: O jogador agora pode escolher entre atacar à distância ou arriscar uma abordagem para capturar o navio.
    2.  **Risco vs. Recompensa**: A abordagem bem-sucedida rende recompensas maiores, mas a falha resulta em dano severo e perda de moral, criando uma decisão de jogo interessante.
    3.  **Lógica de Resolução**: O método `boardEncounter` foi implementado com uma rolagem de dados contestada para determinar o resultado.
- **Status**: **Concluído e Verificado.** Cenários de sucesso (com tripulação forte) e falha (contra um inimigo superior) funcionam como esperado.

---

## v1.6 - Tornando os Portos Vivos (Em Andamento)

### Fase 1: Ação de Porto "Ir ao Estaleiro" (Concluído)

- **Descrição**: Implementada a ação de porto "Ir ao Estaleiro" e a funcionalidade de reparo do casco do navio. Isso cria um ciclo de jogabilidade onde o dano sofrido no mar tem um custo financeiro, dando mais propósito à acumulação de ouro.
- **Status**: **Concluído e Verificado.**

---

### Fase 2: Melhorias de Navio no Estaleiro (Concluído)

- **Descrição**: Implementado o sistema de compra de melhorias para o navio. O jogador agora pode gastar ouro para adquirir melhorias que afetam os atributos do navio, como casco e canhões. A API lista apenas melhorias que o jogador não possui e valida a compra contra o ouro do jogador e duplicatas.
- **Status**: **Concluído e Verificado.**

---

## Próxima Tarefa: v1.6 - Tornando os Portos Vivos

### Fase 3: Inventário do Estaleiro por Facção

- **[Próximo Passo Lógico]**: Fazer com que o inventário de melhorias do estaleiro dependa da facção do porto.
- **[Justificativa (Lição de Arquitetura/Design)]**: Atualmente, todos os estaleiros vendem as mesmas melhorias. Para aprofundar a imersão no mundo e tornar a exploração mais recompensadora, o inventário deve refletir a ideologia da facção que controla o porto. Portos do Império podem vender os melhores canhões, portos da Guilda podem focar em melhorias de carga, e portos Piratas/Livres podem oferecer modificações únicas como porões de contrabando. Isso força o jogador a viajar e interagir com diferentes facções para customizar seu navio, reforçando os pilares de "Mundo" e "Customização".
- **[Opções ou Considerações Criativas]**:
    1.  **Modelagem**: Podemos adicionar um campo `PortType` à entidade `ShipUpgrade` para indicar onde ela pode ser encontrada. Uma abordagem mais flexível seria uma tabela de junção `port_type_upgrades` para permitir que uma melhoria seja vendida em múltiplos tipos de porto. Para começar, um simples campo `PortType` na melhoria é suficiente.
    2.  **Lógica de Serviço**: O método `getShipyardInfo` no `GameService` precisará ser atualizado para filtrar as melhorias não apenas pelas que o jogador já possui, mas também pelo `PortType` do porto atual.
    3.  **Balanceamento**: Isso nos dá uma alavanca para balancear o jogo. As melhores melhorias podem estar em portos de facções com as quais o jogador tem baixa reputação, criando um desafio interessante.
- **Status Atual**: **Aguardando aprovação.**
