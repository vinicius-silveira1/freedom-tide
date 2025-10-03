package com.tidebreakerstudios.freedom_tide.controller;

import com.tidebreakerstudios.freedom_tide.mapper.GameMapper;
import com.tidebreakerstudios.freedom_tide.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    // O serviço e o mapper são mantidos para futura refatoração,
    // caso os endpoints de contrato sejam movidos para este controller.
    private final ContractService contractService;
    private final GameMapper gameMapper;

    // O endpoint GET /api/contracts foi movido para GameController
    // como GET /api/games/{gameId}/contracts para ser sensível ao contexto do jogo.
}