-- V8__seed_initial_data_oficina.sql
-- Seed inicial do MVP da oficina mecanica (Atualizado para multiplos orcamentos)
-- Contextos: acesso, cadastro, estoque, atendimento
-- Autor: Nicole (Refatorado para suportar os_orcamentos)

-- =====================================================
-- CONTEXTO ACESSO: usuarios
-- =====================================================
INSERT INTO usuarios (id, nome, email, senha, telefone, perfil, cpf_cnpj) VALUES
('11111111-1111-4111-8111-111111111111', 'Carlos Alberto Souza', 'admin@oficina.local', crypt('123456', gen_salt('bf')), '(11) 98888-1000', 'ADMIN', '123.456.789-09'),
('22222222-2222-4222-8222-222222222221', 'Marcos Vinicius Lima', 'marcos.lima@oficina.local', crypt('123456', gen_salt('bf')), '(11) 98888-2001', 'FUNCIONARIO', '987.654.321-00'),
('22222222-2222-4222-8222-222222222222', 'Fernando Rocha Alves', 'fernando.alves@oficina.local', crypt('123456', gen_salt('bf')), '(11) 98888-2002', 'FUNCIONARIO', '321.654.987-10'),
('33333333-3333-4333-8333-333333333331', 'Ana Paula Ribeiro', 'ana.ribeiro@cliente.local', crypt('123456', gen_salt('bf')), '(11) 97777-3001', 'CLIENTE', '529.982.247-25'),
('33333333-3333-4333-8333-333333333332', 'Ricardo Mendes Costa', 'ricardo.costa@cliente.local', crypt('123456', gen_salt('bf')), '(11) 97777-3002', 'CLIENTE', '111.444.777-35')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO CADASTRO: clientes
-- =====================================================
INSERT INTO clientes (
    id, usuario_id, nome, cpf_cnpj, telefone, cep, rua, numero, complemento, cidade, estado
) VALUES
('44444444-4444-4444-8444-444444444441', '33333333-3333-4333-8333-333333333331', 'Ana Paula Ribeiro', '529.982.247-25', '(11) 97777-3001', '01310-100', 'Avenida Paulista', '1578', 'Apto 92', 'Sao Paulo', 'SP'),
('44444444-4444-4444-8444-444444444442', '33333333-3333-4333-8333-333333333332', 'Ricardo Mendes Costa', '111.444.777-35', '(11) 97777-3002', '04003-001', 'Rua Domingos de Morais', '2450', NULL, 'Sao Paulo', 'SP'),
('44444444-4444-4444-8444-444444444443', NULL, 'Patricia Gomes Nunes', '390.533.447-05', '(11) 96666-4003', '09750-530', 'Rua das Acacias', '312', 'Casa 2', 'Sao Bernardo do Campo', 'SP'),
('44444444-4444-4444-8444-444444444444', NULL, 'Joao Batista Ferreira', '862.883.667-57', '(11) 96666-4004', '06020-000', 'Avenida dos Autonomistas', '4550', NULL, 'Osasco', 'SP')
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
INSERT INTO peca_insumo (id, nome, descricao, preco_venda, preco_compra, quantidade_por_unidade, quantidade_estoque) VALUES
('66666666-6666-4666-8666-666666666661', 'Oleo de Motor 5W30 Sintetico', 'Lubrificante sintetico API SN para motores flex, embalagem de 1 litro.', 59.90, 36.50, 'Litro', 40),
('66666666-6666-4666-8666-666666666662', 'Filtro de Oleo', 'Filtro de oleo spin-on para motores 1.0 a 2.0.', 42.00, 24.80, 'Unidade', 35),
('66666666-6666-4666-8666-666666666663', 'Pastilha de Freio Dianteira', 'Jogo de pastilhas ceramicas para eixo dianteiro.', 189.00, 120.00, 'Jogo com 4', 28),
('66666666-6666-4666-8666-666666666664', 'Disco de Freio Ventilado', 'Par de discos ventilados para veiculos compactos e medios.', 329.90, 220.00, 'Par', 22),
('66666666-6666-4666-8666-666666666665', 'Fluido de Arrefecimento', 'Aditivo pronto uso para sistema de arrefecimento, frasco de 1 litro.', 34.90, 18.50, 'Litro', 30)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: servico_catalogo
-- =====================================================
INSERT INTO servico_catalogo (id, nome, descricao, preco_mao_de_obra) VALUES
('77777777-7777-4777-8777-777777777771', 'Troca de Oleo e Filtro', 'Substituicao do oleo do motor, filtro de oleo e reset de indicador de manutencao.', 120.00),
('77777777-7777-4777-8777-777777777772', 'Alinhamento e Balanceamento', 'Alinhamento computadorizado e balanceamento das quatro rodas.', 180.00),
('77777777-7777-4777-8777-777777777773', 'Troca de Pastilhas de Freio', 'Substituicao de pastilhas dianteiras com limpeza e lubrificacao de componentes.', 160.00),
('77777777-7777-4777-8777-777777777774', 'Diagnostico Computadorizado', 'Leitura de falhas, analise de parametros e emissao de relatorio tecnico.', 150.00)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: ordens_servico (Ajustado valor_total_acumulado)
-- =====================================================
INSERT INTO ordens_servico (
    id, cliente_id, veiculo_id, mecanico_id, status, valor_total_acumulado, data_criacao, data_inicio_execucao, data_finalizacao
) VALUES
-- OS 1: Recebida, sem mecanico e sem itens
('88888888-8888-4888-8888-888888888881', '44444444-4444-4444-8444-444444444443', '55555555-5555-4555-8555-555555555554', NULL, 'RECEBIDA', 0.00, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, NULL),
-- OS 2: Aguardando aprovacao
('88888888-8888-4888-8888-888888888882', '44444444-4444-4444-8444-444444444441', '55555555-5555-4555-8555-555555555551', '22222222-2222-4222-8222-222222222221', 'AGUARDANDO_APROVACAO', 0.00, CURRENT_TIMESTAMP - INTERVAL '12 hours', NULL, NULL),
-- OS 3: Em execucao (Soma dos itens aprovados = 384.80)
('88888888-8888-4888-8888-888888888883', '44444444-4444-4444-8444-444444444442', '55555555-5555-4555-8555-555555555553', '22222222-2222-4222-8222-222222222222', 'EM_EXECUCAO', 384.80, CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', NULL),
-- OS 4: Entregue (Soma dos itens aprovados = 465.90)
('88888888-8888-4888-8888-888888888884', '44444444-4444-4444-8444-444444444444', '55555555-5555-4555-8555-555555555555', '22222222-2222-4222-8222-222222222221', 'ENTREGUE', 465.90, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '3 days 18 hours', CURRENT_TIMESTAMP - INTERVAL '3 days 6 hours')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- NOVO: CONTEXTO ATENDIMENTO: os_orcamentos
-- Vincula os cenarios de teste a um Orcamento estruturado
-- =====================================================
INSERT INTO os_orcamentos (id, ordem_servico_id, tipo, status, valor_total, prazo_estipulado, data_criacao) VALUES
-- Orcamento para OS 2 (Pendente de aprovacao do cliente)
('b1111111-1111-1111-1111-111111111111', '88888888-8888-4888-8888-888888888882', 'INICIAL', 'PENDENTE', 270.00, CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '12 hours'),
-- Orcamento para OS 3 (Ja aprovado pelo cliente)
('b3333333-3333-3333-3333-333333333333', '88888888-8888-4888-8888-888888888883', 'INICIAL', 'APROVADO', 384.80, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '6 hours'),
-- Orcamento para OS 4 (Ja aprovado e concluido)
('b4444444-4444-4444-4444-444444444444', '88888888-8888-4888-8888-888888888884', 'INICIAL', 'APROVADO', 465.90, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '4 days')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: os_servicos (Remapeado para orcamento_id)
-- =====================================================
INSERT INTO os_servicos (id, orcamento_id, servico_id, preco_mao_de_obra_aplicado) VALUES
-- Servico do orcamento da OS 2
('99999999-9999-4999-8999-999999999921', 'b1111111-1111-1111-1111-111111111111', '77777777-7777-4777-8777-777777777773', 160.00),
-- Servicos do orcamento da OS 3
('99999999-9999-4999-8999-999999999922', 'b3333333-3333-3333-3333-333333333333', '77777777-7777-4777-8777-777777777771', 120.00),
('99999999-9999-4999-8999-999999999923', 'b3333333-3333-3333-3333-333333333333', '77777777-7777-4777-8777-777777777774', 150.00),
-- Servico do orcamento da OS 4
('99999999-9999-4999-8999-999999999924', 'b4444444-4444-4444-4444-444444444444', '77777777-7777-4777-8777-777777777772', 180.00)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- CONTEXTO ATENDIMENTO: os_pecas (Remapeado para orcamento_id)
-- =====================================================
INSERT INTO os_pecas (id, orcamento_id, peca_id, quantidade, preco_venda_aplicado) VALUES
-- Peca do orcamento da OS 2 (110.00) -> Total Orcamento = 160 + 110 = 270.00
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa1', 'b1111111-1111-1111-1111-111111111111', '66666666-6666-4666-8666-666666666663', 1, 110.00),
-- Pecas do orcamento da OS 3 (4 * 26.80 = 107.20 + 7.60?? Ajustado para bater o total original de 384.80 -> Servicos: 270.00, Peças: 114.80)
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa2', 'b3333333-3333-3333-3333-333333333333', '66666666-6666-4666-8666-666666666661', 1, 88.00),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa3', 'b3333333-3333-3333-3333-333333333333', '66666666-6666-4666-8666-666666666662', 1, 26.80),
-- Pecas do orcamento da OS 4 (110.90 + 175.00 = 285.90 + Mão de obra 180.00 = 465.90)
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa4', 'b4444444-4444-4444-4444-444444444444', '66666666-6666-4666-8666-666666666663', 1, 175.00),
('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaa5', 'b4444444-4444-4444-4444-444444444444', '66666666-6666-4666-8666-666666666664', 1, 110.90)
ON CONFLICT (id) DO NOTHING;