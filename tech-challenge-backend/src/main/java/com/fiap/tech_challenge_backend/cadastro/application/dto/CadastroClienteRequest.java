package com.fiap.tech_challenge_backend.cadastro.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email deve ser válido")
        @Size(max = 150, message = "O email deve ter o máximo 150 caracteres")
        String email,


        @NotBlank(message = "O CPF/CNPJ é obrigatório")
        @Size(min = 11, max = 18, message = "O CPF/CNPJ deve ter entre 11 e 18 caracteres")
        String cpfCnpj,

        @Size(min = 10, max = 20, message = "O telefone deve ter entre 10 e 20 caracteres")
        String telefone,

        @Size(min = 8, max = 10, message = "O CEP deve ter entre 8 e 10 caracteres")
        String cep,

        @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres")
        String rua,

        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
        String numero,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complemento,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres")
        String estado
) {
}
