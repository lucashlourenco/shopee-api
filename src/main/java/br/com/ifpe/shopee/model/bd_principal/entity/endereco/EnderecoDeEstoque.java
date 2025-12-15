// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoDeEstoque.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_secundario.entity.Variacao;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;
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

    // Nome de um endereço de estoque.
    @Column
    private String nome;

    @OneToMany(mappedBy = "endereco")
    private List<InformacaoDeRetirada> informacoesDeRetirada;

    @Transient
    private List<Variacao> variacoes;

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        // Hashcode baseado na igualdade semântica
        return Objects.hash(getCep(), getRua(), getBairro(), getCidade(), getEstado(), getNumero(), getComplemento(), nome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EnderecoDeEstoque that = (EnderecoDeEstoque) o;

        // Regra 1: Igualdade por Identidade (UUID)
        if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
            return true;
        }

        // Regra 2: Igualdade Semântica (Endereços são iguais se todos os campos geográficos coincidirem
        // e tiverem o mesmo nome). Desconsidera referencia - a análise é independente dela.
        return Objects.equals(getCep(), that.getCep()) &&
               Objects.equals(getRua(), that.getRua()) &&
               Objects.equals(getBairro(), that.getBairro()) &&
               Objects.equals(getCidade(), that.getCidade()) &&
               Objects.equals(getEstado(), that.getEstado()) &&
               Objects.equals(getNumero(), that.getNumero()) &&
               Objects.equals(getComplemento(), that.getComplemento()) &&
               Objects.equals(nome, that.nome);
    }
}
