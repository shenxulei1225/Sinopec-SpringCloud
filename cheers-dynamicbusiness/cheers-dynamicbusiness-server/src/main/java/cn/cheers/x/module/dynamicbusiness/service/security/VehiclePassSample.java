package cn.cheers.x.module.dynamicbusiness.service.security;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassEventCodes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * 从网关已经收好的车辆进出样本里取出标准字段。
 * <p>负责：车牌、车道、方向、时间、照片。方向收成进场或出场。
 * <p>不负责：比对名单、写进出账。
 * <p>禁止：缺车牌或车道时编一个。已经进出缺时间时用当前时间补上。
 */
public final class VehiclePassSample {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private VehiclePassSample() {
    }

    public record Parsed(
            boolean seen,
            long laneId,
            String plate,
            String plateColor,
            String direction,
            LocalDateTime when,
            String scenePhoto,
            String platePhoto,
            boolean uncertain) {
    }

    /**
     * 识别到车要有车牌和车道。已经进出还要有方向和时间。
     * 缺了就报错，不写进出。
     */
    public static Parsed read(CollectionSample sample) {
        Map<String, Object> fields = sample.fields();
        boolean seen = VehiclePassEventCodes.SEEN.equals(sample.messageKind());
        String plate = text(fields.get("车牌"));
        if (plate.isEmpty()) {
            throw new ServiceException(400, "没有车牌");
        }
        Long laneId = longValue(fields.get("车道"));
        if (laneId == null) {
            throw new ServiceException(400, "车道对不上");
        }
        String direction = "";
        LocalDateTime when = null;
        if (!seen) {
            direction = direction(text(fields.get("方向")));
            if (direction.isEmpty()) {
                throw new ServiceException(400, "这条车道的方向对不上");
            }
            when = time(text(fields.get("时间")));
            if (when == null) {
                throw new ServiceException(400, "没有进出时间");
            }
        }
        return new Parsed(
                seen,
                laneId,
                plate,
                text(fields.get("车牌颜色")),
                direction,
                when,
                text(fields.get("全景照片")),
                text(fields.get("车牌照片")),
                truth(fields.get("识别不确定")));
    }

    private static String direction(String raw) {
        return switch (raw) {
            case "进场", "进入" -> VehiclePassDecision.IN;
            case "出场", "出去" -> VehiclePassDecision.OUT;
            default -> "";
        };
    }

    private static LocalDateTime time(String raw) {
        if (raw.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(raw, STAMP);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static boolean truth(Object value) {
        if (value instanceof Boolean flag) {
            return flag;
        }
        return "true".equalsIgnoreCase(text(value)) || "1".equals(text(value));
    }

    private static Long longValue(Object value) {
        if (value instanceof Number number) {
            long id = number.longValue();
            return id > 0 ? id : null;
        }
        String raw = text(value);
        if (raw.isEmpty()) {
            return null;
        }
        try {
            long id = Long.parseLong(raw);
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
