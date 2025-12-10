// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoDeEstoque.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco_de_estoque")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDeEstoque extends Endereco {

    @OneToMany(mappedBy = "endereco")
    private List<InformacaoDeRetirada> informacoesDeRetirada;

    @Transient
    private List<Variacao> variacoes;
}
