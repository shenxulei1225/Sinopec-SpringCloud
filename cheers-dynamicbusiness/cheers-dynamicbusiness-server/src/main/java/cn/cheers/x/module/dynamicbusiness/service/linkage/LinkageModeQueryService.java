package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从联动方式这一格读出要对当前事件执行的目标和动作。
 *
 * <p>权威：联动方式 JSON 的 rows。来源设备写在 when 里，目标和动作写在 steps 里。</p>
 * <p>禁止：扫描绑定子实体、解析联动事件旧长文本、没有目标时猜一台设备。</p>
 */
@Service
public class LinkageModeQueryService {

    private static final Pattern DEVICE_WHEN = Pattern.compile("^(?:告警设备|device)\\s+等于\\s+(\\d+)$");

    /**
     * 返回来源为空或与当前事件来源一致的一对。
     */
    public List<LinkagePair> findMatchingPairs(EntityDO linkage, String sourceEntityTypeCode, long sourceEntityId) {
        List<LinkagePair> pairs = parsePairs(LinkageFieldValues.value(linkage, LinkageFieldCodes.MODE));
        if (pairs.isEmpty()) {
            return List.of();
        }
        boolean equipmentSource = LinkageFieldCodes.TARGET_EQUIPMENT.equals(
                sourceEntityTypeCode == null ? "" : sourceEntityTypeCode.trim());
        List<LinkagePair> matched = new ArrayList<>();
        for (LinkagePair pair : pairs) {
            if (pair.sourceEquipmentId() == null) {
                matched.add(pair);
                continue;
            }
            if (equipmentSource && pair.sourceEquipmentId() == sourceEntityId) {
                matched.add(pair);
            }
        }
        return List.copyOf(matched);
    }

    static List<LinkagePair> parsePairs(Object raw) {
        if (raw instanceof String text) {
            String trimmed = text.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                return parsePairs(JSONUtil.parse(trimmed));
            }
            return List.of();
        }
        Object rowsRaw = rowsOf(raw);
        if (!(rowsRaw instanceof List<?> rows) || rows.isEmpty()) {
            return List.of();
        }
        List<LinkagePair> pairs = new ArrayList<>();
        long index = 0;
        for (Object rowRaw : rows) {
            if (!(rowRaw instanceof Map<?, ?> row)) {
                continue;
            }
            Long sourceId = sourceEquipmentId(row.get("when"));
            Object stepsRaw = row.get("steps");
            if (!(stepsRaw instanceof List<?> steps) || steps.isEmpty()) {
                continue;
            }
            for (Object stepRaw : steps) {
                if (!(stepRaw instanceof Map<?, ?> step)) {
                    continue;
                }
                Long targetId = FacilityOwningFieldCodes.extractId(step.get("equipmentId"));
                Long actionId = FacilityOwningFieldCodes.extractId(step.get("actionId"));
                if (targetId == null || actionId == null) {
                    continue;
                }
                index += 1;
                pairs.add(new LinkagePair(index, sourceId, targetId, actionId));
            }
        }
        return List.copyOf(pairs);
    }

    private static Object rowsOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            return map.get("rows");
        }
        if (raw instanceof List<?> list) {
            return list;
        }
        return null;
    }

    private static Long sourceEquipmentId(Object whenRaw) {
        if (!(whenRaw instanceof List<?> when) || when.isEmpty()) {
            return null;
        }
        Long found = null;
        for (Object item : when) {
            if (item == null) {
                continue;
            }
            Matcher matcher = DEVICE_WHEN.matcher(String.valueOf(item).trim());
            if (!matcher.matches()) {
                continue;
            }
            found = Long.parseLong(matcher.group(1));
        }
        return found;
    }
}
