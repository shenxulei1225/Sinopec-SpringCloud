package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model 关联声明 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - Model 关联声明")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelRelationDeclarationVO {

    @Schema(description = "声明 ID", example = "1")
    private Long id;

    @Schema(description = "Model ID", example = "2")
    private Long modelId;

    @Schema(description = "目标业务类型编码", example = "personnel")
    private String targetBusinessType;

    @Schema(description = "目标业务类型名称", example = "人员管理")
    private String targetBusinessTypeName;

}
