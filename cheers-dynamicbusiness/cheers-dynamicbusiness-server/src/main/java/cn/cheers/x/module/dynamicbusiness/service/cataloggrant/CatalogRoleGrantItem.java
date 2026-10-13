package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import java.util.List;

/**
 * 一个数据目录上已勾选的操作。
 * operations 只含 query、create、update、delete。
 */
public record CatalogRoleGrantItem(String registryCode, List<String> operations) {
}
