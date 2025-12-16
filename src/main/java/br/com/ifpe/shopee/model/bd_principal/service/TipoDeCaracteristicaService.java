package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeCaracteristicaRepository;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeCaracteristicaRequest;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class TipoDeCaracteristicaService {

    @Autowired
    private TipoDeCaracteristicaRepository repository;

    @Transactional
    public TipoDeCaracteristica criar(TipoDeCaracteristicaRequest request) {
        if (repository.existsByNomeIgnoreCase(request.getNome())) {
            throw new EntidadeDuplicadaException("Já existe um tipo de característica com o nome: " + request.getNome());
        }

        TipoDeCaracteristica novo = TipoDeCaracteristica.builder()
            .nome(request.getNome())
            .tipoDeDado(request.getTipoDeDado())
            .eObrigatoria(request.isEObrigatoria())
            .build();
        
        novo.setHabilitado(true);
        novo.setDataCriacao(LocalDateTime.now());
        
        return repository.save(novo);
    }

    @Transactional
    public TipoDeCaracteristica atualizar(UUID id, TipoDeCaracteristicaRequest request) {
        TipoDeCaracteristica existente = buscarPorId(id);

        // Valida duplicidade excluindo o próprio ID
        if (repository.existsByNomeIgnoreCaseAndIdNot(request.getNome(), id)) {
            throw new EntidadeDuplicadaException("Já existe outro tipo com o nome: " + request.getNome());
        }

        existente.setNome(request.getNome());
        existente.setTipoDeDado(request.getTipoDeDado());
        existente.setEObrigatoria(request.isEObrigatoria());
        existente.setDataUltimaModificacao(LocalDateTime.now());

        return repository.save(existente);
    }

    @Transactional
    public void deletar(UUID id) {
        TipoDeCaracteristica existente = buscarPorId(id);
        existente.setHabilitado(false); // Deleção Lógica
        existente.setDataUltimaModificacao(LocalDateTime.now());
        repository.save(existente);
    }

    public List<TipoDeCaracteristica> listarTodos() {
        return repository.findAllByHabilitadoTrue();
    }

    public TipoDeCaracteristica buscarPorId(UUID id) {
        return repository.findById(id)
            .filter(TipoDeCaracteristica::getHabilitado)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de Característica não encontrado: " + id));
    }
}