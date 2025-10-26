import React from 'react';
import './EventLog.css';
import '../styles/ancient-documents.css';

function EventLog({ logs }) {
  if (!logs || logs.length === 0) {
    return null; // Não renderiza nada se não houver logs
  }

  return (
    <div className="ancient-document ship-logbook">
      <h2>Diário de Bordo</h2>
      <ul className="log-list">
        {logs.map((log, index) => (
          <li key={index} className="log-entry">
            {log}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default EventLog;
