package cn.cheers.x.workorder.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RPC / 派工创建工单请求
 */
@Schema(description = "工单创建 Request DTO")
@Data
public class WorkOrderCreateReqDTO {

    @Schema(description = "业务域范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "inspection")
    @NotBlank(message = "业务域范围不能为空")
    @Size(max = 32, message = "业务域范围长度不能超过32个字符")
    private String scope;

    @Schema(description = "工单标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "离心泵月检工单")
    @NotBlank(message = "工单标题不能为空")
    @Size(max = 256, message = "工单标题长度不能超过256个字符")
    private String title;

    @Schema(description = "关联资产 ID", example = "1001")
    private Long assetId;

    @Schema(description = "资产类型编码", example = "pump")
    @Size(max = 64, message = "资产类型编码长度不能超过64个字符")
    private String assetTypeCode;

    @Schema(description = "频率编码", example = "MONTHLY")
    @Size(max = 32, message = "频率编码长度不能超过32个字符")
    private String frequencyCode;

    @Schema(description = "现场作业标准 ID（与 standardCode 二选一）", example = "20")
    private Long standardId;

    @Schema(description = "现场作业标准编码（取该 code 下最新已发布版本；与 standardId 二选一）",
            example = "pump-monthly")
    @Size(max = 64, message = "标准编码长度不能超过64个字符")
    private String standardCode;

    @Schema(description = "运行时作业 ID", example = "job-001")
    @Size(max = 64, message = "运行时作业 ID 长度不能超过64个字符")
    private String runtimeJobId;

    @Schema(description = "排程槽位 ID", example = "slot-001")
    @Size(max = 64, message = "排程槽位 ID 长度不能超过64个字符")
    private String scheduleSlotId;

    @Schema(description = "业务键", example = "insp-20260719-001")
    @Size(max = 128, message = "业务键长度不能超过128个字符")
    private String businessKey;

    @Schema(description = "指派人用户 ID", example = "1")
    private Long assigneeUserId;

}
