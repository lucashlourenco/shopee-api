package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.Date; // O diagrama pede Date, mas LocalDateTime é melhor. Mantenha Date se preferir seguir estrito.

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Estoque {
    
    // Conforme Diagrama
    private int id; // Pode ser int ou UUID dependendo da sua padronização interna
    private Double preco;
    private int qtdInicial;
    private int qtdVendida;
    private Date dataFinal;

    // --- CORREÇÃO DO ERRO ---
    // Adicionamos este campo para facilitar a lógica do CarrinhoService
    // Ou ele pode ser calculado: (qtdInicial - qtdVendida)
    private int qtdAtual; 

    // Se preferir calculado (apague o campo acima e use este método):
    // public int getQtdAtual() {
    //    return this.qtdInicial - this.qtdVendida;
    // }
}