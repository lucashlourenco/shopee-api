package br.com.ifpe.shopee.model.bd_secundario.entity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.enums.EstadoDeProdutoEnum;
import br.com.ifpe.shopee.model.enums.StatusDeProdutoEnum;
import br.com.ifpe.shopee.util.entity.bd_nao_relacional.EntidadeAuditavelData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "variacao")
@Getter
@Setter
@Builder // Adicionado para facilitar a criação de objetos
@AllArgsConstructor
@NoArgsConstructor
public class Variacao extends EntidadeAuditavelData {

    // --- CAMPO NOVO (CRÍTICO PARA O CHECKOUT) ---
    private Double preco; 
    // --------------------------------------------

    private EstadoDeProdutoEnum estado;
    private String nome;
    private StatusDeProdutoEnum status;
    private List<String> fotos;
    private String descricao;
    private double peso;

    @Field("tamanho_envio_x")
    private double tamanhoDeEnvioX;

    @Field("tamanho_envio_y")
    private double tamanhoDeEnvioY;

    @Field("tamanho_envio_z")
    private double tamanhoDeEnvioZ;

    private Estoque estoque;

    @DBRef
    private List<Caracteristica> caracteristicas;

    // --- RELACIONAMENTO COM PRODUTO ---
    // Mantendo sua estratégia correta de ID + Transient
    @Transient
    private Produto produto;
    
    // Removi o @JsonIgnore do idProduto, pois o Front pode precisar saber o ID do pai
    private UUID idProduto; 

    // --- RELACIONAMENTOS COM POSTGRES (IDs ÚNICOS) ---
    
    @Transient
    private EnderecoDeEstoque enderecoDeEstoque;
    
    @JsonIgnore
    private UUID idEnderecoDeEstoque; 

    @Transient
    private InformacaoDeRetirada informacaoDeRetirada;
    
    @JsonIgnore
    private UUID idInformacaoDeRetirada;
}