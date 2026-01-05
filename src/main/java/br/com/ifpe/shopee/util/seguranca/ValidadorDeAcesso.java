// src/main/java/br/com/ifpe/shopee.util.seguranca/ValidadorDeAcesso.java

package br.com.ifpe.shopee.util.seguranca;

import java.util.UUID;
import org.springframework.stereotype.Component;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.util.exception.AcessoNegadoException;

@Component
public class ValidadorDeAcesso {

    /**
     * Valida se o ID do dono do recurso bate com o ID do usuário logado.
     * 
     * @param idDono O UUID do proprietário do dado (que veio do banco).
     * @param usuarioLogado O usuário que está tentando fazer a ação.
     */
    public void validarPosse(UUID idDono, Usuario usuarioLogado) {
        if (!idDono.equals(usuarioLogado.getId())) {
            throw new AcessoNegadoException("Acesso Negado: Você não tem permissão para manipular este recurso.");
        }
    }
}