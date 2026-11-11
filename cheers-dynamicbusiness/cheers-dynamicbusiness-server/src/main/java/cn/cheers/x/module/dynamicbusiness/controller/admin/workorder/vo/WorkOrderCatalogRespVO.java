package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import lombok.Data;

/**
 * 工单中心和执行页读到的一条工单目录实体。
 * scope 是型号上的业务域。status 是这条实体上的工单状态，不是启用开关。
 */
@Data
public class WorkOrderCatalogRespVO {

    private Long id;

    private String woNo;

    private String title;

    private String workContent;

    private String scope;

    private String status;

    private String businessKey;

    private Long assigneeTeamId;

    private Long assigneeUserId;

    private String handlingTime;

    private String plannedStartTime;

    private String plannedFinishTime;

    private String actualStartTime;

    private String actualFinishTime;

    private Long assetId;

    private String phenomenon;

    private String cause;

    private String remedy;

    private String laborHours;

    private String createTime;

    /** 来源告警。复核工单完工时把结果写回这一条。 */
    private Long sourceAlarmId;

    /** review 是复核工单。维修工单不填。 */
    private String alarmOrderKind;
}
