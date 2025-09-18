package com.company.fundtransfer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.fundtransfer.dto.TransferRequest;
import com.company.fundtransfer.service.FundTransferService;

@RestController
@RequestMapping("/api/transfer")
public class FundTransferController {
    private final FundTransferService fundTransferService;

    public FundTransferController(FundTransferService fundTransferService) {
        this.fundTransferService = fundTransferService;
    }

    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {
        String result = fundTransferService.transfer(req);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/ping")
    public String ping() { return "pong"; }
}
