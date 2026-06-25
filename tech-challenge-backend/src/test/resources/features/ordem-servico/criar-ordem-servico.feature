# language: pt
Funcionalidade: Criação de Ordem de Serviço

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Criar ordem de serviço com sucesso
    Dado que existe um cliente cadastrado com CPF "12345678901"
    E que existe um veículo cadastrado com placa "ABC1234"
    E que o cliente "12345678901" possui o veículo "ABC1234" associado
    Quando uma nova ordem de serviço é criada com os dados:
      | cpfCnpj | 12345678901 |
      | placa | ABC1234 |
      | descricao | Revisão completa do veículo |
    Então o status HTTP deve ser 201
    E a ordem de serviço deve ser criada com status "RECEBIDA"
    E a ordem de serviço deve conter a descrição "Revisão completa do veículo"

  Cenário: Criar ordem com cliente inexistente deve falhar
    Dado que existe um veículo cadastrado com placa "ABC1234"
    Quando uma nova ordem de serviço é criada com os dados:
      | cpfCnpj | 99999999999 |
      | placa | ABC1234 |
      | descricao | Revisão |
    Então o status HTTP deve ser 404

  Cenário: Criar ordem com veículo inexistente deve falhar
    Dado que existe um cliente cadastrado com CPF "12345678901"
    Quando uma nova ordem de serviço é criada com os dados:
      | cpfCnpj | 12345678901 |
      | placa | INVALIDO |
      | descricao | Revisão |
    Então o status HTTP deve ser 404

  Cenário: Criar ordem com cliente e veículo não associados deve falhar
    Dado que existe um cliente cadastrado com CPF "11111111111"
    E que existe um veículo cadastrado com placa "XYZ9999"
    E que estes não estão associados
    Quando uma nova ordem de serviço é criada com os dados:
      | cpfCnpj | 11111111111 |
      | placa | XYZ9999 |
      | descricao | Revisão |
    Então o status HTTP deve ser 400 ou 404

  Cenário: Listar todas as ordens de serviço
    Dado que existem 2 ordens de serviço criadas no sistema
    Quando a lista de ordens de serviço é solicitada
    Então o status HTTP deve ser 200
    E a lista deve conter 2 ordens de serviço

  Cenário: Buscar ordem de serviço por ID
    Dado que existe uma ordem de serviço criada
    Quando a ordem de serviço é buscada por ID
    Então o status HTTP deve ser 200
    E a ordem de serviço deve ser encontrada

  Cenário: Buscar ordem inexistente deve retornar 404
    Quando uma ordem de serviço é buscada com ID inexistente
    Então o status HTTP deve ser 404
