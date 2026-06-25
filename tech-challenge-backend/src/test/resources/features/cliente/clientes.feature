# language: pt
Funcionalidade: Gerenciamento de Clientes

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Criar novo cliente com sucesso
    Quando um cliente é cadastrado com os seguintes dados:
      | nome | João Silva |
      | cpfCnpj | 12345678901 |
      | email | joao@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 201
    E o cliente deve conter o CPF/CNPJ "12345678901"
    E o cliente deve conter o nome "João Silva"

  Cenário: Criar cliente com dados inválidos (sem nome)
    Quando um cliente é cadastrado com os seguintes dados:
      | nome |  |
      | cpfCnpj | 12345678902 |
      | email | teste@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400

  Cenário: Criar cliente com CPF duplicado deve falhar
    Dado que existe um cliente cadastrado com CPF "98765432100"
    Quando um cliente é cadastrado com os seguintes dados:
      | nome | João Silva |
      | cpfCnpj | 98765432100 |
      | email | joao@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400 ou 409

  Cenário: Buscar cliente por CPF existente
    Dado que existe um cliente cadastrado com CPF "12345678901"
    Quando o cliente é buscado por CPF "12345678901"
    Então o status HTTP deve ser 200
    E o cliente deve ser encontrado

  Cenário: Buscar cliente inexistente deve retornar 404
    Quando o cliente é buscado por CPF "99999999999"
    Então o status HTTP deve ser 404

  Cenário: Listar clientes
    Dado que existem 3 clientes cadastrados no sistema
    Quando a lista de clientes é solicitada
    Então o status HTTP deve ser 200
    E a lista deve conter 3 clientes

  Cenário: Atualizar dados do cliente
    Dado que um cliente foi cadastrado com CPF "11111111111"
    Quando os dados do cliente são atualizados:
      | nome | João Silva Atualizado |
      | email | novoemail@example.com |
    Então o status HTTP deve ser 200
    E o cliente deve ser atualizado com sucesso

  Cenário: Deletar cliente
    Dado que um cliente foi cadastrado com CPF "22222222222"
    Quando o cliente é deletado
    Então o status HTTP deve ser 200
    E o cliente deve ser removido com sucesso
    E ao buscar o cliente deletado por CPF "22222222222" deve retornar 404

  Cenário: Atualizar cliente inexistente deve retornar 404
    Quando os dados do cliente são atualizados com CPF inexistente:
      | cpfCnpj | 99999999999 |
      | nome | Novo Nome |
    Então o status HTTP deve ser 404
