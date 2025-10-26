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

## v1.22 - Sistema Tutorial Interativo (Concluído e Verificado)

**Versão do Jogo:** 1.22
**Foco:** Implementar sistema tutorial completo com personalização baseada na escolha moral inicial e identidade visual aprimorada.

**Descrição:** Foi implementado um sistema tutorial abrangente que guia novos jogadores através de todas as mecânicas core do jogo de forma diegética e personalizada. O tutorial inclui três caminhos distintos baseados na escolha moral inicial (Cooperar, Resistir, Neutro), cada um oferecendo experiências narrativas diferentes mas equivalentes em aprendizado. Adicionalmente, foi implementada uma identidade visual coesa com nova logomark, paleta de cores aprimorada, e componentes consistentes. O sistema tutorial personalizado ensina economia, tripulação, navegação e contratos através de cenários contextualizados, garantindo que todos os jogadores compreendam as mecânicas antes de entrar no jogo completo.

**Status:** **Concluído e Verificado.**

## v1.23.1 - Rebalanceamento Econômico (Concluído e Verificado)

**Versão do Jogo:** 1.23.1
**Foco:** Rebalancear completamente a economia do jogo para criar progressão mais equilibrada e pressão financeira significativa.

**Descrição:** Foi realizada uma auditoria econômica completa identificando desequilíbrios críticos nos sistemas de contratos, salários e preços. As principais mudanças implementadas incluem:

**Contratos Rebalanceados:**
- Guild: 500 → 200 ouro (redução para equilibrar)
- Revolutionary: 50 → 150 ouro (aumento para viabilizar)
- Brotherhood: 300 → 250 ouro (redução moderada)

**Sistema de Salários Reformulado:**
- Salário base: 10 → 20 ouro (dobrado para pressão econômica)
- Cálculo de atributos: divisor 10 → 8 (salários mais altos)
- Salário mínimo: 1 → 5 ouro (piso mais realista)

**Preços de Mercado Dinâmicos:**
- Sistema de variação ±20% nos preços base
- Seed baseado em gameId + portId para consistência
- Preços diferentes a cada jogo mantendo estratégia

O resultado é uma economia mais equilibrada onde contratos Revolutionary são viáveis, salários criam pressão financeira real, preços dinâmicos incentivam estratégia de trading, e a progressão é mais gradual e envolvente.

**Status:** **Concluído e Verificado.**

## v1.23 - Próximas Funcionalidades (Planejamento)
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

## v1.22 - A Primeira Jornada (Concluído)

**Foco:** Sistema de tutorial integrado e padronização da identidade visual.

### Implementações
✅ **Backend:** TutorialService, DTOs (TutorialStateDTO, TutorialChecklistDTO), endpoint `/api/games/{id}/tutorial`  
✅ **Frontend:** TutorialOverlay.jsx com progressão dinâmica e checklist visual  
✅ **Design System:** Identidade visual unificada, cores e sombras padronizadas  
✅ **UX:** Tutorial náutico integrado, LocationStatus compacto, layout consistente

#### **Personalização Baseada na Escolha Moral Inicial**

**Conceito: Tutorial Adaptativo**
O tutorial deve refletir e reforçar a escolha moral feita na introdução, criando três experiências distintas que ensinam as mesmas mecânicas através de lentes diferentes.

**Caminho A: "COOPERATE" (Contrato da Guilda)**
- **Tripulação Disponível**: Marinheiros "respeitáveis" com histórico na Guilda
- **Primeiro Destino**: Colônia Imperial (entrega de especiarias para plantação)
- **Contratos Disponíveis**: Apenas missões da Guilda, bem remuneradas e "legais"
- **Eventos de Mar**: Encontro com patrulha imperial (teste de lealdade)
- **Lição Tutorial**: Cooperação com o sistema oferece segurança, mas exige compromissos morais

**Caminho B: "RESIST" (Saquear Mercador)**  
- **Tripulação Disponível**: Piratas veteranos, mais caros mas experientes em combate
- **Primeiro Destino**: Porto Pirata (vender produtos "adquiridos")
- **Contratos Disponíveis**: Missões de saque e contrabando, arriscadas mas lucrativas
- **Eventos de Mar**: Emboscada a navio mercante (tutorial de combate naval)
- **Lição Tutorial**: A força bruta traz resultados rápidos, mas faz inimigos e atrai atenção

**Caminho C: "NEUTRAL" (Contrabando Humanitário)**
- **Tripulação Disponível**: Idealistas e refugiados, mais baratos mas com moral alta
- **Primeiro Destino**: Colônia Bloqueada (entrega clandestina de medicamentos)
- **Contratos Disponíveis**: Missões humanitárias, baixo pagamento mas alta satisfação moral
- **Eventos de Mar**: Ajudar refugiados em barco à deriva (tutorial de recursos limitados)
- **Lição Tutorial**: Ajudar os necessitados requer sacrifícios, mas constrói alianças duradouras

**Implementação da Personalização:**

**Narrativa Específica por Caminho:**
```javascript
const tutorialPaths = {
  cooperate: {
    destination: "Nova Esperança (Colônia Imperial)",
    missionType: "Entrega de Especiarias da Guilda", 
    crewTypes: ["Marinheiro Experiente", "Navegador Certificado", "Carpinteiro da Guilda"],
    firstEvent: "tutorial_imperial_patrol",
    moralLesson: "O sistema oferece segurança em troca de cumplicidade"
  },
  resist: {
    destination: "Baía dos Corsários (Porto Livre)",
    missionType: "Venda de Produtos Adquiridos",
    crewTypes: ["Pirata Veterano", "Artilheiro Experiente", "Saqueador"],
    firstEvent: "tutorial_merchant_raid", 
    moralLesson: "A força traz resultados, mas cria inimigos"
  },
  neutral: {
    destination: "Porto Livre (Colônia Bloqueada)", 
    missionType: "Contrabando de Medicamentos",
    crewTypes: ["Médico Refugiado", "Navegador Idealista", "Carpinteiro Voluntário"],
    firstEvent: "tutorial_refugee_rescue",
    moralLesson: "Ajudar requer sacrifício, mas constrói esperança"
  }
};
```

**Mecânicas Diferenciadas:**

**Cooperate (Guilda):**
- Preços fixos e justos, sem negociação
- Acesso a upgrade oficiais de melhor qualidade  
- Eventos focados em burocracia e cumprimento de regras
- Tutorial de reputação: como manter boa standing com autoridades

**Resist (Pirata):**
- Preços flutuantes, tudo é negociável na base da intimidação
- Acesso a equipamentos ilegais mais poderosos
- Eventos focados em combate e tomada de decisões sob pressão
- Tutorial de infâmia: como usar medo como ferramenta

**Neutral (Humanitário):**
- Economia baseada em troca/escambo, menos moedas mais favores
- Acesso a informações e rotas secretas através de contatos
- Eventos focados em dilemas morais e gestão de recursos escassos
- Tutorial de aliança: como construir rede de apoio mútuo

**Estrutura Técnica da Personalização:**
```java
// Adicionar ao modelo Game
@Column(name = "intro_choice")
private String introChoice; // "cooperate", "resist", "neutral"

@Column(name = "tutorial_path") 
private String tutorialPath; // derivado da introChoice

// TutorialService
public TutorialPathDTO getTutorialPath(String introChoice);
public List<CrewMemberDTO> getTutorialCrewOptions(String tutorialPath);
public SeaEncounterDTO getPathSpecificTutorialEvent(String tutorialPath, int eventIndex);
```

#### **Fase 2: A Jornada Tutorial (No Mar)**

**2.1 - Primeiro Destino Forçado:**
- Apenas "Ilha da Tartaruga" disponível no mapa (outros portos "em construção")
- Distância curta (1-2 turnos) para minimizar frustração
- Razão narrativa: "Entrega de suprimentos médicos urgente"

**2.2 - Eventos de Mar Personalizados por Caminho:**

**Turno 1 - Evento Universal (Navegação Básica):**
```
"O mar está calmo, mas seu navegador aponta nuvens escuras no horizonte.
 
 Suas opções:
 → Continuar no curso atual (Seguro, 1 turno extra)  
 → Navegar ao redor da tempestade (Rápido, mas consome rum extra)
 → Enfrentar a tempestade (Rápido, mas danifica o casco)"
```

**Turno 2 - Evento Específico por Caminho:**

**Caminho Cooperate - Patrulha Imperial:**
```
"Uma fragata imperial se aproxima. O capitão solicita inspeção de rotina.
 
 Suas opções:
 → Permitir inspeção completa (+15 Reputação, -1 turno)
 → Mostrar documentos da Guilda (+5 Reputação, sem demora)  
 → Tentar subornar (-50 moedas, +10 Reputação se sucesso)"
```

**Caminho Resist - Mercador Vulnerável:**
```
"Um galeão mercante pesadamente carregado navega sem escolta.
 
 Suas opções:
 → Atacar e saquear (+200 moedas, +15 Infâmia, risco de combate)
 → Intimidar para tributo (+100 moedas, +8 Infâmia, sem luta)  
 → Ignorar e seguir viagem (sem ganhos, tripulação questiona liderança)"
```

**Caminho Neutral - Refugiados à Deriva:**
```
"Um pequeno bote com famílias refugiadas pede socorro.
 
 Suas opções:
 → Resgatar todos (-20 comida, -10 água, +25 Aliança)
 → Dar suprimentos (+10 Aliança, -10 comida)  
 → Apenas indicar direção (sem custos, sem ganhos)"
```

**2.3 - Chegada Personalizada por Caminho:**

**Cooperate - Nova Esperança (Colônia Imperial):**
- Entrega oficial na administração colonial (+200 moedas, +10 Reputação)
- Novo contrato da Guilda garantido: "Transporte de Funcionários"
- Porto organizado: preços fixos, sem negociação, qualidade garantida

**Resist - Baía dos Corsários (Porto Livre):**
- Venda no mercado negro (+150 moedas base + bônus por negociação)
- Novo contrato pirata: "Interceptar Comboio Mercante" 
- Porto caótico: tudo negociável, preços flutuantes, risco de golpe

**Neutral - Porto Livre (Colônia Bloqueada):**
- Entrega clandestina de medicamentos (+50 moedas, +20 Aliança)
- Novo contrato humanitário: "Evacuar Civis de Zona de Guerra"
- Porto solidário: economia de favores, informações valiosas, network de contatos

#### **Fase 3: Implementação Técnica**

**3.1 - Modelo de Dados:**
```java
// Adicionar ao modelo Game
@Column(name = "tutorial_completed")
private Boolean tutorialCompleted = false;

@Column(name = "tutorial_phase")  
private String tutorialPhase = "PREPARATION"; // PREPARATION, JOURNEY, COMPLETED
```

**3.2 - Estados da Aplicação:**
```javascript
// Novo estado entre INTRO e PLAYING
const [gameState, setGameState] = useState('MENU');
// MENU → LOADING → INTRO → TUTORIAL → PLAYING → ERROR

// Estado tutorial específico
const [tutorialPhase, setTutorialPhase] = useState('PREPARATION');
// PREPARATION → JOURNEY → COMPLETED
```

**3.3 - Componentes Frontend:**
```
src/components/tutorial/
├── TutorialOverlay.jsx        # Container principal do tutorial
├── TutorialHighlight.jsx      # Destaque visual de elementos
├── TutorialChecklist.jsx      # Lista de tarefas na Mesa do Capitão  
├── TutorialTooltip.jsx        # Tooltips educativos contextuais
├── TutorialScriptedEvent.jsx  # Eventos de mar predeterminados
└── TutorialOverlay.css        # Estilos específicos do tutorial
```

**3.4 - Backend Services:**
```java
// GameService
public void completeTutorialPhase(Long gameId, String phase);
public List<PortActionDTO> getTutorialAvailableActions(Long gameId);
public SeaEncounterDTO getNextTutorialEvent(Long gameId);

// TutorialService (novo)
public boolean isTutorialCompleted(Long gameId);
public TutorialChecklistDTO getTutorialChecklist(Long gameId);  
public void progressTutorial(Long gameId, String action);
```

#### **Fase 4: Balanceamento e Progressão**

**4.1 - Economia Tutorial:**
- Ouro inicial: 500 → 800 (garantir contratação + reparos)
- Preços da taverna: 50% desconto durante tutorial
- Primeiro contrato: pagamento garantido de 300 moedas
- Suprimentos iniciais: dobrados para evitar escassez

**4.2 - Dificuldade Progressiva:**
- Eventos tutorial: sem possibilidade de game over
- Dano limitado: máximo 20 pontos de casco por evento
- Moral: não pode cair abaixo de 50 durante tutorial
- Encontros: apenas neutros ou positivos

**4.3 - Transição para Jogo Completo:**
- Após completar primeira jornada: unlock de todos os portos
- Economia volta ao normal
- Eventos randomizados ativados
- Achievement desbloqueado: "Primeiro Comando"

### Critérios de Aceitação

**Funcional:**
- [ ] Jogador não pode viajar sem completar preparação mínima
- [ ] Tutorial personalizado baseado na escolha moral inicial (cooperate/resist/neutral)
- [ ] Cada caminho ensina as mesmas mecânicas com narrativas diferentes  
- [ ] Impossível falhar durante tutorial (morte/game over)
- [ ] Transição suave para jogo normal após conclusão

**Educacional:**
- [ ] Jogador aprende todas as mecânicas básicas organicamente
- [ ] Não há texto explicativo - apenas descoberta através da ação
- [ ] Cada decisão reforça a filosofia da escolha inicial
- [ ] Tutorial completável em 10-15 minutos independente do caminho
- [ ] Três experiências distintas mas equivalentes em aprendizado

**Personalização:**
- [ ] Tripulação disponível reflete a escolha moral (Guilda/Piratas/Idealistas)
- [ ] Destinos tutorial condizentes com a filosofia escolhida
- [ ] Eventos de mar específicos por caminho mas mesma dificuldade
- [ ] Economia tutorial adaptada (preços fixos vs negociação vs escambo)
- [ ] Recompensas finais equivalentes mas tematicamente consistentes

**Técnico:**  
- [ ] Estado tutorial e escolha inicial persistidos no banco de dados
- [ ] Sistema de caminhos tutorial facilmente extensível
- [ ] Componentes reutilizáveis para futuras expansões
- [ ] Performance não afetada pelo sistema de personalização
- [ ] Fácil desabilitar tutorial para jogadores experientes

### Riscos e Mitigações

**Risco 1: Tutorial muito longo**
- Mitigação: Manter jornada em máximo 3-4 turnos de mar independente do caminho

**Risco 2: Jogador se sente restrito**  
- Mitigação: Apresentar como "primeira missão especial" não como tutorial

**Risco 3: Complexidade técnica da personalização**
- Mitigação: Implementar primeiro um caminho genérico, depois adicionar personalização
- Usar padrão Strategy para diferentes caminhos tutorial
- Começar com MVP funcional antes de adicionar variações

**Risco 4: Desbalanceamento entre caminhos**
- Mitigação: Garantir que todos os caminhos tenham recompensas equivalentes
- Testar cada caminho separadamente para dificuldade similar
- Métricas de conclusão e satisfação por caminho tutorial

**Risco 5: Manutenção de múltiplas narrativas**
- Mitigação: Sistema de templates para eventos tutorial
- Documentação clara das diferenças narrativas vs mecânicas
- Testes automatizados para garantir consistência

---

## v1.23 - Próximas Funcionalidades (Planejamento)

**Prioridades para as próximas versões:**

### **P1 - Críticas (Alta Prioridade)**
1. **Otimização de Performance**: Melhorar tempo de carregamento e responsividade da aplicação
2. **Polimento de Combat**: Melhorar feedback visual e balanceamento do sistema de combate naval
3. **Validação de Balance**: Testes extensivos do novo sistema econômico implementado na v1.23.1

### **P2 - Funcionalidades (Média Prioridade)**  
1. **Sistema de Save/Load**: Permitir múltiplos saves e carregamento de jogos
2. **Eventos Dinâmicos**: Expandir variedade de encontros no mar e eventos de porto
3. **Progressão de Tripulação**: Sistema de XP e evolução dos tripulantes
4. **Melhorias na Taverna**: Mais variedade de tripulantes e personalidades

### **P3 - Expansão (Baixa Prioridade)**
1. **Novos Portos**: Adicionar mais localizações com características únicas
2. **Facções Avançadas**: Sistema de diplomacia mais profundo
3. **Campanhas**: Múltiplas histórias além da jornada tutorial
4. **Sistema de Reputação Expandido**: Consequências de longo prazo para ações do jogador

### **Próximos Passos Imediatos:**
- **v1.24 - Otimização de Performance**: Foco em melhorar responsividade e tempo de carregamento
- **v1.25 - Polimento de UX**: Refinamentos na interface baseados em feedback de gameplay
- **v1.26 - Expansão de Conteúdo**: Novos eventos, tripulantes e contratos

## v1.24 - Teatro da Batalha Naval (Concluído)

**Versão do Jogo:** 1.24
**Foco:** Melhorar drasticamente a experiência de combate com interface cinemática e mecânicas aprimoradas.

### Fase 1: Auditoria e Design do Sistema de Combate (Concluído)
✅ **Descrição**: Análise completa do sistema de combate existente, identificação de pontos fracos (previsibilidade, falta de feedback visual, repetitividade) e criação do conceito "Teatro da Batalha Naval"
✅ **Status**: **Concluído e Verificado.**

### Fase 2: Implementação do BattleScene Component (Concluído)
✅ **Descrição**: Criado novo componente `BattleScene.jsx` com interface cinemática inspirada em pergaminho antigo, incluindo animações de navios, barras de vida, log de combate e ações táticas
✅ **Status**: **Concluído e Verificado.**

### Fase 3: Resolução de Issues Técnicos (Concluído)
✅ **Descrição**: Correção de erros de TypeError, integração com dados da tripulação, ajustes de layout, animações de navios e problemas de compilação
✅ **Status**: **Concluído e Verificado.**

**Issues Conhecidos:**
⚠️ **Barra de vida do inimigo**: A barra visual não atualiza durante o combate (funcionalidade básica operacional, issue cosmético para correção futura)

---

## v1.25 - Expansão do Mundo de Alvor (Em Planejamento)

**Versão do Jogo:** 1.25  
**Foco:** Expandir significativamente o mundo do jogo com novos portos, encontros e eventos narrativos.

### Objetivos Principais:
1. **Novos Portos e Regiões**: Expandir de 3 para 8-10 portos com características únicas
2. **Eventos Narrativos**: Criar sistema robusto de eventos aleatórios e específicos por região
3. **Encontros Marítimos**: Diversificar tipos de encontros no mar além do combate básico
4. **Profundidade Temática**: Cada região com identidade cultural, econômica e política própria

### Expansões Planejadas:

#### **Novos Portos:**
- **Região Norte (Imperial)**:
  - *Fortaleza de Ferro*: Base militar imperial, upgrades militares premium
  - *Porto da Coroa*: Capital administrativa, contratos governamentais
  
- **Região Leste (Guild)**:
  - *Entreposto Dourado*: Centro de comércio internacional, preços variáveis
  - *Ilha dos Mercadores*: Especializada em itens raros e exóticos
  
- **Região Sul (Neutral/Wild)**:
  - *Vila dos Pescadores*: Porto humilde, reparos baratos, tripulação simples
  - *Ruínas de Atlântida*: Local misterioso com tesouros e perigos
  
- **Região Oeste (Pirate + Wilderness)**:
  - *Refúgio do Kraken*: Porto pirata secreto, contrabando e mercado negro
  - *Ilhas Selvagens*: Território inexplorado, recursos naturais

#### **Sistema de Eventos Narrativos:**
- **Eventos por Região**: Cada área com eventos temáticos únicos
- **Eventos Sazonais**: Mudanças baseadas em "estações" do jogo
- **Eventos de Reputação**: Consequências das ações passadas do jogador
- **Eventos de Tripulação**: Histórias pessoais dos membros da crew

#### **Novos Tipos de Encontro:**
- **Mercadores Navegantes**: Comércio em alto mar
- **Navios Fantasmas**: Encontros sobrenaturais com loot único
- **Tempestades**: Eventos ambientais com escolhas de navegação
- **Piratas Diplomáticos**: Encontros que podem ser resolvidos sem combate
- **Patrulhas Navais**: Encontros com autoridades baseados em reputação

### **Próximos Candidatos Imediatos:**
1. **Lazy Loading**: Implementar carregamento sob demanda de componentes
2. **Otimização de API**: Reduzir chamadas desnecessárias e implementar caching
3. **Feedback Visual**: Melhorar indicadores de loading e transições
4. **Debugging Tools**: Implementar ferramentas de debug para desenvolvimento futuro

---

## v1.26 - Mapa Artístico Interativo & Sistema de Ícones PNG (Concluído)

**Versão do Jogo:** 1.26  
**Foco:** Transformar a tela de seleção de destinos em um mapa artístico interativo com sistema completo de ícones PNG.

### Conceito "Carta Náutica do Arquipélago":
✅ **MapView Component**: Desenvolvido completamente substituindo TravelPanel  
✅ **Estilo Visual**: Pergaminho antigo com decorações náuticas autênticas  
✅ **Interatividade**: Portos clicáveis com preview, seleção visual e confirmação  
✅ **Sistema de Coordenadas**: 10 portos posicionados no mapa com precisão  

### Sistema de Ícones PNG Implementado:

#### **Estrutura de Assets:**
```
public/assets/icons/
├── ports/           # Ícones das facções (24x24px)
│   ├── imperial-sword.png      # Império - Espada elegante
│   ├── guild-coins.png         # Guilda - Pilha de moedas douradas  
│   ├── pirate-flag.png         # Piratas - Bandeira com caveira
│   └── free-anchor.png         # Porto Livre - Âncora simples
├── compass/         # Rosa dos ventos (64x64px)
│   └── compass.png             # Bússola ornamentada central
├── decorative/      # Elementos decorativos (16-48px)
│   ├── anchor-small.png        # Âncoras pequenas para header
│   ├── telescope.png           # Luneta (32x32px)
│   ├── treasure-map.png        # Mapa desenrolado (48x32px)
│   └── steering-wheel.png      # Timão (40x40px)
├── ships/           # Navios decorativos
│   └── ships.png               # Navio para animações
└── ui/              # Interface geral
    └── treasure-chest.png      # Baú para painéis
```

#### **Implementação nos Componentes:**

**MapView.jsx & MapView.css:**
- ✅ **Ícones das Facções**: Sistema de mapeamento automático por tipo de porto
- ✅ **Animações Temáticas**: Brilho dourado (Guilda), gleam azul (Império), tremulação (Piratas), balanço (Porto Livre)
- ✅ **Rosa dos Ventos**: Ícone PNG limpo sem fundos indesejados
- ✅ **Decorações**: Telescópio (superior esquerdo), timão (inferior direito), mapa desenrolado, âncoras no header
- ✅ **Substituição de Emojis**: Âncora do título convertida para PNG

**CaptainCompass.jsx & CaptainCompass.css:**
- ✅ **Ícone de Fundo**: Bússola PNG integrada com agulhas funcionais
- ✅ **Dimensionamento**: Container expandido (200x200px) com ícone otimizado (140x140px)
- ✅ **Z-index Organizado**: Ícone (z:1), centro (z:20), agulhas (z:15)
- ✅ **Posicionamento**: Centralização precisa com margens calculadas

#### **Melhorias Visuais:**
- ✅ **Filtros CSS**: Drop-shadows, sepia, brightness para integração temática
- ✅ **Animações Personalizadas**: `coin-glow`, `sword-gleam`, `flag-wave`, `anchor-sway`
- ✅ **Transparências**: Opacidades variáveis para profundidade visual
- ✅ **Overflow Management**: Elementos posicionados para extrapolação controlada

### Status:
✅ **Concluído** - Sistema completo de ícones PNG implementado com sucesso.

**Todos os emojis foram substituídos por ícones PNG profissionais, criando uma experiência visual autêntica e imersiva no universo náutico do Freedom Tide.**

---

## v1.27 - Economia Balanceada & Consequências Finais (Concluído)

**Versão do Jogo:** 1.27  
**Foco:** Implementar limitações econômicas realistas e consequências definitivas de derrota no gameplay.

### Contexto
Durante testes de gameplay, foram identificados dois problemas críticos que tornavam a experiência muito fácil e sem tensão:
1. **Contratação Ilimitada**: Jogadores podiam contratar infinitos tripulantes pagando apenas salário mensal
2. **Imortalidade Naval**: Navios podiam ter casco negativo e continuar funcionando indefinidamente

### Implementações

#### **Sistema de Custo de Contratação**
**Backend (`GameService.java`):**
- ✅ **Custo Inicial**: 3x salário mensal + bônus por atributos altos (>45 pontos = +10 moedas por ponto extra)
- ✅ **Validação Financeira**: Verificação de fundos antes da contratação
- ✅ **Cobrança Automática**: Dedução do custo inicial do ouro do jogador

**Frontend (`TavernView.jsx`):**
- ✅ **Interface Atualizada**: Exibição clara do custo inicial e salário mensal separadamente
- ✅ **DTO Expandido**: `TavernRecruitDTO` com campo `hiringCost` para transparência total

**Fórmula do Custo:**
```java
int hiringCost = (salary * 3) + (attributeSum > 45 ? (attributeSum - 45) * 10 : 0);
```

#### **Sistema de Game Over por Destruição Naval**
**Backend (`GameService.java`):**
- ✅ **Verificação Crítica**: Implementada em todos os métodos que causam dano ao casco:
  - `attackEncounter()` - Combate naval direto
  - `boardEncounter()` - Tentativas de abordagem falhadas
  - `investigateEncounter()` - Danos acidentais por destroços

**Modelo de Dados (`Game.java`):**
- ✅ **Campos Adicionados**: `gameOver` (boolean) e `gameOverReason` (String)
- ✅ **Persistência**: Auto-criação via `ddl-auto=create`

**Frontend (`App.jsx` + `GameOver.jsx`):**
- ✅ **Tela Temática**: Interface de game over com estética naval (pergaminho náufrago)
- ✅ **Narrativa Contextual**: Mensagens específicas por tipo de derrota
- ✅ **Funcionalidades**: Botões para nova jornada ou menu principal

#### **Melhorias Técnicas**
**DTOs Expandidos:**
- ✅ `GameActionResponseDTO`: Adicionado campo `gameOver` boolean
- ✅ `GameStatusResponseDTO`: Adicionados `gameOver` e `gameOverReason`
- ✅ `GameMapper`: Mapeamento automático dos campos de game over

**Verificação Unificada:**
```javascript
const checkGameOver = (response) => {
  if (response.gameOver || response.gameStatus.gameOver) {
    setGameState('GAME_OVER');
    return true;
  }
  return false;
};
```

#### **Interface de Game Over**
**Design Temático (`GameOver.jsx` + `GameOver.css`):**
- 🌊 **Estética Naval**: Pergaminho envelhecido com ondas animadas
- 💀 **Narrativa Dramática**: Epitáfio poético e razão específica da derrota
- ⚓ **Opções de Continuação**: Nova jornada ou retorno ao menu principal
- 🎯 **Animações Contextuais**: Balanço suave, ondas em movimento, efeitos fantasmagóricos

#### **Mensagens de Derrota por Contexto**
- **Combate Naval**: *"O casco se parte sob o bombardeio inimigo..."*
- **Abordagem Falhada**: *"O dano estrutural é irreparável..."*
- **Acidente com Destroços**: *"Os destroços perfuraram completamente o casco..."*

### Impacto no Gameplay

#### **Economia Mais Realista:**
- Contratação agora requer planejamento financeiro estratégico
- Tripulantes de alta qualidade custam significativamente mais
- Balanceamento natural entre quantidade vs. qualidade de tripulação

#### **Tensão de Combate:**
- Cada decisão de combate agora tem consequências permanentes potenciais
- Gestão de riscos torna-se fundamental
- Reparos no porto ganham importância crítica

#### **Progressão Orgânica:**
- Jogadores são incentivados a crescer gradualmente
- Economia de recursos naturalmente limitada
- Decisões táticas ganham peso real

### Compatibilidade
- ✅ **Backward Compatible**: Jogos existentes funcionam normalmente
- ✅ **Auto-Migration**: Campos `gameOver` adicionados automaticamente
- ✅ **Graceful Degradation**: Sistema funciona mesmo com dados parciais

### Status:
✅ **Concluído** - Sistema completo de economia balanceada e game over implementado.

**O Freedom Tide agora oferece uma experiência de gameplay com tensão real e consequências definitivas, mantendo o equilíbrio entre desafio e diversão.**

---

## v1.28 - Sistema de Progressão do Capitão & Revisão Narrativa (Concluído)

**Versão do Jogo:** 1.28  
**Foco:** Implementar sistema completo de progressão do capitão com XP e habilidades, além de revisar a sequência de introdução para melhor crítica social.

### Contexto
Com o sistema de jogo amadurecido, tornou-se essencial implementar progressão do personagem e refinar a narrativa introdutória para capturar melhor a crítica social do projeto.

### Implementações

#### **Sistema de Progressão do Capitão**

**Backend - Modelo de Dados (`Game.java`):**
- ✅ **Campos de Progressão**: `captainLevel`, `captainXP`, `captainSkillPoints`
- ✅ **Três Árvores de Habilidades**:
  - **Combate**: `skillCombatProwess`, `skillNavalTactics`, `skillCrewInspiration`, `skillArtilleryMaster`
  - **Comércio**: `skillMerchantEye`, `skillDiplomacy`, `skillResourceManagement`, `skillTradeNetworks`
  - **Exploração**: `skillWeatherReading`, `skillNavigationMaster`, `skillSurvivalInstinct`, `skillTreasureHunting`

**Enums de Habilidades (`CaptainSkill.java` & `CaptainSkillTree.java`):**
- ✅ **Sistema Estruturado**: Enums organizados por árvore com bônus específicos
- ✅ **Cálculos de Bônus**: Métodos para determinar modificadores baseados no nível das habilidades
- ✅ **Balanceamento**: Progressão linear com marcos de desbloqueio

**Serviço de Progressão (`CaptainProgressionService.java`):**
- ✅ **Sistema de XP**: Ganho baseado em ações (viagem, combate, comércio, contratos)
- ✅ **Level Up**: Cálculo automático e concessão de skill points
- ✅ **Investimento**: Sistema para alocar pontos nas árvores de habilidades
- ✅ **Integração**: Bônus aplicados automaticamente em todas as mecânicas relevantes

**API REST (`CaptainController.java`):**
```java
GET  /api/games/{id}/captain/progression  // Status completo da progressão
POST /api/games/{id}/captain/invest       // Investir skill points
```

**Frontend - Interface de Progressão (`CaptainProgression.jsx`):**
- ✅ **Três Árvores Visuais**: Interface organizada por especialização
- ✅ **Investimento Interativo**: Clique para alocar pontos com confirmação
- ✅ **Feedback Visual**: Indicadores de nível, bônus e progresso para próximo nível
- ✅ **Estética Náutica**: Mantém identidade visual do jogo com pergaminhos antigos

#### **Integração com Gameplay Existente**

**Sistema de XP Automático:**
- ✅ **Viagens**: +50 XP por destino alcançado
- ✅ **Combates**: +100 XP por vitória naval
- ✅ **Comércio**: +25 XP por transação no mercado
- ✅ **Contratos**: XP proporcional à recompensa do contrato

**Bônus Aplicados Automaticamente:**
- ✅ **GameService**: Integração em combate, comércio, navegação
- ✅ **Cálculos Dinâmicos**: Modificadores baseados nas habilidades do capitão
- ✅ **Transparência**: Bônus visíveis nos logs de ação

#### **Revisão da Sequência de Introdução**

**Problema Identificado:**
A versão anterior havia perdido parte da crítica social essencial do jogo ao focar demais em "oportunidade" em vez de "opressão".

**Solução - Narrativa Híbrida:**
- ✅ **Tom Corrigido**: Mantém imersão histórica mas restaura crítica social
- ✅ **Contexto de Classe**: Protagonista é ex-trabalhador das plantações com dívida artificial
- ✅ **Linguagem Corporativa**: Eufemismos reveladores da exploração sistêmica
- ✅ **Escolhas Morais Pesadas**: Cada opção com implicações éticas claras

**Documentos Redesenhados (`IntroSequence.jsx`):**

**Documento 1 - "Notificação de Transição":**
- Revela origem como trabalhador endividado pelo sistema
- Dívida acumulada através de custos obrigatórios (alojamento, comida, ferramentas)
- Alternativa aos trabalhos forçados nas minas

**Documento 2 - "Contrato de Quitação":**
- Termos de "liberdade" controlada sob supervisão da Guilda
- Obrigações permanentes disfarçadas de taxas de serviço
- Cláusula 847: Retorno ao trabalho forçado em caso de inadimplência

**Documento 3 - "Certificado do Navio":**
- Descrição realista alinhada com mecânicas do jogo
- Navio funcional mas precário, necessitando cuidados
- Contextualização histórica da Era da Vela

**Escolhas Finais Reformuladas:**
1. **"Aceitar o Sistema"**: Cooperação que perpetua exploração
   - Transportar "produtos de produtividade" (drogas para trabalhadores)
   - Segurança através da cumplicidade

2. **"Quebrar as Correntes"**: Resistência individual violenta
   - Saquear luxos da elite enquanto trabalhadores sofrem
   - Força contra opressão, mas criando ciclo de violência

3. **"Buscar Alternativas"**: Construção de resistência organizada
   - Contrabandear medicamentos proibidos para forçar dependência
   - Mudança sistêmica através da solidariedade

#### **Melhorias de Estilo e UX**

**CSS Aprimorado (`IntroSequence.css`):**
- ✅ **Elementos Temáticos**: Selo da Guilda (âncora), separadores náuticos
- ✅ **Animações Suaves**: Entrada de documentos, balanço marítimo
- ✅ **Destacamento Visual**: Bordas coloridas por árvore de habilidade
- ✅ **Névoa Marítima**: Efeito de fundo para maior imersão

**Integração com Progressão:**
- ✅ **Conexão Clara**: Escolhas iniciais conectadas às árvores de habilidades
- ✅ **Preview de Progressão**: Cada caminho mostra desenvolvimento futuro
- ✅ **Ícones Temáticos**: Símbolos visuais para cada especialização

### Resultados

#### **Progressão Orgânica do Personagem:**
- Sistema de XP incentiva gameplay ativo e diversificado
- Três especializações oferecem builds distintos e viáveis
- Progressão visível e impactante nas mecânicas de jogo

#### **Narrativa Socialmente Consciente:**
- Crítica ao capitalismo exploratório restaurada e refinada
- Contexto histórico mantém imersão sem comprometer mensagem
- Escolhas morais com peso real e consequências claras

#### **Experiência de Usuário Aprimorada:**
- Interface de progressão intuitiva e visualmente atraente
- Introdução mais envolvente e tematicamente consistente
- Feedback constante sobre desenvolvimento do capitão

### Compatibilidade
- ✅ **Retrocompatibilidade**: Jogos existentes recebem campos de progressão automaticamente
- ✅ **Migração Suave**: Valores padrão aplicados a saves antigos
- ✅ **Performance**: Sistema otimizado sem impacto na responsividade

### Status:
✅ **Concluído** - Sistema de progressão do capitão implementado com integração completa ao gameplay existente, e narrativa introdutória revisada para melhor impacto social.

**O Freedom Tide agora oferece progressão significativa do personagem e uma introdução que captura efetivamente sua crítica social, mantendo alta qualidade narrativa e técnica.**

## v1.28 - Sistema Completo de Progressão da Tripulação (Concluído)

**Versão do Jogo:** 1.28
**Foco:** Implementação completa do sistema de gamificação através de progressão da tripulação, personagens únicos e melhorias visuais.

### Objetivos Alcançados

#### **Sistema de Progressão da Tripulação**
- ✅ **9 Profissões Especializadas**: Cada uma com ícones e cores distintivas
- ✅ **5 Níveis de Patente**: De Novato a Lenda, com thresholds de XP balanceados
- ✅ **Ganho Automático de XP**: Sistema integrado que recompensa ações de jogo
- ✅ **Interface Visual Completa**: Barras de XP, estatísticas e indicadores visuais

#### **28 Personagens Únicos**
- ✅ **Distribuição por Portos**: Organização temática por tipo de porto
- ✅ **Backgrounds Ricos**: Histórias elaboradas para cada personagem
- ✅ **Balanceamento Estratégico**: Diferentes custos e atributos por raridade

#### **Sistema de Ícones Pixel Art**
- ✅ **Substituição Completa de Emojis**: Transição para arte consistente
- ✅ **Organização Estruturada**: Diretórios organizados com READMEs
- ✅ **Fallback Gracioso**: Sistema robusto com tratamento de erros

#### **Melhorias na Interface de Game Over**
- ✅ **Simplificação Visual**: Remoção de elementos desnecessários
- ✅ **Foco no Essencial**: Caveira ampliada e título mais proeminente
- ✅ **Design Limpo**: Botões apenas com texto, sem ícones

### Arquitetura Técnica

#### **Backend (Spring Boot)**
```java
// Enum de Profissões com Métodos Visuais
public enum CrewProfession {
    NAVIGATOR("Navegador", "compass.png", "#4A90E2"),
    GUNNER("Artilheiro", "cannon.png", "#E24A4A");
    
    public String getIcon() { return iconFile; }
    public String getColor() { return colorCode; }
}

// Sistema de Patentes com XP
public enum CrewRank {
    NOVICE(0), EXPERIENCED(100), SKILLED(300), 
    VETERAN(600), LEGENDARY(1000);
}
```

#### **Frontend (React)**
```jsx
// Componente de Gestão da Tripulação
const CrewManagementView = () => {
    return (
        <div className="crew-management">
            {crew.map(member => (
                <div className="crew-card">
                    <XPBar current={member.experience} 
                           next={member.nextRankThreshold} />
                    <ProfessionIcon profession={member.profession} />
                </div>
            ))}
        </div>
    );
};
```

#### **Serviço de Personagens Únicos**
```java
@Service
public class UniqueCharacterService {
    // 28 personagens organizados por 4 tipos de porto
    // Cada personagem com background único e atributos balanceados
}
```

### Impacto no Gameplay

#### **Engajamento Aprofundado**
- **Progressão Visível**: Jogadores veem crescimento em tempo real
- **Especialização Estratégica**: Diferentes profissões oferecem vantagens únicas
- **Coleta de Personagens**: 28 personagens únicos incentivam exploração

#### **Recompensas Automáticas**
- **XP por Ações**: Combate, viagem e comércio concedem experiência
- **Progressão Natural**: Sistema integrado sem microgerenciamento
- **Feedback Imediato**: Barras de XP mostram progresso instantâneo

### Melhorias Visuais

#### **Consistência Artística**
- **Pixel Art Unificada**: Identidade visual coesa em todo o jogo
- **Ícones Temáticos**: Representações visuais claras de cada profissão
- **Interface Polida**: Game over screen simplificada e elegante

### Status:
✅ **Concluído** - Sistema completo de progressão da tripulação implementado com sucesso.

**O Freedom Tide agora oferece um sistema de gamificação robusto que recompensa o jogador com progressão visual e mecânica, mantendo o engajamento através de recompensas contínuas e coleta de personagens únicos.**

## v1.29 - Sistema de Encontros Orientados por Contratos (Concluído)

**Versão do Jogo:** 1.29
**Foco:** Implementar conexão inteligente entre contratos ativos e encontros marítimos para criar narrativa coesa.

### Problema Identificado

O sistema anterior de encontros era completamente aleatório, desconectado dos contratos ativos do jogador. Isso criava uma experiência fragmentada onde as missões não tinham impacto real na jornada, reduzindo a imersão e o senso de propósito narrativo.

### Solução Implementada

#### **Sistema de Peso Alto para Contratos**
- **70% de chance** de encontros relacionados ao contrato ativo
- **30% de encontros aleatórios** para manter variedade
- **Encontros contextuais** baseados na facção do contrato

#### **Novos Tipos de Encontros por Facção**

**🏛️ GUILD (Guilda Mercante)**
```java
GUILD_CONVOY,       // Comboio da Guilda transportando mercadorias valiosas
TRADE_DISPUTE,      // Disputa comercial que precisa de mediação  
MERCHANT_DISTRESS   // Mercador em apuros pedindo ajuda
```

**⚔️ EMPIRE (Império de Alvor)**
```java
IMPERIAL_ESCORT,    // Escolta imperial transportando diplomatas ou tesouros
REBEL_SABOTEURS,    // Sabotadores tentando interceptar recursos imperiais
TAX_COLLECTORS      // Coletores de impostos imperiais fazendo inspeções
```

**🏴‍☠️ BROTHERHOOD (Irmandade de Grani)**
```java
SMUGGLER_MEET,      // Encontro secreto with contrabandistas
IMPERIAL_PURSUIT,   // Perseguição imperial a atividades ilícitas
PIRATE_ALLIANCE     // Proposta de aliança com outros piratas
```

**🗽 REVOLUTIONARY (Movimento Revolucionário)**
```java
FREEDOM_FIGHTERS,   // Lutadores pela liberdade pedindo apoio
IMPERIAL_OPPRESSION,// Testemunhar atos de opressão imperial
UNDERGROUND_NETWORK // Contato com a rede clandestina revolucionária
```

### Implementação Técnica

#### **Serviço Especializado**
```java
@Service
public class ContractEncounterService {
    private static final double CONTRACT_ENCOUNTER_WEIGHT = 0.7;
    
    public SeaEncounterType generateEncounterType(Game game) {
        Contract activeContract = game.getActiveContract();
        
        if (activeContract != null && random.nextDouble() < CONTRACT_ENCOUNTER_WEIGHT) {
            return generateContractRelatedEncounter(activeContract);
        }
        
        return generateBasicEncounter();
    }
}
```

#### **Sistema de Bônus por Lealdade**
```java
private int applyContractBonus(Game game, SeaEncounterType encounterType, 
                              int baseReward, List<String> eventLog) {
    // 25% de bônus quando encontro relaciona com contrato ativo
    double bonusMultiplier = 1.25;
    int bonusReward = (int) (baseReward * bonusMultiplier);
    
    String bonusMessage = switch (activeContract.getFaction()) {
        case GUILD -> "💰 Bônus da Guilda: +{bonus} ouro pela cooperação comercial!";
        case EMPIRE -> "⚔️ Bônus Imperial: +{bonus} ouro por servir o Império!";
        case BROTHERHOOD -> "🏴‍☠️ Bônus da Irmandade: +{bonus} ouro pela lealdade!";
        case REVOLUTIONARY -> "🗽 Bônus Revolucionário: +{bonus} ouro pela liberdade!";
    };
}
```

#### **Detecção Inteligente de Combate vs Narrativo**
```java
public boolean isCombatEncounter(SeaEncounterType type) {
    return switch (type) {
        case MERCHANT_SHIP, PIRATE_VESSEL, NAVY_PATROL,
             GUILD_CONVOY, IMPERIAL_ESCORT, REBEL_SABOTEURS,
             SMUGGLER_MEET, PIRATE_ALLIANCE, FREEDOM_FIGHTERS -> true;
             
        case MYSTERIOUS_WRECK, TRADE_DISPUTE, MERCHANT_DISTRESS,
             IMPERIAL_OPPRESSION, UNDERGROUND_NETWORK -> false;
    };
}
```

### Impacto no Gameplay

#### **Narrativa Coesa**
- **Missões com Propósito**: Cada viagem faz sentido no contexto do contrato
- **Imersão Aprofundada**: Jogador sente estar cumprindo uma missão real
- **Consequências Reais**: Escolhas de contrato impactam a experiência de viagem

#### **Incentivos Estratégicos**
- **Recompensas por Lealdade**: 25% bônus por ajudar facção do contrato ativo
- **Decisões Informadas**: Jogadores podem prever tipos de encontros baseados no contrato
- **Variedade Mantida**: 30% de encontros aleatórios preservam surpresas

#### **Experiência Personalizada**
- **Diferentes Narrativas**: Cada facção oferece experiência única
- **Rejogar Incentivado**: Diferentes contratos = diferentes aventuras
- **Progressão Natural**: Sistema se integra naturalmente ao gameplay existente

### Exemplos de Funcionamento

#### **Cenário: Contrato da Guilda Ativo**
```
Jogador aceita "Transporte de Especiarias" da Guilda
↓
Próxima viagem tem 70% chance de encontrar:
- Comboio da Guilda precisando de escolta (+25% ouro de bônus)
- Disputa comercial entre mercadores (mediação narrativa)
- Mercador da Guilda em apuros (missão de resgate)
```

#### **Cenário: Contrato da Irmandade Ativo**
```
Jogador aceita "Contrabando Discreto" da Irmandade
↓
Encontros prováveis:
- Encontro secreto com contrabandistas (informações valiosas)
- Fuga de perseguição imperial (combate tenso)
- Proposta de aliança pirata (decisão estratégica)
```

### Status:
✅ **Concluído** - Sistema completo de encontros orientados por contratos implementado com sucesso.

**O Freedom Tide agora oferece uma experiência narrativa coesa onde contratos realmente importam, transformando cada viagem em parte de uma missão maior e recompensando a lealdade às facções com bônus tangíveis.**

## Próxima Tarefa: v1.30 - Sistema de Níveis do Capitão

**Versão do Jogo:** 1.30
**Foco:** Implementar sistema de progressão pessoal do capitão com habilidades desbloqueáveis e benefícios estratégicos.

### Justificativa

Enquanto a tripulação agora possui um sistema completo de progressão, o próprio capitão (jogador) permanece estático. Para completar o ciclo de gamificação, precisamos implementar um sistema onde o capitão também evolui, desbloqueando habilidades que alteram fundamentalmente o gameplay e oferecem novas estratégias.

### Conceito do Sistema

#### **Filosofia de Design**
- **Progressão Horizontal**: Diferentes caminhos de especialização ao invés de simples power creep
- **Escolhas Significativas**: Cada nível oferece opções que definem o estilo de jogo
- **Impacto Estratégico**: Habilidades que alteram mecânicas fundamentais do jogo

#### **Estrutura de Níveis (1-20)**
```
Níveis 1-5:   Fundamentos (Navegação, Combate Básico, Comércio)
Níveis 6-10:  Especialização (Escolha de Caminho Principal)
Níveis 11-15: Maestria (Habilidades Avançadas)
Níveis 16-20: Lenda (Habilidades Únicas e Raras)
```

#### **Três Caminhos de Especialização**

**🗺️ Explorador (Navigator's Path)**
- **Filosofia**: Descoberta, eficiência de viagem, conhecimento de mundo
- **Habilidades Exemplo**:
  - **Cartógrafo** (Nível 6): Revela informações extras sobre portos no mapa
  - **Navegação Intuitiva** (Nível 8): 25% menos tempo de viagem
  - **Olho do Marinheiro** (Nível 12): Previsão de condições meteorológicas
  - **Lenda dos Mares** (Nível 18): Acesso a rotas secretas e portos ocultos

**⚔️ Corsário (Warrior's Path)**
- **Filosofia**: Combate, intimidação, domínio através da força
- **Habilidades Exemplo**:
  - **Tática Naval** (Nível 6): +15% dano em combate naval
  - **Reputação Temida** (Nível 8): Inimigos menores podem fugir sem luta
  - **Mestre das Armas** (Nível 12): Acesso a equipamentos exclusivos
  - **Terror dos Mares** (Nível 18): Habilidade de intimidar portos inteiros

**💰 Mercador (Merchant's Path)**
- **Filosofia**: Economia, diplomacia, influência através de riqueza
- **Habilidades Exemplo**:
  - **Olho para Negócios** (Nível 6): +20% lucro em transações comerciais
  - **Rede de Contatos** (Nível 8): Acesso a contratos exclusivos
  - **Magnata** (Nível 12): Pode investir em melhorias de portos
  - **Barão dos Mares** (Nível 18): Influência política em decisões de porto

### Implementação Técnica

#### **Backend - Estrutura de Dados**
```java
@Entity
public class Captain {
    private Long id;
    private Integer level = 1;
    private Integer experience = 0;
    private CaptainPath specialization;
    private Set<CaptainSkill> unlockedSkills;
    
    // Métodos para cálculo de XP e progressão
}

public enum CaptainPath {
    EXPLORER("🗺️", "Explorador"),
    CORSAIR("⚔️", "Corsário"), 
    MERCHANT("💰", "Mercador");
}

@Entity 
public class CaptainSkill {
    private String name;
    private String description;
    private Integer requiredLevel;
    private CaptainPath requiredPath;
    private SkillEffect effect;
}
```

#### **Sistema de XP do Capitão**
```java
@Service
public class CaptainProgressionService {
    
    public void awardExperience(Captain captain, ExperienceSource source, int amount) {
        // Diferentes fontes de XP com multiplicadores por caminho
        // Exploradores ganham mais XP de viagens
        // Corsários ganham mais XP de combates
        // Mercadores ganham mais XP de comércio
    }
    
    public List<CaptainSkill> getAvailableSkills(Captain captain) {
        // Retorna habilidades disponíveis baseadas em nível e caminho
    }
}
```

#### **Frontend - Interface de Progressão**
```jsx
const CaptainProgressionView = () => {
    return (
        <div className="captain-progression">
            <div className="captain-level-display">
                <h2>Capitão Nível {captain.level}</h2>
                <XPBar current={captain.experience} 
                       next={calculateNextLevelXP(captain.level)} />
            </div>
            
            <div className="specialization-paths">
                {paths.map(path => (
                    <SpecializationPath 
                        path={path}
                        selected={captain.specialization === path}
                        availableSkills={getAvailableSkills(path)}
                    />
                ))}
            </div>
            
            <div className="skill-tree">
                <SkillTree captain={captain} />
            </div>
        </div>
    );
};
```

### Plano de Implementação

#### **Fase 1: Estrutura Base**
1. **Modelo de Dados**: Entidades Captain, CaptainSkill, sistema de XP
2. **Serviços Core**: CaptainProgressionService, SkillEffectService
3. **Integração**: Conectar com sistema existente de Game

#### **Fase 2: Sistema de XP**
1. **Fontes de Experiência**: Integrar com ações existentes do jogo
2. **Cálculos de Nível**: Implementar curva de progressão balanceada
3. **Persistência**: Salvar progresso do capitão no banco de dados

#### **Fase 3: Árvore de Habilidades**
1. **Definição de Skills**: Criar 15-20 habilidades únicas
2. **Efeitos de Gameplay**: Implementar modificadores de jogo
3. **Balanceamento**: Ajustar impacto das habilidades

#### **Fase 4: Interface Visual**
1. **Tela de Progressão**: Componente dedicado para evolução do capitão
2. **Skill Tree Visual**: Interface intuitiva para seleção de habilidades
3. **Indicadores**: Mostrar progresso e efeitos ativos na UI principal

### Benefícios Esperados

#### **Replayability**
- **Diferentes Builds**: Três caminhos distintos incentivam múltiplas jogatinas
- **Experimentação**: Jogadores podem testar diferentes estratégias

#### **Progressão Pessoal**
- **Investimento Emocional**: Capitão cresce junto com o jogador
- **Recompensas Tangíveis**: Habilidades alteram mecânicas de jogo

#### **Estratégia Profunda**
- **Planejamento de Build**: Decisões de longo prazo sobre especialização
- **Adaptação Tática**: Habilidades permitem diferentes abordagens para desafios

### Status:
🔄 **Em Planejamento** - Próxima grande funcionalidade a ser implementada.

**O sistema de níveis do capitão completará o ciclo de gamificação do Freedom Tide, oferecendo progressão pessoal significativa e escolhas estratégicas que alteram fundamentalmente a experiência de jogo.**

## v1.30 - Sistema Tutorial Finalizado (Concluído)

**Versão do Jogo:** 1.30  
**Foco:** Correção completa dos bugs de progressão do tutorial e otimização visual para melhor experiência do usuário.

### Problemas Resolvidos

#### **Progressão Interrompida**
- **Problema**: Tutorial ficava travado na fase JOURNEY_MECHANICS após resolver encontros marítimos
- **Solução**: Implementado sistema flexível de progressão que aceita múltiplas ações (ENCOUNTER_RESOLUTION, CONTINUE, UNDERSTOOD)
- **Resultado**: Fluxo natural entre fases independente do tipo de ação do jogador

#### **Checklist Regressivo**  
- **Problema**: Checklist de progresso perdia estado quando navio sofria dano durante viagem
- **Solução**: Sistema de checklist persistente usando campos específicos no banco (tutorialCrewCompleted, tutorialShipCompleted, tutorialSuppliesCompleted)
- **Resultado**: Progresso mantido independente de mudanças no estado do jogo

#### **Comunicação Frontend-Backend**
- **Problema**: Ações CONTINUE/UNDERSTOOD não notificavam o backend para progressão
- **Solução**: Correção no handleTutorialAction para incluir chamadas de notifyTutorialProgress
- **Resultado**: Todas as ações do tutorial sincronizam corretamente com o backend

### Melhorias Visuais Implementadas

#### **Tema de Pergaminho Antigo**
- **Background**: Gradientes radiais simulando textura de papel envelhecido
- **Header**: Gradiente diagonal metálico com efeitos de desgaste
- **Seções**: Fundos temáticos diferenciados (objetivos: creme/dourado, checklist: bege, dicas: amarelo pálido)
- **Botões**: Efeitos 3D com gradientes e sombras para aparência de botões antigos

#### **Otimização de Tamanho**
- **Largura**: Reduzida de 350px → 280px (normal) e 240px (minimizado)
- **Altura**: Limitada a 70vh com scroll automático para evitar overflow
- **Layout**: Removido sistema de horizontalização, mantido layout vertical consistente para todas as fases

#### **Tipografia Melhorada**
- **Texto Principal**: 12px → 14px com line-height otimizado (1.4 → 1.5)
- **Títulos**: Padronizados em 15px para consistência visual
- **Botões**: 13px → 14px com padding aumentado para melhor clicabilidade
- **Listas**: 12px → 13px para melhor legibilidade de textos longos

### Arquitetura Técnica

#### **Backend (Spring Boot)**
- **TutorialServiceImpl**: Lógica flexível de progressão com validação robusta
- **Game Model**: Campos persistentes para checklist independente do estado do jogo
- **GameService**: Integração completa com notificações de progresso tutorial

#### **Frontend (React)**
- **TutorialOverlay**: Componente simplificado com layout vertical unificado
- **TutorialOverlay.css**: Sistema de estilização temática completo
- **App.jsx**: Comunicação corrigida entre frontend e backend

### Impacto na Experiência do Usuário

#### **Onboarding Confiável**
- Tutorial funciona consistentemente em todos os cenários de jogo
- Progressão previsível elimina confusão para novos jogadores
- Checklist persistente fornece orientação clara

#### **Interface Polida**
- Visual profissional com tema coerente ao jogo
- Legibilidade excelente para instruções complexas
- Tamanho otimizado não interfere na jogabilidade

#### **Manutenibilidade**
- Código simplificado facilita futuras expansões
- Sistema de debug robusto para identificação rápida de problemas
- Arquitetura flexível suporta novos tipos de tutorial

### Status:
✅ **Concluído** - Sistema tutorial completamente funcional e visualmente polido.

**O tutorial agora oferece uma introdução perfeita ao Freedom Tide, guiando novos jogadores através de todas as mecânicas essenciais com interface elegante e progressão confiável.**

## v1.24 - Sistema de Gerenciamento de Tripulação Aprimorado (Concluído e Verificado)

**Versão do Jogo:** 1.24
**Foco:** Implementar sistema completo de gerenciamento de tripulação com capacidade limitada, prevenção de duplicatas, tooltips informativos e melhorias na experiência do usuário.

### Principais Funcionalidades Implementadas

#### **1. Sistema de Capacidade da Tripulação Baseado em Habilidade**
- **Skill de Liderança**: Nova habilidade do capitão que determina o tamanho máximo da tripulação
- **Capacidade Escalável**: Base de 3 tripulantes + 1 por nível de Liderança
- **Validação de Contratação**: Prevenção automática de contratação quando capacidade máxima é atingida
- **Feedback Visual**: Display claro de tripulação atual vs. máxima (ex: "2/4 tripulantes")

#### **2. Sistema de Prevenção de Contratação Duplicada**
- **Geração Determinística**: Tripulantes gerados usando seed baseada em `gameId + portId + crewSize`
- **Consistência Temporal**: Mesmos 3 personagens sempre aparecem na taverna até contratar alguém
- **Renovação Automática**: Lista de tripulantes se renova automaticamente após cada contratação
- **Eliminação de Duplicatas**: Impossível contratar o mesmo personagem duas vezes

#### **3. Sistema de Tooltips Informativos**
- **Descrições de Personalidades**: Tooltips explicativos para cada personalidade de tripulante
- **Comportamentos Detalhados**: Explicações de como cada personalidade afeta o gameplay
- **Integração Visual**: Tooltips estilizados seguindo tema náutico do jogo
- **Posicionamento Inteligente**: Sistema de posicionamento que evita overflow da tela

#### **4. Melhorias na Interface da Taverna**
- **Badges de Personalidade**: Indicadores visuais coloridos para cada personalidade
- **Informações de Capacidade**: Display sempre visível do status da tripulação
- **Mensagens Contextuais**: Feedback específico sobre limitações e possibilidades
- **Estilização Aprimorada**: CSS polido para badges, tooltips e elementos visuais

#### **5. Sistema de Personalidades Baseado em Escolhas**
- **Influência da Intro**: Personalidades dos tripulantes refletem escolha moral inicial
- **Variedade Temática**: Mix apropriado de personalidades por escolha (Cooperar, Resistir, Neutro)
- **Consistência Narrativa**: Tripulação alinhada com filosofia escolhida pelo jogador

### Implementações Técnicas

#### **Backend (Java/Spring Boot)**
- **CaptainProgressionService**: 
  - Adicionada skill LEADERSHIP com sistema de progressão
  - Método `getMaxCrewCapacity()` para cálculo dinâmico de capacidade
- **UniqueCharacterService**: 
  - Métodos `generateTavernCharactersWithSeed()` para geração consistente
  - Sistema de seed determinística baseado em estado do jogo
  - Integração com IntroChoice para personalidades temáticas
- **GameService**: 
  - Validação de capacidade em `recruitCrewMember()`
  - Cálculo de seed em `getTavernInfo()` 
  - Mensagens contextuais sobre status da tripulação

#### **Frontend (React)**
- **TavernView**: 
  - Integração de tooltips com componente reutilizável
  - Display de capacidade e status da tripulação
  - Badges visuais para personalidades
- **Tooltip Component**: 
  - Componente genérico e reutilizável
  - Sistema de posicionamento inteligente
  - Estilização temática náutica
- **CSS Aprimorado**: 
  - Estilos para badges de personalidade
  - Animações e transições suaves
  - Posicionamento responsivo de tooltips

### Melhorias na Experiência do Usuário

#### **Progressão Clara**
- Jogadores entendem como aumentar capacidade da tripulação (skill Liderança)
- Feedback imediato sobre limitações e possibilidades
- Progressão visual clara através de níveis de habilidade

#### **Prevenção de Frustração**
- Eliminação completa de contratações duplicadas
- Tripulantes sempre renovam após contratação
- Validações impedem ações impossíveis

#### **Informação Acessível**
- Tooltips explicam todas as personalidades disponíveis
- Impacto no gameplay claramente comunicado
- Interface intuitiva sem sobrecarga de informação

#### **Consistência Temática**
- Tripulação reflete escolhas narrativas do jogador
- Personalidades variadas mantêm interesse
- Sistema coerente com lore e mecânicas existentes

### Arquivos Modificados
- `CaptainProgressionService.java`: Sistema de progressão de Liderança
- `UniqueCharacterService.java`: Geração determinística com seed
- `GameService.java`: Validação de capacidade e feedback
- `TavernView.jsx`: Interface aprimorada com tooltips
- `Tooltip.jsx`: Componente reutilizável criado
- `Tooltip.css`: Estilização temática completa
- `TavernView.css`: Badges de personalidade e melhorias visuais

### Status:
✅ **Concluído e Verificado** - Sistema de gerenciamento de tripulação completamente implementado e funcional.

**O sistema agora oferece uma experiência de gerenciamento de tripulação rica e balanceada, com progressão clara, prevenção de problemas comuns, e interface polida que mantém os jogadores informados e engajados.**

## v1.24 - Reformulação Visual Completa (Concluído e Verificado)

**Versão do Jogo:** 1.24
**Foco:** Completar a transformação visual do jogo com sistema de documentos antigos unificado e otimização de contraste.

### Implementações Realizadas

**Sistema de Documentos Antigos Unificado:**
✅ **9 Componentes Modernizados**: PortView, IntroSequence (4 subcomponentes), MapView, ShipyardView, MarketView, TavernView, CaptainProgression, CrewManagementView, TutorialOverlay
✅ **Classes CSS Unificadas**: `.ancient-document`, `.nautical-chart`, `.military-order`, `.small-tag`, `.ancient-button`
✅ **Efeitos Avançados**: Bordas orgânicas com `clip-path`, texturas de pergaminho, manchas de tinta, linhas de escrita
✅ **Sistema de Animações**: Aparecer sequencial, efeitos de flutuação, transições suaves

**CaptainCompass Simplificado:**
✅ **Remoção da Imagem da Bússola**: Interface limpa focada na informação
✅ **Layout Simplificado**: Nome do capitão + status numéricos (Reputação, Infâmia, Aliança)
✅ **Integração Visual**: Uso do sistema `small-tag` para consistência

**Otimização de Contraste e Fundo:**
✅ **App-Container**: Fundo oceânico escuro com textura de madeira naval
✅ **App-Header**: Design náutico autêntico removido da mesa do capitão
✅ **Dashboard**: Mesa do capitão com madeira escura para máximo contraste
✅ **Overlay de Contraste**: Camada semi-transparente sobre backgrounds para legibilidade

**TutorialOverlay Corrigido:**
✅ **Classes de Botão**: Correção de `authentic-anchor` para `ancient-button`
✅ **Barra de Rolagem**: Estilização náutica personalizada
✅ **Animações Tutorial**: Sistema específico de aparição e transições

### Resultado Final
- **Identidade Visual Coesa**: Todos os componentes seguem o tema náutico autêntico
- **Contraste Otimizado**: Documentos antigos se destacam claramente sobre fundos escuros
- **UX Aprimorada**: Navegação mais limpa, informações organizadas, feedback visual claro
- **Performance**: Animações fluidas sem comprometer responsividade
- **Consistência**: Sistema de design unificado em toda a aplicação

### Arquivos Principais Modificados
- `ancient-documents.css`: Sistema CSS unificado
- `CaptainCompass.jsx/css`: Simplificação e modernização
- `App.css`: Fundo náutico e header otimizado
- `Dashboard.css`: Mesa escura para contraste
- `TutorialOverlay.jsx/css`: Correções e melhorias
- Todos os 9 componentes principais: Classes e estilos unificados

### Status:
✅ **Concluído e Verificado** - Reformulação visual completa implementada com sucesso.

**O jogo agora apresenta uma identidade visual náutica autêntica e coesa, com contraste otimizado, interface simplificada e experiência de usuário aprimorada em todos os aspectos.**