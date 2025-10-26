import React, { useState, useEffect, useCallback } from 'react';
import './MarketView.css';
import '../styles/ancient-documents.css';

const ResourceRow = ({ resourceName, price, shipQuantity, onTrade }) => {
  const [amount, setAmount] = useState('');

  const handleAmountChange = (e) => {
    const value = e.target.value;
    // Permitir campo vazio ou números válidos
    if (value === '' || (parseInt(value, 10) > 0 && !isNaN(parseInt(value, 10)))) {
      setAmount(value);
    }
  };

  const handleFocus = (e) => {
    // Limpar o campo quando focar se estiver vazio
    if (amount === '') {
      setAmount('');
    }
  };

  const handleBlur = (e) => {
    // Se o campo estiver vazio quando perder foco, definir como 1
    if (amount === '' || parseInt(amount, 10) < 1) {
      setAmount('1');
    }
  };

  const getAmountValue = () => {
    // Retorna o valor atual ou 1 se estiver vazio
    const numValue = parseInt(amount, 10);
    return isNaN(numValue) || numValue < 1 ? 1 : numValue;
  };

  return (
    <div className="resource-row small-tag">
      <span className="resource-name">{resourceName}</span>
      <span className="ship-quantity">A Bordo: {shipQuantity}</span>
      <span className="market-price">Preço: {price} Ouro</span>
      <div className="trade-actions">
        <input
          type="number"
          min="1"
          value={amount}
          onChange={handleAmountChange}
          onFocus={handleFocus}
          onBlur={handleBlur}
          placeholder="1"
          className="quantity-input"
        />
        <button onClick={() => onTrade('buy', resourceName, getAmountValue())} className="action-button buy-button authentic-anchor">Comprar</button>
        <button onClick={() => onTrade('sell', resourceName, getAmountValue())} className="action-button sell-button authentic-anchor">Vender</button>
      </div>
    </div>
  );
};

function MarketView({ gameId, onBuy, onSell, onBack }) {
  const [market, setMarket] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchMarketData = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/games/${gameId}/port/market`);
      if (!response.ok) {
        throw new Error(`Erro ao buscar dados do mercado: ${response.status}`);
      }
      const data = await response.json();
      setMarket(data);
      setError(null);
    } catch (e) {
      setError(e.message);
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [gameId]);

  useEffect(() => {
    fetchMarketData();
  }, [fetchMarketData]);

  const handleTrade = async (tradeType, resource, quantity) => {
    const tradeRequest = { item: resource.toUpperCase(), quantity };
    if (tradeType === 'buy') {
      await onBuy(tradeRequest);
    } else {
      await onSell(tradeRequest);
    }
    fetchMarketData(); // Re-fetch para feedback imediato
  };

  return (
    <div className="market-view ancient-document">
      <div className="market-header">
        <div className="market-title">
          <div className="market-decoration ancient-scales"></div>
          <h2>Mercado Portuário</h2>
          <div className="market-decoration ancient-coins"></div>
        </div>
        <button onClick={onBack} className="action-button back-button authentic-anchor">Voltar ao Porto</button>
      </div>

      {loading && <p>Avaliando os preços das mercadorias...</p>}
      {error && <p className="error-message">{error}</p>}

      {market && (
        <div className="market-content">
            <div className="player-gold small-tag">Seu Ouro: {market.shipGold}</div>
            <div className="resources-list">
                <ResourceRow resourceName="Food" price={market.foodPrice} shipQuantity={market.shipFood} onTrade={handleTrade} />
                <ResourceRow resourceName="Rum" price={market.rumPrice} shipQuantity={market.shipRum} onTrade={handleTrade} />
                <ResourceRow resourceName="Tools" price={market.toolsPrice} shipQuantity={market.shipTools} onTrade={handleTrade} />
                <ResourceRow resourceName="Shot" price={market.shotPrice} shipQuantity={market.shipShot} onTrade={handleTrade} />
            </div>
        </div>
      )}
    </div>
  );
}

export default MarketView;
