// src/main/java/br/com/ifpe/shopee/model/bd_secundario/entity/ProdutoComprado.java

package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeNegocioData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "produto_comprado")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoComprado extends EntidadeNegocioData {

    // ID original do produto
    private UUID idProduto;

    // ID da variação original escolhida
    private UUID idVariacao;

    // ID do item de carrinho original - registro permanece no bd_principal inalterável (sem edição por usuário)
    private UUID idItemDeCarrinho;

    // Por comodidade, inclui aqui a variação inteira que foi comprada; cópia da variação original
    private Variacao variacaoComprada;

    // O produto inteiro; cópia do produto original
    private Produto produto;
}
