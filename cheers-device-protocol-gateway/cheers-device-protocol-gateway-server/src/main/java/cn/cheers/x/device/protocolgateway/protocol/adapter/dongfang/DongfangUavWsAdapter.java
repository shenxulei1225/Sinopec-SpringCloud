package cn.cheers.x.device.protocolgateway.protocol.adapter.dongfang;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.device.protocolgateway.api.mission.MissionWaypoint;
import cn.cheers.x.device.protocolgateway.api.opcode.UavMissionOpcode;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.device.protocolgateway.protocol.adapter.ProtocolAdapter;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 东方无人机 WebSocket 协议适配器（protocolCode = dongfang-uav-ws）。
 * <p>按本次 waypoints 动态组包：首点起飞、中间移动、末点降落；点上可附加拍照。
 */
@Component
@RequiredArgsConstructor
public class DongfangUavWsAdapter implements ProtocolAdapter {

    private final EnvelopeJsonCodec envelopeJsonCodec;

    @Override
    public String protocolCode() {
        return ProtocolCodes.DONGFANG_UAV_WS;
    }

    @Override
    public CommandSendEnvelope translate(DeviceMissionPlan plan) {
        if (plan.waypoints() == null || plan.waypoints().isEmpty()) {
            throw new IllegalArgumentException("执行意图 waypoints 不能为空（须由本次任务动态给出）");
        }
        List<MissionWaypoint> waypoints = plan.waypoints();
        List<InstructionPackageItem> items = new ArrayList<>();
        int sequence = 0;

        MissionWaypoint first = waypoints.get(0);
        items.add(new InstructionPackageItem(
                UavMissionOpcode.TAKEOFF.code(),
                sequence++,
                positionRequest(first, false)
        ));

        for (int i = 0; i < waypoints.size(); i++) {
            MissionWaypoint waypoint = waypoints.get(i);
            boolean last = i == waypoints.size() - 1;
            if (i > 0 || waypoints.size() == 1) {
                items.add(new InstructionPackageItem(
                        UavMissionOpcode.MOVE.code(),
                        sequence++,
                        positionRequest(waypoint, true)
                ));
            }
            if (waypoint.action() == MissionPointActionType.PHOTO) {
                items.add(new InstructionPackageItem(
                        UavMissionOpcode.PHOTO.code(),
                        sequence++,
                        photoRequest(waypoint)
                ));
            }
            if (last) {
                items.add(new InstructionPackageItem(
                        UavMissionOpcode.LAND.code(),
                        sequence++,
                        Map.of("blank", "0000")
                ));
            }
        }

        return new CommandSendEnvelope(
                newMsgId(),
                plan.deviceId(),
                plan.templateId(),
                envelopeJsonCodec.encodePackagesJson(items)
        );
    }

    private static Map<String, Object> positionRequest(MissionWaypoint waypoint, boolean withPointId) {
        Map<String, Object> request = new LinkedHashMap<>();
        putIfPresent(request, "lng", waypoint.lng());
        putIfPresent(request, "lat", waypoint.lat());
        putIfPresent(request, "height", waypoint.height() != null ? waypoint.height() : "0");
        if (withPointId && waypoint.pointId() != null) {
            request.put("pointId", waypoint.pointId());
        }
        return request;
    }

    private static Map<String, Object> photoRequest(MissionWaypoint waypoint) {
        Map<String, Object> request = new LinkedHashMap<>();
        putIfPresent(request, "lat", waypoint.lat());
        putIfPresent(request, "lng", waypoint.lng());
        putIfPresent(request, "height", waypoint.height() != null ? waypoint.height() : "0");
        request.put("pointId", waypoint.pointId());
        request.put("angleLevel", "10");
        request.put("angleVertical", "10");
        request.put("number", "1");
        request.put("focuses", "2");
        request.put("time", "20");
        request.put("parking", "true");
        return request;
    }

    private static void putIfPresent(Map<String, Object> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }

    private static String newMsgId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
