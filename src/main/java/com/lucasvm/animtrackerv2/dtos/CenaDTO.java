package com.lucasvm.animtrackerv2.dtos;

import com.lucasvm.animtrackerv2.models.CenaModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CenaDTO {
    private UUID id;
    private String numero_codigo;          // Código identificador da cena
    private String descricao;              // Descrição da cena
    private CenaModel.StatusCena status;   // Status da cena (enum)
    private CenaModel.EstagioCena estagio; // Estágio da cena (enum)
    private int frames;                    // Número de frames da cena
    private BigDecimal duracao;            // Duração da cena em segundos
    private int tempoProducao;             // Tempo de produção em minutos
    private int pontuacao;                 // Pontuação atribuída à cena
    private LocalDate data_inicio;         // Data de início
    private LocalDate data_previsao;       // Data prevista de conclusão
    private LocalDate data_conclusao;      // Data real de conclusão
    private String observacoes;            // Observações gerais
    private UUID projetoId;                // ID do projeto relacionado
}
