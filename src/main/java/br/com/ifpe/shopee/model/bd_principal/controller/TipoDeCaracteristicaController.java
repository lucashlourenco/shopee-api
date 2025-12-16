package br.com.ifpe.shopee.model.bd_principal.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeCaracteristicaRequest;
import br.com.ifpe.shopee.model.bd_principal.service.TipoDeCaracteristicaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-caracteristica")
public class TipoDeCaracteristicaController {

    @Autowired
    private TipoDeCaracteristicaService service;

    @PostMapping
    public ResponseEntity<TipoDeCaracteristica> criar(@RequestBody @Valid TipoDeCaracteristicaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDeCaracteristica> atualizar(@PathVariable UUID id, @RequestBody @Valid TipoDeCaracteristicaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TipoDeCaracteristica>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDeCaracteristica> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}