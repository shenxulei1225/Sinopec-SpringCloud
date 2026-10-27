package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendPointVO;
import cn.cheers.x.inspection.task.service.execution.openrun.DongguBatteryRemain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 按这次任务总耗时和设备最长飞行时间，算出预计剩余电量线。
 * <p>起点是当前电量。没有报上电量时不画。终点是当前减去这段耗时按最长飞行时间匀速用掉的电量，不是 0。
 * <p>不负责实际线。没有最长飞行时间或没有总耗时时不画。
 */
public final class BatteryTrendEstimate {

    private BatteryTrendEstimate() {
    }

    /**
     * 从起点电量走完这次总耗时。每分钟一个点。没有曲线时按最长飞行时间匀速下降。
     */
    public static List<BatteryTrendPointVO> points(
            String startElectricity, Integer requiredSeconds, Integer fullBatterySeconds, Object dischargeCurve) {
        if (requiredSeconds == null || requiredSeconds <= 0) {
            return List.of();
        }
        if (startElectricity == null || startElectricity.isBlank()) {
            return List.of();
        }
        String start = startElectricity.trim();
        Integer startRemain = DongguBatteryRemain.seconds(start, fullBatterySeconds, dischargeCurve);
        if (startRemain == null) {
            return List.of();
        }
        int lastMinute = (requiredSeconds + 59) / 60;
        List<BatteryTrendPointVO> points = new ArrayList<>();
        for (int minute = 0; minute <= lastMinute; minute++) {
            int remain = Math.max(0, startRemain - minute * 60);
            BigDecimal percent = DongguBatteryRemain.percentFromRemainSeconds(
                    remain, fullBatterySeconds, dischargeCurve);
            if (percent == null) {
                return List.of();
            }
            points.add(new BatteryTrendPointVO(minute, percent.doubleValue()));
        }
        return List.copyOf(points);
    }
}
