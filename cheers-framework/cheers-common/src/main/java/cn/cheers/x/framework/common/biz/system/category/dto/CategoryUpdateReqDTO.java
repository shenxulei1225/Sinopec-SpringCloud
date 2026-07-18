package cn.cheers.x.framework.common.biz.system.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 分类更新 Request DTO
 * 
 * 用于RPC接口，其他模块更新分类时使用
 * 
 * @author 系统生成
 */
@Schema(description = "RPC 服务 - 分类更新 Request DTO")
@Data
public class CategoryUpdateReqDTO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "分类编号不能为空")
    private Long id;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "生产设备")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "emergency_event")
    @NotBlank(message = "分类类型编码不能为空")
    @Size(max = 50, message = "分类类型编码长度不能超过50")
    private String categoryTypeCode;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态（1启用，0禁用）", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "描述", example = "设备分类")
    @Size(max = 500, message = "描述长度不能超过500")
    private String description;
}

