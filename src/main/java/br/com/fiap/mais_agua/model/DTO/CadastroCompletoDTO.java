package br.com.fiap.mais_agua.model.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroCompletoDTO(
        // Dados do Usuário
        @NotBlank(message = "campo obrigatório")
        String nomeUsuario,
        @Email(message = "email inválido")
        @NotBlank(message = "campo obrigatório")
        String email,
        @Size(min = 5)
        String senha,

        // Dados da Unidade
        @NotBlank(message = "Campo obrigatório")

        @NotBlank String nomeUnidade,
        @NotNull(message = "Campo obrigatório")
        Integer capacidade_total_litros,

        // Dados do Endereço
        @Column(nullable = false, length = 50)
         String logradouro,

        @Column(nullable = false)
        Integer numero,
        @Column(length = 50)
         String complemento,

        @Column(nullable = false, length = 8)
         String cep,
        @NotNull Integer idCidade
) {}