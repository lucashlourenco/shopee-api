package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.Categoria;
import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.model.bd_principal.repository.CategoriaRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeCaracteristicaRepository;
import br.com.ifpe.shopee.model.bd_principal.request.CategoriaRequest;
import br.com.ifpe.shopee.model.bd_principal.response.CategoriaTreeResponse;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TipoDeCaracteristicaRepository tipoCaracteristicaRepository;

    // =========================================================================
    // CREATE
    // =========================================================================
    @Transactional
    public Categoria criarCategoria(CategoriaRequest request) {
        
        Categoria novaCategoria = new Categoria();
        // Habilitado e Ativa por padrão
        novaCategoria.setHabilitado(true);
        novaCategoria.setDataCriacao(LocalDateTime.now());

        // 1. Configura Pai (Hierarquia)
        configurarPai(novaCategoria, request.getIdPai());

        // 2. Valida Duplicidade de Nome (Irmãos não podem ter mesmo nome)
        validarDuplicidadeNome(request.getNome(), novaCategoria.getPai());

        // 3. Preenche dados básicos
        novaCategoria.setNome(request.getNome());
        novaCategoria.setEstaAtiva(request.isEstaAtiva());
        novaCategoria.setMarcasReconhecidas(request.getMarcasReconhecidas());

        // 4. Configura Relacionamento ManyToMany (Tipos de Característica)
        configurarCaracteristicas(novaCategoria, request.getIdsTipoCaracteristica());

        return categoriaRepository.save(novaCategoria);
    }

    // =========================================================================
    // UPDATE
    // =========================================================================
    @Transactional
    public Categoria atualizarCategoria(UUID id, CategoriaRequest request) {
        Categoria categoriaExistente = buscarPorId(id);

        // 1. Atualiza Pai (Se mudou)
        if (request.getIdPai() != null && !request.getIdPai().equals(categoriaExistente.getPai() != null ? categoriaExistente.getPai().getId() : null)) {
            // Evita ciclo básico (Uma categoria não pode ser pai de si mesma)
            if (request.getIdPai().equals(id)) {
                throw new IllegalArgumentException("Uma categoria não pode ser pai de si mesma.");
            }
            configurarPai(categoriaExistente, request.getIdPai());
        } else if (request.getIdPai() == null) {
            categoriaExistente.setPai(null); // Virou Raiz
        }

        // 2. Valida Nome (Se mudou, verifica duplicidade no novo contexto)
        if (!categoriaExistente.getNome().equals(request.getNome())) {
            validarDuplicidadeNome(request.getNome(), categoriaExistente.getPai());
            categoriaExistente.setNome(request.getNome());
        }

        categoriaExistente.setEstaAtiva(request.isEstaAtiva());
        categoriaExistente.setMarcasReconhecidas(request.getMarcasReconhecidas());
        categoriaExistente.setDataUltimaModificacao(LocalDateTime.now());

        // 3. Atualiza Características
        configurarCaracteristicas(categoriaExistente, request.getIdsTipoCaracteristica());

        return categoriaRepository.save(categoriaExistente);
    }

    // =========================================================================
    // DELETE (Lógico e em Cascata)
    // =========================================================================
    @Transactional
    public void deletarCategoria(UUID id) {
        Categoria categoria = buscarPorId(id);

        // Desabilita a categoria pai
        categoria.setHabilitado(false);
        categoria.setEstaAtiva(false);
        categoria.setDataUltimaModificacao(LocalDateTime.now());

        // Desabilita Recursivamente todas as filhas e netas
        desabilitarFilhasRecursivamente(categoria);

        categoriaRepository.save(categoria);
    }

    private void desabilitarFilhasRecursivamente(Categoria pai) {
        if (pai.getFilhas() != null) {
            for (Categoria filha : pai.getFilhas()) {
                // Só processa se ainda estiver habilitada (evita retrabalho)
                if (Boolean.TRUE.equals(filha.getHabilitado())) {
                    filha.setHabilitado(false);
                    filha.setEstaAtiva(false);
                    filha.setDataUltimaModificacao(LocalDateTime.now());
                    desabilitarFilhasRecursivamente(filha); // Recursão para netas
                }
            }
        }
    }

    // =========================================================================
    // READ (Buscas e Árvore)
    // =========================================================================
    
    // Lista a árvore completa (Pai -> Filhas -> Netas) para o Menu Principal
    public List<CategoriaTreeResponse> listarArvoreCompleta() {
        // Busca apenas as raízes ativas
        List<Categoria> raizes = categoriaRepository.findByPaiIsNullAndHabilitadoTrue();

        // Converte cada raiz e suas filhas
        return raizes.stream()
            .map(this::converterParaTreeDTO)
            .collect(Collectors.toList());
    }

    public List<Categoria> listarRaizes() {
        return categoriaRepository.findByPaiIsNullAndHabilitadoTrue();
    }

    public List<Categoria> listarFilhas(UUID idPai) {
        return categoriaRepository.findByPaiIdAndHabilitadoTrue(idPai);
    }

    public Categoria buscarPorId(UUID id) {
        return categoriaRepository.findById(id)
            .filter(c -> Boolean.TRUE.equals(c.getHabilitado())) // Garante que está habilitada
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada: " + id));
    }

    // =========================================================================
    // AUXILIARES
    // =========================================================================
    
    private CategoriaTreeResponse converterParaTreeDTO(Categoria categoria) {
        List<CategoriaTreeResponse> filhasDTO = new ArrayList<>();

        if (categoria.getFilhas() != null && !categoria.getFilhas().isEmpty()) {
            filhasDTO = categoria.getFilhas().stream()
                .filter(filha -> Boolean.TRUE.equals(filha.getHabilitado())) // Filtra deletadas logicamente
                .map(this::converterParaTreeDTO) // Recursão
                .collect(Collectors.toList());
        }

        // Se usar Lombok @Builder no DTO
        return CategoriaTreeResponse.builder()
            .id(categoria.getId())
            .nome(categoria.getNome())
            .estaAtiva(categoria.getEstaAtiva() != null ? categoria.getEstaAtiva() : false)
            .subcategorias(filhasDTO)
            .build();
    }

    private void configurarPai(Categoria categoria, UUID idPai) {
        if (idPai != null) {
            Categoria pai = categoriaRepository.findById(idPai)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria Pai não encontrada: " + idPai));
            categoria.setPai(pai);
        } else {
            categoria.setPai(null);
        }
    }

    private void validarDuplicidadeNome(String nome, Categoria pai) {
        boolean existe;
        if (pai == null) {
            existe = categoriaRepository.existsByNomeAndPaiIsNull(nome);
        } else {
            existe = categoriaRepository.existsByNomeAndPai(nome, pai);
        }
        
        if (existe) {
            throw new EntidadeDuplicadaException("Já existe uma categoria com este nome neste nível.");
        }
    }

    private void configurarCaracteristicas(Categoria categoria, List<UUID> idsTipos) {
        if (idsTipos != null && !idsTipos.isEmpty()) {
            List<TipoDeCaracteristica> tipos = tipoCaracteristicaRepository.findAllById(idsTipos);
            categoria.setCaracteristicas(tipos);
        } else {
            categoria.setCaracteristicas(new ArrayList<>());
        }
    }
}