package cn.cheers.x.device.protocolgateway.instructiondispatch;

/**
 * 对照一行：已分组的来源字段 → 指令空包路径。
 *
 * @param slot       来源字段
 * @param sourcePath 取哪一项；空表示参数本身
 * @param path       填到指令空包的哪条路径
 * @param sourceKind 平台预先定义的来源分组
 */
public record SlotMapping(String slot, String sourcePath, String path, String sourceKind) {

    /** 兼容旧对照与旧测试：未写来源分组的动作参数按动作参数（ACTION_PARAM）处理。 */
    public SlotMapping(String slot, String sourcePath, String path) {
        this(slot, sourcePath, path, "ACTION_PARAM");
    }

    public SlotMapping {
        if (slot == null || slot.isBlank()) {
            throw new IllegalArgumentException("对照缺少参数槽");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("对照缺少空包路径");
        }
        slot = slot.trim();
        path = path.trim();
        sourcePath = sourcePath == null || sourcePath.isBlank() ? "" : sourcePath.trim();
        sourceKind = normalizeSourceKind(sourceKind);
    }

    private static String normalizeSourceKind(String raw) {
        String value = raw == null ? "" : raw.trim().toUpperCase();
        return switch (value) {
            case "INTERACTION", "EXECUTOR_MOUNT", "SYSTEM", "REFERENCE_RESOURCE", "ACTION_PARAM" -> value;
            case "USER" -> "ACTION_PARAM";
            default -> "ACTION_PARAM";
        };
    }
}
