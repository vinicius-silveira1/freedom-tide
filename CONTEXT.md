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

1.  **Cálculo de Salário:** Ao ser recrutado, cada tripulante agora tem um salário calculado dinamicamente com base em suas habilidades e nível de desespero.
2.  **Consumo de Suprimentos:** Ao final de um ciclo, a tripulação consome uma quantidade fixa de comida e rum.
3.  **Pagamento de Salários:** O total dos salários da tripulação é deduzido do ouro do jogador.
4.  **Penalidades de Moral:** Se não houver comida, rum ou ouro suficiente para os pagamentos, a moral da tripulação é severamente penalizada, com mensagens narrativas claras no log de eventos.

**Verificação:**
- O cenário de sucesso foi verificado. Após resolver um contrato, os recursos (comida, rum, ouro) foram deduzidos corretamente, e o `eventLog` narrou todos os acontecimentos com precisão.
- A verificação dos cenários de falha foi adiada por pragmatismo, devido à dificuldade de manipular o estado do jogo para forçar a falha sem ferramentas de depuração.

---

## v1.3: Fase 1 - Modelagem de Portos (Concluído)

- **Descrição**: Implementada a base para a "Fase de Planejamento" do GDD, introduzindo o conceito de Portos e refatorando as entidades relacionadas.
- **Mudanças**:
    1.  **`PortType.java`**: Enum criado com os tipos `IMPERIAL`, `GUILD`, `PIRATE`, `FREE`.
    2.  **`Port.java`**: Entidade criada, representando os portos do jogo.
    3.  **`Game.java`**: Modificada para incluir o campo `currentPort`, que rastreia onde o jogador está atracado.
    4.  **`Contract.java`**: Refatorada para incluir o campo `originPort` e a anotação `@JsonBackReference` para gerenciar a relação bidirecional.
    5.  **`PortRepository.java`**: Interface de repositório criada.
    6.  **`DataSeeder.java`**: Refatorado para criar portos e associar os contratos existentes a eles.
- **Status**: **Concluído e Verificado.** A aplicação inicia sem erros e o `DataSeeder` popula e associa os dados corretamente.

---

## v1.3: Fase 2 - API de Viagem e Estado do Porto (Concluído)

- **Descrição**: Implementada a API que dá vida aos portos, permitindo ao jogador saber onde está e se mover pelo mundo.
- **Mudanças**:
    1.  **`PortDTO` e `TravelRequestDTO`**: DTOs criados para comunicação com a API.
    2.  **`GameMapper`**: Atualizado para converter `Port` em `PortDTO`.
    3.  **`GameService`**: Lógica implementada para buscar o porto atual e para viajar entre portos. O método `createNewGame` agora define um porto inicial.
    4.  **`GameController`**: Adicionados os endpoints `GET /api/games/{gameId}/port` e `POST /api/games/{gameId}/travel`.
- **Status**: **Concluído e Verificado.** Os endpoints funcionam, a validação de viagem impede movimentos redundantes e o estado do jogo é atualizado corretamente.

---

## v1.3: Fase 3 - Contratos por Porto (Concluído)

- **Descrição**: A localização do jogador agora determina quais contratos estão disponíveis, tornando a viagem uma decisão estratégica.
- **Mudanças**:
    1.  **`ContractRepository`**: Adicionado um método de busca que filtra contratos por `originPort`, status e pré-requisitos da Bússola do Capitão.
    2.  **`ContractService`**: O método `getAvailableContractsForGame` foi refatorado para usar a nova consulta do repositório, delegando a lógica de filtragem para o banco de dados e retornando apenas os contratos relevantes para a localização atual do jogador.
- **Status**: **Concluído e Verificado.** A API `GET /api/games/{gameId}/contracts` agora retorna apenas os contratos do porto onde o jogador está atracado.

---

## v1.3: Fase 4 - Ações de Porto (Concluído)

- **Descrição**: Criado um endpoint de descoberta que informa ao cliente quais ações são possíveis em um porto, tornando a API mais robusta e escalável.
- **Mudanças**:
    1.  **`PortActionType.java` e `PortActionDTO.java`**: Criados para modelar as ações de porto e suas representações para o cliente.
    2.  **`GameService`**: Implementado o método `getAvailablePortActions` que constrói a lista de ações disponíveis.
    3.  **`GameController`**: Adicionado o endpoint `GET /api/games/{gameId}/port/actions`.
- **Status**: **Concluído e Verificado.** O endpoint retorna corretamente a lista de ações (`VIEW_CONTRACTS`, `TRAVEL`) disponíveis em um porto.

---

## v1.3: Fase 5 - Encontros no Mar (Fundação) (Concluído)

- **Descrição**: A viagem foi transformada de um teletransporte para o início de uma jornada, introduzindo o conceito de encontros em alto mar e mudando o estado do jogador.
- **Mudanças**:
    1.  **`SeaEncounterType`**, **`SeaEncounter`**, **`SeaEncounterDTO`**: Novas classes para modelar, persistir e expor os encontros.
    2.  **`Game.java`**: Modificada para incluir o campo `currentEncounter`, rastreando o estado do jogador em alto mar.
    3.  **`GameService.travelToPort`**: O método foi refatorado para remover o jogador de um porto, gerar um encontro aleatório e associá-lo ao jogo.
    4.  **`GameController.travelToPort`**: O endpoint agora retorna um `SeaEncounterDTO`, informando ao cliente sobre o encontro iniciado.
- **Status**: **Concluído e Verificado.** A API de viagem agora inicia um encontro, e o estado do jogo reflete corretamente a transição de "atracado" para "em alto mar".

---

## Próxima Tarefa: Fase 6 - Ações de Encontro no Mar

- **Descrição**: Agora que o jogador pode ter encontros, ele precisa poder reagir a eles. Esta tarefa consiste em criar um endpoint de descoberta para listar as ações possíveis durante um encontro específico (ex: Atacar, Fugir, Investigar), tornando os encontros interativos.
- **Objetivos**:
    1.  **Criar `EncounterActionType.java`**: Um enum para as ações possíveis (`ATTACK`, `FLEE`, `INVESTIGATE`, etc.).
    2.  **Criar `EncounterActionDTO.java`**: Um DTO para descrever a ação para o cliente.
    3.  **Implementar `GET /api/games/{gameId}/encounter/actions`**: Um novo endpoint e a lógica de serviço que retorna uma lista de ações com base no tipo do `currentEncounter` do jogo.
- **Status Atual**: **Aguardando Aprovação.**
