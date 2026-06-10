-- V3__create_table_veiculos.sql
-- Criação da tabela de veículos para o contexto de cadastro
-- Autor: Sistema
-- Data: 2026-06-08

CREATE TABLE IF NOT EXISTS veiculos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(8) NOT NULL,
    modelo VARCHAR(100) NOT NULL,

    CONSTRAINT uk_veiculo_placa UNIQUE (placa)
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_veiculo_placa ON veiculos(placa);
CREATE INDEX IF NOT EXISTS idx_veiculo_modelo ON veiculos(modelo);

-- Comentários nas colunas para documentação
COMMENT ON TABLE veiculos IS 'Tabela de veículos cadastrados - Contexto: cadastro';
COMMENT ON COLUMN veiculos.id IS 'Identificador único do veículo (UUID)';
COMMENT ON COLUMN veiculos.placa IS 'Placa única do veículo (formato Mercosul ou antigo)';
COMMENT ON COLUMN veiculos.modelo IS 'Modelo/marca do veículo';

