package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 业务实体导入响应 VO
 */
@Schema(description = "管理后台 - 业务实体导入 Response VO")
@Data
@Builder
public class EntityImportRespVO {

    @Schema(description = "创建成功的实体名称数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> createEntityNames;

    @Schema(description = "更新成功的实体名称数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> updateEntityNames;

    @Schema(description = "导入失败的实体集合，key 为实体名称，value 为失败原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> failureEntityNames;

    @Schema(description = "导入总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "成功数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer successCount;

    @Schema(description = "失败数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer failureCount;
}
