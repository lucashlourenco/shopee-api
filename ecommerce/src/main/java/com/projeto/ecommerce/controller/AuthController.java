package com.projeto.ecommerce.controller;

import com.projeto.ecommerce.dto.AuthDTO;
import com.projeto.ecommerce.dto.LoginResponseDTO;
import com.projeto.ecommerce.dto.RegisterDTO;
import com.projeto.ecommerce.model.Usuario;
import com.projeto.ecommerce.repository.UsuarioRepository;
import com.projeto.ecommerce.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDTO data){
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        Usuario newUser = new Usuario();
        newUser.setNome(data.nome());
        newUser.setEmail(data.email());
        newUser.setSenhaHash(encryptedPassword);
        newUser.setTipo(data.tipo());
        
        this.repository.save(newUser);
        return ResponseEntity.ok().build();
    }
}