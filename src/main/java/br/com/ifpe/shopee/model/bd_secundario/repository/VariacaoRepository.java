package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;

@Repository
public interface VariacaoRepository extends MongoRepository<Variacao, UUID> {

    // 1. Busca Segura (Apenas habilitados)
    Optional<Variacao> findByIdAndHabilitadoTrue(UUID id);

    // 2. Listar variações de um produto (Ignorando as deletadas)
    // Essencial para carregar a página de detalhes do produto
    List<Variacao> findByIdProdutoAndHabilitadoTrue(UUID idProduto);

    // 3. Buscar variações ativas de um produto (Para o Carrinho de Compras)
    // Se a variação estiver PAUSADA, não pode comprar.
    // Assumindo que você tem um enum StatusDeProdutoEnum.ATIVO
    // Query: { 'idProduto': ?, 'habilitado': true, 'status': 'ATIVO' }
    // Nota: O Spring Data resolve enums automaticamente se o nome bater.
}