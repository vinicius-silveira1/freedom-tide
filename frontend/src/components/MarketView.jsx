import React, { useState, useEffect, useCallback } from 'react';
import './MarketView.css';

const ResourceRow = ({ resourceName, price, shipQuantity, onTrade }) => {
  const [amount, setAmount] = useState(1);

  const handleAmountChange = (e) => {
    const value = parseInt(e.target.value, 10);
    setAmount(isNaN(value) || value < 1 ? 1 : value);
  };

  return (
    <div className="resource-row">
      <span className="resource-name">{resourceName}</span>
      <span className="ship-quantity">A Bordo: {shipQuantity}</span>
      <span className="market-price">Preço: {price} Ouro</span>
      <div className="trade-actions">
        <input
          type="number"
          min="1"
          value={amount}
          onChange={handleAmountChange}
          className="quantity-input"
        />
        <button onClick={() => onTrade('buy', resourceName, amount)} className="action-button buy-button">Comprar</button>
        <button onClick={() => onTrade('sell', resourceName, amount)} className="action-button sell-button">Vender</button>
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
    <div className="market-view status-panel">
      <div className="market-header">
        <h2>Mercado Portuário</h2>
        <button onClick={onBack} className="action-button back-button">Voltar ao Porto</button>
      </div>

      {loading && <p>Avaliando os preços das mercadorias...</p>}
      {error && <p className="error-message">{error}</p>}

      {market && (
        <div className="market-content">
            <div className="player-gold">Seu Ouro: {market.shipGold}</div>
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
