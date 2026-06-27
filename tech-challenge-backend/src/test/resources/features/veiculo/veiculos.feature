# language: pt
Funcionalidade: Gerenciamento de Veículos

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Criar novo veículo com sucesso
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa | ABC1234 |
      | marca | Toyota |
      | modelo | Corolla |
      | ano | 2020 |
    Então o status HTTP deve ser 201
    E o veículo deve conter a placa "ABC1234"
    E o veículo deve conter a marca "Toyota"

  Cenário: Criar veículo com placa duplicada deve falhar
    Dado que existe um veículo cadastrado com placa "XYZ5678"
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa | XYZ5678 |
      | marca | Ford |
      | modelo | Focus |
      | ano | 2021 |
    Então o status HTTP deve ser 400 ou 409

  Cenário: Criar veículo com dados inválidos (sem placa)
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa |  |
      | marca | Honda |
      | modelo | Civic |
      | ano | 2019 |
    Então o status HTTP deve ser 400

  Cenário: Buscar veículo por placa existente
    Dado que existe um veículo cadastrado com placa "LMN9876"
    Quando o veículo é buscado por placa "LMN9876"
    Então o status HTTP deve ser 200
    E o veículo deve ser encontrado

  Cenário: Buscar veículo inexistente deve retornar 404
    Quando o veículo é buscado por placa "INVALIDO"
    Então o status HTTP deve ser 404

  Cenário: Listar veículos
    Dado que existem 2 veículos cadastrados no sistema
    Quando a lista de veículos é solicitada
    Então o status HTTP deve ser 200
    E a lista deve conter 2 veículos

  Cenário: Atualizar dados do veículo
    Dado que um veículo foi cadastrado com placa "OLD1234"
    Quando os dados do veículo são atualizados:
      | marca | Honda |
      | modelo | Civic |
      | ano | 2022 |
    Então o status HTTP deve ser 200
    E o veículo deve ser atualizado com sucesso

  Cenário: Deletar veículo
    Dado que um veículo foi cadastrado com placa "DEL5678"
    Quando o veículo é deletado
    Então o status HTTP deve ser 200
    E o veículo deve ser removido com sucesso
    E ao buscar o veículo deletado por placa "DEL5678" deve retornar 404
