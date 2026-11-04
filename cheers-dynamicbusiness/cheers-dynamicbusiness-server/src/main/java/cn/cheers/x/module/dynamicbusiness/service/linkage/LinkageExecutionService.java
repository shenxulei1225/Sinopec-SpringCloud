package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionStatus;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 保存联动整体记录，并按动作状态聚合最终结果。
 *
 * <p>不保存动作执行错误的第二份权威。执行项只保留用于联动聚合的状态和错误摘要。</p>
 */
@Service
@RequiredArgsConstructor
public class LinkageExecutionService {

    private final LinkageExecutionMapper executionMapper;
    private final LinkageExecutionItemMapper itemMapper;

    @Transactional(rollbackFor = Exception.class)
    public LinkageExecutionStart begin(PlatformEventMessage event, long linkageRuleId) {
        LinkageExecutionDO existing = executionMapper.selectByEventAndRule(event.eventId(), linkageRuleId);
        if (existing != null) {
            return new LinkageExecutionStart(existing.getId(), false);
        }
        LinkageExecutionDO row = new LinkageExecutionDO();
        row.setEventId(event.eventId());
        row.setEventTopicId(event.eventTopicId());
        row.setLinkageRuleId(linkageRuleId);
        row.setFacilityId(event.facilityId());
        row.setSourceEntityTypeCode(event.sourceEntityTypeCode());
        row.setSourceEntityId(event.sourceEntityId());
        row.setStatus(LinkageExecutionStatus.PENDING);
        row.setItemCount(0);
        row.setSuccessCount(0);
        row.setFailureCount(0);
        row.setTenantId(TenantContextHolder.getRequiredTenantId());
        try {
            executionMapper.insert(row);
            return new LinkageExecutionStart(row.getId(), true);
        } catch (DuplicateKeyException race) {
            LinkageExecutionDO raced = executionMapper.selectByEventAndRule(event.eventId(), linkageRuleId);
            return new LinkageExecutionStart(raced.getId(), false);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordAcceptedItem(long linkageExecutionId, long pairIndex, long actionExecutionId) {
        LinkageExecutionItemDO item = new LinkageExecutionItemDO();
        item.setLinkageExecutionId(linkageExecutionId);
        item.setPairIndex(pairIndex);
        item.setActionExecutionId(actionExecutionId);
        item.setStatus(LinkageExecutionStatus.RUNNING);
        item.setTenantId(TenantContextHolder.getRequiredTenantId());
        itemMapper.insert(item);
        aggregate(linkageExecutionId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordRejectedItem(long linkageExecutionId, long pairIndex, String error) {
        LinkageExecutionItemDO item = new LinkageExecutionItemDO();
        item.setLinkageExecutionId(linkageExecutionId);
        item.setPairIndex(pairIndex);
        item.setStatus(LinkageExecutionStatus.FAILED);
        item.setLastError(clip(error));
        item.setTenantId(TenantContextHolder.getRequiredTenantId());
        itemMapper.insert(item);
        aggregate(linkageExecutionId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordWaitingConfirm(long linkageExecutionId) {
        LinkageExecutionDO execution = executionMapper.selectById(linkageExecutionId);
        if (execution == null) {
            return;
        }
        execution.setStatus(LinkageExecutionStatus.WAITING_CONFIRM);
        execution.setLastError(null);
        executionMapper.updateById(execution);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recordNoPairs(long linkageExecutionId) {
        LinkageExecutionDO execution = executionMapper.selectById(linkageExecutionId);
        if (execution == null) {
            return;
        }
        execution.setStatus(LinkageExecutionStatus.FAILED);
        execution.setLastError("联动方式里没有可执行的一对");
        executionMapper.updateById(execution);
    }

    @Transactional(rollbackFor = Exception.class)
    public void applyActionStatus(long actionExecutionId, String actionStatus, String error) {
        LinkageExecutionItemDO item = itemMapper.selectByActionExecutionId(actionExecutionId);
        if (item == null) {
            return;
        }
        item.setStatus(ActionExecutionStatus.SUCCEEDED.equals(actionStatus)
                ? LinkageExecutionStatus.SUCCEEDED
                : LinkageExecutionStatus.FAILED);
        item.setLastError(ActionExecutionStatus.SUCCEEDED.equals(actionStatus) ? null : clip(error));
        itemMapper.updateById(item);
        aggregate(item.getLinkageExecutionId());
    }

    private void aggregate(long linkageExecutionId) {
        List<LinkageExecutionItemDO> items = itemMapper.selectByLinkageExecutionId(linkageExecutionId);
        LinkageExecutionDO execution = executionMapper.selectById(linkageExecutionId);
        if (execution == null) {
            return;
        }
        int succeeded = (int) items.stream()
                .filter(item -> LinkageExecutionStatus.SUCCEEDED.equals(item.getStatus()))
                .count();
        int failed = (int) items.stream()
                .filter(item -> LinkageExecutionStatus.FAILED.equals(item.getStatus()))
                .count();
        execution.setItemCount(items.size());
        execution.setSuccessCount(succeeded);
        execution.setFailureCount(failed);
        if (items.isEmpty()) {
            execution.setStatus(LinkageExecutionStatus.PENDING);
        } else if (succeeded + failed < items.size()) {
            execution.setStatus(LinkageExecutionStatus.RUNNING);
        } else if (failed == 0) {
            execution.setStatus(LinkageExecutionStatus.SUCCEEDED);
        } else if (succeeded == 0) {
            execution.setStatus(LinkageExecutionStatus.FAILED);
        } else {
            execution.setStatus(LinkageExecutionStatus.PARTIAL_FAILED);
        }
        executionMapper.updateById(execution);
    }

    private static String clip(String value) {
        if (value == null) {
            return "未知错误";
        }
        return value.length() <= 2000 ? value : value.substring(0, 2000);
    }
}
