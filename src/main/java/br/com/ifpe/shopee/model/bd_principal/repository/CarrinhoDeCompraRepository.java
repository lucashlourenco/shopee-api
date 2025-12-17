package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;

@Repository
public interface CarrinhoDeCompraRepository extends JpaRepository<CarrinhoDeCompra, UUID> {
    
    // Busca o carrinho ativo de um cliente específico
    Optional<CarrinhoDeCompra> findByClienteIdAndHabilitadoTrue(UUID idCliente);
}