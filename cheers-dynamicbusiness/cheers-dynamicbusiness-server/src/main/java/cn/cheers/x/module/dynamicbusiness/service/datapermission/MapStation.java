package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import java.util.List;

/**
 * 地图上的一座场站。
 * 经纬度来自场站专用列。管线段来自所属管线段关系，一段都没挂时 segmentIds 为空。
 * segmentSeq 是这段上的站序。同一段有两个及以上有坐标的站时，地图按这个顺序连成管线。
 */
public record MapStation(
        Long id,
        String name,
        Double longitude,
        Double latitude,
        List<Long> segmentIds,
        Integer segmentSeq) {
}
