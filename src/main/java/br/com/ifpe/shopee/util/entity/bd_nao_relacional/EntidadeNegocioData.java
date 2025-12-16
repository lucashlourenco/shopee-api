package br.com.ifpe.shopee.util.entity.bd_nao_relacional;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntidadeNegocioData {
    
    @Id 
    private UUID id;

    @JsonIgnore
    private Boolean habilitado = true;

    public void gerarId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}