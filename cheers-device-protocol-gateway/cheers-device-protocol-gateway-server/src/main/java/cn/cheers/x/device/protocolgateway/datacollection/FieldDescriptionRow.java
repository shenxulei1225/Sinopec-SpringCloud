package cn.cheers.x.device.protocolgateway.datacollection;

/**
 * 字段说明里的一行：空包路径、类型、是否必填。
 * <p>负责：给协议合格核对提供一条说明书行。
 * <p>不负责：写说明书、猜缺行。
 */
public record FieldDescriptionRow(
        String path,
        String type,
        String label,
        boolean required
) {

    public FieldDescriptionRow {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("字段说明缺少路径");
        }
        path = path.trim();
        type = type == null || type.isBlank() ? "string" : type.trim().toLowerCase();
        label = label == null || label.isBlank() ? path : label.trim();
    }
}
