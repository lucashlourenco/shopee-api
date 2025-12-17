package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import br.com.ifpe.shopee.model.enums.StatusDePagamentoEnum;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pagamento")
@Builder // Essencial para o Service construir o objeto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento extends EntidadeAuditavelData {

    private UUID idPedido;

    // --- CAMPOS ADICIONADOS (Necessários para o Checkout) ---
    private String formaDePagamento; // Ex: PIX, CREDITO
    private Double valor;
    private int parcelas;
    // --------------------------------------------------------

    private StatusDePagamentoEnum statusAtual;

    // Histórico de Status (Mantendo seu nome de variável)
    @Builder.Default
    private List<StatusDePagamento> historicoStatus = new ArrayList<>();

    // Detalhes da API externa (Mantendo seu campo)
    private RespostaDePagamento detalhes;

    private String observacaoGeral;

    // Método auxiliar usado no Service
    public void adicionarStatus(StatusDePagamentoEnum novoStatus, String obs) {
        if (this.historicoStatus == null) this.historicoStatus = new ArrayList<>();
        
        StatusDePagamento statusObj = StatusDePagamento.builder()
            .status(novoStatus)
            .observacao(obs)
            .build(); // Data e ID gerados automaticamente
            
        this.historicoStatus.add(statusObj);
        this.statusAtual = novoStatus;
    }
}