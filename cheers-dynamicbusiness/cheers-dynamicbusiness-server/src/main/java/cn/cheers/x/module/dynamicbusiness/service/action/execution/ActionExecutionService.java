package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.action.ActionFieldCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 标准动作调用入口。
 *
 * <p>负责：校验动作和目标引用，按幂等键创建待执行记录并立即返回动作执行编号。</p>
 * <p>不负责：决定应该调用哪个动作，不读取联动或巡检内部字段，也不直接下发协议。</p>
 */
@Service
@RequiredArgsConstructor
public class ActionExecutionService {

    private final ActionExecutionMapper actionExecutionMapper;
    private final EntityRepository entityRepository;

    /**
     * 同一租户的同一幂等键只生成一条动作执行记录。
     */
    @Transactional(rollbackFor = Exception.class)
    public ActionExecutionReceipt submit(ActionExecutionRequest request) {
        validate(request);
        ActionExecutionDO existing = actionExecutionMapper.selectByIdempotencyKey(request.idempotencyKey().trim());
        if (existing != null) {
            return new ActionExecutionReceipt(existing.getId(), existing.getStatus());
        }
        if (entityRepository.findById(request.actionId(), ActionFieldCodes.ENTITY_TYPE_CODE) == null) {
            throw new ServiceException(404, "动作不存在");
        }
        if (entityRepository.findById(request.targetEntityId(), request.targetEntityTypeCode().trim()) == null) {
            throw new ServiceException(404, "动作目标不存在");
        }

        ActionExecutionDO row = new ActionExecutionDO();
        row.setIdempotencyKey(request.idempotencyKey().trim());
        row.setCallerType(request.callerType().trim());
        row.setCallerReference(request.callerReference().trim());
        row.setFacilityId(request.facilityId());
        row.setActionId(request.actionId());
        row.setTargetEntityTypeCode(request.targetEntityTypeCode().trim());
        row.setTargetEntityId(request.targetEntityId());
        row.setParameters(copy(request.parameters()));
        row.setStatus(ActionExecutionStatus.PENDING);
        row.setAttempts(0);
        row.setAvailableAt(LocalDateTime.now());
        row.setTenantId(TenantContextHolder.getRequiredTenantId());
        actionExecutionMapper.insert(row);
        return new ActionExecutionReceipt(row.getId(), row.getStatus());
    }

    private static void validate(ActionExecutionRequest request) {
        if (request == null
                || !StringUtils.hasText(request.idempotencyKey())
                || !StringUtils.hasText(request.callerType())
                || !StringUtils.hasText(request.callerReference())
                || request.facilityId() <= 0
                || request.actionId() <= 0
                || !StringUtils.hasText(request.targetEntityTypeCode())
                || request.targetEntityId() <= 0) {
            throw new ServiceException(400, "动作调用缺少幂等键、动作或目标");
        }
    }

    private static Map<String, Object> copy(Map<String, Object> values) {
        return values == null ? Map.of() : Map.copyOf(new LinkedHashMap<>(values));
    }
}
