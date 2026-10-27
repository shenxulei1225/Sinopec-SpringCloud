package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;

import java.util.List;
import java.util.Map;

/**
 * 开跑前的电量检查：用设备报上的剩余可飞秒数，去比这一步已经记下的需要秒数。
 * <p>够才允许下发。不够、秒数空着、或算不出剩余时间，返回给用户看的那一句。
 * <p>不负责下发、不改执行状态。东古的百分比先换成秒数，不把百分比直接和秒数比。
 */
public final class BatteryHeadGate {

    static final String ROBOT_BATTERY = "act-robot-battery-check";
    static final String REQUIRED_DURATION = "required_duration";

    private BatteryHeadGate() {
    }

    /**
     * 电量检查已经记下的需要秒数。没有这一步，或这一格空着，返回 null。
     */
    public static Integer requiredSeconds(Object stepTree) {
        return find(stepTree).requiredSeconds();
    }

    /**
     * 有电量检查且不能下发时，返回提示。没有这一步，或剩余可飞时间够，返回 null。
     */
    public static String blockPrompt(Object stepTree, Integer remainFlySeconds, String electricity) {
        return blockPrompt(stepTree, remainFlySeconds, electricity, false);
    }

    /**
     * 东古按耗电曲线算出剩余秒数时，fromFullCharge 为真。大疆直接用报上的剩余可飞秒数时为假。
     */
    public static String blockPrompt(
            Object stepTree, Integer remainFlySeconds, String electricity, boolean fromFullCharge) {
        BatteryStep step = find(stepTree);
        if (!step.present()) {
            return null;
        }
        if (step.requiredSeconds() == null) {
            return "电量检查没有记下这次任务需要的秒数。这次无法执行。";
        }
        if (remainFlySeconds == null) {
            if (!fromFullCharge) {
                return "设备没有报上剩余可飞时间。这次需要 " + step.requiredSeconds() + " 秒。这次无法执行。";
            }
            if (electricity == null || electricity.isBlank()) {
                return "设备没有报上电量。这次需要 " + step.requiredSeconds() + " 秒。这次无法执行。";
            }
            return "设备报上的电量是 " + electricity.trim()
                    + "%。这台设备没有最长飞行时间，算不出还能用多久。这次需要 "
                    + step.requiredSeconds() + " 秒。这次无法执行。";
        }
        if (remainFlySeconds < step.requiredSeconds()) {
            if (fromFullCharge) {
                return "按耗电曲线算出还能用 " + remainFlySeconds
                        + " 秒，这次需要 " + step.requiredSeconds() + " 秒。这次无法执行。";
            }
            return "剩余可飞时间 " + remainFlySeconds + " 秒，这次需要 "
                    + step.requiredSeconds() + " 秒。这次无法执行。";
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static BatteryStep find(Object stepTree) {
        if (!(stepTree instanceof Map<?, ?> tree)) {
            return BatteryStep.absent();
        }
        Object nodes = tree.get("nodes");
        if (!(nodes instanceof List<?> list)) {
            return BatteryStep.absent();
        }
        for (Object raw : list) {
            if (!(raw instanceof Map<?, ?> node)) {
                continue;
            }
            String refCode = text(node.get("refCode"));
            if (!ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK.equals(refCode)
                    && !ROBOT_BATTERY.equals(refCode)) {
                continue;
            }
            Object params = node.get("params");
            Integer required = params instanceof Map<?, ?> map ? seconds(map.get(REQUIRED_DURATION)) : null;
            return new BatteryStep(true, required);
        }
        return BatteryStep.absent();
    }

    private static Integer seconds(Object raw) {
        if (raw instanceof Number number) {
            return number.intValue();
        }
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private record BatteryStep(boolean present, Integer requiredSeconds) {
        static BatteryStep absent() {
            return new BatteryStep(false, null);
        }
    }
}
