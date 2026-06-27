# language: pt
Funcionalidade: Gerenciamento de Serviços do Catálogo

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Cadastrar novo serviço com sucesso
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome | Troca de óleo |
      | descricao | Troca de óleo do motor |
      | valor_hora | 100.00 |
      | tempo_estimado_horas | 0.5 |
    Então o status HTTP deve ser 201
    E o serviço deve conter o nome "Troca de óleo"
    E o serviço deve ter valor por hora "100.00"

  Cenário: Criar serviço com nome duplicado deve falhar
    Dado que existe um serviço cadastrado com nome "Revisão Completa"
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome | Revisão Completa |
      | descricao | Outra descrição |
      | valor_hora | 150.00 |
      | tempo_estimado_horas | 2.0 |
    Então o status HTTP deve ser 400 ou 409

  Cenário: Criar serviço com dados inválidos (sem nome)
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome |  |
      | descricao | Descrição |
      | valor_hora | 100.00 |
      | tempo_estimado_horas | 1.0 |
    Então o status HTTP deve ser 400

  Cenário: Buscar serviço existente
    Dado que existe um serviço cadastrado com nome "Troca de Pneu"
    Quando o serviço é buscado por nome "Troca de Pneu"
    Então o status HTTP deve ser 200
    E o serviço deve ser encontrado

  Cenário: Buscar serviço inexistente deve retornar 404
    Quando o serviço é buscado por nome "Inexistente"
    Então o status HTTP deve ser 404

  Cenário: Listar serviços do catálogo
    Dado que existem 4 serviços cadastrados no sistema
    Quando a lista de serviços é solicitada
    Então o status HTTP deve ser 200
    E a lista deve conter 4 serviços

  Cenário: Atualizar dados do serviço
    Dado que existe um serviço cadastrado com nome "Alinhamento"
    Quando os dados do serviço são atualizados:
      | valor_hora | 120.00 |
      | tempo_estimado_horas | 1.5 |
    Então o status HTTP deve ser 200
    E o serviço deve ter valor por hora "120.00"

  Cenário: Deletar serviço
    Dado que existe um serviço cadastrado com nome "Balanceamento"
    Quando o serviço é deletado
    Então o status HTTP deve ser 200
    E o serviço deve ser removido do sistema

  Cenário: Não deve permitir valor negativo
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome | Serviço Teste |
      | descricao | Descrição |
      | valor_hora | -50.00 |
      | tempo_estimado_horas | 1.0 |
    Então o status HTTP deve ser 400

  Cenário: Não deve permitir tempo estimado negativo
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome | Serviço Teste |
      | descricao | Descrição |
      | valor_hora | 100.00 |
      | tempo_estimado_horas | -0.5 |
    Então o status HTTP deve ser 400
