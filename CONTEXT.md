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

## v1.2: Ciclo de Fim de Turno (Consumo e Salários)

**Funcionalidade:** Um ciclo de fim de turno foi integrado ao final da resolução de contratos e eventos, aprofundando o gerenciamento de recursos e tripulação.

1.  **Cálculo de Salário:** Ao ser recrutado, cada tripulante agora tem um salário calculado dinamicamente com base em suas habilidades e nível de desespero.
2.  **Consumo de Suprimentos:** Ao final de um ciclo, a tripulação consome uma quantidade fixa de comida e rum.
3.  **Pagamento de Salários:** O total dos salários da tripulação é deduzido do ouro do jogador.
4.  **Penalidades de Moral:** Se não houver comida, rum ou ouro suficiente para os pagamentos, a moral da tripulação é severamente penalizada, com mensagens narrativas claras no log de eventos.

**Verificação:**
- O cenário de sucesso foi verificado. Após resolver um contrato, os recursos (comida, rum, ouro) foram deduzidos corretamente, e o `eventLog` narrou todos os acontecimentos com precisão.
- A verificação dos cenários de falha foi adiada por pragmatismo, devido à dificuldade de manipular o estado do jogo para forçar a falha sem ferramentas de depuração.

---

## v1.3: Fundações do Mundo Interativo (Concluído)

- **Fase 1: Modelagem de Portos**: Introduzido o conceito de Portos.
- **Fase 2: API de Viagem e Estado do Porto**: Permitido ao jogador se mover pelo mundo.
- **Fase 3: Contratos por Porto**: Contratos passaram a ser específicos de cada porto.
- **Fase 4: Ações de Porto**: Endpoint de descoberta para ações possíveis em um porto.
- **Fase 5: Encontros no Mar (Fundação)**: Viagem passou a gerar encontros em alto mar.
- **Fase 6: Ações de Encontro no Mar**: Endpoint de descoberta para ações possíveis em um encontro.
- **Fase 7: Resolução de Encontros (Fugir)**: Fechado o primeiro loop de gameplay (Viajar -> Encontro -> Fugir -> Chegar).

---

## v1.4: Aprofundando Consequências

### Fase 1: Fuga Baseada em Habilidade (Concluído)

- **Descrição**: Alinhada a ação "Fugir" com o pilar de design "Consequência". A fuga agora é um teste de habilidade (Navegação da tripulação) contra uma dificuldade aleatória.
- **Mudanças**:
    1.  **`GameService.fleeEncounter`**: A lógica foi refatorada para incluir o teste de habilidade.
    2.  **Penalidade de Falha**: Falhar na fuga agora causa dano ao casco do navio e mantém o jogador no encontro.
    3.  **Feedback Melhorado**: O log de eventos informa o jogador sobre o resultado do teste, sua habilidade e a dificuldade, tornando a mecânica transparente.
- **Status**: **Concluído e Verificado.**
- **Nota de Manutenção**: Durante testes subsequentes, foi identificado e corrigido um bug onde a dificuldade de fuga era excessivamente alta. A fórmula foi rebalanceada para ser mais justa.

### Fase 2: Resolução de Encontros (Investigar) (Concluído)

- **Descrição**: Implementada a funcionalidade da ação "Investigar" para o encontro `MYSTERIOUS_WRECK`, adicionando uma camada de exploração e recompensa ao jogo.
- **Mudanças**:
    1.  **Endpoint Adicionado**: `POST /api/games/{gameId}/encounter/investigate` foi criado no `GameController`.
    2.  **Lógica de Serviço**: O método `investigateEncounter` foi implementado no `GameService`.
    3.  **Risco vs. Recompensa**: A investigação agora inclui um teste de habilidade (Inteligência da tripulação) contra uma dificuldade. O sucesso rende mais recursos (ouro, peças de reparo), enquanto a falha rende menos. Há também uma pequena chance de dano ao casco, adicionando um elemento de risco.
    4.  **Conclusão do Loop**: Após a investigação, o encontro é concluído e o jogador chega ao seu destino, finalizando o loop de viagem.
- **Status**: **Concluído e Verificado.** Cenários de sucesso (com tripulação inteligente) e falha (com tripulação não inteligente) funcionam como esperado.

---

