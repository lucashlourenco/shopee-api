// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/contato/ContatoDeLoja.java

package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.contato.ContatoBasico;
import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contato_de_loja")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDeLoja extends ContatoBasico {

    @ManyToOne
    @JoinColumn(name = "id_loja")
    private Loja loja;

    @ManyToMany(mappedBy = "contatos")
    private List<InformacaoDeRetirada> informacoesDeRetirada;

    @Override
    public int hashCode() {
        // Usa o ID se existir (padrão JPA) OU a igualdade semântica
        if (getId() != null) {
            return getId().hashCode();
        }
        
        return Objects.hash(getLoja().getId(), getValor(), getTipo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ContatoDeLoja that = (ContatoDeLoja) o;

        // Regra 1: Igualdade por Identidade (UUID)
        if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
            return true;
        }

        // Regra 2: Igualdade Semântica (mesmo Valor e Tipo de uma mesma Loja)
        return  Objects.equals(getLoja().getId(), that.getLoja().getId()) &&
                Objects.equals(getValor(), that.getValor()) &&
                Objects.equals(getTipo(), that.getTipo());
    }
}
