package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_secundario.entity.ProdutoComprado;

@Repository
public interface ProdutoCompradoRepository extends MongoRepository<ProdutoComprado, UUID> {
    
    // Encontrar todas as vezes que um produto específico foi vendido
    List<ProdutoComprado> findByIdProduto(UUID idProduto);
}