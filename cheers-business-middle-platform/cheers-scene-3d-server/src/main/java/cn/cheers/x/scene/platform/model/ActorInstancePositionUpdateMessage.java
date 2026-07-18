package cn.cheers.x.scene.platform.model;

import lombok.Data;

/**
 * Actor 实例位置更新 WebSocket 消息
 */
@Data
public class ActorInstancePositionUpdateMessage {

    /** 消息类型常量 */
    public static final String TYPE = "INSTANCE_POSITION_UPDATE";

    /** 消息类型 */
    private String type;

    /** 推送时间戳 (毫秒) */
    private Long timestamp;

    /** 消息数据 */
    private PayloadData data;

    @Data
    public static class PayloadData {

        /** Actor 实例 ID */
        private Long instanceId;

        /** Actor 实例编码 */
        private String instanceCode;

        /** 完整 Transform（含位置/旋转/缩放） */
        private Transform transform;

        /** 实例可见性 */
        private Boolean visibleFlag;

        /** 实例所在图层 Key */
        private String layerKey;
    }

    /**
     * 静态工厂方法：创建位置更新消息
     */
    public static ActorInstancePositionUpdateMessage createPositionUpdate(
            Long instanceId, String instanceCode, Transform transform) {
        ActorInstancePositionUpdateMessage msg = new ActorInstancePositionUpdateMessage();
        msg.setType(TYPE);
        msg.setTimestamp(System.currentTimeMillis());
        PayloadData payload = new PayloadData();
        payload.setInstanceId(instanceId);
        payload.setInstanceCode(instanceCode);
        payload.setTransform(transform);
        msg.setData(payload);
        return msg;
    }

    /**
     * 静态工厂方法：创建可见性变更消息
     */
    public static ActorInstancePositionUpdateMessage createVisibilityUpdate(
            Long instanceId, String instanceCode, Boolean visibleFlag) {
        ActorInstancePositionUpdateMessage msg = new ActorInstancePositionUpdateMessage();
        msg.setType(TYPE);
        msg.setTimestamp(System.currentTimeMillis());
        PayloadData payload = new PayloadData();
        payload.setInstanceId(instanceId);
        payload.setInstanceCode(instanceCode);
        payload.setVisibleFlag(visibleFlag);
        msg.setData(payload);
        return msg;
    }
}
