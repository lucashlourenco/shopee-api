package com.projeto.ecommerce.model;

import com.projeto.ecommerce.enums.TipoDocumento;
import com.projeto.ecommerce.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    
    @Column(unique = true)
    private String email;
    
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;

    private String telefone;

    @Enumerated(EnumType.STRING)
    private TipoDocumento docTipo;

    @Column(unique = true)
    private String docNumero;

    private String nacionalidade;
    private java.time.LocalDate dataNascimento;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Loja loja;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.tipo == TipoUsuario.VENDEDOR) 
            return List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR"), new SimpleGrantedAuthority("ROLE_CLIENTE"));
        else 
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
    @Override
    public String getPassword() { return senhaHash; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}