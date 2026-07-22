package cn.iocoder.yudao.module.emergency.dal.dataobject.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 应急事件
 * 
 * 按照数据模型.md中的定义实现
 */
@TableName("emergency_event")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventDO extends EmergencyBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 事件编号（唯一、可报送）
     */
    private String eventCode;

    /**
     * 事件名称
     */
    @TableField("event_name")
    private String eventName;

    /**
     * 事件分类ID（BIGINT类型，存储分类ID）
     * 关联基础服务的分类管理模块，支持多级分类
     * 创建时可选（允许为空），更新时必须设置并验证有效性（存在且未删除）
     * 原eventType字段，现改为存储分类ID（categoryId）
     */
    private Long eventType;

    /**
     * 细分类型（已废弃）
     * 该字段已废弃，不再使用
     * 保留字段仅用于兼容历史数据，新数据不应使用此字段
     * @deprecated 已废弃，请使用eventType（分类ID）代替
     */
    @Deprecated
    private String eventSubType;

    /**
     * 特别重大/重大/较大/一般
     */
    private String eventLevel;

    /**
     * 发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 发现时间
     */
    private LocalDateTime discoveredAt;

    /**
     * 地址位置
     */
    private String locationAddress;

    /**
     * GIS坐标（PostGIS POINT类型，存储为WKT格式字符串，如"POINT(116.397128 39.916527)"）
     */
    private String locationGis;

    /**
     * BIM坐标
     */
    private String locationBim;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 影响概述（人员/财产/环境/秩序）- JSONB
     */
    @TableField(typeHandler = cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> impactSummary;

    /**
     * 法律状态：pending/confirmed/assessing/handling/handled/closed
     */
    private String status;

    /**
     * 上报单位
     */
    private String reportedBy;

    /**
     * 指挥机构
     */
    private String commandOrg;

    /**
     * 是否已被政府认定
     */
    private Boolean isGovernmentConfirmed;

    /**
     * 附件信息（照片、视频等）- JSONB
     */
    @TableField(typeHandler = cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> attachments;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系方式
     */
    private String contactPhone;

    /**
     * Flowable 流程实例编号（接报 start 后写入；可为流程位置投影的绑定键）
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 告警来源类型（如 iot_alert_record）；与 sourceAlertId 组成跨模块关联
     */
    @TableField("source_alert_type")
    private String sourceAlertType;

    /**
     * 来源告警 id（只存关联标识，不存告警实体快照）
     */
    @TableField("source_alert_id")
    private String sourceAlertId;
}

