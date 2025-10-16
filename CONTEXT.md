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

- **Descrição**: Alinhada a ação "Fugir" com o pilar de design "Consequência".
- **Nota de Manutenção**: Durante testes subsequentes, foi identificado e corrigido um bug onde a dificuldade de fuga era excessivamente alta. A fórmula foi rebalanceada para ser mais justa.

### Fase 2: Resolução de Encontros (Investigar) (Concluído)

- **Descrição**: Implementada a funcionalidade da ação "Investigar" para o encontro `MYSTERIOUS_WRECK`, adicionando uma camada de exploração e recompensa ao jogo.
- **Status**: **Concluído e Verificado.**

---

## v1.5: Combate Naval (Concluído)

### Fase 1: Resolução de Encontros (Atacar)

- **Descrição**: Implementado um sistema de combate naval simplificado para a ação "Atacar", com trocas de dano baseadas em atributos.
- **Status**: **Concluído e Verificado.**

### Fase 2: Resolução de Encontros (Abordar)

- **Descrição**: Adicionada a ação "Abordar" como uma alternativa tática ao combate. A abordagem é um evento único e decisivo, baseado na habilidade de `Combate` da tripulação.
- **Mudanças**:
    1.  **Escolha Tática**: O jogador agora pode escolher entre atacar à distância ou arriscar uma abordagem para capturar o navio.
    2.  **Risco vs. Recompensa**: A abordagem bem-sucedida rende recompensas maiores, mas a falha resulta em dano severo e perda de moral, criando uma decisão de jogo interessante.
    3.  **Lógica de Resolução**: O método `boardEncounter` foi implementado com uma rolagem de dados contestada para determinar o resultado.
- **Status**: **Concluído e Verificado.** Cenários de sucesso (com tripulação forte) e falha (contra um inimigo superior) funcionam como esperado.

---

## Próxima Tarefa: v1.6 - Tornando os Portos Vivos

### Fase 1: Ação de Porto "Ir ao Estaleiro"

- **[Próximo Passo Lógico]**: Implementar a Ação de Porto "Ir ao Estaleiro".
- **[Justificativa (Lição de Arquitetura/Design)]**: Nosso loop no mar está robusto, mas os portos ainda são apenas pontos de transição. Para dar profundidade ao pilar de "Customização" e criar um ciclo de recompensa significativo, precisamos de uma forma para o jogador gastar o ouro e os recursos que acumula. Implementar um "Estaleiro" nos portos é o passo fundamental. Isso introduz uma nova camada de gameplay (gerenciamento e upgrade do navio) e dá um propósito maior para as atividades de risco no mar. Arquitetonicamente, isso significa adicionar uma nova ação de porto e, crucialmente, um novo conjunto de endpoints para listar e comprar melhorias para o navio, conectando a economia do jogo com a progressão do jogador.
- **[Opções ou Considerações Criativas]**:
    1.  **Reparo do Casco (Fase 1)**: A primeira e mais óbvia funcionalidade do estaleiro é permitir que o jogador pague para reparar o `hullIntegrity` do navio. Isso cria uma mecânica de "money sink" e torna o dano sofrido em combate mais significativo.
    2.  **Melhorias de Atributos (Fase 2 - Futuro)**: O estaleiro pode oferecer a compra de melhorias permanentes, como "Canhões de Qualidade Superior" (+5% de dano de artilharia) ou "Velas Reforçadas" (+5 de bônus em testes de fuga). Isso cria um sistema de progressão de longo prazo para o navio.
    3.  **Disponibilidade por Porto**: Nem todos os estaleiros precisam ser iguais. Portos da Marinha Imperial podem ter as melhores melhorias de canhão, enquanto Portos Livres podem se especializar em porões de contrabando ou melhorias de velocidade, incentivando o jogador a explorar o mundo.
- **Status Atual**: **Aguardando Aprovação.**
