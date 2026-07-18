package cn.cheers.x.nats.api.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 运行时 Actor 状态数据模型
 *
 * 这是"状态流运行时"中 Actor 状态的统一表示。
 * 所有 Actor（仪表、巡检设备、阀门等）都映射到这个模型。
 *
 * 与 SceneServiceImpl 中的 ActorStateDTO 不同：
 * - 这个模型不包含 sceneId（场景信息是订阅过滤层的事，不在状态数据中）
 * - 这个模型不包含 Actor 类型枚举（ActorType）（类型信息在运行时 Subject 中）
 * - 这个模型不包含 SceneLayer 相关字段（渲染层是独立系统）
 *
 * 这个模型只关心：这个 Actor 当前有什么状态。
 *
 * @author Kevin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActorState implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Actor ID（全局唯一）
     * 对应 SceneActor.id
     */
    private String actorId;

    /**
     * 运行时 Actor 类型
     * 与业务层的 ActorType 不同，这里用字符串标识
     * 例如："Device"（仪表）、"InspectRobot"（巡检机器人）
     */
    private String runtimeType;

    /**
     * 运行时状态值
     * 由 payload 和 runtimeType 决定结构
     * 例如：Device → { temperature: 82, pressure: 1.2 }
     *       InspectRobot → { position: {x: 10, y: 20, z: 0}, rotation: {yaw: 45} }
     */
    private Map<String, Object> runtimeData;

    /**
     * 状态序列号（单调递增）
     * 用于丢弃旧包，UE 端只接受 seq > lastSeq 的包
     */
    private Long sequence;

    /**
     * 更新时间戳（毫秒）
     */
    private Long timestamp;
}
