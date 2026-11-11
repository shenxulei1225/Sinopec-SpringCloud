package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 在工单目录里新建一条。
 * 维修、巡检、保养靠 domain 找到对应型号。
 */
@Data
public class WorkOrderCatalogCreateReqVO {

    @Schema(description = "业务域，与型号上的域一致，如维修", example = "维修")
    @NotBlank(message = "请选择业务域")
    private String domain;

    @Schema(description = "标题")
    @NotBlank(message = "请填写工单标题")
    private String title;

    @Schema(description = "工作内容。接单人凭这一段知道要处理什么")
    @NotBlank(message = "请填写工作内容")
    private String workContent;

    @Schema(description = "班组分类节点编号")
    @NotNull(message = "请选择派单班组")
    private Long teamId;

    @Schema(description = "执行人。空着表示班组待领")
    private Long assigneeUserId;

    @Schema(description = "设备实体编号。没有就不写，不用场站编号代替")
    private Long equipmentId;

    @Schema(description = "报修实体编号。手工建单为空。告警转入时不填")
    private Long repairId;

    @Schema(description = "来源告警编号。有值时这张工单来自该告警，不写报修编号")
    private Long alarmId;

    @Schema(description = "告警转入的用途。review 是复核工单，不填或 repair 是维修工单")
    private String alarmOrderKind;

    @Schema(description = "所属场站")
    @NotNull(message = "请先选择场站")
    private Long facilityId;

    @Schema(description = "处理时刻 yyyy-MM-dd HH:mm:ss。报修派工必填")
    private String handlingTime;

    @Schema(description = "计划开始。与计划结束成对")
    private String plannedStartTime;

    @Schema(description = "计划结束。与计划开始成对")
    private String plannedFinishTime;
}
