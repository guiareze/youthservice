CREATE TABLE IF NOT EXISTS pessoas (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INTEGER NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_pessoas_nome ON pessoas (nome);
