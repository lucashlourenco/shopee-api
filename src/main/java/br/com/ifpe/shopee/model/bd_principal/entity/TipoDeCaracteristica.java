// src/main/java/br/com/ifpe/shopee/model/bd_principal/TipoDeCaracteristica.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;
import br.com.ifpe.shopee.model.bd_secundario.entity.Caracteristica;
import br.com.ifpe.shopee.model.enums.TipoDeDadoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_de_caracteristica")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoDeCaracteristica extends EntidadeAuditavelJPA {

	@NotBlank(message = "O nome é obrigatório.")
	@Column(nullable = false)
	private String nome;

	@NotBlank(message = "O tipo de dado é obrigatório.")
	@Column(nullable = false)
	private TipoDeDadoEnum tipoDeDado;

	@Column
	private boolean eObrigatoria;

	@ManyToMany(mappedBy = "caracteristicas")
	private List<Categoria> categorias;

	@Transient
	private List<Caracteristica> caracteristicas;

	@Override
    public int hashCode() {
        // Se o id existe, use o ID. Caso contrário, use o hash semântico
        if (getId() != null) {
            return getId().hashCode();
        }
        return Objects.hash(nome, tipoDeDado, eObrigatoria);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TipoDeCaracteristica that = (TipoDeCaracteristica) o;

        // Regra 1: Igualdade por Identidade (UUID)
        if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
            return true;
        }

        // Regra 2: Igualdade Semântica (Nome, Tipo de Dado, e Obrigatoriedade)
        if (Objects.equals(nome, that.nome) &&
            Objects.equals(tipoDeDado, that.tipoDeDado) &&
            eObrigatoria == that.eObrigatoria) {
            return true;
        }

        return false;
    }
}
