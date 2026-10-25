package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

/**
 * 当前登录人在一个数据目录上的有效场站范围和四项操作。
 * 多项角色同时存在时，范围取更宽的那一档：全部场站宽于仅当前站。
 */
public record CatalogStationScopeDecision(
        String viewScope,
        String writeScope,
        boolean canQuery,
        boolean canCreate,
        boolean canUpdate,
        boolean canDelete) {
}
