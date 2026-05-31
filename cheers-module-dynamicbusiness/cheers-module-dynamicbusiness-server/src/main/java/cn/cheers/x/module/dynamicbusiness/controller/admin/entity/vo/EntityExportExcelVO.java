package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 业务实体 Excel 导出 VO
 * 
 * 用于将业务实体数据导出到 Excel 文件。
 * 自定义字段以 JSON 格式存储在 customFields 列中。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityExportExcelVO {

    @ExcelProperty("实体ID")
    @ColumnWidth(15)
    private Long id;

    @ExcelProperty("实体名称")
    @ColumnWidth(25)
    private String name;

    @ExcelProperty("业务类型编码")
    @ColumnWidth(20)
    private String businessTypeCode;

    @ExcelProperty("模型ID")
    @ColumnWidth(15)
    private Long modelId;

    @ExcelProperty("模型名称")
    @ColumnWidth(25)
    private String modelName;

    @ExcelProperty("自定义字段(JSON)")
    @ColumnWidth(50)
    private String customFields;

    @ExcelProperty("状态(1启用/0禁用)")
    @ColumnWidth(18)
    private Integer status;

    @ExcelProperty("创建时间")
    @ColumnWidth(22)
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @ColumnWidth(22)
    private LocalDateTime updateTime;
}
