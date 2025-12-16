package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_principal.repository.ContatoDeLojaRepository; // Certifique-se de ter este repo
import br.com.ifpe.shopee.model.bd_principal.repository.EnderecoDeEstoqueRepository; // Certifique-se de ter este repo
import br.com.ifpe.shopee.model.bd_principal.repository.InformacaoDeRetiradaRepository;
import br.com.ifpe.shopee.model.bd_principal.request.InformacaoDeRetiradaRequest;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class InformacaoDeRetiradaService {

    @Autowired
    private InformacaoDeRetiradaRepository repository;

    @Autowired
    private EnderecoDeEstoqueRepository enderecoRepository;

    @Autowired
    private ContatoDeLojaRepository contatoRepository;

    @Transactional
    public InformacaoDeRetirada criar(InformacaoDeRetiradaRequest request) {
        // 1. Busca Relacionamentos
        EnderecoDeEstoque endereco = enderecoRepository.findById(request.getIdEndereco())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado: " + request.getIdEndereco()));

        List<ContatoDeLoja> contatos = contatoRepository.findAllById(request.getIdsContatos());
        if (contatos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum contato válido encontrado.");
        }

        // 2. Monta Entidade
        InformacaoDeRetirada nova = InformacaoDeRetirada.builder()
            .diaEHorario(request.getDiaEHorario())
            .endereco(endereco)
            .contatos(contatos)
            .build();
        
        // 3. Auditoria
        nova.setHabilitado(true);
        nova.setDataCriacao(LocalDateTime.now()); // Só funciona se herdar de EntidadeAuditavelJPA

        return repository.save(nova);
    }

    @Transactional
    public InformacaoDeRetirada atualizar(UUID id, InformacaoDeRetiradaRequest request) {
        InformacaoDeRetirada info = buscarPorId(id);

        if (!info.getEndereco().getId().equals(request.getIdEndereco())) {
            EnderecoDeEstoque endereco = enderecoRepository.findById(request.getIdEndereco())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));
            info.setEndereco(endereco);
        }

        List<ContatoDeLoja> contatos = contatoRepository.findAllById(request.getIdsContatos());
        info.setContatos(contatos);
        info.setDiaEHorario(request.getDiaEHorario());
        
        info.setDataUltimaModificacao(LocalDateTime.now());
        return repository.save(info);
    }

    @Transactional
    public void deletar(UUID id) {
        InformacaoDeRetirada info = buscarPorId(id);
        info.setHabilitado(false);
        info.setDataUltimaModificacao(LocalDateTime.now());
        repository.save(info);
    }

    public List<InformacaoDeRetirada> listarTodos() {
        return repository.findAllByHabilitadoTrue();
    }

    public InformacaoDeRetirada buscarPorId(UUID id) {
        return repository.findById(id)
            .filter(InformacaoDeRetirada::getHabilitado)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Informação de Retirada não encontrada: " + id));
    }
}