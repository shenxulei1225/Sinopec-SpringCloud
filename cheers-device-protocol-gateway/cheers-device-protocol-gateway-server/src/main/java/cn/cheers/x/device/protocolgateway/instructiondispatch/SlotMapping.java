package cn.cheers.x.device.protocolgateway.instructiondispatch;

/**
 * 对照一行：动作参数槽 → 空包路径。
 *
 * @param slot       动作参数槽
 * @param sourcePath 取哪一项；空表示参数本身
 * @param path       填到指令空包的哪条路径
 */
public record SlotMapping(String slot, String sourcePath, String path) {

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
    }
}
