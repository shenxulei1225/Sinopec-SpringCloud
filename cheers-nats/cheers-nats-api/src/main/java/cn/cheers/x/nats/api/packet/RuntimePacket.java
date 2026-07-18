package cn.cheers.x.nats.api.packet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 运行时数据包 - 统一通信协议
 *
 * WebSocket / UE / Three.js / Replay / Diff 都依赖此格式
 *
 * 职责：通信协议，不包含业务状态字段
 * 业务状态字段放到 Runtime 模型中
 *
 * @author Kevin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuntimePacket<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 来源 Subject（原始 NATS Subject）
     * 用于调试和日志
     */
    private String subject;

    /**
     * 时间戳（毫秒）
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 消息类型: gauge/inspector/valve/pipe/...
     * UE 端根据此类型分发
     */
    private String type;

    /**
     * 载荷数据
     * 结构由 type 决定
     */
    private T payload;
}
