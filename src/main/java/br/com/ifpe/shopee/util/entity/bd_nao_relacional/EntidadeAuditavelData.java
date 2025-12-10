// src/main/java/br/com/ifpe/shopee/util/entity/bd_nao_relacional/EntidadeAuditavelData.java

package br.com.ifpe.shopee.util.entity.bd_nao_relacional;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
// O TipoDeUsuario deve ser mapeável para o MongoDB (o Spring Data Mongo
// salvará a referência/ID do TipoDeUsuario, não a entidade completa por padrão).

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Não usamos @MappedSuperclass em MongoDB, apenas herança simples.
public abstract class EntidadeAuditavelData extends EntidadeNegocioData {

    // O Spring Data MongoDB garante que a operação de salvamento (save())
    // só terá sucesso se o campo versao no documento for o mesmo valor lido.
    // Se houver divergência, ele lança um OptimisticLockingFailureException.
    @JsonIgnore
    @Version
    private Long versao;

    @JsonIgnore
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    @JsonIgnore
    @LastModifiedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataUltimaModificacao;

    // TODO: AuditorAware  deve ser configurado para retornar o UUID do usuário logado.

    // No MongoDB será salvo apenas a referência (ID) do usuário.
    @CreatedBy
    private UUID criadoPorId;

    @LastModifiedBy
    private UUID ultimaModificacaoPorId;
}