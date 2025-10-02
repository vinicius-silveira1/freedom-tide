# Freedom Tide - Backend API

> No comando de um navio na traiçoeira Era da Vela, o jogador deve navegar entre a honra de um comerciante, a infâmia de um pirata ou a esperança de um revolucionário. Gerenciando tripulação, navio e um mundo marcado pela opressão de um sistema capitalista explorador, cada escolha molda seu legado e determina seu destino: fortuna, medo ou liberdade.

---

## 📖 Sobre o Projeto

Este repositório contém o código-fonte para a **API de back-end** do jogo **Freedom Tide**. Ele é responsável por gerenciar toda a lógica de negócio, estado do jogo, persistência de dados e regras definidas no Game Design Document (GDD).

O conceito central do jogo é um RPG de gerenciamento que explora temas de liberdade, opressão e desigualdade em um mundo de fantasia "low-poly" com estética pixel art. O jogador é forçado a tomar decisões difíceis dentro de um sistema inerentemente injusto, onde o lucro muitas vezes anda de mãos dadas com a exploração.

## 🛠️ Stack Tecnológica

*   **Java 17**
*   **Spring Boot 3**
*   **Spring Data JPA**
*   **PostgreSQL** (Banco de Dados)
*   **Maven** (Gerenciador de Dependências)

## 🚀 Começando

Para compilar e executar este projeto localmente, você precisará de:

*   JDK 17 (ou superior)
*   Maven
*   Uma instância do PostgreSQL em execução

**Passos para instalação:**

1.  Clone o repositório:
    ```sh
    git clone <URL_DO_SEU_REPOSITORIO>
    ```
2.  Configure o banco de dados:
    *   Copie o arquivo `application.properties` (ou crie um `application-local.properties`).
    *   Altere as propriedades `spring.datasource.url`, `spring.datasource.username`, e `spring.datasource.password` para corresponder à sua configuração do PostgreSQL.
3.  Compile o projeto:
    ```sh
    ./mvnw clean install
    ```
4.  Execute a aplicação:
    ```sh
    ./mvnw spring-boot:run
    ```

## 🗺️ Roadmap

*   [x] Estrutura inicial do projeto Spring Boot
*   [x] Modelagem de Dados Incial (`Game`, `Ship`, `CrewMember`)
*   [x] Refatoração do modelo e adição de recursos do navio
*   [x] Criação do `README.md`
*   [ ] **Próximo Passo:** Criação dos Repositórios JPA
*   [ ] Camada de Serviço e Endpoints da API
*   [ ] Implementação da lógica da "Bússola do Capitão"
*   [ ] Implementação da mecânica de "Moral da Tripulação"

