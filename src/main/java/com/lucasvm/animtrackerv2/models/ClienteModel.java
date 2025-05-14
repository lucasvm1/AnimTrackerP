package com.lucasvm.animtrackerv2.models;

import com.lucasvm.animtrackerv2.utils.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Data
public class ClienteModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    @Convert(converter = AttributeEncryptor.class)
    private String nome;                           // Nome do cliente (criptografado)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCliente tipo;                      // Tipo do cliente (enum)

    @Convert(converter = AttributeEncryptor.class)
    private String email_principal;                // E-mail principal (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String telefone_principal;             // Telefone principal (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String site;                           // Site do cliente (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String nome_contato;                   // Nome do contato (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String cargo_contato;                  // Cargo do contato (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String email_secundario;               // E-mail secundário (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String telefone_secundario;            // Telefone secundário (criptografado)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCliente categoria;            // Categoria do cliente (enum)

    @Column(columnDefinition = "TEXT")
    private String observacoes;                    // Observações gerais

    @CreationTimestamp
    private LocalDateTime data_cadastro;           // Data de cadastro (automática)

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario;                  // Usuário associado

    // Enum para tipo de cliente
    @Getter
    public enum TipoCliente {
        PESSOA_FISICA("Pessoa Física"),
        PESSOA_JURIDICA("Pessoa Jurídica");

        private final String displayName;

        TipoCliente(String displayName) {
            this.displayName = displayName;
        }
    }

    // Enum para categoria do cliente
    @Getter
    public enum CategoriaCliente {
        CINEMA("Cinema"),
        STREAMING("Streaming"),
        JOGOS("Jogos"),
        PUBLICIDADE("Publicidade"),
        YOUTUBE("YouTube"),
        REDES_SOCIAIS("Redes Sociais"),
        EDUCACAO("Educação"),
        CORPORATIVO("Corporativo"),
        TECNOLOGIA("Tecnologia"),
        EVENTOS("Eventos"),
        MUSICA("Música"),
        MARKETING_DIGITAL("Marketing Digital"),
        OUTROS("Outros");

        private final String displayName;

        CategoriaCliente(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
