package cn.cheers.x.module.dynamicbusiness.service.datapermission;

/**
 * 换算可见记录的起点：某本目录里的一条记录，是否连它的下级一起算。
 * 任职：管理单元目录里任职所在的单元，总是含下级。
 * 右上角选中的站：场站目录里这一座站，不含下级。
 */
record ScopeNode(String registryCode, Long entityId, boolean includeDescendants) {
}
