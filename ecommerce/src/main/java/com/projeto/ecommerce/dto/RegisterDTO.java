package com.projeto.ecommerce.dto;
import com.projeto.ecommerce.enums.TipoUsuario;
public record RegisterDTO(String nome, String email, String senha, TipoUsuario tipo) {}