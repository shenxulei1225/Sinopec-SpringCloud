package cn.cheers.x.inspection.inspection_content.service.binding;

import java.util.List;

/**
 * 对象↔停靠点绑定写服务。
 */
public interface ObjectStationBindingService {

    void replaceBindings(Long facilityId, Long objectId, List<String> stationNodeIds, Integer workMinutesPerStop);
}
