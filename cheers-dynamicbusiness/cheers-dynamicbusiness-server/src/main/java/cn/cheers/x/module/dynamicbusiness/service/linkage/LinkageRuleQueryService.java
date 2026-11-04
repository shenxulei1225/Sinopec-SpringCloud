package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 按事件主题查询已经启用的告警联动。
 *
 * <p>权威：联动保存产生的标准实体关系。先按事件主题反查候选，再校验业务域、场站、触发方式和启用状态。</p>
 * <p>禁止：扫描全部联动、读取旧联动文本、把全部成立当成任一触发、按事件名称或动作名称匹配。</p>
 */
@Service
@RequiredArgsConstructor
public class LinkageRuleQueryService {

    private final EntityRelationService entityRelationService;
    private final EntityRepository entityRepository;

    /**
     * 事件到达时只返回同场站的启用告警联动。
     *
     * @param eventTopicId 事件主题实体编号
     * @param facilityId 事件所属场站
     */
    public List<EntityDO> findEnabledAlarmRules(long eventTopicId, long facilityId) {
        List<Long> ids = entityRelationService.listSubjectEntityIdsByRefFieldAndTargetIds(
                LinkageFieldCodes.EVENT_TOPICS,
                LinkageFieldCodes.LINKAGE_DIRECTORY,
                List.of(eventTopicId));
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityRepository.findByIdsWithDedicatedBaseFields(ids, LinkageFieldCodes.LINKAGE_DIRECTORY)
                .stream()
                .filter(this::isEnabledAlarmRule)
                .filter(rule -> facilityId == facilityId(rule))
                .toList();
    }

    private boolean isEnabledAlarmRule(EntityDO rule) {
        return rule != null
                && rule.getId() != null
                && Integer.valueOf(1).equals(rule.getStatus())
                && LinkageFieldCodes.DOMAIN_ALARM.equals(rule.getDomain())
                && LinkageFieldCodes.TRIGGER_ANY.equals(triggerMode(rule));
    }

    private static String triggerMode(EntityDO rule) {
        Object raw = rule.getDedicatedBaseFieldValues() == null
                ? null
                : rule.getDedicatedBaseFieldValues().get(LinkageFieldCodes.TRIGGER_MODE);
        if (raw == null && rule.getCustomFields() != null) {
            raw = rule.getCustomFields().get(LinkageFieldCodes.TRIGGER_MODE);
        }
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private long facilityId(EntityDO rule) {
        Long id = FacilityOwningFieldCodes.extractId(
                rule.getDedicatedBaseFieldValues() == null
                        ? null
                        : rule.getDedicatedBaseFieldValues().get(FacilityOwningFieldCodes.FIELD_CODE));
        return id == null ? -1L : id;
    }
}
