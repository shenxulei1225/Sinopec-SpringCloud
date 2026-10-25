package cn.cheers.x.module.dynamicbusiness.service.organization;

/**
 * 一座场站要挂到哪个运营区域节点。
 * regionCategoryId 为空：没有唯一的区域关联，或关联的节点不在这棵树上。
 */
public record OrganizationStationPlacement(
        long categoryId,
        String name,
        long entityId,
        Long regionCategoryId) {
}
