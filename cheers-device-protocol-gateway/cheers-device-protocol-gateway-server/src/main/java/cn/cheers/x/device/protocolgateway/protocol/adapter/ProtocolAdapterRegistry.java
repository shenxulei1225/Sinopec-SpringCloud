package cn.cheers.x.device.protocolgateway.protocol.adapter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 按对接协议编码查找适配器。
 */
@Component
public class ProtocolAdapterRegistry {

    private final Map<String, ProtocolAdapter> byCode;

    public ProtocolAdapterRegistry(List<ProtocolAdapter> adapters) {
        this.byCode = adapters.stream()
                .collect(Collectors.toMap(ProtocolAdapter::protocolCode, Function.identity(), (a, b) -> {
                    throw new IllegalStateException("重复的对接协议编码: " + a.protocolCode());
                }));
    }

    public ProtocolAdapter require(String protocolCode) {
        ProtocolAdapter adapter = byCode.get(protocolCode);
        if (adapter == null) {
            throw new IllegalArgumentException("未注册的对接协议编码: " + protocolCode);
        }
        return adapter;
    }
}
