INSERT INTO usuarios (nome, email, senha, telefone, perfil, cpf_cnpj)
VALUES (
  'Daniel Souza',
  'daniel@oficina.com',
  crypt('minhasenha123', gen_salt('bf', 10)),
  '11999998888',
  'ADMIN',
  '00000000000'
);
