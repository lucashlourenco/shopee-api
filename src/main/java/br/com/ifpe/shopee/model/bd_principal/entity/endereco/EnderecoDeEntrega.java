// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoDeEntrega.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;
import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco_de_entrega")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDeEntrega extends Endereco {

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "enderecoDeEntrega")
    private List<CarrinhoDeCompra> carrinhos;
}
