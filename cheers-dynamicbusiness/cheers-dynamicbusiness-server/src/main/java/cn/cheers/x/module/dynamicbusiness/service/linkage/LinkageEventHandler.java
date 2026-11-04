package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionReceipt;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionRequest;
import cn.cheers.x.module.dynamicbusiness.service.action.execution.ActionExecutionService;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventHandler;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventKinds;
import cn.cheers.x.module.dynamicbusiness.service.event.PlatformEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 消息通道把告警事件送到联动之后的入口。
 *
 * <p>事件主题反查联动，再按联动方式里的一对提交标准动作调用。</p>
 * <p>执行方式是人工确认后执行时，只记下等人确认，不立刻提交动作。</p>
 * <p>禁止：读取告警记录、扫描全部联动、让告警包直接调用本入口、直接调用设备协议。</p>
 */
@Component
@RequiredArgsConstructor
public class LinkageEventHandler implements PlatformEventHandler {

    private final LinkageRuleQueryService ruleQueryService;
    private final LinkageModeQueryService modeQueryService;
    private final LinkageExecutionService linkageExecutionService;
    private final ActionExecutionService actionExecutionService;

    @Override
    public boolean supports(String eventKind) {
        return PlatformEventKinds.ALARM_LIFECYCLE.equals(eventKind);
    }

    @Override
    public void handle(PlatformEventMessage event) {
        for (EntityDO rule : ruleQueryService.findEnabledAlarmRules(event.eventTopicId(), event.facilityId())) {
            LinkageExecutionStart started = linkageExecutionService.begin(event, rule.getId());
            if (!started.created()) {
                continue;
            }
            if (LinkageExecutionMode.waitForConfirm(LinkageFieldValues.value(rule, LinkageFieldCodes.EXECUTION))) {
                linkageExecutionService.recordWaitingConfirm(started.executionId());
                continue;
            }
            List<LinkagePair> pairs = modeQueryService.findMatchingPairs(
                    rule, event.sourceEntityTypeCode(), event.sourceEntityId());
            if (pairs.isEmpty()) {
                linkageExecutionService.recordNoPairs(started.executionId());
                continue;
            }
            for (LinkagePair pair : pairs) {
                submitPair(event, rule.getId(), started.executionId(), pair);
            }
        }
    }

    private void submitPair(PlatformEventMessage event, long ruleId, long executionId, LinkagePair pair) {
        try {
            if (pair.targetEquipmentId() <= 0 || pair.actionId() <= 0) {
                throw new ServiceException(400, "联动方式这一对缺少目标和动作");
            }
            ActionExecutionReceipt receipt = actionExecutionService.submit(new ActionExecutionRequest(
                    event.eventId() + ":" + ruleId + ":" + pair.index(),
                    "LINKAGE",
                    "linkage-execution:" + executionId,
                    event.facilityId(),
                    pair.actionId(),
                    LinkageFieldCodes.TARGET_EQUIPMENT,
                    pair.targetEquipmentId(),
                    Map.of()));
            linkageExecutionService.recordAcceptedItem(
                    executionId, pair.index(), receipt.actionExecutionId());
        } catch (RuntimeException failure) {
            linkageExecutionService.recordRejectedItem(executionId, pair.index(), failure.getMessage());
        }
    }
}
