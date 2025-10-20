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

## v1.11 - O Contrato Social do Capitão (Concluído)

**Versão do Jogo:** 1.11
**Foco:** Aprofundar a mecânica de contratos, tornando-os uma escolha ativa com consequências claras.

### Fase 1: Implementar a Interface de Contratos (Concluído)
 **Descrição**: Criada a `ContractsView.jsx`, uma tela que permite ao jogador visualizar os contratos disponíveis em um porto e aceitá-los, tornando-os o contrato ativo do jogador.
 **Status**: **Concluído e Verificado.**

## v1.12 - A Palavra do Capitão (Concluído e Verificado)

**Versão do Jogo:** 1.12
**Foco:** Implementar a lógica de finalização e resolução de contratos.
**Status:** **Concluído e Verificado.**

## v1.13 - Pilar de Estilo Visual (Vertical Slice) (Concluído e Verificado)

### Justificativa

Conforme nossa discussão estratégica, após fechar um loop de gameplay fundamental (contratos), a prioridade agora é estabelecer a identidade visual do jogo. Em vez de deixar toda a arte para o final, criaremos um "pilar de estilo" (ou "fatia vertical") ao estilizar completamente um componente-chave. Isso servirá como um guia de qualidade, um "kit de peças" reutilizável (Design System) e um impulso de motivação, garantindo que a gameplay e a estética evoluam em harmonia.
**Status:** **Concluído e Verificado.** O componente `ContractsView` agora serve como nosso pilar de estilo visual, com uma estética de painel de RPG pixel art, guiado por inspirações como Chrono Trigger e Sea of Stars.

## v1.14 - Estilização do Dashboard Principal (Concluído e Verificado)

**Descrição:** Com o pilar de estilo estabelecido, esta tarefa aplicou a identidade visual ao restante da interface para criar uma experiência coesa. Os componentes `ShipStatus`, `CrewStatus`, `CaptainCompass`, `LocationStatus` e `EventLog` foram todos estilizados para compartilhar a mesma aparência de painel, garantindo que a tela principal do jogo tenha uma qualidade visual unificada.
**Status:** **Concluído e Verificado.**

## v1.15 - O Painel Imersivo (Fase 1) (Concluído e Verificado)

**Descrição:** Atendendo a uma nova diretriz de design para aumentar a imersão, esta tarefa transformou o componente `CaptainCompass` de um painel de texto em um widget temático. A bússola agora é um elemento visual autônomo, posicionado no canto da tela, com agulhas que se movem dinamicamente e um mostrador numérico para clareza de informação.
**Status:** **Concluído e Verificado.**

## Próxima Tarefa: v1.16 - O Painel Imersivo (Fase 2): Fundo Contextual

### Justificativa

Para aprofundar a imersão, a interface não deve ser estática. O fundo da tela deve refletir o ambiente atual do jogador. Se o jogador está em um porto movimentado, a cena de fundo deve mostrar isso. Se está em alto mar, a vastidão do oceano deve ser visível. Isso conecta visualmente o jogador ao estado do jogo de uma forma poderosa e imediata.

### Plano de Implementação

1.  **Lógica de Classe Dinâmica:** No `App.jsx`, vamos implementar uma lógica que adiciona uma classe CSS ao container principal do aplicativo (`app-container`) com base no estado do jogo. Por exemplo, a classe `in-port` será adicionada se `game.currentPort` existir, e `at-sea` caso contrário.
2.  **Estilos de Fundo:** No `App.css`, criaremos as regras para essas novas classes. Cada classe definirá um `background-image` diferente.
3.  **Busca de Assets:** Procurarei por imagens de pixel art (livres de direitos autorais ou com licenças permissivas) que possam servir como placeholders para um porto e para o oceano. O objetivo é encontrar artes que se alinhem com a estética de "low fantasy" e com nossa inspiração em Chrono Trigger/Sea of Stars.

### Status

A ser implementado.
