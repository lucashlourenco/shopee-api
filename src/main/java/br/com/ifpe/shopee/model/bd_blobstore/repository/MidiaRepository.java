// src/main/java/br/com/ifpe/shopee/model/bd_blobstore/repository/MidiaRepository.java

package br.com.ifpe.shopee.model.bd_blobstore.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.ifpe.shopee.model.bd_blobstore.entity.Midia;

public interface MidiaRepository extends JpaRepository<Midia, UUID> {
    // Métodos padrão do JPA já são suficientes por enquanto
}