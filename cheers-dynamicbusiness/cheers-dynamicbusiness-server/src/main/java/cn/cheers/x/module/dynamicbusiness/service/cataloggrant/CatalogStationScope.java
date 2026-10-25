package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

/**
 * 一个数据目录上已保存的场站范围。
 * viewScope、writeScope 只取 ALL（全部场站）、CURRENT（仅当前站）。旧的不限制按全部场站读。
 */
public record CatalogStationScope(String registryCode, String viewScope, String writeScope) {
}
