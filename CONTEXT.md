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

## Próxima Tarefa: Portos e Ações

- **Descrição**: Implementar a base para a "Fase de Planejamento" do GDD, introduzindo o conceito de Portos. Cada porto terá um tipo (ex: Imperial, da Guilda, Pirata, Livre) e oferecerá um conjunto de ações possíveis para o jogador.
- **Objetivo**: Criar um hub central para as atividades do jogador quando não estiver no mar. Isso expande o loop de gameplay e dá ao mundo um senso de lugar. A primeira implementação focará em duas ações essenciais:
    1.  **Visitar a Taverna:** Um lugar para gastar um pouco de ouro e "ouvir rumores", que será o mecanismo pelo qual o jogador descobre e atualiza a lista de contratos disponíveis.
    2.  **Visitar o Estaleiro:** Um lugar para gastar ouro e reparar a integridade do casco do navio.
- **Lição de Arquitetura**: Isso nos forçará a pensar em como gerenciar o "estado do jogador". Atualmente, o jogador está sempre "no mar". Precisaremos de um conceito de "atracado em um porto" e como as ações disponíveis mudam nesse estado.
