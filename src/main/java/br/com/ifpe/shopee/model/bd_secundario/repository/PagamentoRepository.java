package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_secundario.entity.Pagamento;
import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;

@Repository
public interface PagamentoRepository extends MongoRepository<Pagamento, UUID> {
    
    Optional<Pagamento> findByIdAndHabilitadoTrue(UUID id);
    
    // Busca apenas se o pagamento estiver em um status "Ativo" (Em Análise ou A Confirmar)
    // Isso permite criar um novo se o anterior estiver CANCELADO ou RECUSADO.
    // Query personalizada ou lógica no Service (faremos no Service para simplificar)
    Optional<Pagamento> findByIdPedidoAndHabilitadoTrue(UUID idPedido);
    
    List<Pagamento> findByStatusAtualAndHabilitadoTrue(StatusDePagamentoEnum status);

    // --- NOVO: Busca pelo ID do Gateway (Webhook) ---
    // O Spring Data entende o "_" como navegação no objeto aninhado
    Optional<Pagamento> findByDetalhes_IdTransacaoExterna(String idTransacaoExterna);
}