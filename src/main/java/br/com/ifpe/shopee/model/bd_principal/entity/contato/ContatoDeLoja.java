package br.com.ifpe.shopee.model.bd_principal.entity.contato;

import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.shopee.model.abstrato.contato.ContatoBasico;
import br.com.ifpe.shopee.model.bd_principal.entity.InformacaoDeRetirada;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contato_de_loja")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContatoDeLoja extends ContatoBasico {

    @ManyToOne
    @JoinColumn(name = "id_loja")
    private Loja loja;

    @ManyToMany(mappedBy = "contatos")
    private List<InformacaoDeRetirada> informacoesDeRetirada;
}
