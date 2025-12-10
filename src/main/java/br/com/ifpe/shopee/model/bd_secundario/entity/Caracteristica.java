// src/main/java/br/com/ifpe/shopee/model/bd_secundario/entity/Caracteristica.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import br.com.ifpe.shopee.model.bd_principal.entity.TipoDeCaracteristica;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "caracteristica")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Caracteristica extends EntidadeAuditavelData {

	// A característica tem um tipo e o tipo pode ser de várias características
	@Transient
	private TipoDeCaracteristica tipo;
	private UUID idTipo;

	// O valor vai depender do que TipoDeCaracteristica determinar.
	// Se for texto, vai ser String se Int ou se for número vai ser Double
	private Object valor;

	// A característica pode estar em vários produtos e o produto pode ter varias características
	@DBRef
	private List<Produto> produtos;

	// A característica pode estar em várias variações e uma variação pode ter várias características
	@DBRef
	private List<Variacao> variacoes;


	@Override
    public int hashCode() {
        // Se o id existe (já foi persistido), use o ID.
        if (getId() != null) {
            return getId().hashCode();
        }
        // Se não, use o hash do tipo e valor (igualdade semântica).
        return Objects.hash(idTipo, valor);
    }

    // Método equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        // Verifica se é da mesma classe
        if (o == null || getClass() != o.getClass()) return false;
        
        Caracteristica that = (Caracteristica) o;

        // Regra 1: Igualdade por Identidade (UUID)
        // Se ambos têm IDs e eles são iguais, são o mesmo objeto persistido.
        if (getId() != null && that.getId() != null && getId().equals(that.getId())) {
            return true;
        }
        
        // Regra 2: Igualdade Semântica (Para prevenção de duplicação/comparação lógica)
        // Se ambos têm o mesmo TipoDeCaracteristica E o mesmo valor, são logicamente iguais.
        if (Objects.equals(idTipo, that.idTipo) && Objects.equals(valor, that.valor)) {
            return true;
        }

        // Se nenhuma das regras for satisfeita, eles são diferentes.
        return false;
    }
}
