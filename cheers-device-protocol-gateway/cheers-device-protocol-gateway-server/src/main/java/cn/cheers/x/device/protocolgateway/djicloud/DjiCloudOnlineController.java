package cn.cheers.x.device.protocolgateway.djicloud;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查看当前已经完成拓扑应答的机场或遥控器。
 * <p>只读内存会话。不写台账，也不表示任务已经开始。
 */
@RestController
@RequestMapping("/device-protocol/dji-cloud")
@RequiredArgsConstructor
public class DjiCloudOnlineController {

    private final DjiCloudOnlineRegistry onlineRegistry;

    /**
     * 上云开关没开、或还没有设备报拓扑时，返回空列表。
     */
    @GetMapping("/online")
    public List<DjiCloudOnlineDevice> online() {
        return onlineRegistry.listOnline();
    }
}
