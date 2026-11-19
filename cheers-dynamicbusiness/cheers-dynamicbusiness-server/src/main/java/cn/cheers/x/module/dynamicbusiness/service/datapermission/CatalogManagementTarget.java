package cn.cheers.x.module.dynamicbusiness.service.datapermission;

/**
 * 一本目录在数据管理方式里勾中的一跳：指向哪本目录，用的哪个归属字段。
 * 数据页用字段编码决定左边列场站还是列管线段。
 */
public record CatalogManagementTarget(String targetRegistryCode, String fieldCode) {
}
