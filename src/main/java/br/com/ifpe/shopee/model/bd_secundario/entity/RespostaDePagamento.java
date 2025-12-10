// src/main/java/br/com/ifpe/shopee.model.bd_secundario.entity/RespostaDePagamento.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

// REMOVER: import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeNegocioData;
// REMOVER: import org.springframework.data.mongodb.core.mapping.Document;
// Esta classe é apenas o subdocumento (Não herda, não tem @Document)

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespostaDePagamento {

    // status, valor, formaDePagamento, nDeVezes... Uma emulação de resposta de um pedido de pagamento
    @Field("resposta_de_pagamento")
    private Object respostaDePagamento;
}