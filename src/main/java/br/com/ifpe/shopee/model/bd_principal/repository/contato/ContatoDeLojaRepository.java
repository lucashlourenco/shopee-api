// src/main/java/br/com/ifpe/shopee/model/bd_principal/repository/contato/ContatoDeLojaRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.contato;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.enums.TipoDeContatoEnum;

public interface ContatoDeLojaRepository extends JpaRepository<ContatoDeLoja, UUID> {
    
    // Consulta personalizada: buscar todos os contatos de uma Loja específica pelo seu ID
    // O Spring Data JPA cria a implementação automaticamente
    List<ContatoDeLoja> findByLojaId(UUID idLoja);

    // Consulta para verificar unicidade: busca um contato específico em uma loja.
    ContatoDeLoja findByLojaIdAndValorAndTipo(UUID idLoja, String valor, TipoDeContatoEnum tipo);
}
