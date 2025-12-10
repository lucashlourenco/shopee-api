// src/main/java/br/com/ifpe/shopee/util/entity/bd_relacional/EntidadeNegocioJPA.java

package br.com.ifpe.shopee.util.entity.bd_relacional;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@MappedSuperclass
public abstract class EntidadeNegocioJPA {

    @Id
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @JsonIgnore
    @Column
    private Boolean habilitado;

    // Método para gerar o UUID ANTES de persistir
    @PrePersist
    public void gerarId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}