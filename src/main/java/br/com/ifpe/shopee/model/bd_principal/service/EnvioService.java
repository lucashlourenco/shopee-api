package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.Envio;
import br.com.ifpe.shopee.model.bd_principal.entity.StatusDeEnvio;
import br.com.ifpe.shopee.model.bd_principal.repository.EnvioRepository;
import br.com.ifpe.shopee.model.enums.StatusDeEnvioEnum;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Transactional
    public Envio atualizarRastreio(UUID idEnvio, StatusDeEnvioEnum novoStatus, String observacao) {
        Envio envio = envioRepository.findById(idEnvio)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Envio não encontrado: " + idEnvio));

        // Cria o novo status no histórico (SQL Relacional)
        StatusDeEnvio status = StatusDeEnvio.builder()
            .status(novoStatus)
            .observacao(observacao)
            .dataDeRegistro(LocalDateTime.now())
            .envio(envio)
            .build();
            
        status.gerarId();
        status.setHabilitado(true);

        envio.getStatusDeEnvios().add(status);
        
        // Regra de Negócio: Se entregue, poderia disparar atualização no Pedido (Mongo) via mensageria/evento
        // Por enquanto, ficamos no escopo da logística.
        
        return envioRepository.save(envio);
    }
}