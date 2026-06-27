# language: pt
Funcionalidade: Controle de Estoque de Peças e Insumos

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Cadastrar nova peça no estoque com sucesso
    Quando uma nova peça é cadastrada com os seguintes dados:
      | codigo | PECA-001 |
      | nome | Correia de distribuição |
      | quantidade | 10 |
      | preco_unitario | 150.00 |
      | localizacao | Prateleira A1 |
    Então o status HTTP deve ser 201
    E a peça deve conter o código "PECA-001"
    E a peça deve ter quantidade "10"

  Cenário: Criar peça com código duplicado deve falhar
    Dado que existe uma peça cadastrada com código "PECA-002"
    Quando uma nova peça é cadastrada com os seguintes dados:
      | codigo | PECA-002 |
      | nome | Outra peça |
      | quantidade | 5 |
      | preco_unitario | 100.00 |
      | localizacao | Prateleira B1 |
    Então o status HTTP deve ser 400 ou 409

  Cenário: Criar peça com dados inválidos (sem nome)
    Quando uma nova peça é cadastrada com os seguintes dados:
      | codigo | PECA-003 |
      | nome |  |
      | quantidade | 5 |
      | preco_unitario | 100.00 |
      | localizacao | Prateleira C1 |
    Então o status HTTP deve ser 400

  Cenário: Buscar peça existente por código
    Dado que existe uma peça cadastrada com código "PECA-004"
    Quando a peça é buscada por código "PECA-004"
    Então o status HTTP deve ser 200
    E a peça deve ser encontrada

  Cenário: Buscar peça inexistente deve retornar 404
    Quando a peça é buscada por código "INVALIDO"
    Então o status HTTP deve ser 404

  Cenário: Listar peças do estoque
    Dado que existem 3 peças cadastradas no estoque
    Quando a lista de peças é solicitada
    Então o status HTTP deve ser 200
    E a lista deve conter 3 peças

  Cenário: Atualizar quantidade de peça
    Dado que existe uma peça cadastrada com código "PECA-005"
    Quando a quantidade da peça é atualizada para 25
    Então o status HTTP deve ser 200
    E a peça deve ter quantidade "25"

  Cenário: Atualizar preço unitário de peça
    Dado que existe uma peça cadastrada com código "PECA-006"
    Quando o preço unitário da peça é atualizado para 200.00
    Então o status HTTP deve ser 200
    E a peça deve ter preço unitário "200.00"

  Cenário: Deletar peça do estoque
    Dado que existe uma peça cadastrada com código "PECA-007"
    Quando a peça é deletada
    Então o status HTTP deve ser 200
    E a peça deve ser removida do sistema

  Cenário: Não deve permitir quantidade negativa
    Dado que existe uma peça cadastrada com código "PECA-008"
    Quando tenta-se atualizar a quantidade para -5
    Então o status HTTP deve ser 400

  Cenário: Registrar entrada de peça no estoque
    Dado que existe uma peça cadastrada com código "PECA-009" e quantidade 10
    Quando uma movimentação de entrada é registrada para 5 unidades
    Então o status HTTP deve ser 201
    E a quantidade da peça deve ser 15
    E deve haver um registro de movimentação

  Cenário: Registrar saída de peça do estoque
    Dado que existe uma peça cadastrada com código "PECA-010" e quantidade 20
    Quando uma movimentação de saída é registrada para 5 unidades
    Então o status HTTP deve ser 201
    E a quantidade da peça deve ser 15
    E deve haver um registro de movimentação

  Cenário: Não deve permitir saída maior que quantidade disponível
    Dado que existe uma peça cadastrada com código "PECA-011" e quantidade 10
    Quando tenta-se registrar saída de 15 unidades
    Então o status HTTP deve ser 400
