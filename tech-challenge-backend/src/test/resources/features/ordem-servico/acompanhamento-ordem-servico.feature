# language: pt
Funcionalidade: Acompanhamento e Gerenciamento de Status de Ordem de Serviço

  Contexto:
    Dado que o sistema está inicializado

  Cenário: Alterar status de RECEBIDA para EM_PROGRESSO
    Dado que existe uma ordem de serviço com status "RECEBIDA"
    Quando o status da ordem é alterado para "EM_PROGRESSO"
    Então o status HTTP deve ser 200
    E a ordem deve ter status "EM_PROGRESSO"
    E deve haver um registro no histórico de status

  Cenário: Alterar status de EM_PROGRESSO para CONCLUIDA
    Dado que existe uma ordem de serviço com status "EM_PROGRESSO"
    Quando o status da ordem é alterado para "CONCLUIDA"
    Então o status HTTP deve ser 200
    E a ordem deve ter status "CONCLUIDA"

  Cenário: Alterar status de CONCLUIDA para ENTREGUE
    Dado que existe uma ordem de serviço com status "CONCLUIDA"
    Quando o status da ordem é alterado para "ENTREGUE"
    Então o status HTTP deve ser 200
    E a ordem deve ter status "ENTREGUE"

  Cenário: Cancelar ordem de serviço
    Dado que existe uma ordem de serviço com status "RECEBIDA"
    Quando a ordem é cancelada
    Então o status HTTP deve ser 200
    E a ordem deve ter status "CANCELADA"

  Cenário: Não deve permitir alteração para status inválido
    Dado que existe uma ordem de serviço com status "RECEBIDA"
    Quando tenta-se alterar o status para "STATUS_INVALIDO"
    Então o status HTTP deve ser 400

  Cenário: Cliente consegue acompanhar sua ordem de serviço
    Dado que existe um cliente com CPF "12345678901"
    E que este cliente possui uma ordem de serviço em aberto
    Quando o cliente busca suas ordens de serviço
    Então o status HTTP deve ser 200
    E a ordem deve ser listada na resposta

  Cenário: Cliente não consegue ver ordens de outros clientes
    Dado que existe um cliente com CPF "11111111111"
    E existe outro cliente com CPF "22222222222"
    E que o cliente "22222222222" possui uma ordem de serviço
    Quando o cliente "11111111111" tenta buscar suas ordens
    Então o status HTTP deve ser 200
    E a ordem do cliente "22222222222" não deve estar na resposta

  Cenário: Deletar ordem de serviço (apenas ADMIN)
    Dado que existe uma ordem de serviço com status "RECEBIDA"
    Quando a ordem é deletada por usuário ADMIN
    Então o status HTTP deve ser 200
    E a ordem deve ser removida do sistema

  Cenário: Usuário não-ADMIN não consegue deletar ordem
    Dado que existe uma ordem de serviço com status "RECEBIDA"
    Quando tenta-se deletar a ordem com usuário não-ADMIN
    Então o status HTTP deve ser 403
