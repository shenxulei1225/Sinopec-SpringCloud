package cn.cheers.x.inspection.task.service.execution.steptree;

/**
 * 路网同步进点位台账时写下的业务编码：PN-{设施编号}-{路网节点编号}。
 * 系统内步骤关联点位用点位编号（实体 id）。这个编码只用来按编码读到那一条点位，读到后把名称放进返回的步骤。
 * 不把路网节点编号当成点位编号，也不把它写成点位编码。
 * 与点位同步 {@code PathStationPointSyncService.buildPointCode} 同一写法。
 */
public final class RoutePointCode {

    private RoutePointCode() {
    }

    public static String of(long facilityId, String networkNodeId) {
        String safe = networkNodeId == null ? "" : networkNodeId.trim().replaceAll("[^A-Za-z0-9_-]", "_");
        if (safe.length() > 64) {
            safe = safe.substring(0, 64);
        }
        String code = "PN-" + facilityId + "-" + safe;
        if (code.length() > 100) {
            code = code.substring(0, 100);
        }
        return code;
    }
}
