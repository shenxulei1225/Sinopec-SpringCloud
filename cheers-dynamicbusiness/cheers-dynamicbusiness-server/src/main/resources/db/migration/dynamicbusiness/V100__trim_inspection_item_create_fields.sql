-- 检查项新建表单收敛：
-- 这三个字段属于执行统计语义，不应出现在「标准检查项」新建弹窗。
-- 通过下线 inspection_item 规范型号上的字段分配，避免前端继续渲染。
UPDATE dynamicbusiness.dynamic_model_field_assignment
SET deleted = true
WHERE deleted = false
  AND model_code = 'inspection_item'
  AND field_code IN ('FLD-INS-004', 'FLD-INS-008', 'FLD-INS-009');
