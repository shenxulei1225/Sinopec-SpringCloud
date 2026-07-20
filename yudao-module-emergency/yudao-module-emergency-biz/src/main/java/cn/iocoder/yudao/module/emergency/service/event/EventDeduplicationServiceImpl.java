package cn.iocoder.yudao.module.emergency.service.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 事件去重检测 Service 实现类
 * 
 * 实现事件去重检测逻辑：
 * - 时间窗口：30分钟内
 * - 距离阈值：500米范围内
 * - 类型匹配：相同事件类型（分类ID）
 */
@Service
@Slf4j
public class EventDeduplicationServiceImpl implements EventDeduplicationService {

    @Resource
    private EmergencyEventMapper eventMapper;

    /**
     * 默认时间窗口（分钟）
     */
    @Value("${emergency.event.deduplication.time-window-minutes:30}")
    private Integer defaultTimeWindowMinutes;

    /**
     * 默认距离阈值（米）
     */
    @Value("${emergency.event.deduplication.distance-meters:500}")
    private Integer defaultDistanceMeters;

    /**
     * 地球半径（米）
     */
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    /**
     * WKT POINT格式正则表达式
     */
    private static final Pattern POINT_PATTERN = Pattern.compile("POINT\\(([\\d.]+)\\s+([\\d.]+)\\)");

    @Override
    public List<EmergencyEventDO> checkDuplicates(EmergencyEventDO event) {
        if (event.getEventType() == null || event.getLocationGis() == null || event.getDiscoveredAt() == null) {
            // 如果缺少必要字段，无法进行去重检测
            log.debug("事件缺少必要字段，跳过去重检测：eventType={}, locationGis={}, discoveredAt={}",
                    event.getEventType(), event.getLocationGis(), event.getDiscoveredAt());
            return new ArrayList<>();
        }

        return checkDuplicates(
                event.getEventType(),
                event.getLocationGis(),
                event.getDiscoveredAt(),
                defaultTimeWindowMinutes,
                defaultDistanceMeters
        );
    }

    @Override
    public List<EmergencyEventDO> checkDuplicates(Long eventType, String locationGis,
                                                  LocalDateTime discoveredAt,
                                                  Integer timeWindowMinutes,
                                                  Integer distanceMeters) {
        if (eventType == null || locationGis == null || discoveredAt == null) {
            return new ArrayList<>();
        }

        // 使用默认值
        int timeWindow = timeWindowMinutes != null ? timeWindowMinutes : defaultTimeWindowMinutes;
        int distance = distanceMeters != null ? distanceMeters : defaultDistanceMeters;

        // 计算时间范围
        LocalDateTime startTime = discoveredAt.minusMinutes(timeWindow);
        LocalDateTime endTime = discoveredAt.plusMinutes(timeWindow);

        // 查询相同类型、时间窗口内的事件
        List<EmergencyEventDO> candidateEvents = eventMapper.selectList(
                new LambdaQueryWrapper<EmergencyEventDO>()
                        .eq(EmergencyEventDO::getEventType, eventType)
                        .ge(EmergencyEventDO::getDiscoveredAt, startTime)
                        .le(EmergencyEventDO::getDiscoveredAt, endTime)
                        .isNotNull(EmergencyEventDO::getLocationGis)
        );

        // 过滤距离范围内的重复事件
        List<EmergencyEventDO> duplicates = new ArrayList<>();
        for (EmergencyEventDO candidate : candidateEvents) {
            double distanceInMeters = calculateDistance(locationGis, candidate.getLocationGis());
            if (distanceInMeters <= distance) {
                duplicates.add(candidate);
            }
        }

        return duplicates;
    }

    @Override
    public double calculateDistance(String gis1, String gis2) {
        if (gis1 == null || gis2 == null) {
            return Double.MAX_VALUE;
        }

        try {
            double[] coord1 = parsePoint(gis1);
            double[] coord2 = parsePoint(gis2);

            if (coord1 == null || coord2 == null) {
                return Double.MAX_VALUE;
            }

            // 使用Haversine公式计算两点间距离
            return haversineDistance(coord1[0], coord1[1], coord2[0], coord2[1]);
        } catch (Exception e) {
            log.warn("计算距离失败：gis1={}, gis2={}, error={}", gis1, gis2, e.getMessage());
            return Double.MAX_VALUE;
        }
    }

    /**
     * 解析WKT POINT格式的坐标
     *
     * @param gis WKT格式字符串，如"POINT(116.397128 39.916527)"
     * @return [经度, 纬度]
     */
    private double[] parsePoint(String gis) {
        if (gis == null || gis.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher(gis.trim());
        if (matcher.matches()) {
            try {
                double longitude = Double.parseDouble(matcher.group(1));
                double latitude = Double.parseDouble(matcher.group(2));
                return new double[]{longitude, latitude};
            } catch (NumberFormatException e) {
                log.warn("解析GIS坐标失败：gis={}, error={}", gis, e.getMessage());
                return null;
            }
        }

        log.warn("GIS坐标格式不正确：gis={}", gis);
        return null;
    }

    /**
     * 使用Haversine公式计算两点间距离
     *
     * @param lon1 第一个点的经度
     * @param lat1 第一个点的纬度
     * @param lon2 第二个点的经度
     * @param lat2 第二个点的纬度
     * @return 距离（米）
     */
    private double haversineDistance(double lon1, double lat1, double lon2, double lat2) {
        // 转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        // Haversine公式
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}






