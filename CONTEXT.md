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
7.  **Interfaces de Ação de Porto (Taverna, Estaleiro, Mercado)**

## v1.10 - Interfaces de Ação de Porto (Concluído)

### Fase 1: Implementar a Interface da Taverna (Concluído)
 **Descrição**: Criada a `TavernView.jsx`, uma tela dedicada para a taverna que permite ao jogador visualizar e contratar novos tripulantes.
 **Status**: **Concluído e Verificado.**

### Fase 2: Implementar a Interface do Estaleiro (Concluído)
 **Descrição**: Criada a `ShipyardView.jsx`. A nova tela permite ao jogador reparar o navio e comprar melhorias, com feedback visual imediato.
 **Status**: **Concluído e Verificado.**

### Fase 3: Implementar a Interface do Mercado (Concluído)
 **Descrição**: Criada a `MarketView.jsx`. A tela permite ao jogador comprar e vender recursos usando campos de input e botões, completando o ciclo de interações no porto.
 **Status**: **Concluído e Verificado.**

## Próxima Tarefa: v1.11 - O Contrato Social do Capitão

**Versão do Jogo:** 1.11
**Foco Atual:** Aprofundar a mecânica de contratos, tornando-os uma escolha ativa com consequências claras.

## Resumo do Estado Atual
O loop de gameplay está robusto. O jogador pode navegar, encontrar eventos, lutar e interagir completamente com os portos (recrutar, reparar, melhorar, comerciar). No entanto, o sistema de contratos, que deveria ser um pilar do jogo, ainda é passivo. O jogador pode ver uma lista de contratos, mas não pode interagir com eles pela UI.

## Próxima Tarefa: Implementar a Interface de Contratos

**Objetivo:** Criar uma `ContractsView.jsx` que permita ao jogador visualizar os contratos disponíveis em um porto e aceitá-los, tornando-os o contrato ativo do jogador.

**Estratégia de Implementação:**
1.  **Gerenciamento de Visão**: Adicionar um caso `VIEW_CONTRACTS` no `handleAction` em `App.jsx` para mudar a `currentView` para `'CONTRACTS'`.
2.  **Criar `ContractsView.jsx`**: Este componente irá:
    *   Buscar os contratos disponíveis via `GET /api/games/{id}/contracts`.
    *   Exibir os detalhes de cada contrato (título, descrição, recompensas).
    *   Exibir o contrato ativo do jogador (se houver).
3.  **Ação de Aceitar**: Cada contrato disponível terá um botão "Aceitar". O clique chamará a API `POST /api/games/{id}/contracts/{contractId}/accept`.
4.  **Feedback Imediato**: Após aceitar um contrato, a `ContractsView` deve se atualizar para mover o contrato da lista de "disponíveis" para a seção "ativo".
5.  **Navegação**: Incluir um botão "Voltar" para retornar ao dashboard do porto.