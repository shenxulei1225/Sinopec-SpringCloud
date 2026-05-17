package cn.iocoder.yudao.module.scene.platform.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 运行时数据包 - NATS 消息结构
 * 用于定义 Actor 运行时数据的传输协议
 *
 * @author Sinopec
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimePacket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型: position/data/visibility/event
     */
    private String type;

    /**
     * 场景编码
     */
    private String sceneCode;

    /**
     * Actor 实例 ID
     */
    private Long instanceId;

    /**
     * Actor 实例编码
     */
    private String instanceCode;

    /**
     * Actor 类型: gauge/inspector/valve/pipe
     */
    private String actorType;

    /**
     * 数据指标: x/y/z/temperature/pressure/...
     */
    private String metric;

    /**
     * 数据值
     */
    private Double value;

    /**
     * 变换信息（仅 type=position 时有值）
     */
    private Transform transform;

    /**
     * 额外数据（用于扩展）
     */
    private Map<String, Object> extra;

    /**
     * 数据版本号（用于帧版本过滤）
     */
    private Long version;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 是否触发推送（变化检测引擎设置）
     */
    private Boolean triggered;
}