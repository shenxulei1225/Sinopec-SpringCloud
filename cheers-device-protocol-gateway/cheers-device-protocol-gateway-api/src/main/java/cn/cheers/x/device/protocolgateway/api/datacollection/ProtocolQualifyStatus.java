package cn.cheers.x.device.protocolgateway.api.datacollection;

/**
 * 协议合格状态。依据字段说明核对；未核过不得写成合格。
 */
public enum ProtocolQualifyStatus {

    /** 刚解析完、尚未按字段说明核对。进总线前必须已经被核对替换。 */
    UNCHECKED,

    /** 对照说明书合格 */
    QUALIFIED,

    /** 缺必填、类型不对或对不上空包 */
    UNQUALIFIED
}
