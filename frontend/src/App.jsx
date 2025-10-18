import { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [game, setGame] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Função para criar um novo jogo e buscar seu estado
    const createAndFetchGame = async () => {
      try {
        // 1. Criar um novo jogo
        const createResponse = await fetch('/api/games', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (!createResponse.ok) {
          throw new Error(`HTTP error! status: ${createResponse.status}`);
        }

        const newGame = await createResponse.json();
        
        // 2. Buscar o estado completo do jogo recém-criado
        const gameId = newGame.id;
        const fetchResponse = await fetch(`/api/games/${gameId}`);

        if (!fetchResponse.ok) {
          throw new Error(`HTTP error! status: ${fetchResponse.status}`);
        }

        const gameData = await fetchResponse.json();
        setGame(gameData);

      } catch (e) {
        setError(e.message);
        console.error("Erro ao criar ou buscar o jogo:", e);
      }
    };

    createAndFetchGame();
  }, []); // O array vazio [] garante que este efeito rode apenas uma vez, quando o componente montar

  return (
    <div className="App">
      <header className="App-header">
        <h1>Freedom Tide - Status do Jogo</h1>
      </header>
      <main>
        {error && (
          <div>
            <h2>Ocorreu um erro:</h2>
            <pre style={{ color: 'red' }}>{error}</pre>
          </div>
        )}
        {game ? (
          <div>
            <h2>Dados do Jogo (ID: {game.id})</h2>
            <pre>{JSON.stringify(game, null, 2)}</pre>
          </div>
        ) : (
          <p>Criando e carregando o jogo...</p>
        )}
      </main>
    </div>
  );
}

export default App;