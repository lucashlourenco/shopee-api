// src/main/java/br/com/ifpe/shopee.config.seguranca.SecurityConfiguration.java

package br.com.ifpe.shopee.config.seguranca;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.ifpe.shopee.util.seguranca.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // Configurado no application.properties - define de onde aceitará requisições
    @Value("${api.cors.allowed-origins}")
    private List<String> allowedOrigins;

    private final AuthenticationProvider authenticationProvider; // Configurado no ApplicationConfig
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                // 1. Área Pública
                // (Login, Cadastro inicial, Visualização)
                .requestMatchers(HttpMethod.POST, "/api/v1/publico/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/publico/**").permitAll()
                
                // Cadastro (qualquer um pode se registrar)
                .requestMatchers(HttpMethod.POST, "/api/v1/cliente").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/vendedor").permitAll()

                // Swagger (Documentação)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // 2. Área do vendedor (Gestão de Loja, Estoque, Produtos...)
                .requestMatchers("/api/v1/vendedor/**").hasRole("VENDEDOR") 

                // 3. Área do cliente (Endereços de Entrega, Compras...)
                .requestMatchers("/api/v1/cliente/**").hasRole("CLIENTE")

                // 4. Área comum (Dados Pessoais, Senha...)
                // Qualquer usuário autenticado pode acessar
                .requestMatchers("/api/v1/usuario/**").authenticated()

                // 5. Bloqueia todo o resto por padrão
                .anyRequest().authenticated()
            )
            // Define que não usaremos Cookies/Sessão do servidor
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        
        // Define quem sabe checar a senha
        .authenticationProvider(authenticationProvider)
        
        // Adiciona o nosso filtro de Token antes do filtro padrão
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true); // Importante para Cookies/Auth

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}