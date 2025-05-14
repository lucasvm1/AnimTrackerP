package com.lucasvm.animtrackerv2.models;

import com.lucasvm.animtrackerv2.utils.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projetos")
@Data
public class ProjetoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Convert(converter = AttributeEncryptor.class)
    private String nome;                          // Nome do projeto (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    @Column(columnDefinition = "TEXT")
    private String descricao;                     // Descrição do projeto (criptografado)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private statusProjeto status;                 // Status do projeto (enum)

    private LocalDate data_inicio;                // Data de início

    private LocalDate data_previsao;              // Data de previsão de término

    private LocalDate data_conclusao;             // Data real de conclusão

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private tipoAnimacao tipo_animacao;           // Tipo de animação (enum)

    private BigDecimal duracao_segundos;          // Duração total em segundos

    @Convert(converter = AttributeEncryptor.class)
    private String responsavel;                   // Responsável pelo projeto (criptografado)

    @Convert(converter = AttributeEncryptor.class)
    private String pasta_arquivos;                // Caminho da pasta de arquivos (criptografado)

    @Column(columnDefinition = "TEXT")
    private String observacoes;                   // Observações gerais

    @CreationTimestamp
    private LocalDateTime data_cadastro;          // Data de cadastro (automática)

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel cliente;                 // Cliente associado ao projeto

    // Enum para status do projeto
    public enum statusProjeto {
        NAO_INICIADO("Não Iniciado"),
        EM_ANDAMENTO("Em Andamento"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado");

        private final String displayName;

        statusProjeto(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Enum para tipo de animação do projeto
    public enum tipoAnimacao {
        TRADICIONAL("Animação Tradicional"),
        ANIMACAO_2D("Animação 2D"),
        ANIMACAO_3D("Animação 3D"),
        MOTION_GRAPHICS("Motion Graphics"),
        STOP_MOTION("Stop Motion"),
        CUTOUT("Cutout"),
        OUTROS("Outros");

        private final String displayName;

        tipoAnimacao(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
