package cn.iocoder.yudao.module.emergency.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 指令删除事件
 * 
 * 当指令被删除时发布此事件，用于触发级联处理（从预案步骤的commandIdList中移除该指令ID）
 *
 * @author 芋道源码
 */
@Getter
@Setter
public class CommandDeletedEvent extends ApplicationEvent {
    
    /**
     * 被删除的指令ID
     */
    private Long commandId;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    public CommandDeletedEvent(Object source, Long commandId, String operator, Long tenantId) {
        super(source);
        this.commandId = commandId;
        this.operator = operator;
        this.tenantId = tenantId;
    }
}

