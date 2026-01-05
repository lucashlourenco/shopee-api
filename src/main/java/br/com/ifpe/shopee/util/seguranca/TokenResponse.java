// src/main/java/br/com/ifpe/shopee/util/seguranca/TokenResponse.java

package br.com.ifpe.shopee.util.seguranca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private long expiresIn; // Tempo em milissegundos ou segundos
}