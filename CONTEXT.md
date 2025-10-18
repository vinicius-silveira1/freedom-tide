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

### Fase 2: Componentização da UI e Exibição de Status

- **[Próximo Passo Lógico]**: Transformar a exibição de dados brutos (JSON) em uma interface de usuário estruturada e legível.
- **[Justificativa (Lição de Arquitetura/Design)]**: Exibir o JSON foi ótimo para provar a conexão, mas não é uma UI. O próximo passo é aplicar o princípio fundamental do React: **dividir a interface em componentes reutilizáveis e de responsabilidade única**. Em vez de um bloco de texto, teremos painéis de informação distintos, como `StatusDoNavio`, `BussolaDoCapitao`, `InformacoesDoPorto`, etc. Isso não apenas organiza o código, tornando-o mais fácil de manter, mas também estabelece as fundações visuais do jogo.
- **[Opções ou Considerações Criativas]**:
    1.  **Estrutura de Componentes**: Criar uma nova pasta `src/components` e dentro dela, arquivos para cada novo componente (ex: `ShipStatus.jsx`, `CaptainCompass.jsx`).
    2.  **Passando Propriedades (Props)**: O componente `App.jsx` continuará responsável por buscar o estado do jogo, mas em vez de exibir o JSON, ele passará as partes relevantes do objeto `game` como *props* para os componentes filhos. Por exemplo: `<ShipStatus ship={game.ship} />`.
    3.  **Estilização Básica**: Podemos usar CSS simples para adicionar bordas e espaçamento aos componentes, criando uma distinção visual clara entre os diferentes painéis de informação na tela.
- **Status Atual**: **Aguardando aprovação.**