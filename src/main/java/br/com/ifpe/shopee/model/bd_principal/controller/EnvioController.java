package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.Envio;
import br.com.ifpe.shopee.model.bd_principal.service.EnvioService;
import br.com.ifpe.shopee.model.enums.StatusDeEnvioEnum;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Envio> atualizarStatus(
            @PathVariable UUID id, 
            @RequestParam StatusDeEnvioEnum status, 
            @RequestParam(required = false) String observacao) {
        
        return ResponseEntity.ok(envioService.atualizarRastreio(id, status, observacao));
    }
}