package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.ItemDeCarrinho;

@Repository
public interface ItemDeCarrinhoRepository extends JpaRepository<ItemDeCarrinho, UUID> {
    // Métodos padrão do JpaRepository já são suficientes por enquanto
}