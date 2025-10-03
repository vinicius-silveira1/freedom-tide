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

6.  **Mecânica de Moral da Tripulação:**
    - Refatorada a lógica de resolução de evento para aplicar mudanças de moral diferenciadas com base na `CrewPersonality`.

7.  **DTOs de Resposta para a API (Game Status):**
    - Criados `GameStatusResponseDTO` e DTOs aninhados para fornecer uma visão segura e customizada do estado do jogo.
    - Implementado `GameMapper` para a lógica de conversão.
    - Refatorados os endpoints do `GameController` para não exporem mais a entidade `Game`.

---

### **Próxima Tarefa:**

*   **Nome da Funcionalidade:** Criar DTO de Resposta para Tripulantes.
*   **Objetivo:** Continuar a boa prática de não expor entidades JPA na API, refatorando o endpoint de recrutamento para que ele retorne um DTO em vez da entidade `CrewMember`.
*   **Plano:**
    1.  Criar um `CrewMemberResponseDTO` que represente a visão pública de um tripulante (nome, personalidade, moral, atributos, etc.).
    2.  Adicionar um método de mapeamento ao `GameMapper` para converter `CrewMember` em `CrewMemberResponseDTO`.
    3.  Refatorar o endpoint `POST /api/games/{gameId}/ship/crew` no `GameController` para usar o mapper e retornar o novo DTO.