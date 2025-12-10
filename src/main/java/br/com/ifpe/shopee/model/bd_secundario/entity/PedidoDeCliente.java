// src/main/java/br/com/ifpe/shopee/model/bd_secundario/entity/PedidoDeCliente.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pedido_cliente")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDeCliente extends Pedido {

    // As cópias serão feitas via ItemDeCarrinho da superclasse
    
    // Relativo ao cliente
    // Salva uma cópia, por segurança, para registro fixo
    private UUID idCliente;

    // Relativo ao vendedor
    // Servirá de link para o vendedor ainda que ele possa mudar suas informações
    private UUID idVendedor;
    // Salva uma cópia, por segurança, para registro fixo
    private String nomeDeVendedor;

    // Relativo a loja
    // Servirá de link para a loja (pelo id) ainda que ela possa mudar suas informações
    // Salva uma cópia, por segurança, para registro fixo
    private Loja loja;
    private EnderecoComercial enderecoDaLoja;
    private List<ContatoDeLoja> contatosDaLoja;
}