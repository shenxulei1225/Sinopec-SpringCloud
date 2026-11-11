-- 开通结果记下该业务下勾了哪些页面。空值表示这一包的全部页面（旧数据）。
SET search_path TO dynamicbusiness;

ALTER TABLE tenant_capability_pack
    ADD COLUMN IF NOT EXISTS opened_page_codes TEXT;
