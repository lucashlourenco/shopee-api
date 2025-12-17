package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;
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
public class ProdutoComprado extends EntidadeAuditavelData {

    // ID original do produto
    private UUID idProduto;

    // ID da variação original escolhida
    private UUID idVariacao;

    // ID do item de carrinho original (que fica no Postgres/bd_principal)
    private UUID idItemDeCarrinho;

    // Cópia PROFUNDA (Snapshot)
    // Ao salvar aqui, o objeto Variacao inteiro é serializado dentro do documento.
    private Variacao variacaoComprada;

    private Produto produto;
}