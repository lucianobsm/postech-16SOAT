# language: pt
Funcionalidade: Validações Obrigatórias do Sistema

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Validar formato de CPF com menos de 11 dígitos
    Quando um cliente é cadastrado com CPF inválido "123456789"
    Então o status HTTP deve ser 400

  Cenário: Validar CPF vazio
    Quando um cliente é cadastrado com os seguintes dados:
      | nome | João Silva |
      | cpfCnpj |  |
      | email | joao@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400

  Cenário: Validar nome do cliente obrigatório
    Quando um cliente é cadastrado com os seguintes dados:
      | nome |  |
      | cpfCnpj | 12345678901 |
      | email | joao@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400

  Cenário: Validar tamanho mínimo do nome do cliente
    Quando um cliente é cadastrado com os seguintes dados:
      | nome | JJ |
      | cpfCnpj | 12345678901 |
      | email | joao@example.com |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400

  Cenário: Validar tamanho máximo do nome do cliente
    Quando um cliente é cadastrado com nome com mais de 150 caracteres
    Então o status HTTP deve ser 400

  Cenário: Validar formato de email (se requerido)
    Quando um cliente é cadastrado com os seguintes dados:
      | nome | João Silva |
      | cpfCnpj | 12345678901 |
      | email | email_invalido |
      | telefone | 11999999999 |
    Então o status HTTP deve ser 400

  Cenário: Validar formato de placa de veículo
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa | A1B2C34 |
      | marca | Toyota |
      | modelo | Corolla |
      | ano | 2020 |
    Então o status HTTP deve ser 400

  Cenário: Validar ano do veículo não pode ser futuro
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa | ABC1234 |
      | marca | Toyota |
      | modelo | Corolla |
      | ano | 2050 |
    Então o status HTTP deve ser 400

  Cenário: Validar descrição da ordem de serviço obrigatória
    Dado que existe um cliente cadastrado com CPF "12345678901"
    E que existe um veículo cadastrado com placa "ABC1234"
    E que o cliente "12345678901" possui o veículo "ABC1234" associado
    Quando uma nova ordem de serviço é criada com os dados:
      | cpfCnpj | 12345678901 |
      | placa | ABC1234 |
      | descricao |  |
    Então o status HTTP deve ser 400

  Cenário: Validar valor do orçamento não pode ser negativo
    Dado que existe uma ordem de serviço criada
    Quando um orçamento é criado para a ordem de serviço com:
      | descricao | Orçamento |
      | valorTotal | -100.00 |
    Então o status HTTP deve ser 400

  Cenário: Validar quantidade de peça não pode ser negativa
    Quando uma nova peça é cadastrada com os seguintes dados:
      | codigo | PECA-001 |
      | nome | Peça Teste |
      | quantidade | -5 |
      | preco_unitario | 100.00 |
      | localizacao | Prateleira A1 |
    Então o status HTTP deve ser 400

  Cenário: Validar preço unitário não pode ser negativo
    Quando uma nova peça é cadastrada com os seguintes dados:
      | codigo | PECA-001 |
      | nome | Peça Teste |
      | quantidade | 10 |
      | preco_unitario | -50.00 |
      | localizacao | Prateleira A1 |
    Então o status HTTP deve ser 400

  Cenário: Validar campos obrigatórios de serviço
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome |  |
      | descricao | Descrição |
      | valor_hora | 100.00 |
      | tempo_estimado_horas | 1.0 |
    Então o status HTTP deve ser 400
