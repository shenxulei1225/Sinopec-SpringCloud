package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 按事件主题查询已经启用的告警联动。
 *
 * <p>权威：联动保存产生的标准实体关系。先按事件主题反查候选，再按联动目录的归属声明对上事件载荷里的同一跳。</p>
 * <p>禁止：扫描全部联动、读取旧联动文本、把全部成立当成任一触发、按事件名称或动作名称匹配。</p>
 */
@Service
@RequiredArgsConstructor
public class LinkageRuleQueryService {

    private final EntityRelationService entityRelationService;
    private final EntityRepository entityRepository;
    private final CatalogOwnershipQueryService catalogOwnershipQueryService;

    /**
     * 事件到达时只返回同一归属的启用告警联动。
     * 联动目录没声明归属时，不按归属再筛。
     * 声明了的每一栏，规则上的编号要和事件载荷里同一栏相同。空着的对不上有值的。
     *
     * @param eventTopicId 事件主题实体编号
     * @param ownership 事件载荷里按字段编码记下的归属编号
     */
    public List<EntityDO> findEnabledAlarmRules(long eventTopicId, Map<String, Long> ownership) {
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
                .filter(rule -> matchesOwnership(rule, ownership))
                .toList();
    }

    /** 按联动目录的声明逐栏对。没声明则这条规则不再被归属拦住。 */
    private boolean matchesOwnership(EntityDO rule, Map<String, Long> ownership) {
        List<CatalogOwnershipDO> declarations =
                catalogOwnershipQueryService.listByRegistryCode(LinkageFieldCodes.LINKAGE_DIRECTORY);
        if (declarations == null || declarations.isEmpty()) {
            return true;
        }
        Map<String, Long> eventOwnership = ownership == null ? Map.of() : ownership;
        boolean compared = false;
        for (CatalogOwnershipDO declaration : declarations) {
            if (declaration == null || declaration.getFieldCode() == null || declaration.getFieldCode().isBlank()) {
                continue;
            }
            String field = declaration.getFieldCode().trim();
            Long ruleId = fieldId(rule, field);
            Long eventId = eventOwnership.get(field);
            if (ruleId == null && eventId == null) {
                continue;
            }
            compared = true;
            if (ruleId == null || !ruleId.equals(eventId)) {
                return false;
            }
        }
        return compared;
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

    private static Long fieldId(EntityDO rule, String fieldCode) {
        Object raw = rule.getDedicatedBaseFieldValues() == null
                ? null
                : rule.getDedicatedBaseFieldValues().get(fieldCode);
        if (raw == null && rule.getCustomFields() != null) {
            raw = rule.getCustomFields().get(fieldCode);
        }
        return FacilityOwningFieldCodes.extractId(raw);
    }
}
