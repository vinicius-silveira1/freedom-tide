# Freedom Tide

> No comando de um navio na traiçoeira Era da Vela, o jogador deve navegar entre a honra de um comerciante, a infâmia de um pirata ou a esperança de um revolucionário. Gerenciando tripulação, navio e um mundo marcado pela opressão de um sistema capitalista explorador, cada escolha molda seu legado e determina seu destino: fortuna, medo ou liberdade.

---

## 📖 Sobre o Projeto

Este repositório contém o código-fonte para o jogo **Freedom Tide**. O projeto é dividido em duas partes principais:

1.  **Backend (API RESTful):** Construído com Java e Spring Boot, responsável por gerenciar toda a lógica de negócio, estado do jogo, persistência de dados e regras definidas no Game Design Document (GDD).
2.  **Frontend (UI):** Construído com React e Vite, responsável por fornecer a interface de usuário para que o jogador possa interagir com os sistemas do jogo.

## 🛠️ Stack Tecnológica

### Backend
*   Java 17
*   Spring Boot 3
*   Spring Data JPA
*   PostgreSQL
*   Maven

### Frontend
*   React
*   Vite
*   JavaScript

## 📂 Estrutura do Projeto

O projeto é organizado em duas pastas principais na raiz:

*   `/` (raiz): Contém todo o código-fonte do backend Java/Spring Boot.
*   `/frontend`: Contém todo o código-fonte do frontend React.

## 🚀 Começando

Para compilar e executar este projeto localmente, você precisará de:

*   JDK 17 (ou superior)
*   Maven
*   Node.js (que inclui o npm)
*   Uma instância do PostgreSQL em execução

### 1. Backend (Servidor Spring Boot)

Execute estes passos a partir do diretório raiz do projeto (`/freedom-tide`).

1.  **Configure o banco de dados:**
    *   No arquivo `src/main/resources/application.properties`, altere as propriedades `spring.datasource.url`, `spring.datasource.username`, e `spring.datasource.password` para corresponder à sua configuração do PostgreSQL.

2.  **Execute a aplicação:**
    ```sh
    # O DataSeeder populará o banco de dados na primeira execução
    ./mvnw spring-boot:run
    ```
    O servidor backend estará em execução em `http://localhost:8090`.

### 2. Frontend (Servidor Vite)

Execute estes passos em um **novo terminal**, a partir do diretório raiz do projeto (`/freedom-tide`).

1.  **Navegue até a pasta do frontend:**
    ```sh
    cd frontend
    ```

2.  **Instale as dependências (apenas na primeira vez):**
    ```sh
    npm install
    ```

3.  **Execute o servidor de desenvolvimento:**
    ```sh
    npm run dev
    ```
    A interface do jogo estará acessível em `http://localhost:5173` (ou outra porta indicada no terminal). O Vite já está configurado com um proxy para se comunicar com o backend.

## 🎨 Assets e Recursos Visuais

O jogo utiliza um sistema de ícones pixel art para manter a identidade visual náutica/pirata. Os assets estão organizados em:

*   `/frontend/public/assets/icons/professions/` - Ícones das 10 profissões da tripulação
*   `/frontend/public/assets/icons/game-over/` - Ícones da tela de game over (caveira, bandeira pirata, vela, gancho)
*   `/frontend/public/assets/icons/stats/` - Ícones para estatísticas (ouro, reputação, infâmia, etc.)

### Especificações dos Ícones
*   **Formato:** PNG com transparência
*   **Tamanho:** 20x20px (interface), 32x32px (títulos), 80x80px (ícones principais)
*   **Estilo:** Pixel art náutico/pirata
*   **Renderização:** Pixel-perfect (crisp-edges)

## ✨ Funcionalidades Implementadas

### 🎮 **Sistema de Jogo Principal**
*   **Criação e Gerenciamento de Jogo:** API completa para iniciar, salvar e consultar o estado do jogo
*   **Mundo Interativo:** Sistema de viagem entre portos com encontros aleatórios no mar
*   **Combate Naval:** Batalhas táticas com outros navios (atacar, abordar, fugir)
*   **Sistema de Moral:** Consumo de recursos e pagamento de salários afetando a tripulação

### ⚓ **Ações nos Portos**
*   **Estaleiro:** Reparo de navio e compra de melhorias com inventário baseado na facção
*   **Mercado:** Compra e venda de recursos com preços dinâmicos por facção
*   **Taverna:** Recrutamento de tripulação com personagens únicos e temáticos
*   **Contratos:** Sistema de missões com recompensas e consequências

### 👥 **Sistema de Progressão da Tripulação (v1.28)**
*   **9 Profissões Especializadas:** Navegador, Artilheiro, Combatente, Médico, Carpinteiro, Estrategista + 3 híbridas
*   **Sistema de Ranks:** 5 níveis de progressão por profissão com habilidades únicas
*   **XP Automático:** Ganho de experiência em todas as ações (combate +20, navegação +15, reparos +15, contratos +25)
*   **28 Personagens Únicos:** Caracteres temáticos organizados por tipo de porto com backgrounds completos

### 🖥️ **Interface do Usuário**
*   **Painel de Gerenciamento da Tripulação:** Interface completa com barras de XP, estatísticas e progressão
*   **Ícones Pixel Art:** Sistema visual coeso com ícones temáticos para profissões e interface
*   **Tela de Game Over:** Interface dramática com elementos visuais pixel art
*   **Interface Responsiva:** Design adaptável para diferentes tamanhos de tela

### 🎨 **Sistema Visual**
*   **Ícones das Profissões:** 10 ícones pixel art únicos para cada especialização
*   **Fallback Inteligente:** Sistema que reverte para emojis se ícones não carregarem
*   **Tema Náutico/Pirata:** Visual coerente em toda a aplicação

## 📋 Versão Atual: v1.28 - "Gamificação da Tripulação"

### Principais Atualizações:
*   ✅ Sistema completo de progressão da tripulação
*   ✅ 9 profissões especializadas com ranks e habilidades
*   ✅ 28 personagens únicos organizados por porto
*   ✅ Interface de gerenciamento de tripulação com barras de XP
*   ✅ Sistema visual com ícones pixel art
*   ✅ XP automático integrado em todas as ações do jogo

## 🔜 Próximos Passos

*   **Habilidades Ativas:** Implementar habilidades especiais por rank
*   **Tutorial Interativo:** Guia para novos jogadores
*   **Sistema de Conquistas:** Marcos e recompensas por progresso
*   **Eventos Dinâmicos:** Eventos aleatórios baseados na composição da tripulação
*   **Personalização Visual:** Temas e skins para interface