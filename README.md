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

## ✨ Funcionalidades Implementadas

*   **Criação e Gerenciamento de Jogo:** API para iniciar e consultar o estado do jogo.
*   **Mundo Interativo:** Sistema de viagem entre portos com possibilidade de encontros aleatórios no mar.
*   **Ações no Porto:**
    *   **Estaleiro:** Reparo de navio e compra de melhorias de status. O inventário de melhorias varia com a facção do porto.
    *   **Mercado:** Compra e venda de recursos (comida, rum, etc.) com preços que variam conforme a facção.
    *   **Taverna:** Geração procedural de tripulantes para recrutamento, com atributos e personalidades influenciados pela facção do porto.
*   **Combate Naval:** Lógica para encontros com outros navios, permitindo ações como atacar, abordar ou fugir.
*   **Sistema de Moral:** Consumo de recursos (comida, rum) e pagamento de salários que afetam a moral da tripulação.
*   **Frontend Básico:** Interface inicial em React que se conecta ao backend, cria um jogo e exibe o estado atual.