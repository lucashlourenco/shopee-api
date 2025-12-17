package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.model.abstrato.Pedido;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_secundario.entity.snapshot.EnderecoSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pedido")
@TypeAlias("PedidoDeCliente")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDeCliente extends Pedido {

    private UUID idCliente;
    
    // Vendedor Principal (Snapshot/Referência)
    private UUID idVendedor;
    private String nomeDeVendedor;
    private Loja loja;
    private EnderecoComercial enderecoDaLoja;
    private List<ContatoDeLoja> contatosDaLoja;

    // Financeiro
    private Double valorTotal;

    // Navegação para sub-pedidos
    @Builder.Default
    private List<UUID> idsPedidosDeVendedores = new ArrayList<>();

    // --- ROBUSTEZ: SNAPSHOT DE ENDEREÇO ---
    // Mantemos o ID para referência rápida
    private UUID idEnderecoEntregaOriginal; 
    
    // Guardamos a cópia real dos dados para auditoria/entrega
    private EnderecoSnapshot enderecoEntregaSnapshot;
}