package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取动作组中有独立节点身份的成员引用。
 * 权威输入只认 {@code action_tree_json.members}。
 * 本类不判断协议支持，也不按动作名称推断成员。
 */
@Component
@RequiredArgsConstructor
public class ActionGroupMemberParser {

    private final ObjectMapper objectMapper;

    /**
     * 返回当前执行方式下按顺序排列的动作组成员。
     * 正式成员必须有 memberKey，保证同一动作重复引用时仍能分别保存参数。
     */
    public List<Member> parse(Object treeRaw) {
        Object parsed = parseJson(treeRaw);
        if (parsed instanceof Map<?, ?> root) {
            Object membersRaw = root.get("members");
            if (membersRaw instanceof List<?> members) {
                return parseMembers(members);
            }
        }
        return List.of();
    }

    private List<Member> parseMembers(List<?> members) {
        List<Member> result = new ArrayList<>();
        int fallbackOrder = 0;
        for (Object raw : members) {
            if (!(raw instanceof Map<?, ?> member)) {
                continue;
            }
            String actionCode = textOf(member.get("actionCode"));
            if (!StringUtils.hasText(actionCode)) {
                continue;
            }
            String memberKey = textOf(member.get("memberKey"));
            if (!StringUtils.hasText(memberKey)) {
                throw ServiceExceptionUtil.invalidParamException("动作组成员缺少 memberKey");
            }
            result.add(new Member(
                    memberKey,
                    actionCode,
                    objectMap(member.get("params")),
                    numberOf(member.get("order"), fallbackOrder++),
                    !Boolean.FALSE.equals(member.get("required"))));
        }
        return result.stream().sorted(Comparator.comparingInt(Member::order)).toList();
    }

    private Object parseJson(Object raw) {
        if (!(raw instanceof String text) || !StringUtils.hasText(text)) {
            return raw;
        }
        try {
            return objectMapper.readValue(text, Object.class);
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("动作组成员结构不是合法 JSON");
        }
    }

    private static Map<String, Object> objectMap(Object raw) {
        if (!(raw instanceof Map<?, ?> map)) {
            return Map.of();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        map.forEach((key, value) -> {
            if (key != null) {
                result.put(String.valueOf(key), value);
            }
        });
        return Collections.unmodifiableMap(result);
    }

    private static int numberOf(Object raw, int fallback) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        try {
            return raw == null ? fallback : Integer.parseInt(String.valueOf(raw));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static String textOf(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    public record Member(
            String memberKey,
            String actionCode,
            Map<String, Object> params,
            int order,
            boolean required
    ) {
    }
}
