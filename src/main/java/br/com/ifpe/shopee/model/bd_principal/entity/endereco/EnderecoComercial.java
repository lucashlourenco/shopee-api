// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoComercial.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco_comercial")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoComercial extends Endereco {

    @OneToMany(mappedBy = "endereco")
    private List<Loja> lojas;

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        // Hashcode baseado na igualdade semântica
        return Objects.hash(getCep(), getRua(), getBairro(), getCidade(), getEstado(), getNumero(), getComplemento());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EnderecoComercial that = (EnderecoComercial) o;

        // Regra 1: Igualdade por Identidade (UUID).
        if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
            return true;
        }

        // Regra 2: Igualdade Semântica (Endereços são iguais se todos os campos geográficos coincidirem)
        // Desconsidera referencia - a análise é independente dela.
        return Objects.equals(getCep(), that.getCep()) &&
               Objects.equals(getRua(), that.getRua()) &&
               Objects.equals(getBairro(), that.getBairro()) &&
               Objects.equals(getCidade(), that.getCidade()) &&
               Objects.equals(getEstado(), that.getEstado()) &&
               Objects.equals(getNumero(), that.getNumero()) &&
               Objects.equals(getComplemento(), that.getComplemento());
    }
}
