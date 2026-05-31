package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 模型字段批量操作结果")
@Data
public class ModelFieldBatchOperationRespVO {

    @Schema(description = "请求总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer totalCount;

    @Schema(description = "成功数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Integer successCount;

    @Schema(description = "失败数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer failCount;

    @Schema(description = "是否全部成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean allSuccess;

    public static ModelFieldBatchOperationRespVO of(int totalCount, int successCount) {
        ModelFieldBatchOperationRespVO resp = new ModelFieldBatchOperationRespVO();
        resp.setTotalCount(totalCount);
        resp.setSuccessCount(successCount);
        int failCount = Math.max(totalCount - successCount, 0);
        resp.setFailCount(failCount);
        resp.setAllSuccess(failCount == 0);
        return resp;
    }
}
