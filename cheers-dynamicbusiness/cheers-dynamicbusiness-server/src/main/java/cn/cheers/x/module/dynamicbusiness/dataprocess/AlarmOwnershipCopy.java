package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警归属按告警目录已经声明的那一跳来抄。
 * 产生时从设备上已经填写的同一栏抄。再次出现和消除把告警上已经记下的再提交。
 * 不负责：谁能看见这条告警。那仍按声明逐跳算。
 * 禁止：在这里写死场站、管线段、校区或行政区划。
 */
final class AlarmOwnershipCopy {

    /**
     * 告警目录上的一跳。名称是指向的那本目录的名称，用来提示人，不参与判断。
     */
    record Hop(String fieldCode, String label, String targetRegistryCode, boolean required) {
    }

    /** 从设备或已有告警上取出的一条归属。 */
    record Ref(String fieldCode, String label, String targetRegistryCode, long id) {
    }

    private AlarmOwnershipCopy() {
    }

    /**
     * 产生告警时从设备上抄。必填的一栏设备没填，不记。选填没填就跳过。
     */
    static List<Ref> fromDevice(Map<String, Object> deviceBase, Map<String, Object> deviceCustom, List<Hop> hops) {
        List<Ref> copied = new ArrayList<>();
        for (Hop hop : hops) {
            Long id = idOf(deviceBase, deviceCustom, hop.fieldCode());
            if (id == null) {
                if (hop.required()) {
                    throw new ServiceException(400, "这台设备没有填写「" + hop.label() + "」，不记告警");
                }
                continue;
            }
            copied.add(new Ref(hop.fieldCode(), hop.label(), hop.targetRegistryCode(), id));
        }
        return List.copyOf(copied);
    }

    /**
     * 改已有告警、发消除事件时，用这条告警上已经记下的。不回设备重算。
     * 必填的一栏告警上没有，按调用方给的说法拒绝。
     */
    static List<Ref> fromStored(Map<String, Object> alarmBase, List<Hop> hops, String missingMessage) {
        List<Ref> copied = new ArrayList<>();
        for (Hop hop : hops) {
            Long id = idOf(alarmBase, null, hop.fieldCode());
            if (id == null) {
                if (hop.required()) {
                    throw new ServiceException(400, missingMessage + "「" + hop.label() + "」");
                }
                continue;
            }
            copied.add(new Ref(hop.fieldCode(), hop.label(), hop.targetRegistryCode(), id));
        }
        return List.copyOf(copied);
    }

    /** 把已经取出的归属写成提交用的引用。目标目录用声明里的，不从编号猜。 */
    static void writeRefs(Map<String, Object> baseFields, List<Ref> refs) {
        for (Ref ref : refs) {
            Map<String, Object> value = new LinkedHashMap<>(2);
            value.put("entityTypeCode", ref.targetRegistryCode());
            value.put("id", ref.id());
            baseFields.put(ref.fieldCode(), value);
        }
    }

    static Map<String, Long> ids(List<Ref> refs) {
        Map<String, Long> ids = new LinkedHashMap<>();
        for (Ref ref : refs) {
            ids.put(ref.fieldCode(), ref.id());
        }
        return Map.copyOf(ids);
    }

    private static Long idOf(Map<String, Object> base, Map<String, Object> custom, String fieldCode) {
        Object raw = base == null ? null : base.get(fieldCode);
        if (raw == null && custom != null) {
            raw = custom.get(fieldCode);
        }
        return FacilityOwningFieldCodes.extractId(raw);
    }
}
