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

Concluido.

## v1.17 - O Porto Interativo (Concluído e Verificado)

**Versão do Jogo:** 1.17
**Foco:** Aumentar a imersão no porto, transformando a navegação de um menu de botões para uma experiência visual interativa.
**Descrição:** O antigo componente `PortActions.jsx`, que exibia uma lista de texto para ações no porto, foi substituído por um novo componente `PortView.jsx`. Este novo componente renderiza a imagem de fundo do porto e, sobre ela, exibe "marcadores de local" estilizados e clicáveis (Taverna, Mercado, etc.). Isso transforma a tela do porto de um painel de controle em uma "janela para o mundo", alinhando a mecânica com a apresentação visual. A estilização foi refinada para que os marcadores pareçam textos brilhantes e pulsantes, integrados à cena.
**Status:** **Concluído e Verificado.**

## v1.18 - A Tela de Início (Concluído e Verificado)

**Versão do Jogo:** 1.18
**Foco:** Estruturar a experiência inicial do jogador, introduzindo um fluxo de menu principal antes do início do jogo.
**Descrição:** Atendendo a uma diretriz de design do usuário, o jogo não começa mais diretamente no painel. Foi implementada uma máquina de estados (`MENU`, `LOADING`, `PLAYING`) no `App.jsx`. Um novo componente `MenuView.jsx` agora serve como tela de título, apresentando o nome do jogo e um botão "Novo Jogo". Clicar neste botão inicia o processo de criação do jogo e exibe uma tela de carregamento, conduzindo o jogador de forma coesa para a experiência principal.
**Status:** **Concluído e Verificado.**

## v1.19 - O Som do Mundo (Concluído e Verificado)

**Versão do Jogo:** 1.19
**Foco:** Implementar um sistema de áudio para aumentar a imersão através de música de fundo e efeitos sonoros.
**Descrição:** Foi criado um serviço singleton, `AudioService.js`, para gerenciar todo o áudio do jogo. Este serviço controla a reprodução de música de fundo, com transições suaves (fade) entre as faixas de porto e de alto mar, baseando-se na localização atual do jogador. Adicionalmente, efeitos sonoros de clique foram integrados aos botões da UI no menu principal e na vista do porto, fornecendo feedback auditivo para as ações do jogador.
**Status:** **Concluído e Verificado.**

## v1.20 - A Mesa do Capitão (Concluído e Verificado)

**Versão do Jogo:** 1.20
**Foco:** Transformar o dashboard em uma experiência diegética, onde os painéis se tornam documentos antigos espalhados sobre a mesa do capitão.

**Descrição:** Esta foi uma revolução visual completa do dashboard principal. Todos os componentes de status (`PortView`, `LocationStatus`, `CrewStatus`, `ShipStatus`, `EventLog`, `CaptainCompass`) foram totalmente redesenhados para parecerem documentos náuticos autênticos e envelhecidos. Cada painel agora possui:

- **Texturas de papel antigo** com linhas de caderno desbotadas, manchas de envelhecimento, e efeitos de papel amassado
- **Bordas irregulares** simulando documentos rasgados à mão e muito manuseados
- **Rotações individuais** para criar o efeito de papéis naturalmente espalhados sobre uma mesa
- **Manchas temáticas específicas** (sal marinho, óleo de navio, tinta derramada, umidade)
- **Sombras complexas** criando profundidade e a ilusão de documentos físicos
- **Efeitos pixelados dramáticos** usando filtros CSS avançados para harmonizar com os ícones do jogo
- **Vincos e dobras** simulados através de pseudo-elementos e gradientes

O `PortView` foi especialmente refinado com linhas de caderno antigo (sem as vermelhas modernas) e textura de papel heavily amassado. O resultado final é uma interface que não parece uma UI digital, mas sim uma mesa de trabalho real de um capitão do século XVIII, mantendo perfeita funcionalidade enquanto oferece imersão sem precedentes.

**Status:** **Concluído e Verificado.**

## v1.21 - O Peso das Correntes (Em Progresso)

**Versão do Jogo:** 1.21
**Foco:** Criar uma sequência introdutória narrativa que contextualiza o jogador no mundo e expõe imediatamente os temas de crítica social do jogo.

### Justificativa

A transição abrupta do menu "Novo Jogo" diretamente para a mesa do capitão quebra a imersão e perde uma oportunidade crucial de estabelecer contexto narrativo. O jogador precisa entender não apenas as mecânicas, mas **por que está ali** e **como o sistema o colocou nessa posição**. Esta sequência introdutória servirá tanto como tutorial diegético quanto como apresentação dos temas centrais do jogo sobre exploração capitalista e desigualdade.

### Plano de Implementação

**Narrativa: "Do Trabalhador ao Capitão - A Armadilha da Oportunidade"**
O jogador não é um aventureiro privilegiado, mas um trabalhador endividado que o sistema "promoveu" numa cruel armadilha econômica.

**Sequência de 4 Documentos:**
1. **Notificação de Cobrança** - Revela dívidas trabalhistas impossíveis de pagar
2. **Contrato de Oportunidade** - A "generosa" oferta da Guilda que triplica a dívida
3. **Inventário do Navio** - O "presente" envenenado: navio sucateado e tripulação desesperada
4. **A Primeira Escolha** - Decisão moral que define valores iniciais da Bússola do Capitão

**Implementação Técnica:**
- Novo componente `IntroSequence.jsx` com estado interno para navegação entre documentos
- Cada documento como subcomponente com estética de papel antigo (reutilizando padrões da Mesa do Capitão)
- Integração no fluxo de estados do App.jsx: `MENU` → `LOADING` → `INTRO` → `PLAYING`
- Conectar escolha final com sistema da Bússola do Capitão para valores iniciais

### Status
**Concluído** - Implementação completa da sequência introdutória:

**Frontend (Concluído):**
- Componente `IntroSequence.jsx` criado com navegação entre 4 documentos narrativos
- Styling `IntroSequence.css` com efeito de documentos oficiais antigos e pixelização
- Integração no `App.jsx` com estado INTRO e transição para PLAYING
- Sequência narrativa completa: dívida → oportunidade → inventário → primeira escolha

**Backend (Concluído):**
- DTO `IntroChoiceRequestDTO` para processar escolhas do jogador
- Método `processIntroChoice()` no `GameService` para aplicar valores iniciais na Bússola
- Endpoint `POST /api/games/{id}/compass` no `GameController`
- Três caminhos de escolha: cooperate (Guilda), resist (violência), neutral (humanitário)
- Valores balanceados para reputação, infâmia e aliança baseados na escolha

**Integração (Concluída):**
- Frontend conectado ao backend via requisição POST
- Tratamento de erros e atualização do estado do jogo
- Mensagens contextuais baseadas na escolha do jogador
- Transição suave para o gameplay principal

**Refinamentos Finais (Concluído):**
- Correção de legibilidade: fontes aumentadas (16px→20px texto, 18px→22px títulos)
- Crítica social mais sutil: remoção de linguagem "mastigada", mantendo subentendidos
- Correção narrativa: nome do navio "O Andarilho" consistente, tripulação inicial zerada
- Correção técnica: double-fetch para garantir carregamento das ações do porto

### Status
✅ **Concluído** - A sequência introdutória está completamente funcional e narrativamente consistente.

## v1.22 - A Primeira Jornada (Próxima Tarefa Sugerida)

**Versão do Jogo:** 1.22
**Foco:** Criar um tutorial interativo integrado que ensine as mecânicas básicas do jogo através de uma primeira jornada guiada.

### Justificativa

Com a introdução narrativa funcionando, o jogador agora entende *por que* está ali, mas ainda não sabe *como* jogar. A transição abrupta da escolha moral para a mesa do capitão deixa o jogador perdido em meio a várias opções sem contexto. Um tutorial integrado que simule uma primeira viagem curta ensinaria as mecânicas naturalmente.

### Proposta de Implementação

**Tutorial como Primeira Missão Obrigatória:**
O jogo força uma primeira viagem tutorial de Porto Real → Ilha da Tartaruga (distância curta) que ensina:

1. **Contratar Tripulação** - Obrigatório contratar 2-3 tripulantes na taverna
2. **Preparar o Navio** - Reparar pelo menos 20 pontos de casco, comprar suprimentos básicos
3. **Primeira Viagem** - Navegar com eventos scripted para ensinar mecânicas de mar
4. **Chegada e Comércio** - Vender produtos, aceitar contrato de retorno

**Mecânicas Tutorial:**
- Sistema de dicas contextuais integradas à UI existente
- Eventos de mar predeterminados (tempestade leve, encontro amigável)
- Primeiro contrato simples e seguro garantido
- Economia balanceada para garantir sucesso sem ser trivial

**Implementação Técnica:**
- Novo estado `TUTORIAL` no App.jsx entre INTRO e PLAYING
- Componente `TutorialOverlay` com dicas contextuais
- Flag `tutorialCompleted` no modelo Game para controlar o fluxo
- Eventos especiais de tutorial no sistema de encontros marítimos

### Status
**Não Iniciado** - Aguardando aprovação para implementação.



