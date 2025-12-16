package br.com.ifpe.shopee.model.bd_principal.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaTreeResponse {

    private UUID id;
    private String nome;
    private boolean estaAtiva;
    
    private List<CategoriaTreeResponse> subcategorias;
}