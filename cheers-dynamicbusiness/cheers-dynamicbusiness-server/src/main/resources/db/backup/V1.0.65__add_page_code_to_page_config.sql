-- =====================================================
-- 页面配置新增 page_code 字段
-- 版本: V1.0.65
-- 描述: 为 dynamic_page_config 增加 page_code，用于页面唯一定位
-- =====================================================

ALTER TABLE dynamic_page_config
ADD COLUMN IF NOT EXISTS page_code VARCHAR(100);

COMMENT ON COLUMN dynamic_page_config.page_code IS '页面代码（唯一标识）';

-- 为已有数据生成 page_code（格式：page_code_{id}）
UPDATE dynamic_page_config
SET page_code = 'page_code_' || id::text
WHERE page_code IS NULL;

-- 设置 page_code 为 NOT NULL
ALTER TABLE dynamic_page_config
ALTER COLUMN page_code SET NOT NULL;

-- 创建唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_page_code ON dynamic_page_config(page_code);

-- =====================================================
-- 脚本执行完成
-- =====================================================
