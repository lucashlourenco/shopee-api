// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/endereco/EnderecoDeEntrega.java

package br.com.ifpe.shopee.model.bd_principal.entity.endereco;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.model.abstrato.Endereco;
import br.com.ifpe.shopee.model.bd_principal.entity.CarrinhoDeCompra;
import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import jakarta.persistence.Column;
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
import lombok.ToString;

@Entity
@Table(name = "endereco_de_entrega")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"cliente", "carrinhos"})
public class EnderecoDeEntrega extends Endereco {

    // Nome de um endereço de entrega.
    @Column
    private String nome;

    // Nome da pessoa principal que recebe no endereço (pode não ser o destinatário...).
    @Column
    private String recebedor;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    @JsonIgnore
    private Cliente cliente;

    @OneToMany(mappedBy = "enderecoDeEntrega")
    private List<CarrinhoDeCompra> carrinhos;

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
        
        EnderecoDeEntrega that = (EnderecoDeEntrega) o;

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
