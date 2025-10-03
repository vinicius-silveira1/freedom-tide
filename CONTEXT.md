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
- Camadas: `controller`, `service`, `repository`, `model`, `dto`, `config`.
- Foco em APIs RESTful.
- Uso de DTOs para desacoplar a API da camada de persistência.

---

### **Funcionalidades Implementadas:**

1.  **Estrutura Inicial do Projeto:**
    - Setup do Spring Boot com dependências essenciais.

2.  **Modelagem de Dados (Entidades JPA):**
    - `Game`, `Ship`, `CrewMember`.
    - `NarrativeEvent`, `EventOption`, `EventConsequence` (`@Embeddable`).
    - Relacionamentos e serialização JSON corrigidos.

3.  **API de Gerenciamento do Jogo:**
    - `POST /api/games`: Cria uma nova sessão de jogo.
    - `GET /api/games/{id}`: Busca uma sessão de jogo por ID.

4.  **API de Recrutamento:**
    - `POST /api/games/{gameId}/ship/crew`: Recruta um novo tripulante (com validação de DTO).

5.  **Sistema de Eventos Narrativos:**
    - `DataSeeder` para popular o banco com eventos iniciais.
    - `GET /api/events/random`: Endpoint para buscar um evento narrativo aleatório.
    - `POST /api/games/{gameId}/resolve-event`: Aplica as consequências de uma escolha de evento ao estado do jogo.

---

### **Próxima Tarefa:**

*   **Nome da Funcionalidade:** Refinar Lógica de Moral da Tripulação.
*   **Objetivo:** Tornar a mecânica de moral mais profunda e alinhada à temática do jogo, fazendo com que a mudança de moral varie de acordo com a personalidade de cada tripulante.
*   **Plano:**
    1.  Refatorar o método `resolveEvent` no `GameService`.
    2.  Dentro do loop da tripulação, adicionar uma lógica (ex: `switch` ou `if/else`) baseada na `CrewPersonality` do membro.
    3.  Aplicar um multiplicador ou uma lógica customizada ao `crewMoralChange` da consequência. Por exemplo, uma escolha pró-Guilda pode ter um impacto de `-5` para um tripulante `GREEDY`, mas `-15` para um `REBEL`.