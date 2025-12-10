// src/main/java/br/com/ifpe/shopee/util/entity/bd_nao_relacional/EntidadeNegocioData.java

package br.com.ifpe.shopee.util.entity.bd_nao_relacional;

import java.util.UUID;

import org.springframework.data.annotation.Id; // Anotação do Spring Data
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
public abstract class EntidadeNegocioData {
    
    // @Id do Spring Data mapeia para o campo _id nativo do MongoDB.
    // O driver MongoDB/Spring Data é inteligente o suficiente para gerar um UUID
    // se o campo estiver nulo antes de salvar (embora não seja o ObjectId padrão).
    @Id 
    private UUID id;

    @JsonIgnore
    private Boolean habilitado = true;
}
