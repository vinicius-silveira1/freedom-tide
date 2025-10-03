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

8.  **Sistema de Contratos (Básico):**
    - Modelada a entidade `Contract` e as enums `Faction` e `ContractStatus`.
    - Adicionados contratos de exemplo ao `DataSeeder` para as facções Guilda, Revolucionários e Irmandade.
    - Implementado endpoint `GET /api/contracts` para listar os contratos disponíveis.
    - Implementado endpoint `POST /api/games/{gameId}/contracts/{contractId}/accept` para o jogador aceitar um contrato.
    - Atualizado o `GameStatusResponseDTO` para incluir o contrato ativo, fornecendo feedback claro ao jogador.

---

### **Próxima Tarefa:**

*   **Nome da Funcionalidade:** Resolução de Contratos (Básico).
*   **Objetivo:** Implementar a lógica para que um jogador possa concluir (ou falhar) um contrato ativo, recebendo as recompensas e atualizando o estado do jogo. Isso fecha o loop de gameplay principal (Planejamento -> Execução -> Resolução).
*   **Plano:**
    1.  Criar um novo endpoint, por exemplo, `POST /api/games/{gameId}/contracts/resolve`.
    2.  No `GameService`, criar um método `resolveContract(Long gameId)`.
    3.  A lógica do serviço deve:
        - Verificar se o jogo possui um contrato ativo.
        - Aplicar as recompensas (ouro, reputação, etc.) ao estado do jogo.
        - Mudar o status do contrato para `COMPLETED`.
        - Desvincular o contrato do jogo (setar `activeContract` para `null`).
    4.  Considerar a implementação de uma lógica de falha (ex: `POST /api/games/{gameId}/contracts/fail`), que aplicaria penalidades e mudaria o status para `FAILED`.