package cn.cheers.x.module.dynamicbusiness.devicehistory;

/**
 * 一次要交给现场网关的写点。
 * 节点标识来自浏览。on 为真写成开，为假写成关。
 */
public record PlannedWrite(String nodeId, boolean on) {
}
