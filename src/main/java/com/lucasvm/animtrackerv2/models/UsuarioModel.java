package com.lucasvm.animtrackerv2.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;                       // Nome do usuário

    @Column(nullable = true)
    private LocalDate data_nascimento;         // Data de nascimento

    @Email
    @Column(nullable = false)
    private String email;                      // E-mail do usuário

    @Column(nullable = false)
    private String senha;                      // Senha (criptografada)

    @Enumerated(EnumType.STRING)
    private UsuarioStatus status = UsuarioStatus.ATIVO; // Status da conta (enum)

    private String auth_provider = "Local";    // Provedor de autenticação (default: Local)

    // Enum para status do usuário
    @Getter
    public enum UsuarioStatus {
        ATIVO("Ativo"),
        INATIVO("Inativo"),
        BLOQUEADO("Bloqueado");

        private final String displayName;

        UsuarioStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @CreationTimestamp
    private LocalDate data_cadastro;           // Data de cadastro

    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
