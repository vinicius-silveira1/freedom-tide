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
- **Mudanças**:
    1.  A entidade `ShipUpgrade` agora tem um campo `portType` (anulável para itens comuns).
    2.  O `DataSeeder` foi atualizado para atribuir `PortType`s às melhorias, incluindo itens específicos de facção.
    3.  A lógica em `GameService.getShipyardInfo` foi modificada para filtrar as melhorias disponíveis com base no `PortType` do porto atual.
- **Status**: **Concluído e Verificado.**

---

## Próxima Tarefa: v1.6 - Tornando os Portos Vivos

### Fase 4: Ação de Porto "Ir ao Mercado"

- **[Próximo Passo Lógico]**: Implementar a ação de porto "Ir ao Mercado".
- **[Justificativa (Lição de Arquitetura/Design)]**: Atualmente, o jogador não tem como gerenciar ativamente seus recursos (comida, rum, etc.), que são consumidos a cada turno. A introdução de um mercado em cada porto, com preços variados, cria um verdadeiro loop econômico. Isso dá ao jogador agência sobre sua logística e abre as portas para o pilar de gameplay de "Comerciante", permitindo a compra de suprimentos em portos onde são baratos e a venda onde são caros.
- **[Opções ou Considerações Criativas]**:
    1.  **Preços Dinâmicos**: Os preços dos recursos podem variar com base no `PortType`. Portos `IMPERIAL` podem ter comida barata, enquanto portos `PIRATE` podem ter rum barato. Isso incentiva ainda mais a viagem e o planejamento.
    2.  **Modelagem**: Podemos adicionar campos como `foodPrice`, `rumPrice` na entidade `Port` e populá-los no `DataSeeder`. É uma abordagem simples e eficaz para começar.
    3.  **API**: Criar um endpoint `GET /api/games/{gameId}/port/market` que retorna os bens disponíveis e seus preços, e endpoints `POST /api/games/{gameId}/port/market/buy` e `POST /api/games/{gameId}/port/market/sell` para as transações.
- **Status Atual**: **Aguardando aprovação.**