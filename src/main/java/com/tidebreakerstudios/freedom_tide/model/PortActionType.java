package com.tidebreakerstudios.freedom_tide.model;

public enum PortActionType {
    VIEW_CONTRACTS,   // Ver os contratos disponíveis no porto
    TRAVEL,           // Viajar para outro porto
    GO_TO_SHIPYARD,   // Ir ao estaleiro para reparos e melhorias
    GO_TO_MARKET,     // Ir ao mercado para comprar e vender recursos
    GO_TO_TAVERN,     // Ir à taverna para recrutar novos tripulantes
    RESOLVE_CONTRACT  // Resolver o contrato ativo no porto de destino
}
