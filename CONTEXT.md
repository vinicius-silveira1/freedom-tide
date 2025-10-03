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

7.  **DTOs de Resposta para a API:**
    - Criados DTOs de resposta para o estado do jogo (`GameStatusResponseDTO`) e para tripulantes (`CrewMemberResponseDTO`).
    - Implementado `GameMapper` para a lógica de conversão.
    - Refatorados todos os endpoints para não exporem mais as entidades JPA, criando uma API consistente e segura.

---

### **Próxima Tarefa:**

*   **Nome da Funcionalidade:** Sistema de Contratos (Básico).
*   **Objetivo:** Implementar a mecânica central de aceitar contratos, permitindo que o jogador ganhe recursos e influência, avançando no loop de gameplay principal.
*   **Plano:**
    1.  Modelar a entidade `Contract` com campos para título, descrição, recompensas (ouro e Bússola do Capitão) e status.
    2.  Adicionar contratos de exemplo ao `DataSeeder` para as diferentes facções. Um contrato da Guilda Mercante para transportar "bens manufaturados" (lucrativo, aumenta a Reputação). Um contrato de um informante anônimo para "libertar" uma carga de um navio do Império (arriscado, aumenta a Aliança e a Infâmia). Um contrato da Irmandade de Grani para pilhar uma rota comercial (brutal, aumenta muito a Infâmia).
    3.  Criar um `ContractRepository` e `ContractService`.
    4.  Implementar um endpoint `GET /api/contracts` para listar os contratos disponíveis.
    5.  Implementar um endpoint `POST /api/games/{gameId}/contracts/{contractId}/accept` para vincular um contrato a uma sessão de jogo (a lógica de conclusão do contrato será feita em uma tarefa futura).
