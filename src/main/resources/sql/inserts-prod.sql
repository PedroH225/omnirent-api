-- =========================
-- GLOBAL_CONFIGURATIONS
-- =========================
INSERT INTO global_configurations (id, global_token_version)
VALUES (1, 1)
ON CONFLICT (id) DO NOTHING;

-- =========================
-- ROLES
-- =========================
INSERT INTO roles (id, name) VALUES
 (1, 'ROLE_USER'),
 (2, 'ROLE_ADMIN')
ON CONFLICT (id) DO NOTHING;