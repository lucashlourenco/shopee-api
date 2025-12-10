// src/main/java/br/com/ifpe/shopee/util/entity/bd_relacional/EntidadeAuditavelJPA.java

package br.com.ifpe.shopee.util.entity.bd_relacional;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class EntidadeAuditavelJPA extends EntidadeNegocioJPA {
    
    @JsonIgnore
    @Version
    private Long versao;

    @JsonIgnore
    @LastModifiedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataUltimaModificacao;

    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    // TODO: Talvez precise de um AuditorAware além do Spring Security

    @CreatedBy
    @ManyToOne
    @JoinColumn
    private TipoDeUsuario criadoPor;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn
    private TipoDeUsuario ultimaModificacaoPor;
}