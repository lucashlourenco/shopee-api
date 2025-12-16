package br.com.ifpe.shopee.model.bd_secundario.request;

import java.util.List;
import java.util.UUID;

import br.com.ifpe.shopee.model.enums.EstadoDeProdutoEnum;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VariacaoRequest {

    private UUID idProduto;


    @NotBlank(message = "O nome da variação é obrigatório")
    private String nome;

    private EstadoDeProdutoEnum estado;
    private StatusDeProdutoEnum status;
    private String descricao;

    @Min(value = 0, message = "O peso deve ser maior que zero")
    private double peso;
    
    private double tamanhoDeEnvioX;
    private double tamanhoDeEnvioY;
    private double tamanhoDeEnvioZ;

    private List<String> fotos;

    @Valid
    @NotNull(message = "As informações de estoque são obrigatórias")
    private EstoqueRequest estoque;
    
    // Lista de Características (Ex: Cor: Azul, Tamanho: P)
    private List<CaracteristicaRequest> caracteristicas; 

    // IDs para integração com Postgres
    private UUID idEnderecoDeEstoque;
    private UUID idInformacaoDeRetirada;

    @Data
    public static class EstoqueRequest {
        @Min(0)
        private double preco;
        @Min(0)
        private int qtdInicial;
    }
    
    @Data
    public static class CaracteristicaRequest {
        private UUID id; // Opcional (para edição)
        private UUID idTipoCaracteristica; 
        private Object valor; 
    }
}