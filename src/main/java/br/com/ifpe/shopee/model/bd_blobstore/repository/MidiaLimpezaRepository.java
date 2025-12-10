// src/main/java/br/com/ifpe/shopee/model/bd_blobstore/repository/MidiaLimpezaRepository.java

package br.com.ifpe.shopee.model.bd_blobstore.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_blobstore.entity.Midia;

public interface MidiaLimpezaRepository extends JpaRepository<Midia, UUID> {
    /**
     * Encontra todas as mídias com um número de referências menor ou igual ao valor fornecido.
     * @param referencias
     * @return
     */
    List<Midia> findAllByReferenciasLessThanEqual(int referencias);
    
    /**
     * Encontra todas as mídias com um número de referências igual ao valor fornecido.
     * @param referencias
     * @return
     */
    List<Midia> findByReferencias(int referencias); 
}