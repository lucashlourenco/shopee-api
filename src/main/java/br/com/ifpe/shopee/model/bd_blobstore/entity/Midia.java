// src/main/java/br/com/ifpe/shopee/model/bd_blobstore/entity/Midia.java

package br.com.ifpe.shopee.model.bd_blobstore.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeNegocioJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "midia")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Midia extends EntidadeNegocioJPA {

    // Tipo MIME
    @Column(name = "tipo_mime", nullable = false)
    @NotNull
    private String tipoMime;

    // Tamanho em bytes
    @Column(name = "tamanho_bytes")
    private long tamanhoBytes;

    // Caminho interno no disco
    @Column(name = "caminho_storage", unique = true, nullable = false)
    @NotNull
    private String caminhoStorage; 

    // URL que o cliente usa para baixar/exibir
    @Column(name = "url_acesso_publico")
    private String urlAcessoPublico;
    
    // O nome da entidade de onde veio o upload
    @Column(name = "classe_de_origem")
    private String classeDeOrigem;

    // O ID da entidade de onde veio o upload
    @Column(name = "id_classe_de_origem")
    private UUID idClasseDeOrigem;

    // O ID do usuário que subiu a mídia
    @Column(name = "id_usuario_de_origem")
    private UUID idUsuarioDeOrigem;

    // Para controle de Limpeza (qualque uma com 0 poderá ser excluida)
    @Column(name = "referencias", nullable = false)
    @Builder.Default
    private int referencias = 0;
}