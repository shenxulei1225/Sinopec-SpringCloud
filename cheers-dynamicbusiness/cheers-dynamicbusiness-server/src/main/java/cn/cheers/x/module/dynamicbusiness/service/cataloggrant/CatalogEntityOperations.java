package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

/** 登录人在一本目录的实体上能做的四项。只决定按钮，不含数据范围。 */
public record CatalogEntityOperations(boolean canQuery, boolean canCreate, boolean canUpdate, boolean canDelete) {
}
