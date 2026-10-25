-- 登录凭证只存用户编号和口令摘要。昵称、手机、部门、角色不在这张表。
-- user_id 是用户目录里这条用户记录的编号，不是系统用户表的编号。

CREATE TABLE IF NOT EXISTS system_login_credential (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    password VARCHAR(100) NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE system_login_credential IS '登录凭证。只存用户编号和口令摘要。';
COMMENT ON COLUMN system_login_credential.user_id IS '用户目录里这条用户记录的编号';
COMMENT ON COLUMN system_login_credential.password IS '口令摘要。不存明文。';

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_login_credential_user
    ON system_login_credential (tenant_id, user_id)
    WHERE deleted = false;
