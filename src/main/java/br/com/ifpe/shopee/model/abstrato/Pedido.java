package br.com.ifpe.shopee.model.abstrato;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.ifpe.shopee.model.bd_secundario.entity.Pagamento; // Importante
import br.com.ifpe.shopee.model.bd_secundario.entity.StatusDePedido;
import br.com.ifpe.shopee.model.enums.StatusDePedidoEnum;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Pedido extends EntidadeAuditavelData {

    private String observacao;

    // --- CORREÇÃO DO ERRO ---
    // O Service tenta salvar o objeto Pagamento inteiro dentro do pedido.
    // Adicionamos esta lista para o Lombok gerar o setPagamentos().
    private List<Pagamento> pagamentos = new ArrayList<>();

    // Mantive sua lista de IDs caso queira usar para indexação rápida
    private List<UUID> idsPagamento;

    // Histórico de Status
    private List<StatusDePedido> status = new ArrayList<>();
    
    // Status ATUAL
    private StatusDePedidoEnum statusAtual;

    // IDs dos itens (Soft Delete no Postgres)
    private List<UUID> idsItensDeCarrinho;
    
    public void adicionarStatus(StatusDePedidoEnum novoStatus, String obs) {
        if (this.status == null) this.status = new ArrayList<>();
        
        this.status.add(StatusDePedido.criar(novoStatus, obs));
        this.statusAtual = novoStatus;
    }
}