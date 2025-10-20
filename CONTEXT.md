# Freedom Tide - CONTEXTO DE DESENVOLVIMENTO

Este documento serve como um "save state" do nosso processo de desenvolvimento. Ele é atualizado a cada nova funcionalidade implementada para facilitar a continuidade e o onboarding.

### **Stack Tecnolígica:**
Java 17
Spring Boot 3
Spring Data JPA
PostgreSQL (via Supabase)
Maven

### **Arquitetura:**
 Camadas: `controller`, `service`, `repository`, `model`, `dto`, `config`, `mapper`.
 Foco em APIs RESTful.
 Uso de DTOs para desacoplar a API da camada de persistência.
 **Nota:** A porta do servidor foi alterada para `8090` em `application.properties` para evitar conflitos no ambiente de desenvolvimento.

### **Funcionalidades Implementadas:**

1.  **Estrutura Inicial e APIs Core**
2.  **Sistemas de Jogo (Eventos, Contratos, Moral)**
3.  **Mundo Interativo (Viagem, Portos, Encontros, Combate)**
4.  **Economia de Porto (Estaleiro, Mercado, Taverna)**
5.  **Estrutura do Frontend com React e Interatividade Básica**
6.  **Painel de Feedback (Diário de Bordo)**
7.  **Interface da Taverna (Recrutamento)**

## v1.10 - Interfaces de Ação de Porto (Em Andamento)

### Fase 1: Implementar a Interface da Taverna (Concluído)
 **Descrição**: Criada a `TavernView.jsx`, uma nova "tela" na aplicação que é exibida quando o jogador visita a taverna. O componente busca e exibe uma lista de recrutas gerados proceduralmente pela API. A contratação é funcional, atualizando o estado do jogo e fornecendo feedback visual imediato ao atualizar a lista de recrutas.
 **Status**: **Concluído e Verificado.**

## Próxima Tarefa: v1.10 - Interfaces de Ação de Porto

**Versão do Jogo:** 1.10
**Foco Atual:** Continuar a dar vida às ações de porto, implementando a interface do Estaleiro.

## Resumo do Estado Atual
Com a interface da Taverna completa, temos um padrão robusto de "sub-visões" para as ações de porto. O próximo passo lógico é aplicar este mesmo padrão para o Estaleiro, permitindo que o jogador repare o navio e compre melhorias através de uma interface dedicada, em vez de chamadas de API diretas.

## Próxima Tarefa: Implementar a Interface do Estaleiro

**Objetivo:** Criar uma nova visão, `ShipyardView.jsx`, que será exibida ao clicar em "Ir ao Estaleiro". Esta interface mostrará o dano atual do navio, o custo do reparo, e a lista de melhorias disponíveis para compra.

**Estratégia de Implementação (seguindo o padrão da Taverna):**
1.  **Gerenciamento de Visão**: Atualizar o `handleAction` em `App.jsx` para que `GO_TO_SHIPYARD` mude a `currentView` para `'SHIPYARD'`.
2.  **Criar `ShipyardView.jsx`**: Um novo componente que buscará os dados do estaleiro (`GET /api/games/{id}/port/shipyard`) e os exibirá.
3.  **Ação de Reparar**: Adicionar um botão "Reparar Navio" que ficará visível se o navio tiver dano. O clique chamará a API `POST /api/games/{id}/port/shipyard/repair`.
4.  **Ação de Comprar Melhoria**: Listar as melhorias disponíveis, cada uma com um botão "Comprar". O clique chamará a API `POST /api/games/{id}/port/shipyard/upgrades/{upgradeId}`.
5.  **Feedback Imediato**: Após um reparo ou compra, a `ShipyardView` deve buscar novamente seus dados para refletir o novo estado do navio (casco reparado, melhoria desaparecendo da lista de disponíveis).
6.  **Navegação**: Incluir um botão "Voltar" para retornar ao dashboard do porto.