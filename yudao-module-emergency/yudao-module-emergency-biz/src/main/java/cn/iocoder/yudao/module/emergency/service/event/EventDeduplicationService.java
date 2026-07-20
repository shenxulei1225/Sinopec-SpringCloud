package cn.iocoder.yudao.module.emergency.service.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;

import java.util.List;

/**
 * 事件去重检测 Service 接口
 * 
 * 用于检测是否存在重复事件（30分钟内、500米范围内、相同事件类型）
 */
public interface EventDeduplicationService {

    /**
     * 检测是否存在重复事件
     *
     * @param event 待检测的事件
     * @return 重复事件列表（如果存在）
     */
    List<EmergencyEventDO> checkDuplicates(EmergencyEventDO event);

    /**
     * 检测是否存在重复事件（使用自定义参数）
     *
     * @param eventType 事件类型（分类ID）
     * @param locationGis GIS坐标（WKT格式，如"POINT(116.397128 39.916527)"）
     * @param discoveredAt 发现时间
     * @param timeWindowMinutes 时间窗口（分钟，默认30分钟）
     * @param distanceMeters 距离阈值（米，默认500米）
     * @return 重复事件列表（如果存在）
     */
    List<EmergencyEventDO> checkDuplicates(Long eventType, String locationGis, 
                                           java.time.LocalDateTime discoveredAt,
                                           Integer timeWindowMinutes, 
                                           Integer distanceMeters);

    /**
     * 计算两个GIS坐标之间的距离（米）
     *
     * @param gis1 第一个GIS坐标（WKT格式）
     * @param gis2 第二个GIS坐标（WKT格式）
     * @return 距离（米）
     */
    double calculateDistance(String gis1, String gis2);
}






