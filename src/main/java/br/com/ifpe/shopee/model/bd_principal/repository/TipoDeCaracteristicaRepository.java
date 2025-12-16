package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;

@Repository
public interface TipoDeCaracteristicaRepository extends JpaRepository<TipoDeCaracteristica, UUID> {

    // --- BUSCAS E FILTROS ---

    // 1. Listar apenas ativos (Ignorar os deletados logicamente)
    // Essencial para preencher combos no Front-end sem trazer lixo
    List<TipoDeCaracteristica> findAllByHabilitadoTrue();

    // 2. Barra de Busca (Autocomplete)
    // Ex: Admin digita "Vol" e aparece "Voltagem", "Volume"
    List<TipoDeCaracteristica> findByNomeContainingIgnoreCaseAndHabilitadoTrue(String nome);


    // --- VALIDAÇÕES DE INTEGRIDADE ---

    // 3. Verifica se já existe um tipo com esse nome exato
    // Usado no Service para impedir criar dois "Cor" ou "Voltagem" duplicados
    boolean existsByNomeIgnoreCase(String nome);
    
    // 4. Verifica duplicidade excluindo o próprio ID (Para Update)
    // Permite salvar "Cor" se o ID for o mesmo (estou editando ele mesmo),
    // mas bloqueia se tentar mudar "Cor" para "Tamanho" se "Tamanho" já existir em outro ID.
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
}