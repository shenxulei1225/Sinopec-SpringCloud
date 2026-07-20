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
 * 事件处置历史
 * 
 * 按照数据模型.md中的定义实现
 */
@TableName("emergency_event_handling")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyEventHandlingDO extends EmergencyBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的应急事件ID
     */
    private Long emergencyEventId;

    /**
     * 处置人UserId（后端从UserId翻译为姓名写入handlerName，便于审计）
     */
    private Long handlerId;

    /**
     * 处置人姓名（快照）
     */
    private String handlerName;

    /**
     * 处置时间
     */
    private LocalDateTime handlingTime;

    /**
     * 处置措施
     */
    private String measures;

    /**
     * 处置结果
     */
    private String results;

    /**
     * 处置附件 - JSONB
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> attachments;
}









