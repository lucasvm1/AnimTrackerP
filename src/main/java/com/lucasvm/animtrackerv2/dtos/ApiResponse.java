package com.lucasvm.animtrackerv2.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO genérico para padronizar respostas de API (success, message e data)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;         // Dados retornados pela API
    private boolean success; // Indica se a requisição foi bem-sucedida
    private String message;  // Mensagem de retorno (erro ou sucesso)
}
