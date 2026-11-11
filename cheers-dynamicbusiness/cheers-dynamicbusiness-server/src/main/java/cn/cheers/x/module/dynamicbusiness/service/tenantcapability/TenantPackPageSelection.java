package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 一次开通里，某个业务勾了哪些页面。
 * pageCodes 为 null 时表示这一包的全部页面。
 * 空列表且这一包没有页面时，表示只开通数据目录。
 */
public record TenantPackPageSelection(String packCode, List<String> pageCodes) {
}
