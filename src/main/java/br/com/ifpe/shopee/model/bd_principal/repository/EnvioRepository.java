package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    
    // Buscar envio pelo código de rastreio (útil para API de atualização de status)
    Optional<Envio> findByCodigoDeRastreio(String codigoDeRastreio);
}