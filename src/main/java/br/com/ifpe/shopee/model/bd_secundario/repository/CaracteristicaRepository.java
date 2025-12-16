package br.com.ifpe.shopee.model.bd_secundario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_secundario.entity.Caracteristica;

@Repository
public interface CaracteristicaRepository extends MongoRepository<Caracteristica, UUID> {
    
    // Essencial para Filtros Laterais (ex: Cliente clica em "Cor: Azul")
    // Busca todas as características que tenham o valor "Azul"
    List<Caracteristica> findByValor(Object valor);

    // Busca características de um tipo específico (ex: todas as características de "Voltagem")
    List<Caracteristica> findByIdTipo(UUID idTipo);
}