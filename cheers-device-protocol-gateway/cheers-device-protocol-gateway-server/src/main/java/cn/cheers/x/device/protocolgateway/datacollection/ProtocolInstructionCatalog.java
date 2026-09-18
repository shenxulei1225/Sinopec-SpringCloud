package cn.cheers.x.device.protocolgateway.datacollection;

/**
 * 协议指令说明书目录：按操作码取出主包字段说明。
 * <p>负责：从数据协议管理读字段说明。
 * <p>不负责：核报文、猜协议版本、写说明书。
 * <p>禁止：说明书缺失时编造字段行。
 */
public interface ProtocolInstructionCatalog {

    /**
     * 取该操作码主包字段说明。
     * {@code protocolVersion} 有值时只认该版本；空则同一操作码多份不同说明书必须失败。
     */
    CatalogLookup findMainPacket(int opcode, String protocolVersion);
}
