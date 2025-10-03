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

### **Próxima Tarefa:**

*   **Nome da Funcionalidade:** Consequências de Moral Baixa (Estágio 1: Descontentamento).
*   **Objetivo:** Dar um primeiro passo para tornar a mecânica de moral mais impactante. Tripulantes com moral baixa devem começar a causar problemas menores, afetando os resultados das ações do jogador.
*   **Plano:**
    1.  Definir um limiar numérico para o estado de "Descontentamento" (ex: moral < 50).
    2.  Implementar uma consequência inicial simples. Por exemplo, ao resolver um contrato ou evento, se houver tripulantes descontentes, existe uma pequena chance de a recompensa em ouro ser reduzida devido a "furtos" ou "acidentes".
    3.  Modificar os métodos `resolveContract` e `resolveEvent` no `GameService` para incluir essa nova verificação de moral e a possível penalidade.
    4.  A penalidade e sua causa podem ser incluídas em um novo campo na resposta da API para que o jogador saiba o que aconteceu.
