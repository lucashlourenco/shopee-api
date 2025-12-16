package br.com.ifpe.shopee.model.bd_principal.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String nome;

    private boolean estaAtiva = true; // Padrão true

    // Lista de Marcas (Ex: ["Samsung", "Apple", "Xiaomi"])
    private List<String> marcasReconhecidas;

    // ID da Categoria Pai (Opcional - se nulo, é uma categoria Raiz)
    private UUID idPai;

    // IDs dos Tipos de Característica associados (Ex: ID de "Voltagem", ID de "Cor")
    private List<UUID> idsTipoCaracteristica;
}