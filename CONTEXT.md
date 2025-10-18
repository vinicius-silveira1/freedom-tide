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
10. **Disponibilidade de Contratos Condicional**
11. **Ciclo de Fim de Turno (Consumo e Salários)**
12. **Fundações do Mundo Interativo (Viagem, Portos, Encontros)**
13. **Aprofundando Consequências (Fuga, Investigar)**
14. **Combate Naval (Atacar, Abordar)**

---

## v1.6 - Tornando os Portos Vivos (Concluído)

### Fase 1: Ação de Porto "Ir ao Estaleiro" (Concluído)
- **Descrição**: Implementada a funcionalidade de reparo do casco do navio, criando um ciclo de jogabilidade onde o dano sofrido no mar tem um custo financeiro.
- **Status**: **Concluído e Verificado.**

### Fase 2: Melhorias de Navio no Estaleiro (Concluído)
- **Descrição**: Implementado o sistema de compra de melhorias para o navio, permitindo ao jogador gastar ouro para adquirir melhorias que afetam os atributos do navio.
- **Status**: **Concluído e Verificado.**

### Fase 3: Inventário do Estaleiro por Facção (Concluído)
- **Descrição**: O inventário de melhorias do estaleiro agora depende da facção do porto. Portos do Império, da Guilda e Piratas oferecem melhorias distintas, além das comuns, aprofundando a imersão e a necessidade de exploração.
- **Status**: **Conclúido e Verificado.**

### Fase 4: Ação de Porto "Ir ao Mercado" (Concluído)
- **Descrição**: Implementada a mecânica de mercado nos portos, permitindo ao jogador comprar e vender recursos essenciais (comida, rum, ferramentas, munição). Os preços variam conforme a facção do porto, criando um loop econômico e incentivando a viagem e o comércio.
- **Status**: **Concluído e Verificado.**

---

## Próxima Tarefa: v1.6 - Tornando os Portos Vivos

### Fase 5: Ação de Porto "Ir à Taverna" (Concluído)
- **Descrição**: Implementada a mecânica da taverna, onde recrutas são gerados proceduralmente a cada visita. O tipo de recruta é influenciado pela facção do porto, e o jogador pode ver seus atributos e salário antes de contratar. Isso move o sistema de recrutamento para dentro do loop de gameplay do porto.
- **Status**: **Concluído e Verificado.**

---

## Próxima Tarefa: v1.7 - A Interface do Capitão

### Fase 1: Estrutura do Frontend com React + Vite (Concluído)
- **Descrição**: Estruturado um projeto de frontend na pasta `frontend/` utilizando React e Vite. O `vite.config.js` foi configurado com um proxy para a API backend, e o componente `App.jsx` foi modificado para criar um novo jogo e exibir o JSON do estado do jogo, confirmando a comunicação bem-sucedida entre as duas partes da aplicação.
- **Status**: **Concluído e Verificado.**

### Fase 2: Componentização da UI e Exibição de Status (Concluído)
- **Descrição**: A exibição de dados brutos (JSON) foi substituída por uma UI estruturada. Foram criados componentes React dedicados (`CaptainCompass`, `ShipStatus`, `CrewStatus`, `LocationStatus`) que recebem dados via props e os exibem em painéis distintos e estilizados.
- **Status**: **Concluído e Verificado.**

### Fase 3: Interatividade da UI - Ações do Porto (Concluído)
- **Descrição**: Criado o componente `PortActions.jsx`, que busca dinamicamente as ações disponíveis no porto a partir da API e as renderiza como botões estilizados na interface. O `App.jsx` agora exibe condicionalmente este painel de ações.
- **Status**: **Concluído e Verificado.**

### Fase 4: Unificação do Estado e Handlers de Ação

- **[Próximo Passo Lógico]**: Fazer os botões de ação funcionarem, o que exige uma refatoração na forma como gerenciamos o estado do jogo.
- **[Justificativa (Lição de Arquitetura/Design)]**: Atualmente, `App.jsx` busca o estado do jogo, mas os componentes filhos (`PortActions`) não têm como alterá-lo. Para que um clique de botão em um componente filho possa atualizar a UI inteira, precisamos de um padrão chamado **"Lifting State Up" (Elevar o Estado)**. A lógica de *como* atualizar o jogo (fazer chamadas de API que mudam o estado) deve viver no mesmo lugar onde o estado (`game`) vive: em `App.jsx`. Criaremos uma função `updateGameState` em `App.jsx` e a passaremos como *prop* para os componentes filhos. Eles não saberão *como* o estado é atualizado, apenas que devem chamar essa função quando uma ação ocorrer. Isso centraliza nossa lógica de mutação de estado, tornando o aplicativo mais previsível e fácil de depurar.
- **[Opções ou Considerações Criativas]**:
    1.  **Criar `updateGameState`**: Em `App.jsx`, criar uma função `async function updateGameState(url, options)`. Ela fará a chamada `fetch` e atualizará o estado `game` com a resposta.
    2.  **Passar a Função como Prop**: `App.jsx` renderizará `<PortActions onActionClick={updateGameState} />`.
    3.  **Modificar `PortActions.jsx`**: O componente receberá `onActionClick` como prop. O `onClick` de cada botão chamará essa função, passando a URL do endpoint da ação. Ex: `onClick={() => onActionClick(action.apiEndpoint)}`.
    4.  **Foco Inicial**: Nosso primeiro alvo será fazer o botão "Viajar" funcionar, pois ele já tem um endpoint que muda o estado do jogo (de `currentPort` para `currentEncounter`).
- **Status Atual**: **Aguardando aprovação.**