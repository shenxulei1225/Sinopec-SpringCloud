-- 设备型号上的自动模式：检查项没填的动作参数读这里。
-- 各摄像机型号可以不同。不按型号把整份参数再复制进动作。
SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamic_model
    ADD COLUMN IF NOT EXISTS auto_param_defaults text;

COMMENT ON COLUMN dynamic_model.auto_param_defaults IS
    '这一型号的自动模式。键是动作参数编码，值是检查项没填时用的默认值。';
