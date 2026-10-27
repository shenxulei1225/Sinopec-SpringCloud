package cn.cheers.x.device.protocolgateway.datacollection;

/**
 * 协议指令说明书目录：按操作码取出主包字段说明。
 * <p>负责：从数据协议管理读字段说明。
 * <p>不负责：核报文、猜协议版本、写说明书。
 * <p>禁止：说明书缺失时编造字段行。
 */
public interface ProtocolInstructionCatalog {

    /** 下发或上报正文这一卷。目录键，不进设备报文。 */
    String VOLUME_OUTBOUND = "outbound";
    /** 正常响应这一卷。报文带返回码 200 时用。 */
    String VOLUME_INBOUND_SUCCESS = "inboundSuccess";
    /** 异常响应这一卷。报文带了返回码且不是 200 时用。 */
    String VOLUME_INBOUND_FAILURE = "inboundFailure";

    /**
     * 取该操作码主包字段说明。主包只认下发卷。
     * {@code protocolVersion} 有值时只认该版本；空则同一操作码多份不同说明书必须失败。
     */
    CatalogLookup findMainPacket(int opcode, String protocolVersion);

    /**
     * 取字段说明里的一卷，给监控页把值对上中文名。
     * 卷名只用上面三个常量。版本规则与主包相同，不猜一份。
     * 只实现了主包的调用方走默认失败，不把主包行冒充响应卷。
     */
    default CatalogLookup findVolume(int opcode, String protocolVersion, String volume) {
        return CatalogLookup.failed("未提供这一卷字段说明");
    }

    /**
     * 按上云方法名取转发。操作码是 0 的指令不能靠操作码区分。
     * 没实现的目录返回失败，不拿操作码 0 的某一条冒充。
     */
    default CatalogLookup findByProtocolMethod(String protocolMethod) {
        return CatalogLookup.failed("未按方法名查找说明书");
    }
}
