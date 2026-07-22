package cn.iocoder.yudao.module.emergency.dal.dataobject.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 事件对外上报
 * 
 * 按照数据模型.md中的定义实现
 */
@TableName("emergency_event_report_external")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventReportExternalDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的应急事件ID
     */
    private Long emergencyEventId;

    /**
     * 使用的表单模板ID（关联emergency_report_form_template）
     */
    private Long formTemplateId;

    /**
     * 报告单位（应急指挥小组所属单位）
     */
    private String reportOrgName;

    /**
     * 上报人UserId（后端从UserId翻译为姓名写入reporterName，便于审计）
     */
    private Long reporterId;

    /**
     * 上报人姓名（快照）
     */
    private String reporterName;

    /**
     * 上报人联系电话
     */
    private String reporterPhone;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 签发人UserId
     */
    private Long signerId;

    /**
     * 签发人姓名（快照）
     */
    private String signerName;

    /**
     * 接收单位列表（支持多单位，如：["地方政府", "上级单位", "公安部门"]）- JSONB
     */
    @TableField(value = "receive_org_names", typeHandler = cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler.class)
    private List<String> receiveOrgNames;

    // ========== 事件简要情况 ==========

    /**
     * 事件发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 省（自治区）
     */
    private String province;

    /**
     * 市（县）
     */
    private String cityOrCounty;

    /**
     * 乡镇
     */
    private String town;

    /**
     * 详细地址
     */
    private String locationAddress;

    /**
     * GIS坐标（PostGIS POINT类型，存储为WKT格式字符串）
     */
    private String locationGis;

    /**
     * BIM坐标
     */
    private String locationBim;

    // ========== 行业特定字段（可选，按行业显隐） ==========

    /**
     * 管线名称（仅管道场景）
     */
    private String pipelineName;

    /**
     * 标段（仅管道场景）
     */
    private String sectionName;

    /**
     * 机组（仅机组场景）
     */
    private String unitName;

    /**
     * 场站/工地名称
     */
    private String siteName;

    /**
     * 事件发生单位类型：enterprise（企业）/ secondary_enterprise（二级单位）/ contractor（承包商单位）
     */
    private String eventOrgType;

    /**
     * 事件发生单位名称
     */
    private String eventOrgName;

    // ========== 事件经过简要描述（分条） ==========

    /**
     * 突发事件发生经过（如谁发现、谁报警等，初判事件级别）
     */
    private String descriptionPart1;

    /**
     * 是否有火灾、爆炸、环境等情况
     */
    private String descriptionPart2;

    /**
     * 有无人身伤亡、失踪情况
     */
    private String descriptionPart3;

    /**
     * 其他情况补充
     */
    private String descriptionOther;

    // ========== 采取应急措施情况（分条） ==========

    /**
     * 是否造成在役管线停输、其他应急处置措施
     */
    private String measurePipelineAndOther;

    /**
     * 是否向集团/地区公司调控中心及上下游通报
     */
    private String measureReportToUpperLower;

    /**
     * 启动应急响应程序情况
     */
    private String measureResponseActivation;

    /**
     * 向地方政府、公安、消防、卫生等部门报告情况
     */
    private String measureReportToGovernment;

    /**
     * 其他情况（如何时赶赴现场、何时抵达现场等）
     */
    private String measureOther;

    // ========== 附件 ==========

    /**
     * 附件信息（照片、视频、正式报告文档等）- JSONB
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> attachments;

    // ========== 审核流程 ==========

    /**
     * 审核状态：draft（草稿）/ pending（待审核）/ approved（已审核）/ sent（已发送）
     */
    private String auditStatus;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人UserId
     */
    private Long auditorId;

    /**
     * 审核人姓名（快照）
     */
    private String auditorName;

    /**
     * 审核意见
     */
    private String auditComment;

    // ========== 信息接收与领导批示（由接收方填写） ==========

    /**
     * 信息接收时间（由接收方填写）
     */
    private LocalDateTime receiveTime;

    /**
     * 信息接收部门（由接收方填写）
     */
    private String receiveDeptName;

    /**
     * 接收人姓名（由接收方填写）
     */
    private String receiverName;

    /**
     * 接收人联系电话（由接收方填写）
     */
    private String receiverPhone;

    /**
     * 领导批示（由接收方填写）
     */
    private String leaderInstruction;

    /**
     * 自定义字段值（JSON格式，key为字段code，value为字段值）
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> customFields;
}

