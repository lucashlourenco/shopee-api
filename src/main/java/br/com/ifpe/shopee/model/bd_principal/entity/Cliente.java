// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Cliente.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.bd_secundario.entity.PedidoDeCliente;
import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@SQLRestriction("habilitado = true")
@DiscriminatorValue("Cliente")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends TipoDeUsuario {

    @Transient
    private List<PedidoDeCliente> compras;

    @OneToOne(mappedBy = "cliente")
    private CarrinhoDeCompra carrinho;

    @OneToMany(mappedBy = "cliente")
    private List<EnderecoDeEntrega> enderecos;
}
