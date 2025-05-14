package com.lucasvm.animtrackerv2.dtos;

import com.lucasvm.animtrackerv2.models.ProjetoModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjetoDTO {
    private UUID id;
    private String nome;                             // Nome do projeto
    private String descricao;                        // Descrição do projeto
    private ProjetoModel.statusProjeto status;       // Status do projeto (enum)
    private LocalDate data_inicio;                   // Data de início
    private LocalDate data_previsao;                 // Data de previsão de término
    private LocalDate data_conclusao;                // Data real de conclusão
    private ProjetoModel.tipoAnimacao tipo_animacao; // Tipo de animação (enum)
    private BigDecimal duracao_segundos;             // Duração total em segundos
    private String responsavel;                      // Responsável pelo projeto
    private String pasta_arquivos;                   // Caminho da pasta de arquivos
    private String observacoes;                      // Observações gerais
    private LocalDateTime data_cadastro;             // Data de cadastro
    private UUID cliente_id;                         // ID do cliente relacionado
}
