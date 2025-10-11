-- Usar a extensão para gerar UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabela principal de usuários (mantida do seu script)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela para emails, permitindo múltiplos por usuário
CREATE TABLE emails (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email_address VARCHAR(255) UNIQUE NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela para telefones, permitindo múltiplos por usuário
CREATE TABLE phone_numbers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    phone_number VARCHAR(50) UNIQUE NOT NULL, -- Formato E.164
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela para códigos de verificação (email ou telefone)
CREATE TABLE verification_codes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code VARCHAR(10) NOT NULL,
    target VARCHAR(255) NOT NULL, -- O email ou telefone a ser verificado
    target_type VARCHAR(10) NOT NULL, -- 'EMAIL' ou 'PHONE'
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela para vincular contas externas (Google, Outlook) a um usuário
CREATE TABLE external_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL, -- 'GOOGLE', 'OUTLOOK', etc.
    provider_user_id VARCHAR(255) NOT NULL, -- ID do usuário no provedor
    account_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(provider, provider_user_id)
);

-- Tabela para armazenar as credenciais OAuth 2.0 de forma segura
CREATE TABLE credentials (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    external_account_id UUID NOT NULL REFERENCES external_accounts(id) ON DELETE CASCADE,
    -- IMPORTANTE: Armazene os tokens criptografados na produção!
    -- O campo deve ser do tipo BYTEA ou TEXT, dependendo da sua estratégia de criptografia.
    encrypted_token TEXT NOT NULL,
    refresh_token TEXT,
    expires_at TIMESTAMPTZ,
    scopes TEXT[], -- Os escopos de permissão concedidos
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabela principal de compromissos
CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    external_account_id UUID NOT NULL REFERENCES external_accounts(id) ON DELETE CASCADE,
    external_event_id VARCHAR(255) NOT NULL, -- ID do evento no Google/Outlook
    title VARCHAR(255),
    description TEXT,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    location VARCHAR(255),
    status VARCHAR(50), -- 'confirmed', 'tentative', 'cancelled'
    raw_data JSONB, -- Para guardar o evento original e analisar preferências futuras
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(external_account_id, external_event_id)
);

-- Índices para otimizar consultas
CREATE INDEX idx_emails_user_id ON emails(user_id);
CREATE INDEX idx_phone_numbers_user_id ON phone_numbers(user_id);
CREATE INDEX idx_external_accounts_user_id ON external_accounts(user_id);
CREATE INDEX idx_appointments_start_time ON appointments(start_time);
