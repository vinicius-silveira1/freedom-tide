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

### Fase 3: Interatividade da UI - Ações do Porto

- **[Próximo Passo Lógico]**: Adicionar interatividade à UI, começando por exibir as ações disponíveis no porto.
- **[Justificativa (Lição de Arquitetura/Design)]**: Uma UI estática não é um jogo. Precisamos permitir que o jogador interaja com o mundo. O primeiro passo é buscar e exibir as ações contextuais (Ações do Porto) como elementos clicáveis (botões). Isso transforma a UI de um simples painel de status em um hub de interação, introduzindo o conceito de estado que pode ser modificado pelas ações do jogador.
- **[Opções ou Considerações Criativas]**:
    1.  **Componente de Ações**: Criar um novo componente, `PortActions.jsx`, responsável por buscar e exibir as ações da API (`/api/games/{gameId}/port/actions`).
    2.  **Renderização Condicional**: O `App.jsx` só deve renderizar o componente `PortActions` se o jogador estiver de fato em um porto.
    3.  **Exibição**: As ações devem ser renderizadas como botões. Nesta fase, os botões ainda não terão funcionalidade de clique, apenas serão exibidos dinamicamente com base na resposta da API.
- **Status Atual**: **Aguardando aprovação.**