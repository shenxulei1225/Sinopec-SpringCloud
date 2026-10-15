package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import java.util.List;

/**
 * 一个数据目录上、某一类对象已勾选的操作。
 * subject 是 category、model、entity、detail。
 * operations 只含 query、create、update、delete。角色页把后三项显示成一项「维护」。
 */
public record CatalogRoleGrantItem(String registryCode, String subject, List<String> operations) {
}
