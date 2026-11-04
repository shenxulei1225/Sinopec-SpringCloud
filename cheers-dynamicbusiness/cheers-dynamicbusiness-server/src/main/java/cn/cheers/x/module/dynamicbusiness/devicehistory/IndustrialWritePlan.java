package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 把设备动作对上这台设备的写点。
 * 型号不对、写点对不上、或没有节点标识，整次拒绝。不猜标签。
 */
public final class IndustrialWritePlan {

    private IndustrialWritePlan() {
    }

    public record BoundWrite(String fieldCode, String bindingCode) {
    }

    public record Plan(List<PlannedWrite> writes, String refusal) {
        static Plan refuse(String reason) {
            return new Plan(List.of(), reason);
        }
    }

    /**
     * 对得上就给出要写的节点。对不上 refusal 有原因，writes 为空。
     */
    public static Plan plan(IndustrialWriteRecipe recipe, String equipmentModelCode,
                            List<BoundWrite> rows, Map<String, String> nodeByBinding) {
        if (recipe == null) {
            return Plan.refuse("这条动作还没对上写点");
        }
        if (!recipe.appliesTo(equipmentModelCode)) {
            return Plan.refuse("这台设备不是这个动作管的型号");
        }
        if (recipe.writes().isEmpty()) {
            return Plan.refuse("这条动作还没对上写点");
        }
        List<PlannedWrite> planned = new ArrayList<>();
        for (IndustrialWriteRecipe.Write write : recipe.writes()) {
            String binding = onlyBinding(rows, write.fieldCode());
            if (binding == null) {
                return Plan.refuse("点位对照里对不上这个写点");
            }
            String nodeId = nodeByBinding.get(binding);
            if (nodeId == null || nodeId.isBlank()) {
                return Plan.refuse("这个写点还没有节点标识");
            }
            planned.add(new PlannedWrite(nodeId, write.value() == 1));
        }
        return new Plan(List.copyOf(planned), null);
    }

    private static String onlyBinding(List<BoundWrite> rows, String fieldCode) {
        String found = null;
        for (BoundWrite row : rows) {
            if (!fieldCode.equals(row.fieldCode())) {
                continue;
            }
            if (found != null) {
                return null;
            }
            found = row.bindingCode();
        }
        return found;
    }
}
