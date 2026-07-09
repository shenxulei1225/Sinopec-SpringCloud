package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务实体 Excel 导入 VO
 * 
 * 用于从 Excel 文件导入业务实体数据。
 * 自定义字段以 JSON 格式存储在 customFields 列中。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityImportExcelVO {

    @ExcelProperty("实体名称")
    private String name;

    @ExcelProperty("业务类型编码")
    private String entityTypeCode;

    @ExcelProperty("模型ID")
    private Long modelId;

    @ExcelProperty("自定义字段(JSON)")
    private String customFields;

    @ExcelProperty("状态(1启用/0禁用)")
    private Integer status;
}
