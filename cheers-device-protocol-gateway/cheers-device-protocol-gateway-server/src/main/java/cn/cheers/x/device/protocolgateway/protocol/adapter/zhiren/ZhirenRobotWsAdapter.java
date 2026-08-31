package cn.cheers.x.device.protocolgateway.protocol.adapter.zhiren;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.message.InstructionPackageItem;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.device.protocolgateway.api.mission.MissionWaypoint;
import cn.cheers.x.device.protocolgateway.api.opcode.RobotMissionOpcode;
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
 * 知仁机器人 WebSocket 协议适配器（protocolCode = zhiren-robot-ws）。
 * <p>按本次传入的 waypoints 动态组包；移动码 200102。
 */
@Component
@RequiredArgsConstructor
public class ZhirenRobotWsAdapter implements ProtocolAdapter {

    private final EnvelopeJsonCodec envelopeJsonCodec;

    @Override
    public String protocolCode() {
        return ProtocolCodes.ZHIREN_ROBOT_WS;
    }

    @Override
    public CommandSendEnvelope translate(DeviceMissionPlan plan) {
        validate(plan);
        List<InstructionPackageItem> items = new ArrayList<>();
        int sequence = 0;
        for (MissionWaypoint waypoint : plan.waypoints()) {
            items.add(new InstructionPackageItem(
                    RobotMissionOpcode.MOVE.code(),
                    sequence++,
                    moveRequest(waypoint)
            ));
            if (waypoint.action() == MissionPointActionType.PHOTO) {
                items.add(new InstructionPackageItem(
                        RobotMissionOpcode.PHOTO.code(),
                        sequence++,
                        photoRequest(waypoint)
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

    private static void validate(DeviceMissionPlan plan) {
        if (plan.waypoints() == null || plan.waypoints().isEmpty()) {
            throw new IllegalArgumentException("执行意图 waypoints 不能为空（须由本次任务动态给出）");
        }
        for (MissionWaypoint waypoint : plan.waypoints()) {
            if (waypoint.pointId() == null || waypoint.pointId().isBlank()) {
                throw new IllegalArgumentException("停靠点 pointId 必填");
            }
        }
    }

    private static Map<String, Object> moveRequest(MissionWaypoint waypoint) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", waypoint.pointId());
        putIfPresent(request, "lat", waypoint.lat());
        putIfPresent(request, "lng", waypoint.lng());
        if (waypoint.heading() != null) {
            request.put("heading", waypoint.heading());
        }
        return request;
    }

    private static Map<String, Object> photoRequest(MissionWaypoint waypoint) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("parking", "true");
        request.put("number", "1");
        request.put("pointId", waypoint.pointId());
        putIfPresent(request, "lat", waypoint.lat());
        putIfPresent(request, "lng", waypoint.lng());
        if (waypoint.heading() != null) {
            request.put("heading", waypoint.heading());
        }
        request.put("angleVertical", "10");
        request.put("angleLevel", "10");
        request.put("focuses", "2");
        request.put("time", "20");
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
