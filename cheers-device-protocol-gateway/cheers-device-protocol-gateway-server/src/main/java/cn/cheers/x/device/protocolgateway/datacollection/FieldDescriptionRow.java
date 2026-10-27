package cn.cheers.x.device.protocolgateway.datacollection;

/**
 * 字段说明里的一行：设备报文路径、类型、是否必填，以及转成标准事件时用的转发名。
 * <p>负责：给协议合格核对提供一条说明书行。转发名有值时，网关把这一格收成标准事件上的同名字段。
 * <p>不负责：写说明书、猜缺行、把设备路径交给巡检。
 */
public record FieldDescriptionRow(
        String path,
        String type,
        String label,
        boolean required,
        String forwardName
) {

    /** 只核对、不转发的一行。 */
    public FieldDescriptionRow(String path, String type, String label, boolean required) {
        this(path, type, label, required, "");
    }

    public FieldDescriptionRow {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("字段说明缺少路径");
        }
        path = path.trim();
        type = type == null || type.isBlank() ? "string" : type.trim().toLowerCase();
        label = label == null || label.isBlank() ? path : label.trim();
        forwardName = forwardName == null ? "" : forwardName.trim();
    }
}
