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

## Próxima Tarefa: v1.12 - A Palavra do Capitão

**Versão do Jogo:** 1.12
**Foco Atual:** Implementar a lógica de finalização e resolução de contratos.

## Resumo do Estado Atual
O jogador agora pode visualizar e aceitar contratos, que se tornam seu "contrato ativo". Isso ativa um dos pilares do GDD, dando ao jogador um objetivo claro. No entanto, o ciclo não está completo. Não há, no momento, uma forma de o jogador cumprir as condições do contrato e clamar sua recompensa (ou sofrer as penalidades do fracasso).

## Próxima Tarefa: Implementar a Resolução de Contratos

**Objetivo:** Implementar a lógica no backend para o endpoint `POST /api/games/{gameId}/contracts/resolve`. Este endpoint verificará se as condições do contrato ativo foram cumpridas e, em caso afirmativo, aplicará as recompensas (ouro, status) e limpará o contrato ativo.

**Estratégia de Implementação:**
1.  **Expandir o `ContractService`**: Criar um método `resolveContract(gameId)` que conterá a lógica principal.
2.  **Lógica de Verificação**: O serviço precisará obter o `activeContract` do jogo e verificar suas condições de conclusão. Para começar, podemos implementar um tipo simples de condição (ex: "Chegar ao porto de destino X").
3.  **Aplicar Recompensas**: Se as condições forem atendidas, o serviço aplicará as recompensas (`rewardGold`, `rewardReputation`, etc.) ao estado do jogo.
4.  **Limpar Contrato**: Após a resolução (sucesso ou falha), o `activeContract` do jogo deve ser definido como `null`.
5.  **Integrar ao `GameController`**: O `GameController` chamará o novo método do serviço e retornará o estado atualizado do jogo.

Isso fechará o loop de gameplay dos contratos, tornando-os uma mecânica completa: Aceitar -> Executar -> Resolver.