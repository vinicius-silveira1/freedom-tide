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

### Fase 1: Estrutura do Frontend com React + Vite

- **[Próximo Passo Lógico]**: Iniciar a construção da interface de usuário (UI).
- **[Justificativa (Lição de Arquitetura/Design)]**: Até agora, construímos uma base sólida de backend e a validamos via `curl`. No entanto, para que o jogo ganhe vida, precisamos de uma forma de visualizar e interagir com esses sistemas de maneira intuitiva. Iniciar um projeto de frontend é o passo essencial para transformar nossa API em uma experiência de jogador. A escolha de **React com Vite** nos dá um ambiente de desenvolvimento extremamente rápido e moderno, ideal para prototipagem ágil. O objetivo não é a perfeição, mas criar uma "ponte" funcional entre o jogador e o backend.
- **[Opções ou Considerações Criativas]**:
    1.  **Estrutura de Pastas**: Podemos criar um novo diretório `frontend` na raiz do projeto para manter o código da UI separado e organizado.
    2.  **Primeiro Componente**: O primeiro objetivo seria criar um componente principal (`App.jsx`) que busca e exibe o estado geral do jogo (o `GameStatusResponseDTO`), provando que a conexão entre frontend e backend está funcionando.
    3.  **Proxy de API**: Para evitar problemas de CORS (`Cross-Origin Resource Sharing`) durante o desenvolvimento, configuraremos o Vite para usar um proxy, redirecionando as chamadas de API do frontend (ex: `/api/games`) para o nosso backend Spring Boot rodando em `localhost:8090`.
- **Status Atual**: **Aguardando aprovação.**