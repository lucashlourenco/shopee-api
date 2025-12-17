package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA; // <--- IMPORTANTE
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carrinho_de_compra")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// MUDANÇA AQUI: De EntidadeNegocioJPA para EntidadeAuditavelJPA
public class CarrinhoDeCompra extends EntidadeAuditavelJPA { 
    
    @OneToOne
    @JoinColumn(name = "id_cliente", unique = true)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private EnderecoDeEntrega enderecoDeEntrega;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDeCarrinho> itens;
}