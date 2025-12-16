package br.com.ifpe.shopee.model.bd_secundario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.model.bd_principal.repository.CategoriaRepository;
import br.com.ifpe.shopee.model.bd_secundario.entity.Caracteristica;
import br.com.ifpe.shopee.model.bd_secundario.entity.Estoque;
import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;
import br.com.ifpe.shopee.model.bd_secundario.repository.CaracteristicaRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.ProdutoRepository;
import br.com.ifpe.shopee.model.bd_secundario.repository.VariacaoRepository;
import br.com.ifpe.shopee.model.bd_secundario.request.ProdutoRequest;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private VariacaoRepository variacaoRepository;
    @Autowired
    private CaracteristicaRepository caracteristicaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    // =========================================================================
    // CREATE
    // =========================================================================
    public Produto criarProduto(ProdutoRequest request, UUID idLojaVendedor) {
        // 1. Valida Categoria no SQL
        validarCategoria(request.getIdCategoria());

        // 2. Prepara o Produto
        Produto produto = new Produto();
        produto.gerarId(); 
        if (produto.getId() == null) produto.setId(UUID.randomUUID());
        
        preencherDadosBasicos(produto, request, idLojaVendedor);
        produto.setHabilitado(true);
        produto.setDataCriacao(LocalDateTime.now());

        // 3. Salva Caracteristicas
        List<Caracteristica> cars = salvarNovasCaracteristicas(request.getCaracteristicas());
        produto.setCaracteristicas(cars);

        // 4. Salva Variações
        List<Variacao> vars = salvarNovasVariacoes(request.getVariacoes(), produto.getId());
        produto.setVariacoes(vars);

        // 5. Salva Produto
        return produtoRepository.save(produto);
    }

    // =========================================================================
    // UPDATE (Smart Update + Soft Delete)
    // =========================================================================
    @Transactional
    public Produto atualizarProduto(UUID idProduto, ProdutoRequest request, UUID idLojaUsuario) {
        
        Produto produtoAntigo = buscarProdutoPorId(idProduto);

        // Validação de Segurança
        if (!produtoAntigo.getIdLoja().equals(idLojaUsuario)) {
            throw new RuntimeException("Acesso negado: Este produto não pertence à sua loja.");
        }

        validarCategoria(request.getIdCategoria());
        preencherDadosBasicos(produtoAntigo, request, idLojaUsuario);
        produtoAntigo.setDataUltimaModificacao(LocalDateTime.now());

        // Reconciliação de Características (Atualiza, Cria ou Desabilita)
        List<Caracteristica> caracteristicasFinais = reconciliarCaracteristicas(
            produtoAntigo.getCaracteristicas(), 
            request.getCaracteristicas()
        );
        produtoAntigo.setCaracteristicas(caracteristicasFinais);

        // Reconciliação de Variações (Atualiza, Cria ou Desabilita)
        List<Variacao> variacoesFinais = reconciliarVariacoes(
            produtoAntigo.getVariacoes(), 
            request.getVariacoes(), 
            produtoAntigo.getId()
        );
        produtoAntigo.setVariacoes(variacoesFinais);

        return produtoRepository.save(produtoAntigo);
    }

    // =========================================================================
    // DELETE (Lógico)
    // =========================================================================
    @Transactional
    public void deletarProduto(UUID idProduto, UUID idLojaUsuario) {
        Produto produto = buscarProdutoPorId(idProduto);

        if (!produto.getIdLoja().equals(idLojaUsuario)) {
            throw new RuntimeException("Acesso negado: Este produto não pertence à sua loja.");
        }

        // 1. Desabilita o Produto Pai
        produto.setHabilitado(false);
        produto.setDataUltimaModificacao(LocalDateTime.now());
        
        // 2. Desabilita Filhos (Variacões)
        if (produto.getVariacoes() != null) {
            produto.getVariacoes().forEach(v -> {
                v.setHabilitado(false);
                v.setDataUltimaModificacao(LocalDateTime.now());
                variacaoRepository.save(v);
            });
        }

        // 3. Desabilita Filhos (Características)
        if (produto.getCaracteristicas() != null) {
            produto.getCaracteristicas().forEach(c -> {
                c.setHabilitado(false);
                c.setDataUltimaModificacao(LocalDateTime.now());
                caracteristicaRepository.save(c);
            });
        }

        produtoRepository.save(produto);
    }

    // =========================================================================
    // READ (Filtrados por Habilitado=True)
    // =========================================================================
    public List<Produto> listarProdutosPorLoja(UUID idLoja) {
        return produtoRepository.findByIdLojaAndHabilitadoTrue(idLoja);
    }

    public List<Produto> listarProdutosPorCategoria(UUID idCategoria) {
        return produtoRepository.findByIdCategoriaAndHabilitadoTrue(idCategoria);
    }

    public Produto buscarProdutoPorId(UUID idProduto) {
        return produtoRepository.findByIdAndHabilitadoTrue(idProduto)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado ou inativo: " + idProduto));
    }

    public List<Produto> pesquisarProdutos(String termo) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndHabilitadoTrue(termo);
    }

    // =========================================================================
    // AUXILIARES
    // =========================================================================

    private void validarCategoria(UUID idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new RecursoNaoEncontradoException("Categoria não encontrada com ID: " + idCategoria);
        }
    }

    private void preencherDadosBasicos(Produto produto, ProdutoRequest request, UUID idLoja) {
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setStatus(request.getStatus() != null ? request.getStatus() : StatusDeProdutoEnum.ATIVO);
        produto.setFotos(request.getFotos());
        produto.setIdCategoria(request.getIdCategoria());
        produto.setIdLoja(idLoja);
    }

    // --- Helpers de Criação Pura ---
    private List<Caracteristica> salvarNovasCaracteristicas(List<ProdutoRequest.CaracteristicaRequest> listaRequest) {
        List<Caracteristica> salvas = new ArrayList<>();
        if (listaRequest == null) return salvas;
        for (ProdutoRequest.CaracteristicaRequest req : listaRequest) {
            TipoDeCaracteristica tipo = new TipoDeCaracteristica();
            tipo.setId(req.getIdTipoCaracteristica());
            Caracteristica c = Caracteristica.builder()
                .idTipo(req.getIdTipoCaracteristica())
                .tipo(tipo)
                .valor(req.getValor())
                .build();
            c.setId(UUID.randomUUID());
            c.setHabilitado(true);
            c.setDataCriacao(LocalDateTime.now());
            salvas.add(caracteristicaRepository.save(c));
        }
        return salvas;
    }

    private List<Variacao> salvarNovasVariacoes(List<ProdutoRequest.VariacaoRequest> listaRequest, UUID idProduto) {
        List<Variacao> salvas = new ArrayList<>();
        if (listaRequest == null || listaRequest.isEmpty()) throw new IllegalArgumentException("Variação obrigatória");
        for (ProdutoRequest.VariacaoRequest req : listaRequest) {
            Variacao v = construirObjetoVariacao(req, idProduto);
            v.setId(UUID.randomUUID());
            v.setHabilitado(true);
            v.setDataCriacao(LocalDateTime.now());
            salvas.add(variacaoRepository.save(v));
        }
        return salvas;
    }

    // --- Helpers de Reconciliação (Update) ---
    private List<Variacao> reconciliarVariacoes(List<Variacao> listaNoBanco, List<ProdutoRequest.VariacaoRequest> listaRequest, UUID idProduto) {
        List<Variacao> listaFinal = new ArrayList<>();
        List<Variacao> paraDesabilitar = (listaNoBanco != null) ? new ArrayList<>(listaNoBanco) : new ArrayList<>();

        if (listaRequest != null) {
            for (ProdutoRequest.VariacaoRequest req : listaRequest) {
                if (req.getId() != null) {
                    // UPDATE
                    Variacao exist = listaNoBanco.stream()
                        .filter(v -> v.getId().equals(req.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Variação não encontrada: " + req.getId()));

                    atualizarCamposVariacao(exist, req);
                    exist.setDataUltimaModificacao(LocalDateTime.now());
                    
                    listaFinal.add(variacaoRepository.save(exist));
                    paraDesabilitar.remove(exist);
                } else {
                    // CREATE
                    Variacao nova = construirObjetoVariacao(req, idProduto);
                    nova.setId(UUID.randomUUID());
                    nova.setHabilitado(true);
                    nova.setDataCriacao(LocalDateTime.now());
                    listaFinal.add(variacaoRepository.save(nova));
                }
            }
        }

        // DELEÇÃO LÓGICA (Soft Delete)
        if (!paraDesabilitar.isEmpty()) {
            for (Variacao v : paraDesabilitar) {
                v.setHabilitado(false);
                v.setDataUltimaModificacao(LocalDateTime.now());
                variacaoRepository.save(v);
            }
        }
        return listaFinal;
    }

    private List<Caracteristica> reconciliarCaracteristicas(List<Caracteristica> listaNoBanco, List<ProdutoRequest.CaracteristicaRequest> listaRequest) {
        List<Caracteristica> listaFinal = new ArrayList<>();
        List<Caracteristica> paraDesabilitar = (listaNoBanco != null) ? new ArrayList<>(listaNoBanco) : new ArrayList<>();

        if (listaRequest != null) {
            for (ProdutoRequest.CaracteristicaRequest req : listaRequest) {
                if (req.getId() != null) {
                    // UPDATE
                    Caracteristica exist = listaNoBanco.stream()
                        .filter(c -> c.getId().equals(req.getId()))
                        .findFirst().orElse(null);
                    
                    if (exist != null) {
                        exist.setValor(req.getValor());
                        exist.setIdTipo(req.getIdTipoCaracteristica());
                        exist.setDataUltimaModificacao(LocalDateTime.now());
                        listaFinal.add(caracteristicaRepository.save(exist));
                        paraDesabilitar.remove(exist);
                    }
                } else {
                    // CREATE
                    TipoDeCaracteristica tipo = new TipoDeCaracteristica();
                    tipo.setId(req.getIdTipoCaracteristica());
                    Caracteristica nova = Caracteristica.builder()
                        .idTipo(req.getIdTipoCaracteristica())
                        .tipo(tipo)
                        .valor(req.getValor())
                        .build();
                    nova.setId(UUID.randomUUID());
                    nova.setHabilitado(true);
                    nova.setDataCriacao(LocalDateTime.now());
                    listaFinal.add(caracteristicaRepository.save(nova));
                }
            }
        }

        // DELEÇÃO LÓGICA (Soft Delete)
        if (!paraDesabilitar.isEmpty()) {
            for (Caracteristica c : paraDesabilitar) {
                c.setHabilitado(false);
                c.setDataUltimaModificacao(LocalDateTime.now());
                caracteristicaRepository.save(c);
            }
        }
        return listaFinal;
    }

    // --- Builders e Setters ---
    
    // CORRIGIDO: Substituído .builder() por new Variacao() e setters
    private Variacao construirObjetoVariacao(ProdutoRequest.VariacaoRequest req, UUID idProduto) {
        Estoque estoque = Estoque.builder()
                .preco(req.getPreco())
                .qtdInicial(req.getQtdInicial())
                .qtdVendida(0)
                .build();
        
        Variacao v = new Variacao();
        v.setNome(req.getNome());
        v.setEstado(req.getEstado());
        v.setStatus(req.getStatus());
        v.setPeso(req.getPeso());
        v.setTamanhoDeEnvioX(req.getTamanhoDeEnvioX());
        v.setTamanhoDeEnvioY(req.getTamanhoDeEnvioY());
        v.setTamanhoDeEnvioZ(req.getTamanhoDeEnvioZ());
        v.setFotos(req.getFotos());
        v.setEstoque(estoque);
        v.setIdProduto(idProduto);
        
        return v;
    }

    private void atualizarCamposVariacao(Variacao v, ProdutoRequest.VariacaoRequest req) {
        v.setNome(req.getNome());
        v.setEstado(req.getEstado());
        v.setStatus(req.getStatus());
        v.setPeso(req.getPeso());
        v.setTamanhoDeEnvioX(req.getTamanhoDeEnvioX());
        v.setTamanhoDeEnvioY(req.getTamanhoDeEnvioY());
        v.setTamanhoDeEnvioZ(req.getTamanhoDeEnvioZ());
        v.setFotos(req.getFotos());
        if (v.getEstoque() == null) v.setEstoque(new Estoque());
        v.getEstoque().setPreco(req.getPreco());
        v.getEstoque().setQtdInicial(req.getQtdInicial());
    }
}