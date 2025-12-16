package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categoria")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// MUDANÇA 2: Estender EntidadeAuditavelJPA (para ter as datas)
public class Categoria extends EntidadeAuditavelJPA {

    @NotBlank(message = "O nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @Column
    private Boolean estaAtiva; // Mudei para Boolean (Wrapper) para evitar problemas com null no update

    @ElementCollection
    @CollectionTable(name = "categoria_marcas_reconhecidas", joinColumns = @JoinColumn(name = "id_categoria"))
    @Column(name = "marca_reconhecida")
    private List<String> marcasReconhecidas;

    @ManyToOne
    @JoinColumn(name = "id_categoria_pai")
    @JsonIgnore
    private Categoria pai;

    @OneToMany(mappedBy = "pai")
    private List<Categoria> filhas;

    @ManyToMany
    @JoinTable(
        name = "categoria_tipo_caracteristica",
        joinColumns = @JoinColumn(name = "id_categoria"),
        inverseJoinColumns = @JoinColumn(name = "id_tipo_caracteristica")
    )
    private List<TipoDeCaracteristica> caracteristicas;
}