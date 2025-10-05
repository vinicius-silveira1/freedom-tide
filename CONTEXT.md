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

## v1.1: Moral Dinâmica e Consequências (Descontentamento e Insubordinação)

**Funcionalidade:** O sistema de moral foi expandido para ser mais dinâmico e impactante, alinhando-se melhor ao GDD.

1.  **Cálculo Dinâmico da Moral:** A moral inicial de um tripulante não é mais fixa. Agora é calculada com base em sua **Personalidade** e **Nível de Desespero**, tornando a decisão de recrutamento mais estratégica.
    - `moral = 50 (base) + (modificador_personalidade * 5) - (desespero * 2)`

2.  **Consequência de Descontentamento (Moral < 50):** Ao concluir um contrato ou evento, há uma chance de a tripulação descontente causar um "acidente", resultando em uma **penalidade percentual sobre os ganhos de ouro**.
    - *Narrativa:* `"MORAL BAIXA: Tripulantes descontentes causaram problemas! Um pequeno \'acidente\' resultou na perda de X de ouro."`

3.  **Consequência de Insubordinação (Moral < 30):** Se a moral cair ainda mais, o risco aumenta. Há uma chance de a tripulação se tornar insubordinada, resultando em uma **perda percentual de recursos vitais** (comida, rum ou peças de reparo).
    - *Narrativa:* `"INSUBORDINAÇÃO: A negligência tomou conta! X porções de comida estragaram."`

**Verificação:**
- Recrutamos tripulantes com personalidades e níveis de desespero que resultaram em uma moral média de 20.
- Ao resolver um contrato, o sistema primeiro testou a chance de "Insubordinação". Como ela falhou (devido à aleatoriedade), o sistema corretamente procedeu para testar a chance de "Descontentamento", que foi acionada, aplicando a penalidade de ouro e registrando o evento no log.
