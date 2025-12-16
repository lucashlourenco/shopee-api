package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;

@Repository
public interface InformacaoDeRetiradaRepository extends JpaRepository<InformacaoDeRetirada, UUID> {
    List<InformacaoDeRetirada> findAllByHabilitadoTrue();
}