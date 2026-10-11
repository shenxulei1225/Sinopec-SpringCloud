package cn.cheers.x.inspection.task.service.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 已生成任务打开时：把「现在的检查方法」和「生成时写入步骤图的那一版」对照。
 *
 * <p>对照单位是一条已勾检查项在当前巡检方式下的步骤。每一步只带它自己声明的参数槽和这些槽在这台设备上的值。
 * 标准库里多出来、任务没勾上的检查项不进入这里。
 * <p>不管什么：不比较动作耗时总分钟，不比较路线停靠点集合，不决定要不要作废向导。
 * 到达位置、选择位置比点位编号，不比中文名。名称只给界面显示。
 */
public final class GeneratedCheckInputDiff {

    static final String LOCATION_REF = "location_ref";
    static final String SELECT_LOCATION = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";

    private GeneratedCheckInputDiff() {
    }

    /**
     * 一步检查方法：身份、动作、声明的参数槽、这些槽的当前值。
     * 槽名来自这一步自己的声明，不写死到达位置或耗时。
     */
    public record StepView(
            String stepKey,
            String actionRef,
            List<String> declaredSlots,
            Map<String, String> values
    ) {
        public StepView {
            stepKey = stepKey == null ? "" : stepKey.trim();
            actionRef = actionRef == null ? "" : actionRef.trim();
            declaredSlots = declaredSlots == null ? List.of() : List.copyOf(declaredSlots);
            values = values == null ? Map.of() : Map.copyOf(values);
        }
    }

    /**
     * slots：这次对不上的参数槽编码。页面用字段库判断这一格是不是路网点。
     * durationInput：这一格是动作耗时，或总分钟对不上。routeInput 不在这里按槽编码预先标成要去的点。
     */
    public record Change(
            String aspect,
            String detail,
            boolean routeInput,
            boolean durationInput,
            List<String> slots
    ) {
        public Change {
            slots = slots == null ? List.of() : List.copyOf(slots);
        }

        public Change(String aspect, String detail) {
            this(aspect, detail, false, false, List.of());
        }

        public Change(String aspect, String detail, boolean routeInput) {
            this(aspect, detail, routeInput, false, List.of());
        }
    }

    /**
     * 已勾的检查项不在这台设备当前适用关联里。
     * 关联还在则没有这一条；多出来但没勾进任务的项不调用这里。
     */
    public static List<Change> noLongerApplicable() {
        return List.of(new Change("适用关系", "已不再适用于这台设备"));
    }

    public static List<Change> diffItem(List<StepView> liveSteps, List<StepView> snapshotSteps) {
        List<StepView> live = liveSteps == null ? List.of() : liveSteps;
        List<StepView> snapshot = snapshotSteps == null ? List.of() : snapshotSteps;
        List<Change> changes = new ArrayList<>();
        if (snapshot.isEmpty()) {
            return changes;
        }
        if (live.isEmpty()) {
            changes.add(new Change("步骤", "这一档检查方法已经没有步骤"));
            return changes;
        }
        if (structureChanged(live, snapshot)) {
            List<String> structureSlots = differingSlots(live, snapshot);
            changes.add(new Change(
                    "步骤",
                    "检查方法的步骤或步骤上声明的参数变了",
                    false,
                    structureSlots.contains(ActionDurationParamSupport.FIELD_CODE),
                    structureSlots));
        }
        int shared = Math.min(live.size(), snapshot.size());
        List<String> paramDetails = new ArrayList<>();
        List<String> changedSlots = new ArrayList<>();
        boolean durationInput = false;
        for (int i = 0; i < shared; i++) {
            StepView now = live.get(i);
            StepView then = snapshot.get(i);
            if (!now.stepKey().equals(then.stepKey()) || !now.actionRef().equals(then.actionRef())) {
                continue;
            }
            for (String slot : now.declaredSlots()) {
                String liveValue = now.values().getOrDefault(slot, "");
                String savedValue = then.values().getOrDefault(slot, "");
                if (!sameSlotValue(slot, liveValue, savedValue)) {
                    paramDetails.add(slotChangeText(slot, savedValue, liveValue));
                    changedSlots.add(slot);
                    if (ActionDurationParamSupport.FIELD_CODE.equals(slot)) {
                        durationInput = true;
                    }
                }
            }
        }
        if (!paramDetails.isEmpty()) {
            changes.add(new Change(
                    "参数",
                    "参数变了：" + String.join("、", paramDetails),
                    false,
                    durationInput,
                    changedSlots));
        }
        return changes;
    }

    /** 步骤或槽对不上时，列出涉及的槽编码，供页面按字段库区分路网点和动作耗时。 */
    private static List<String> differingSlots(List<StepView> live, List<StepView> snapshot) {
        LinkedHashSet<String> slots = new LinkedHashSet<>();
        int count = Math.max(live.size(), snapshot.size());
        for (int i = 0; i < count; i++) {
            StepView now = i < live.size() ? live.get(i) : null;
            StepView then = i < snapshot.size() ? snapshot.get(i) : null;
            if (now == null || then == null
                    || !now.stepKey().equals(then.stepKey())
                    || !now.actionRef().equals(then.actionRef())) {
                if (now != null) slots.addAll(now.declaredSlots());
                if (then != null) slots.addAll(then.declaredSlots());
                continue;
            }
            LinkedHashSet<String> both = new LinkedHashSet<>();
            both.addAll(now.declaredSlots());
            both.addAll(then.declaredSlots());
            for (String slot : both) {
                if (!now.declaredSlots().contains(slot) || !then.declaredSlots().contains(slot)) {
                    slots.add(slot);
                    continue;
                }
                if (!sameSlotValue(slot, now.values().getOrDefault(slot, ""), then.values().getOrDefault(slot, ""))) {
                    slots.add(slot);
                }
            }
        }
        return List.copyOf(slots);
    }

    /** 界面要看到原来是多少、现在是多少。空着写「空」，不另造一套参数名。 */
    private static String slotChangeText(String slot, String savedValue, String liveValue) {
        String from = savedValue == null || savedValue.isBlank() ? "空" : savedValue;
        String to = liveValue == null || liveValue.isBlank() ? "空" : liveValue;
        return slot + " " + from + "→" + to;
    }

    private static boolean structureChanged(List<StepView> live, List<StepView> snapshot) {
        if (live.size() != snapshot.size()) {
            return true;
        }
        for (int i = 0; i < live.size(); i++) {
            StepView now = live.get(i);
            StepView then = snapshot.get(i);
            if (!now.stepKey().equals(then.stepKey()) || !now.actionRef().equals(then.actionRef())) {
                return true;
            }
            if (!now.declaredSlots().equals(then.declaredSlots())) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, String> valuesOf(List<String> slots, Map<String, ?> params) {
        return valuesOf(slots, params, Map.of());
    }

    /**
     * 选择位置、到达位置收成点位编号。步骤图里若只留下了中文名，用点名表找回编号。
     * 同一编号不算变了。其它槽仍按写下的值比较，数字 {@code 10} 和 {@code "10"} 相同。
     */
    public static Map<String, String> valuesOf(
            List<String> slots, Map<String, ?> params, Map<String, String> pointNames
    ) {
        Map<String, String> values = new LinkedHashMap<>();
        if (slots == null) {
            return values;
        }
        Map<String, String> names = pointNames == null ? Map.of() : pointNames;
        for (String slot : slots) {
            if (slot == null || slot.isBlank()) {
                continue;
            }
            Object raw = params == null ? null : params.get(slot);
            values.put(slot, isLocationSlot(slot) ? locationKey(raw, names) : plain(raw));
        }
        return values;
    }

    /**
     * 设备没填到达点时，更新任务留下步骤上已经有的点。
     * 打开对照用同一规则：这一格现在是空的、步骤上有点，不算变了。
     * 设备另选了一个点，仍算变了。
     */
    private static boolean sameSlotValue(String slot, String liveValue, String savedValue) {
        if (Objects.equals(liveValue, savedValue) || sameNumber(liveValue, savedValue)) {
            return true;
        }
        if (!isLocationSlot(slot)) {
            return false;
        }
        if ((liveValue == null || liveValue.isBlank()) && savedValue != null && !savedValue.isBlank()) {
            return true;
        }
        return liveValue != null && !liveValue.isBlank() && liveValue.equals(savedValue);
    }

    /** 1.666667 和 1.6666670 是同一个分钟数。空字符串不是数字。 */
    private static boolean sameNumber(String left, String right) {
        if (left == null || right == null || left.isBlank() || right.isBlank()) {
            return false;
        }
        try {
            return new BigDecimal(left.trim()).compareTo(new BigDecimal(right.trim())) == 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static boolean isLocationSlot(String slot) {
        return LOCATION_REF.equals(slot) || SELECT_LOCATION.equals(slot);
    }

    /**
     * 点位编号：对象里的 id，否则业务编码。纯中文名通过点名表反查编号。
     * 编号和名称对得上同一个点时，两边收到同一个编号。
     */
    static String locationKey(Object raw, Map<String, String> pointNames) {
        Set<String> aliases = new LinkedHashSet<>();
        String identity = locationIdentity(raw);
        if (!identity.isBlank()) {
            aliases.add(identity);
        }
        String named = locationName(raw);
        if (!named.isBlank()) {
            addKeysForName(aliases, pointNames, named);
        }
        if (!identity.isBlank()) {
            String fromKey = pointNames.get(identity);
            if (fromKey != null && !fromKey.isBlank()) {
                addKeysForName(aliases, pointNames, fromKey);
            }
            addKeysForName(aliases, pointNames, identity);
        }
        String numeric = null;
        for (String alias : aliases) {
            if (isPositiveLong(alias) && (numeric == null || alias.compareTo(numeric) < 0)) {
                numeric = alias;
            }
        }
        if (numeric != null) {
            return numeric;
        }
        if (!identity.isBlank()) {
            return identity;
        }
        return aliases.isEmpty() ? "" : aliases.iterator().next();
    }

    private static void addKeysForName(Set<String> aliases, Map<String, String> pointNames, String name) {
        for (Map.Entry<String, String> entry : pointNames.entrySet()) {
            if (name.equals(entry.getValue()) && entry.getKey() != null && !entry.getKey().isBlank()) {
                aliases.add(entry.getKey().trim());
            }
        }
    }

    private static String locationIdentity(Object raw) {
        if (raw instanceof Map<?, ?> obj) {
            String id = plain(obj.get("id"));
            if (!id.isBlank()) {
                return id;
            }
            String code = plain(obj.get("code"));
            return code;
        }
        return plain(raw);
    }

    private static String locationName(Object raw) {
        if (!(raw instanceof Map<?, ?> obj)) {
            return "";
        }
        return plain(obj.get("name"));
    }

    private static String plain(Object raw) {
        if (raw == null) {
            return "";
        }
        if (raw instanceof Number number) {
            double value = number.doubleValue();
            if (value == Math.rint(value) && !Double.isInfinite(value)) {
                return String.valueOf(number.longValue());
            }
            return String.valueOf(value);
        }
        String text = String.valueOf(raw).trim();
        return "null".equalsIgnoreCase(text) ? "" : text;
    }

    private static boolean isPositiveLong(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        try {
            return Long.parseLong(text.trim()) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean sameStep(StepView left, StepView right) {
        return left != null && right != null
                && Objects.equals(left.stepKey(), right.stepKey())
                && Objects.equals(left.actionRef(), right.actionRef());
    }
}
