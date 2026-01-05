// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Usuario.java

package br.com.ifpe.shopee.model.bd_principal.entity;

import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.ifpe.shopee.model.abstrato.TipoDeUsuario;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.util.entity.bd_relacional.EntidadeAuditavelJPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario extends EntidadeAuditavelJPA implements UserDetails {

	@OneToMany(mappedBy = "usuario")
	private List<ContatoDeLogin> credenciais;

	@JsonIgnore
	@Column(nullable = false)
	@NotNull(message = "A senha é obrigatória.")
	private String senha;

	// src/main/java/br/com/ifpe/shopee/model/bd_principal/entity/Usuario.java
    @OneToOne
    @JoinColumn(name = "id_pessoa", nullable = false, unique = true)
    @NotNull(message = "O usuário precisa está associado a uma pessoa.")
    private Pessoa pessoa;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER) 
    // FetchType.EAGER para carregar os tipos de usuários imediatamente
    private List<TipoDeUsuario> tipos;

    // -------------------------------------------------
    // Métodos do user details
    // -------------------------------------------------

    @Override
    @JsonIgnore // Para não virar loop no JSON
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Transforma a lista de 'tipos' (Cliente/Vendedor) em Roles do Spring
        // Se for Cliente -> vira "ROLE_CLIENTE"
		// Se for Vendedor -> vira "ROLE_VENDEDOR"
        return this.tipos.stream()
            .map(tipo -> new SimpleGrantedAuthority("ROLE_" + tipo.getClass().getSimpleName().toUpperCase())) // Pega o nome da classe
            .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        // Usando o primeiro login ativo como "username" do sistema
        // Já que temos um lista de credenciais, pegamos a primeira válida
        if (credenciais != null && !credenciais.isEmpty()) {
            return credenciais.get(0).getValor();
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return super.getHabilitado(); }
}
