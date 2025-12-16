package br.com.ifpe.shopee.model.bd_secundario.request;

import java.util.List;
import java.util.UUID;

import br.com.ifpe.shopee.model.enums.EstadoDeProdutoEnum;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProdutoRequest {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    private String descricao;
    
    private StatusDeProdutoEnum status;

    private List<String> fotos;

    @NotNull(message = "A categoria é obrigatória")
    private UUID idCategoria;

    // Listas de Sub-entidades
    private List<CaracteristicaRequest> caracteristicas;

    @NotNull(message = "O produto deve ter pelo menos uma variação")
    private List<VariacaoRequest> variacoes;

    // --- SUB-CLASSES DTO ---

    @Data
    public static class CaracteristicaRequest {
        // ID Opcional: Se vier preenchido, é Edição. Se nulo, é Criação.
        private UUID id; 
        
        private UUID idTipoCaracteristica; 
        private Object valor; 
    }

    @Data
    public static class VariacaoRequest {
        // ID Opcional: Se vier preenchido, é Edição. Se nulo, é Criação.
        private UUID id;

        private String nome;
        private EstadoDeProdutoEnum estado;
        private StatusDeProdutoEnum status;
        private double peso;
        private double tamanhoDeEnvioX;
        private double tamanhoDeEnvioY;
        private double tamanhoDeEnvioZ;
        private List<String> fotos;
        
        // Dados do Estoque (Embutido na variação)
        private double preco;
        private int qtdInicial;
    }
}