-- V1_02__SEED_USERS.sql
-- Seed users for different roles

INSERT INTO public.users (
    user_id,
    first_name,
    last_name,
    email,
    password,
    user_type,
    is_email_verified,
    is_deleted,
    created_at
) VALUES (
    uuid_generate_v4(),
    'Super',
    'Admin',
    'admin@clientnest.com',
    '$2a$12$G.vfaMZ0UeVIe2OiBdiZTOQUFfEIWcveSEIsvIndmeLti6zQOxqgi',
    'SUPER_ADMIN',
    TRUE,
    FALSE,
    NOW()
), (
    uuid_generate_v4(),
    'John',
    'Owner',
    'owner@clientnest.com',
    '$2a$12$G.vfaMZ0UeVIe2OiBdiZTOQUFfEIWcveSEIsvIndmeLti6zQOxqgi',
    'BUSINESS_OWNER',
    TRUE,
    FALSE,
    NOW()
), (
    uuid_generate_v4(),
    'Jane',
    'Client',
    'client@clientnest.com',
    '$2a$12$G.vfaMZ0UeVIe2OiBdiZTOQUFfEIWcveSEIsvIndmeLti6zQOxqgi',
    'CLIENT',
    TRUE,
    FALSE,
    NOW()
);
