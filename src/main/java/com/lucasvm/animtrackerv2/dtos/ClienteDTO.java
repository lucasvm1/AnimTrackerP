package com.lucasvm.animtrackerv2.dtos;

import com.lucasvm.animtrackerv2.models.ClienteModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private UUID id;
    private String nome;                          // Nome do cliente
    private ClienteModel.TipoCliente tipo;        // Tipo do cliente (enum)
    private String email_principal;               // E-mail principal
    private String telefone_principal;            // Telefone principal
    private String site;                          // Site do cliente
    private String nome_contato;                  // Nome do contato principal
    private String cargo_contato;                 // Cargo do contato principal
    private String email_secundario;              // E-mail alternativo
    private String telefone_secundario;           // Telefone alternativo
    private ClienteModel.CategoriaCliente categoria; // Categoria do cliente (enum)
    private String observacoes;                   // Observações gerais
    private LocalDateTime data_cadastro;          // Data de cadastro
    private UUID usuario_id;                      // ID do usuário associado
}
