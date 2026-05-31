package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 业务实体批量更新请求 VO
 *
 * 支持批量修改多个实体的字段值
 * 单次操作最多1000条，超过时自动分批异步执行
 *
 * @author yudao
 */
@Schema(description = "管理后台 - 业务实体批量更新请求")
@Data
public class EntityBatchUpdateReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "实体ID列表不能为空")
    @Size(min = 1, max = 1000, message = "单次批量更新最多支持1000条记录")
    private List<Long> ids;

    @Schema(description = "名称（可批量更新）", example = "示例实体")
    private String name;

    @Schema(description = "模型ID（可批量更新）", example = "1")
    private Long modelId;

    @Schema(description = "父实体ID（可批量更新）", example = "1000")
    private Long parentId;

    @Schema(description = "要更新的字段值（key为字段ID，value为字段值）", example = "{\"1\": \"新值\", \"2\": 100}")
    private Map<String, Object> customFields;

    @Schema(description = "状态（0=禁用，1=启用）", example = "1")
    private Integer status;

    @Schema(description = "是否异步执行（当数据量大于100时建议使用异步）", example = "false")
    private Boolean async;
}
