package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_secundario.entity.Produto;

@Repository
public interface ProdutoRepository extends MongoRepository<Produto, UUID> {

    // --- BUSCAS FILTRADAS (Soft Delete / Apenas Ativos) ---

    // 1. Buscar produtos de uma loja (Ignora os deletados logicamente)
    List<Produto> findByIdLojaAndHabilitadoTrue(UUID idLoja);

    // 2. Buscar produtos de uma categoria (Ignora os deletados logicamente)
    List<Produto> findByIdCategoriaAndHabilitadoTrue(UUID idCategoria);

    // 3. Buscar um produto específico (Retorna vazio se ele estiver desabilitado)
    Optional<Produto> findByIdAndHabilitadoTrue(UUID id);

    // 4. Pesquisa por nome (Ignora maiúsculas/minúsculas e deletados)
    List<Produto> findByNomeContainingIgnoreCaseAndHabilitadoTrue(String nome);
}