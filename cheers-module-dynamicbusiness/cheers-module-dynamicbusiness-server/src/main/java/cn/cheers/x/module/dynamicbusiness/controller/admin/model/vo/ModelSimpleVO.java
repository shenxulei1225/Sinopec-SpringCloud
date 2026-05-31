package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model 简单信息 VO
 * 
 * 用于在其他 VO 中嵌套展示 Model 的基本信息
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - Model 简单信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelSimpleVO {

    @Schema(description = "Model ID", example = "1")
    private Long id;

    @Schema(description = "Model 编码", example = "base_task")
    private String code;

    @Schema(description = "Model 名称", example = "基础任务")
    private String name;

    @Schema(description = "业务类型编码", example = "task")
    private String businessTypeCode;
}
