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

### Fase 5: Ação de Porto "Ir à Taverna"

- **[Próximo Passo Lógico]**: Implementar a ação de porto "Ir à Taverna".
- **[Justificativa (Lição de Arquitetura/Design)]**: Atualmente, o recrutamento de tripulantes é feito por uma chamada de API direta, que não está integrada à experiência do jogo. A Taverna servirá como o hub narrativo e mecânico para o recrutamento. Isso conecta o sistema de tripulação ao loop de gameplay no porto, tornando a experiência mais imersiva e diegética. Em vez de apenas "adicionar um tripulante", o jogador "encontra e recruta alguém na taverna".
- **[Opções ou Considerações Criativas]**:
    1.  **Tripulantes Gerados Procedimentalmente**: Ao entrar na taverna, o sistema pode gerar uma lista de 2 a 3 tripulantes disponíveis para recrutamento, com atributos, personalidades e níveis de desespero variados. Isso torna cada visita ao porto única.
    2.  **Influência da Facção**: O tipo de tripulante encontrado pode variar com o `PortType`. Tavernas em portos Piratas teriam mais personalidades "Sedentas por Sangue", enquanto portos do Império teriam mais tripulantes "Honestos".
    3.  **API**: Criar um endpoint `GET /api/games/{gameId}/port/tavern` que retorna a lista de recrutas disponíveis. O endpoint de recrutamento (`POST /api/games/{gameId}/ship/crew`) pode ser reutilizado, mas agora o front-end o chamaria com os dados de um recruta específico da taverna.
- **Status Atual**: **Aguardando aprovação.**