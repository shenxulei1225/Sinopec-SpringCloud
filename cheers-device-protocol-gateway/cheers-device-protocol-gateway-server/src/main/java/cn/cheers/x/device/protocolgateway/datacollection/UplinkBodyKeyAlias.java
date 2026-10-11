package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 收包时，定时状态和步骤结果的外层对象先认两个字段名。
 * <p>负责：厂商按协议书用 {@code packages}、或按模拟器用 {@code result} 时，都转到当前下游已经在读的那个字段。
 * <p>不负责：改模拟器、改下发指令包、把里面的状态字段摊平。
 * <p>禁止：两个字段都有还互相覆盖。厂商确认只用哪一个名字之后，删掉另一侧。
 */
@Slf4j
public final class UplinkBodyKeyAlias {

    private UplinkBodyKeyAlias() {
    }

    /**
     * 只处理定时状态 {@code 500103} 和步骤结果 {@code 500202}。
     * 其它操作码原样返回，避免把下发指令集合的 {@code packages} 当成状态。
     */
    public static Map<String, Object> acceptEitherCarrier(int opcode, Map<String, Object> fields) {
        if (fields == null || fields.isEmpty()) {
            return fields;
        }
        if (opcode == TransportOpcode.COMMAND_RESULT.code()) {
            return aliasStepResult(fields);
        }
        if (opcode == TransportOpcode.TIMED_DEVICE_STATUS.code()) {
            return aliasTimedStatus(fields);
        }
        return fields;
    }

    /**
     * 步骤结果下游读 {@code packages}。只有 {@code result} 且里面带动作码或序号时，补到 {@code packages}。
     * 里面只有结果码、没有这一步身份时不补，缺字段仍由核对报出来。
     */
    private static Map<String, Object> aliasStepResult(Map<String, Object> fields) {
        Object packages = fields.get("packages");
        Object result = fields.get("result");
        if (hasBody(packages)) {
            if (isStepObject(result)) {
                log.info("[device-protocol] 步骤结果同时带了 packages 和 result，先按 packages 处理");
            }
            return fields;
        }
        if (!isStepObject(result)) {
            return fields;
        }
        log.info("[device-protocol] 步骤结果外层字段是 result，暂按 packages 继续处理。厂商确认字段名后只留一个");
        Map<String, Object> next = new LinkedHashMap<>(fields);
        next.put("packages", result);
        return next;
    }

    /**
     * 定时状态下游读 {@code result}。只有对象形态的 {@code packages} 时，补到 {@code result}。
     * 数组形态的 {@code packages} 是指令集合，不拿来当状态。
     */
    private static Map<String, Object> aliasTimedStatus(Map<String, Object> fields) {
        Object result = fields.get("result");
        Object packages = fields.get("packages");
        if (hasBody(result)) {
            if (packages instanceof Map<?, ?> map && !map.isEmpty()) {
                log.info("[device-protocol] 定时状态同时带了 result 和 packages，先按 result 处理");
            }
            return fields;
        }
        if (!(packages instanceof Map<?, ?> map) || map.isEmpty()) {
            return fields;
        }
        log.info("[device-protocol] 定时状态外层字段是 packages，暂按 result 继续处理。厂商确认字段名后只留一个");
        Map<String, Object> next = new LinkedHashMap<>(fields);
        next.put("result", packages);
        return next;
    }

    private static boolean isStepObject(Object value) {
        if (!(value instanceof Map<?, ?> map) || map.isEmpty()) {
            return false;
        }
        return map.containsKey("opcode") || map.containsKey("sequence");
    }

    private static boolean hasBody(Object value) {
        if (value instanceof Map<?, ?> map) {
            return !map.isEmpty();
        }
        if (value instanceof List<?> list) {
            return !list.isEmpty();
        }
        return false;
    }
}
