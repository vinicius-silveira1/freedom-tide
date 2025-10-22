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

## v1.22 - A Primeira Jornada (Concluído)

**Versão do Jogo:** 1.22
**Foco:** Implementar um tutorial orgânico integrado que ensine as mecânicas fundamentais através de uma primeira jornada épica e obrigatória.

### Status

✅ **Concluído** - Sistema de tutorial completo e identidade visual padronizada.

**Implementações Realizadas:**

**Backend Completo:**
- **Entidade `Game` Atualizada:** Campos `introChoice`, `tutorialPhase` e `tutorialCompleted` implementados
- **Enums para Tipagem Forte:** `IntroChoice` e `TutorialPhase` criados e integrados
- **DTOs Dedicados:** `TutorialStateDTO` e `TutorialChecklistDTO` funcionais
- **`TutorialService` Completo:** Lógica de progressão e personalização baseada na escolha moral implementada
- **Endpoint da API:** `GET /api/games/{id}/tutorial` funcionando perfeitamente

**Frontend Completo:**
- **`TutorialOverlay.jsx`:** Sistema de tutorial overlay totalmente funcional com progressão dinâmica
- **Integração Perfeita:** Tutorial conectado ao backend, progredindo automaticamente baseado nas ações do jogador
- **Checklist Visual:** Sistema de objetivos com checkmarks funcionando na mesa do capitão
- **Highlights Contextuais:** Destaque de elementos ativos durante diferentes fases do tutorial

**Melhorias de Design:**
- **Identidade Visual Unificada:** Todos os componentes (PortView, LocationStatus, Tutorial) agora seguem o mesmo sistema de cores e estilo
- **Tutorial Integrado:** Design náutico consistente com pergaminho envelhecido
- **Responsividade:** LocationStatus compacto durante encontros no mar
- **Consistência:** Sombras, bordas, cores e fontes padronizadas em toda a aplicação

### Análise do Problema

**Gap Educacional Crítico:**
Após a sequência introdutória, o jogador possui contexto narrativo mas nenhum conhecimento prático. A Mesa do Capitão apresenta 5 opções simultâneas (Taverna, Estaleiro, Mercado, Contratos, Viajar) sem orientação sobre prioridades ou consequências.

**Consequências do Gap:**
- Alta taxa de abandono por confusão
- Frustração ao tentar navegar sem tripulação
- Experiência inicial negativa que contradiz a narrativa cuidadosa da intro
- Perda do momentum emocional criado pela sequência introdutória

### Solução: Tutorial Narrativo Integrado

**Conceito Central: "Primeira Missão Épica"**
Em vez de tutorial tradicional com popups, criar uma jornada obrigatória que ensina naturalmente através da progressão narrativa. O jogador não percebe que está em tutorial - apenas vive sua primeira aventura como capitão.

### Plano de Implementação Detalhado

#### **Fase 1: Preparação Obrigatória (No Porto)**

**1.1 - Bloqueio Inteligente da Mesa do Capitão:**
- Após a intro, apenas a Taverna está "destacada" visualmente
- Outras opções ficam "esmaecidas" com tooltip: "Primeiro você precisa de uma tripulação"
- Implementar componente `TutorialHighlight` que destaca elementos ativos

**1.2 - Sequência de Contratação Guiada:**
- Na Taverna: automaticamente mostrar apenas 3 candidatos pré-selecionados
- Cada candidato representa uma função essencial: Navegador, Carpinteiro, Artilheiro
- Preços reduzidos (50% do normal) para garantir que o jogador possa contratar
- Após contratar, desbloquear próxima etapa

**1.3 - Preparação do Navio:**
- Estaleiro: highlight no botão de reparo + tooltip educativo
- Mercado: destaque em suprimentos básicos (comida, rum)
- Sistema de "checklist visual" na Mesa do Capitão mostrando progresso:
  ```
  ✓ Tripulação: 3/3 contratados
  ✓ Casco: Reparado (100/100)  
  ⏳ Suprimentos: 80/100 comida, 50/100 rum
  ⏳ Destino: Não selecionado
  ```

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
1. **Balanceamento de Economia**: Revisar preços de mercado, custos de reparo e recompensas de contratos
2. **Polimento de Combat**: Melhorar feedback visual e balanceamento do sistema de combate naval
3. **Performance**: Otimizar carregamento e transições entre telas

### **P2 - Funcionalidades (Média Prioridade)**  
1. **Sistema de Save/Load**: Permitir múltiplos saves e carregamento de jogos
2. **Eventos Dinâmicos**: Expandir variedade de encontros no mar e eventos de porto
3. **Progressão de Tripulação**: Sistema de XP e evolução dos tripulantes

### **P3 - Expansão (Baixa Prioridade)**
1. **Novos Portos**: Adicionar mais localizações com características únicas
2. **Facções Avançadas**: Sistema de diplomacia mais profundo
3. **Campanhas**: Múltiplas histórias além da jornada tutorial

### **Próximos Passos Imediatos:**
- Testes de gameplay completos
- Feedback de usuários sobre tutorial
- Identificação de bugs e pontos de fricção
- Documentação técnica para futuras expansões
1. **MVP**: Implementar Fase 1 com tutorial genérico funcional
2. **Personalização**: Adicionar sistema de caminhos diferenciados
3. **Refinamento**: Balancear narrativas e recompensas entre caminhos  
4. **Polimento**: Testes extensivos e ajustes finos

**Prioridade**: Alta - Essencial para retenção e engajamento pós-introdução.



