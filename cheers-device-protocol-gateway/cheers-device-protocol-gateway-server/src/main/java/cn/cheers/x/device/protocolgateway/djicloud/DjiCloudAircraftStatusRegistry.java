package cn.cheers.x.device.protocolgateway.djicloud;

import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 飞机定时上报的最新一帧，按主题里的序列号留在本进程内存。
 * <p>不写库，不读巡检账。机场报文和缺飞行字段的报文不记。
 */
@Component
public class DjiCloudAircraftStatusRegistry {

    private static final Pattern OSD_TOPIC = Pattern.compile("^thing/product/([^/]+)/osd$");

    private final ConcurrentMap<String, AircraftFlightStatusDTO> latest = new ConcurrentHashMap<>();
    private final DjiCloudAircraftOsd parser = new DjiCloudAircraftOsd(new ObjectMapper());

    /**
     * 收包线程之外调用。不是飞机状态的报文直接丢掉，不覆盖上一帧。
     */
    public void accept(String topic, byte[] payload) {
        String aircraftSn = aircraftSn(topic);
        if (aircraftSn == null) {
            return;
        }
        AircraftFlightStatusDTO parsed = parser.parse(aircraftSn, payload);
        if (parsed != null) {
            latest.put(aircraftSn, parsed);
        }
    }

    public AircraftFlightStatusDTO get(String aircraftSn) {
        if (aircraftSn == null || aircraftSn.isBlank()) {
            return null;
        }
        return latest.get(aircraftSn);
    }

    static String aircraftSn(String topic) {
        if (topic == null) {
            return null;
        }
        Matcher matcher = OSD_TOPIC.matcher(topic);
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            return null;
        }
        return matcher.group(1);
    }
}
