package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页上的一行：字段说明里的中文名，加上报文里对上的值。
 * <p>名字只来自字段说明。路径用来核对是哪一行，不是另起的字段名。
 *
 * @param label 字段说明上的中文名
 * @param path  字段说明上的路径
 * @param value    报文里读到的值；多项用顿号隔开；这一层是对象或列表时为空
 * @param issue    这一行对不上说明书的原因；对上了则为空
 * @param type     字段说明上的类型，如 string、integer、object
 * @param required 字段说明是否标为必填
 */
public record ProtocolMonitorFieldDTO(
        String label,
        String path,
        String value,
        String issue,
        String type,
        boolean required
) {
}
