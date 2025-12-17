package br.com.ifpe.shopee.model.bd_principal.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.shopee.model.enums.StatusDeEnvioEnum;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeNegocioJPA;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status_de_envio")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusDeEnvio extends EntidadeNegocioJPA {

    @NotNull(message = "O status de envio é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDeEnvioEnum status;

    @CreatedDate
    @Builder.Default
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataDeRegistro = LocalDateTime.now();
    
    @Column
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "id_envio")
    private Envio envio;
}