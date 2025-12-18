// src/main/java/br/com/ifpe/shopee.model/bd_principal/repository/LojaRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;

public interface LojaRepository extends JpaRepository<Loja, UUID> {
}