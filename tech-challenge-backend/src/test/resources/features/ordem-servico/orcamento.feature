# language: pt
Funcionalidade: Gerenciamento de Orçamentos de Ordem de Serviço

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Criar orçamento para ordem de serviço com sucesso
    Dado que existe uma ordem de serviço criada
    Quando um orçamento é criado para a ordem de serviço com:
      | descricao | Orçamento para revisão |
      | valorTotal | 500.00 |
    Então o status HTTP deve ser 201
    E o orçamento deve ser criado com status "PENDENTE"
    E o orçamento deve conter o valor total "500.00"

  Cenário: Criar orçamento com valores inválidos
    Dado que existe uma ordem de serviço criada
    Quando um orçamento é criado para a ordem de serviço com:
      | descricao | Orçamento |
      | valorTotal | -100.00 |
    Então o status HTTP deve ser 400

  Cenário: Criar orçamento para ordem inexistente deve falhar
    Quando um orçamento é criado com ID de ordem inexistente e valores:
      | descricao | Orçamento |
      | valorTotal | 500.00 |
    Então o status HTTP deve ser 404

  Cenário: Buscar orçamento existente
    Dado que existe uma ordem de serviço com orçamento criado
    Quando o orçamento é buscado por ID
    Então o status HTTP deve ser 200
    E o orçamento deve ser encontrado

  Cenário: Buscar orçamento inexistente deve retornar 404
    Dado que existe uma ordem de serviço criada
    Quando um orçamento é buscado com ID inexistente
    Então o status HTTP deve ser 404

  Cenário: Aprovar orçamento com sucesso
    Dado que existe uma ordem de serviço com orçamento pendente
    Quando o orçamento é aprovado
    Então o status HTTP deve ser 200
    E o orçamento deve ter status "APROVADO"

  Cenário: Rejeitar orçamento com sucesso
    Dado que existe uma ordem de serviço com orçamento pendente
    Quando o orçamento é rejeitado
    Então o status HTTP deve ser 200
    E o orçamento deve ter status "REJEITADO"

  Cenário: Não deve permitir modificar orçamento já aprovado
    Dado que existe uma ordem de serviço com orçamento aprovado
    Quando tenta-se aprovar novamente o orçamento
    Então o status HTTP deve ser 400 ou 409
