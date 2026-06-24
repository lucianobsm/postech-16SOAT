-- V13__insert_cliente_nicole_afonso_com_os.sql
-- Inserção de dados iniciais: usuário Nicole M Afonso, veículo Ford Ka e Ordem de Serviço em EM_DIAGNOSTICO
-- Autor: Claude Code
-- Data: 2026-06-22

-- 1. Inserir usuário Nicole M Afonso
INSERT INTO usuarios (id, nome, email, senha, telefone, perfil, cpf_cnpj)
VALUES (
    '550e8400-e29b-41d4-a716-446655440001'::UUID,
    'Nicole M Afonso',
    'nicolemafonso@gmail.com',
    '$2a$10$H5lVQmKaHmQ4sPCN7.0p.eHGWlLvs7G.Q3R6K7N9M2E5L3Q2D0X0O', -- Senha: 123456 (hash bcrypt)
    NULL,
    'CLIENTE',
    '10387398790'
) ON CONFLICT DO NOTHING;

-- 2. Inserir cliente Nicole M Afonso
INSERT INTO clientes (id, usuario_id, nome, cpf_cnpj, telefone, cep, rua, numero, complemento, cidade, estado)
VALUES (
    '550e8400-e29b-41d4-a716-446655440002'::UUID,
    '550e8400-e29b-41d4-a716-446655440001'::UUID,
    'Nicole M Afonso',
    '10387398790',
    NULL,
    '87615623',
    NULL,
    '10',
    'casa',
    'São Bernardo do Campo',
    'SP'
) ON CONFLICT DO NOTHING;

-- 3. Inserir veículo Ford Ka
INSERT INTO veiculos (id, placa, marca, modelo, ano, cor)
VALUES (
    '550e8400-e29b-41d4-a716-446655440003'::UUID,
    'AB87D28',
    'Ford',
    'Ka',
    2000,
    'Não especificada'
) ON CONFLICT DO NOTHING;

-- 4. Associar cliente ao veículo
INSERT INTO cliente_veiculo (cliente_id, veiculo_id, ativo)
VALUES (
    '550e8400-e29b-41d4-a716-446655440002'::UUID,
    '550e8400-e29b-41d4-a716-446655440003'::UUID,
    true
) ON CONFLICT DO NOTHING;

-- 5. Inserir Ordem de Serviço em status EM_DIAGNOSTICO
INSERT INTO ordens_servico (
    id,
    cliente_id,
    veiculo_id,
    mecanico_id,
    status,
    valor_total_acumulado,
    valor_total,
    urgente,
    queixa_cliente,
    observacoes,
    data_criacao
) VALUES (
    '550e8400-e29b-41d4-a716-446655440004'::UUID,
    '550e8400-e29b-41d4-a716-446655440002'::UUID,
    '550e8400-e29b-41d4-a716-446655440003'::UUID,
    NULL,
    'EM_DIAGNOSTICO',
    0.00,
    0.00,
    false,
    'Diagnóstico inicial do veículo',
    'Ordem de serviço aberta para diagnóstico do Ford Ka',
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- 6. Inserir histórico do status da Ordem de Serviço
INSERT INTO os_historico_status (id, ordem_servico_id, status_origem, status_destino, usuario_id, data_mudanca)
VALUES (
    '550e8400-e29b-41d4-a716-446655440005'::UUID,
    '550e8400-e29b-41d4-a716-446655440004'::UUID,
    NULL, -- Primeira transição, sem status origem
    'EM_DIAGNOSTICO',
    NULL, -- Sem usuário responsável
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;
