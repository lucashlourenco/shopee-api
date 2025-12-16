package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "informacao_de_retirada")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacaoDeRetirada extends EntidadeAuditavelJPA {

    @NotBlank(message = "É preciso informar algum dia e horário de retirada.")
    @Column(nullable = false)
    private String diaEHorario;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private EnderecoDeEstoque endereco;

    @ManyToMany
    @JoinTable(
        name = "informacao_de_retirada_contato_de_loja",
        joinColumns = @JoinColumn(name = "id_informacao_de_retirada"),
        inverseJoinColumns = @JoinColumn(name = "id_contato_de_loja")
    )
    private List<ContatoDeLoja> contatos;
}