// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoDeCadastro.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco_de_cadastro")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDeCadastro extends Endereco {

    @OneToMany(mappedBy = "endereco")
    private List<Pessoa> pessoas;
}
