package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.service.TenantFrameworkService;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.action.ActionFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 执行已经受理的标准动作调用。
 *
 * <p>动作类型由动作所属模型决定。执行完成状态与状态变化 outbox 同事务提交。</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActionExecutionWorker {

    private static final int BATCH_SIZE = 50;

    private final TenantFrameworkService tenantFrameworkService;
    private final ActionExecutionMapper mapper;
    private final EntityRepository entityRepository;
    private final ModelMapper modelMapper;
    private final List<ActionExecutor> executors;
    private final PlatformEventOutboxService outboxService;
    private final ObjectProvider<ActionExecutionWorker> selfProvider;

    @Scheduled(fixedDelayString = "${dynamicbusiness.action-execution.delay-ms:1000}")
    public void executePending() {
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (tenantIds == null) {
            return;
        }
        for (Long tenantId : tenantIds) {
            if (tenantId != null && tenantId > 0) {
                TenantUtils.execute(tenantId, this::executeCurrentTenant);
            }
        }
    }

    private void executeCurrentTenant() {
        ActionExecutionWorker self = selfProvider.getIfAvailable(() -> this);
        for (ActionExecutionDO row : mapper.selectPending(BATCH_SIZE)) {
            try {
                self.executeOne(row.getId());
            } catch (RuntimeException failure) {
                log.error("动作执行 {} 未完成：{}", row.getId(), failure.getMessage());
            }
        }
    }

    /**
     * 执行失败也提交 FAILED 与状态变化事件，避免调用方一直停在等待中。
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeOne(long executionId) {
        if (mapper.claim(executionId) == 0) {
            return;
        }
        ActionExecutionDO execution = mapper.selectById(executionId);
        try {
            EntityDO action = requireAction(execution);
            ModelDO model = modelMapper.selectById(action.getModelId());
            String modelCode = model == null ? null : model.getCode();
            ActionExecutor executor = executors.stream()
                    .filter(item -> item.supports(modelCode, execution.getTargetEntityTypeCode()))
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(400, "动作没有可用执行器"));
            executor.execute(action, execution.getTargetEntityId(), parameters(execution));
            execution.setStatus(ActionExecutionStatus.SUCCEEDED);
            execution.setErrorCode(null);
            execution.setErrorMessage(null);
        } catch (RuntimeException failure) {
            execution.setStatus(ActionExecutionStatus.FAILED);
            execution.setErrorCode(failure instanceof ServiceException ? "REJECTED" : "EXECUTION_ERROR");
            execution.setErrorMessage(clip(failure.getMessage()));
        }
        execution.setFinishedAt(LocalDateTime.now());
        mapper.updateById(execution);
        outboxService.enqueueActionStatus(execution);
    }

    private EntityDO requireAction(ActionExecutionDO execution) {
        EntityDO action = entityRepository.findById(execution.getActionId(), ActionFieldCodes.ENTITY_TYPE_CODE);
        if (action == null || !Integer.valueOf(1).equals(action.getStatus())) {
            throw new ServiceException(404, "动作不存在或已停用");
        }
        return action;
    }

    private static Map<String, Object> parameters(ActionExecutionDO execution) {
        return execution.getParameters() == null ? Map.of() : Map.copyOf(execution.getParameters());
    }

    private static String clip(String value) {
        if (value == null) {
            return "未知错误";
        }
        return value.length() <= 2000 ? value : value.substring(0, 2000);
    }
}
