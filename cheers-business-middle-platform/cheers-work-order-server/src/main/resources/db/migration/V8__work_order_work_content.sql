SET search_path TO work_order;

-- 接单人要做的事。报修派工时从报修内容写入。手工建单由建单人填写。
ALTER TABLE wo_work_order ADD COLUMN IF NOT EXISTS work_content TEXT;

COMMENT ON COLUMN wo_work_order.work_content IS '工作内容。告诉接单人这张单要处理什么。报修来源在派工时从报修内容抄入。';
