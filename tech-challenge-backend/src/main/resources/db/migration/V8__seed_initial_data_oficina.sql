-- V8__seed_initial_data_oficina.sql
-- Seed inicial do MVP da oficina mecanica
-- Contextos: acesso, cadastro, estoque, atendimento

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================
-- CONTEXTO ACESSO: usuarios
-- =====================================================
INSERT INTO usuarios (id, nome, email, senha, telefone, perfil, cpf_cnpj) VALUES
('11111111-1111-4111-8111-111111111111', 'Carlos Alberto Souza', 'admin@oficina.local', crypt('123456', gen_salt('bf')), '11988881000', 'ADMIN', '12345678909'),
('22222222-2222-4222-8222-222222222221', 'Marcos Vinicius Lima', 'marcos.lima@oficina.local', crypt('123456', gen_salt('bf')), '11988882001', 'FUNCIONARIO', '98765432100'),
('22222222-2222-4222-8222-222222222222', 'Fernando Rocha Alves', 'fernando.alves@oficina.local', crypt('123456', gen_salt('bf')), '11988882002', 'FUNCIONARIO', '32165498710'),
('33333333-3333-4333-8333-333333333331', 'Ana Paula Ribeiro', 'ana.ribeiro@cliente.local', crypt('123456', gen_salt('bf')), '11977773001', 'CLIENTE', '52998224725'),
('33333333-3333-4333-8333-333333333332', 'Ricardo Mendes Costa', 'ricardo.costa@cliente.local', crypt('123456', gen_salt('bf')), '11977773002', 'CLIENTE', '11144477735')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO CADASTRO: clientes
-- 2 clientes com usuario de acesso + 2 clientes sem app
-- =====================================================
INSERT INTO clientes (
    id, usuario_id, nome, cpf_cnpj, telefone, cep, rua, numero, complemento, cidade, estado
) VALUES
(
    '44444444-4444-4444-8444-444444444441',
    '33333333-3333-4333-8333-333333333331',
    'Ana Paula Ribeiro',
    '52998224725',
    '11977773001',
    '01310100',
    'Avenida Paulista',
    '1578',
    'Apto 92',
    'Sao Paulo',
    'SP'
),
(
    '44444444-4444-4444-8444-444444444442',
    '33333333-3333-4333-8333-333333333332',
    'Ricardo Mendes Costa',
    '11144477735',
    '11977773002',
    '04003001',
    'Rua Domingos de Morais',
    '2450',
    NULL,
    'Sao Paulo',
    'SP'
),
(
    '44444444-4444-4444-8444-444444444443',
    NULL,
    'Patricia Gomes Nunes',
    '39053344705',
    '11966664003',
    '09750530',
    'Rua das Acacias',
    '312',
    'Casa 2',
    'Sao Bernardo do Campo',
    'SP'
),
(
    '44444444-4444-4444-8444-444444444444',
    NULL,
    'Joao Batista Ferreira',
    '86288366757',
    '11966664004',
    '06020000',
    'Avenida dos Autonomistas',
    '4550',
    NULL,
    'Osasco',
    'SP'
)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO CADASTRO: veiculos
-- =====================================================
INSERT INTO veiculos (id, placa, modelo) VALUES
('55555555-5555-4555-8555-555555555551', 'BRA2E19', 'Honda Civic EXL 2.0 2020'),
('55555555-5555-4555-8555-555555555552', 'GHI4J52', 'Chevrolet Onix LT 1.0 Turbo 2022'),
('55555555-5555-4555-8555-555555555553', 'QWE1A34', 'Toyota Corolla XEi 2.0 2021'),
('55555555-5555-4555-8555-555555555554', 'MNO7K88', 'Volkswagen T-Cross Comfortline 2023'),
('55555555-5555-4555-8555-555555555555', 'ABC1D23', 'Hyundai HB20 Vision 1.0 2021')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO CADASTRO: cliente_veiculo
-- 1 cliente com 2 carros + 1 historico com ativo = false
-- =====================================================
INSERT INTO cliente_veiculo (cliente_id, veiculo_id, ativo) VALUES
('44444444-4444-4444-8444-444444444441', '55555555-5555-4555-8555-555555555551', true),
('44444444-4444-4444-8444-444444444441', '55555555-5555-4555-8555-555555555552', false),
('44444444-4444-4444-8444-444444444442', '55555555-5555-4555-8555-555555555553', true),
('44444444-4444-4444-8444-444444444443', '55555555-5555-4555-8555-555555555554', true),
('44444444-4444-4444-8444-444444444444', '55555555-5555-4555-8555-555555555555', true)
ON CONFLICT (cliente_id, veiculo_id) DO NOTHING;

-- =====================================================
-- CONTEXTO ESTOQUE: peca_insumo
-- =====================================================
INSERT INTO peca_insumo (
    id, nome, descricao, preco_venda, preco_compra, quantidade_por_unidade, quantidade_estoque
) VALUES
(
    '66666666-6666-4666-8666-666666666661',
    'Oleo de Motor 5W30 Sintetico',
    'Lubrificante sintetico API SN para motores flex, embalagem de 1 litro.',
    59.90, 36.50, 'Litro', 40
),
(
    '66666666-6666-4666-8666-666666666662',
    'Filtro de Oleo',
    'Filtro de oleo spin-on para motores 1.0 a 2.0.',
    42.00, 24.80, 'Unidade', 35
),
(
    '66666666-6666-4666-8666-666666666663',
    'Pastilha de Freio Dianteira',
    'Jogo de pastilhas ceramicas para eixo dianteiro.',
    189.00, 120.00, 'Jogo com 4', 28
),
(
    '66666666-6666-4666-8666-666666666664',
    'Disco de Freio Ventilado',
    'Par de discos ventilados para veiculos compactos e medios.',
    329.90, 220.00, 'Par', 22
),
(
    '66666666-6666-4666-8666-666666666665',
    'Fluido de Arrefecimento',
    'Aditivo pronto uso para sistema de arrefecimento, frasco de 1 litro.',
    34.90, 18.50, 'Litro', 30
)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: servico_catalogo
-- =====================================================
INSERT INTO servico_catalogo (id, nome, descricao, preco_mao_de_obra) VALUES
(
    '77777777-7777-4777-8777-777777777771',
    'Troca de Oleo e Filtro',
    'Substituicao do oleo do motor, filtro de oleo e reset de indicador de manutencao.',
    120.00
),
(
    '77777777-7777-4777-8777-777777777772',
    'Alinhamento e Balanceamento',
    'Alinhamento computadorizado e balanceamento das quatro rodas.',
    180.00
),
(
    '77777777-7777-4777-8777-777777777773',
    'Troca de Pastilhas de Freio',
    'Substituicao de pastilhas dianteiras com limpeza e lubrificacao de componentes.',
    160.00
),
(
    '77777777-7777-4777-8777-777777777774',
    'Diagnostico Computadorizado',
    'Leitura de falhas, analise de parametros e emissao de relatorio tecnico.',
    150.00
)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: ordens_servico
-- Cenarios com status diferentes do ciclo de vida
-- =====================================================
INSERT INTO ordens_servico (
    id,
    cliente_id,
    veiculo_id,
    mecanico_id,
    status,
    valor_total,
    data_criacao,
    data_inicio_execucao,
    data_finalizacao
) VALUES
-- OS 1: Recebida, sem mecanico e sem itens
(
    '88888888-8888-4888-8888-888888888881',
    '44444444-4444-4444-8444-444444444443',
    '55555555-5555-4555-8555-555555555554',
    NULL,
    'RECEBIDA',
    0.00,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    NULL,
    NULL
),
-- OS 2: Aguardando aprovacao, com 1 servico + 1 peca
(
    '88888888-8888-4888-8888-888888888882',
    '44444444-4444-4444-8444-444444444441',
    '55555555-5555-4555-8555-555555555551',
    '22222222-2222-4222-8222-222222222221',
    'AGUARDANDO_APROVACAO',
    270.00,
    CURRENT_TIMESTAMP - INTERVAL '12 hours',
    NULL,
    NULL
),
-- OS 3: Em execucao, com inicio preenchido
(
    '88888888-8888-4888-8888-888888888883',
    '44444444-4444-4444-8444-444444444442',
    '55555555-5555-4555-8555-555555555553',
    '22222222-2222-4222-8222-222222222222',
    'EM_EXECUCAO',
    384.80,
    CURRENT_TIMESTAMP - INTERVAL '6 hours',
    CURRENT_TIMESTAMP - INTERVAL '3 hours',
    NULL
),
-- OS 4: Entregue, com inicio e finalizacao preenchidos
(
    '88888888-8888-4888-8888-888888888884',
    '44444444-4444-4444-8444-444444444444',
    '55555555-5555-4555-8555-555555555555',
    '22222222-2222-4222-8222-222222222221',
    'ENTREGUE',
    505.90,
    CURRENT_TIMESTAMP - INTERVAL '4 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days 18 hours',
    CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours'
)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: os_servicos
-- Precos historicos aplicados na data da OS
-- =====================================================
INSERT INTO os_servicos (id, ordem_servico_id, servico_id, preco_mao_de_obra_aplicado) VALUES
(
    '99999999-9999-4999-8999-999999999921',
    '88888888-8888-4888-8888-888888888882',
    '77777777-7777-4777-8777-777777777773',
    160.00
),
(
    '99999999-9999-4999-8999-999999999922',
    '88888888-8888-4888-8888-888888888883',
    '77777777-7777-4777-8777-777777777771',
    120.00
),
(
    '99999999-9999-4999-8999-999999999923',
    '88888888-8888-4888-8888-888888888883',
    '77777777-7777-4777-8777-777777777774',
    150.00
),
(
    '99999999-9999-4999-8999-999999999924',
    '88888888-8888-4888-8888-888888888884',
    '77777777-7777-4777-8777-777777777772',
    180.00
)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: os_pecas
-- Precos historicos aplicados na data da OS
-- =====================================================
INSERT INTO os_pecas (id, ordem_servico_id, peca_id, quantidade, preco_venda_aplicado) VALUES
(
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1',
    '88888888-8888-4888-8888-888888888882',
    '66666666-6666-4666-8666-666666666663',
    1,
    110.00
),
(
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2',
    '88888888-8888-4888-8888-888888888883',
    '66666666-6666-4666-8666-666666666661',
    4,
    52.00
),
(
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa3',
    '88888888-8888-4888-8888-888888888883',
    '66666666-6666-4666-8666-666666666662',
    1,
    26.80
),
(
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa4',
    '88888888-8888-4888-8888-888888888884',
    '66666666-6666-4666-8666-666666666663',
    1,
    175.00
),
(
    'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa5',
    '88888888-8888-4888-8888-888888888884',
    '66666666-6666-4666-8666-666666666664',
    1,
    290.90
)
ON CONFLICT (id) DO NOTHING;
