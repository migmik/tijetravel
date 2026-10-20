package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.BalanceDTO;
import com.tijetravel.backend.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public ResponseEntity<BalanceDTO> obtenerBalance() {
        BalanceDTO balance = balanceService.obtenerBalance();
        return ResponseEntity.ok(balance);
    }
}

