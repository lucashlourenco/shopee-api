// src/main/java/br/com/ifpe/shopee/model/bd_principal/repository/contato/ContatoDeLoginRepository.java

package br.com.ifpe.shopee.model.bd_principal.repository.contato;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;

public interface ContatoDeLoginRepository extends JpaRepository<ContatoDeLogin, UUID> {
    
    /**
     * Busca um ContatoDeLogin pelo seu valor (email ou telefone).
     * Esta é a consulta principal para:
     * 1. Autenticação (verificar se o login existe).
     * 2. Validação de Unicidade Global antes de um novo cadastro.
     * @param valor O valor do contato (email/telefone).
     * @return Optional contendo o ContatoDeLogin, se encontrado.
     */
    Optional<ContatoDeLogin> findByValor(String valor);
}