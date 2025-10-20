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

## v1.9 - Feedback Visual e Notificações (Concluído)

### Fase 1: Implementar um Painel de Log de Eventos (Concluído)
 **Descrição**: Criado o componente `EventLog.jsx` e integrado ao `App.jsx`. O estado `eventLog` agora captura as mensagens narrativas da API e as exibe em um painel "Diário de Bordo", fornecendo ao jogador feedback imediato e claro sobre as consequências de suas ações.
 **Status**: **Concluído e Verificado.**

## Próxima Tarefa: v1.10 - Interfaces de Ação de Porto

**Versão do Jogo:** 1.10
**Foco Atual:** Dar vida às ações de porto, criando interfaces dedicadas para cada uma.

## Resumo do Estado Atual
O loop de gameplay principal está funcional, mas superficial. O jogador pode clicar em "Ir à Taverna", "Ir ao Mercado", etc., mas a única resposta é uma mensagem no log. Não há uma interface para interagir com esses locais. Precisamos construir as "telas" para cada uma dessas ações.

## Próxima Tarefa: Implementar a Interface da Taverna

**Objetivo:** Criar uma nova "visão" na UI que é exibida quando o jogador clica em "Ir à Taverna". Essa visão buscará os recrutas disponíveis na API e permitirá que o jogador os contrate.

**Estratégia de Implementação:**
1.  **Gerenciamento de Visão**: Atualizar o `handleAction` em `App.jsx` para que, ao clicar em `GO_TO_TAVERN`, ele mude o estado `currentView` para `'TAVERN'`.
2.  **Criar `TavernView.jsx`**: Um novo componente que será renderizado condicionalmente em `App.jsx` quando `currentView` for `'TAVERN'`.
3.  **Buscar Recrutas**: Dentro de `TavernView.jsx`, usar `useEffect` para chamar a API `GET /api/games/{id}/port/tavern` e buscar a lista de recrutas disponíveis.
4.  **Exibir Recrutas**: Mapear a lista de recrutas, exibindo seus atributos, personalidade e salário em um layout claro.
5.  **Ação de Contratar**: Adicionar um botão "Contratar" para cada recruta. O clique nesse botão chamará a API `POST /api/games/{id}/crew/recruit`, usando o objeto `recruitRequest` que já está convenientemente incluído no DTO de cada recruta.
6.  **Navegação**: O componente `TavernView` terá um botão "Voltar" para mudar o `currentView` de volta para `'DASHBOARD'`.
