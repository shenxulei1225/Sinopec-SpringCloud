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
 * 事件内部上报
 * 
 * 按照数据模型.md中的定义实现
 */
@TableName("emergency_event_report_internal")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventReportInternalDO extends EmergencyBaseDO {

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
     * 上报人UserId（后端从UserId翻译为姓名写入reporterName，便于审计）
     */
    private Long reporterId;

    /**
     * 上报人姓名（快照）
     */
    private String reporterName;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 上报内容（标准字段）
     */
    private String content;

    /**
     * 自定义字段值（JSON格式，key为字段code，value为字段值）
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> customFields;

    /**
     * 附件信息（照片、视频、文档等）- JSONB
     */
    @TableField(typeHandler = cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> attachments;

    /**
     * 上报时地址
     */
    private String locationAddress;

    /**
     * 上报时GIS坐标（PostGIS POINT类型，存储为WKT格式字符串，如"POINT(116.397128 39.916527)"）
     */
    private String locationGis;

    /**
     * 上报时BIM坐标
     */
    private String locationBim;
}

