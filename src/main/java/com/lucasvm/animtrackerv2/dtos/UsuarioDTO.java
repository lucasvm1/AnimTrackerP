package com.lucasvm.animtrackerv2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private UUID id;
    private String nome;                // Nome do usuário
    private LocalDate data_nascimento;  // Data de nascimento
    private String email;               // E-mail do usuário
    private String senha;               // Senha (criptografada)
    private String status;              // Status da conta (ex: ATIVO)
    private String auth_provider;       // Provedor de autenticação (ex: Local, Google)
}
