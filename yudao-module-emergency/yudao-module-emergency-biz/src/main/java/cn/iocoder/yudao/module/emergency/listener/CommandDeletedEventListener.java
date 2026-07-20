package cn.iocoder.yudao.module.emergency.listener;

import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import cn.iocoder.yudao.module.emergency.event.CommandDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 指令删除事件监听器
 * 
 * 当指令被删除时，自动从所有关联的预案步骤的commandIdList中移除该指令ID
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class CommandDeletedEventListener {
    
    @Resource
    private EmergencyPlanStepMapper planStepMapper;
    
    /**
     * 处理指令删除事件
     * 自动从所有关联的预案步骤的commandIdList中移除该指令ID
     */
    @EventListener
    @Async // 异步处理，避免阻塞指令删除操作
    @Transactional(rollbackFor = Exception.class)
    public void handleCommandDeleted(CommandDeletedEvent event) {
        Long commandId = event.getCommandId();
        log.info("[handleCommandDeleted][开始处理指令删除级联] commandId={}", commandId);
        
        // 查询所有包含该指令ID的预案步骤
        List<EmergencyPlanStepDO> affectedSteps = findAffectedSteps(commandId);
        
        if (affectedSteps.isEmpty()) {
            log.info("[handleCommandDeleted][没有关联的预案步骤] commandId={}", commandId);
            return;
        }
        
        // 从每个步骤的commandIdList中移除该指令ID
        List<Long> affectedStepIds = new ArrayList<>();
        for (EmergencyPlanStepDO step : affectedSteps) {
            List<Long> commandIdList = step.getCommandIdList();
            if (commandIdList != null && commandIdList.contains(commandId)) {
                commandIdList.remove(commandId);
                step.setCommandIdList(commandIdList.isEmpty() ? null : commandIdList);
                planStepMapper.updateById(step);
                affectedStepIds.add(step.getId());
                log.info("[handleCommandDeleted][更新预案步骤] stepId={}, commandIdList={}", 
                    step.getId(), step.getCommandIdList());
            }
        }
        
        // TODO: 记录审计日志（可选，如果需要审计功能）
        // auditService.recordCommandDelete(commandId, affectedStepIds.size(), affectedStepIds, 
        //     event.getOperator(), event.getTenantId());
        
        log.info("[handleCommandDeleted][处理完成] commandId={}, affectedStepCount={}", 
            commandId, affectedStepIds.size());
    }
    
    /**
     * 查询所有包含指定指令ID的预案步骤
     * 使用PostgreSQL的JSONB数组操作符 @> 进行高效查询
     * 利用GIN索引优化查询性能
     * SQL: SELECT * FROM emergency_plan_step WHERE command @> '[commandId]'::jsonb AND deleted = false
     */
    private List<EmergencyPlanStepDO> findAffectedSteps(Long commandId) {
        // 使用Mapper的专用查询方法，利用GIN索引优化性能
        return planStepMapper.selectByCommandId(commandId);
    }
}

