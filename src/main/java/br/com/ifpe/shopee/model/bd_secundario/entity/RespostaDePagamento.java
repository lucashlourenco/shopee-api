package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespostaDePagamento {
    
    // --- DO SEU CÓDIGO ORIGINAL (Mantidos) ---
    private String idTransacaoExterna; // ID do Gateway (Stripe/PayPal)
    private Double valor;
    private String formaDePagamento;
    private Integer nDeVezes;
    private String urlComprovante;

    // --- CAMPOS DE ROBUSTEZ (Adicionados para Auditoria) ---
    
    // Status que veio do Gateway (ex: "authorized", "failed", "pending")
    // Diferente do nosso StatusDePagamentoEnum, este é o texto puro da operadora.
    private String statusOperadora; 
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataTransacao;
    
    // Guarda o JSON bruto da resposta (Vital para debug se o cliente reclamar)
    private Map<String, Object> payloadCompleto;
    
    private String mensagemErro; // Caso o gateway recuse
}