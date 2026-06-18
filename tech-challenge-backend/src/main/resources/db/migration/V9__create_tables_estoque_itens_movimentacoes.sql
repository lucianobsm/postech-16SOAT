-- V9__create_tables_estoque_itens_movimentacoes.sql
-- Cria tabela de movimentações de estoque vinculada a peca_insumo.
-- Contexto Delimitado: estoque
-- Nota: coluna quantidade_minima em peca_insumo já adicionada em V8.

CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    peca_insumo_id      UUID         NOT NULL,
    tipo_movimentacao   VARCHAR(20)  NOT NULL,
    quantidade          INTEGER      NOT NULL,
    observacao          VARCHAR(500),
    criado_em           TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_movimentacao_peca_insumo FOREIGN KEY (peca_insumo_id) REFERENCES peca_insumo(id),
    CONSTRAINT ck_movimentacao_tipo  CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    CONSTRAINT ck_movimentacao_qtd   CHECK (quantidade > 0)
);

CREATE INDEX IF NOT EXISTS idx_movimentacao_peca_insumo ON movimentacoes_estoque(peca_insumo_id);

COMMENT ON TABLE movimentacoes_estoque IS 'Histórico de movimentações de estoque (entrada/saída) - Contexto: estoque';

-- =====================================================
-- SEED: movimentacoes iniciais de entrada (estoque inicial)
-- =====================================================
INSERT INTO movimentacoes_estoque (peca_insumo_id, tipo_movimentacao, quantidade, observacao, criado_em) VALUES
(
    '66666666-6666-4666-8666-666666666661',
    'ENTRADA', 40, 'Estoque inicial - Oleo de Motor 5W30',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
(
    '66666666-6666-4666-8666-666666666662',
    'ENTRADA', 35, 'Estoque inicial - Filtro de Oleo',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
(
    '66666666-6666-4666-8666-666666666663',
    'ENTRADA', 30, 'Estoque inicial - Pastilha de Freio Dianteira',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
(
    '66666666-6666-4666-8666-666666666664',
    'ENTRADA', 25, 'Estoque inicial - Disco de Freio Ventilado',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
(
    '66666666-6666-4666-8666-666666666665',
    'ENTRADA', 30, 'Estoque inicial - Fluido de Arrefecimento',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
-- Saidas referentes as OS executadas (OS 3 e OS 4)
(
    '66666666-6666-4666-8666-666666666661',
    'SAIDA', 4, 'Uso em OS-883 - Troca de Oleo e Filtro',
    CURRENT_TIMESTAMP - INTERVAL '3 days 12 hours'
),
(
    '66666666-6666-4666-8666-666666666662',
    'SAIDA', 1, 'Uso em OS-883 - Troca de Oleo e Filtro',
    CURRENT_TIMESTAMP - INTERVAL '3 days 12 hours'
),
(
    '66666666-6666-4666-8666-666666666663',
    'SAIDA', 2, 'Uso em OS-882 e OS-884 - Troca de Pastilhas',
    CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours'
),
(
    '66666666-6666-4666-8666-666666666664',
    'SAIDA', 1, 'Uso em OS-884 - Disco de Freio',
    CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours'
);
