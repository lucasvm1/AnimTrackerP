package com.lucasvm.animtrackerv2.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cenas")
@Data
public class CenaModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String numero_codigo;          // Código identificador da cena

    @Column(columnDefinition = "TEXT")
    private String descricao;              // Descrição da cena

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCena status;             // Status da cena (enum)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstagioCena estagio;           // Estágio da cena (enum)

    private int frames;                    // Número de frames da cena

    private BigDecimal duracao;            // Duração em segundos

    private int tempoProducao;             // Tempo de produção em minutos

    private int pontuacao;                 // Pontuação da cena

    private LocalDate data_inicio;         // Data de início

    private LocalDate data_previsao;       // Data prevista de conclusão

    private LocalDate data_conclusao;      // Data real de conclusão

    @Column(columnDefinition = "TEXT")
    private String observacoes;            // Observações gerais

    @CreationTimestamp
    private LocalDateTime data_cadastro;   // Data de cadastro (automática)

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private ProjetoModel projeto;          // Projeto relacionado

    // Enum para status da cena
    @Getter
    public enum StatusCena {
        NAO_INICIADA("Não Iniciada"),
        EM_PRODUCAO("Em Produção"),
        PAUSADA("Pausada"),
        APROVADA("Aprovada");

        private final String displayName;

        StatusCena(String displayName) {
            this.displayName = displayName;
        }
    }

    // Enum para estágio da cena
    @Getter
    public enum EstagioCena {
        POSE("Pose"),
        ANIMACAO("Animação"),
        CORRECAO("Correção");

        private final String displayName;

        EstagioCena(String displayName) {
            this.displayName = displayName;
        }
    }
}
