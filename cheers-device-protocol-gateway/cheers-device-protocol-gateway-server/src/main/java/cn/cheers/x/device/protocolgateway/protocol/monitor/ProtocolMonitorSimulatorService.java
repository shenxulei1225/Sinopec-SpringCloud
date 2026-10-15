package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorSimulatorDTO;
import cn.cheers.x.device.protocolgateway.config.ProtocolMonitorSimulatorProperties;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 协议监控页启动或停止无人机、机器人模拟器。
 * <p>负责：看进程端口和这台设备是否已连上设备口。真实设备已经在线时拒绝再启动，避免占同一条连接。
 * <p>不负责：模拟器应答报文。应答仍在单独进程里。
 * <p>禁止：把模拟器代码并进网关；按页面传入的命令启动别的程序。
 */
@Service
@RequiredArgsConstructor
public class ProtocolMonitorSimulatorService {

    static final String STOPPED = "stopped";
    static final String STARTING = "starting";
    static final String ONLINE = "online";
    private static final long START_WINDOW_MS = 180_000L;

    private final DeviceTransport deviceTransport;
    private final ProtocolMonitorSimulatorProperties properties;
    private final SimulatorProcessLauncher launcher;
    private final Map<String, Long> launching = new ConcurrentHashMap<>();

    public List<ProtocolMonitorSimulatorDTO> list() {
        return List.of(snapshot(uav()), snapshot(robot()));
    }

    /**
     * 进程已经在听就直接回报状态。设备口上已有连接且进程没开，说明是真实设备，这次不启动。
     */
    public ProtocolMonitorSimulatorDTO start(String role) {
        Target target = require(role);
        if (launcher.listening(target.port())) {
            return snapshot(target);
        }
        if (deviceTransport.isOnline(target.deviceId())) {
            throw new IllegalArgumentException("这台设备已经在线，模拟器会占同一条连接，这次没有启动");
        }
        launching.put(target.role(), System.currentTimeMillis());
        try {
            launcher.launch(scriptPath(), target.serviceName());
        } catch (RuntimeException ex) {
            launching.remove(target.role());
            throw ex;
        }
        return snapshot(target);
    }

    /** 停掉这一个进程。端口还没起来时不调用脚本，编译中的启动继续走。 */
    public ProtocolMonitorSimulatorDTO stop(String role) {
        Target target = require(role);
        if (!launcher.listening(target.port())) {
            return snapshot(target);
        }
        launching.remove(target.role());
        launcher.halt(scriptPath(), target.serviceName());
        return snapshot(target);
    }

    private ProtocolMonitorSimulatorDTO snapshot(Target target) {
        boolean listening = launcher.listening(target.port());
        boolean linked = listening && deviceTransport.isOnline(target.deviceId());
        Long launchedAt = launching.get(target.role());
        if (linked) {
            launching.remove(target.role());
            return row(target, ONLINE, "模拟器已连上这台设备", true);
        }
        if (listening) {
            return row(target, STARTING, "模拟器进程已开，正在连接设备口", true);
        }
        String launchError = launcher.launchError(target.serviceName());
        if (launchError != null && !launchError.isBlank()) {
            launching.remove(target.role());
            return row(target, STOPPED, launchError, false);
        }
        if (launchedAt != null && System.currentTimeMillis() - launchedAt < START_WINDOW_MS) {
            return row(target, STARTING, "正在启动。第一次要编译，可能要一两分钟", false);
        }
        if (launchedAt != null) {
            launching.remove(target.role());
            return row(target, STOPPED, "没有启动起来。看 logs 里对应模拟器日志", false);
        }
        return row(target, STOPPED, "未启动", false);
    }

    private static ProtocolMonitorSimulatorDTO row(Target target, String phase, String note, boolean processUp) {
        return new ProtocolMonitorSimulatorDTO(
                target.role(), target.label(), target.deviceId(), phase, note, processUp);
    }

    private Target require(String role) {
        if ("uav".equals(role)) {
            return uav();
        }
        if ("robot".equals(role)) {
            return robot();
        }
        throw new IllegalArgumentException("没有这个模拟器");
    }

    private Target uav() {
        return new Target("uav", "无人机模拟器", "uav", properties.getUavPort(), properties.getUavDeviceId());
    }

    private Target robot() {
        return new Target("robot", "机器人模拟器", "robot", properties.getRobotPort(), properties.getRobotDeviceId());
    }

    /**
     * 启动脚本以仓库里的 start-microservices.sh 为准。配置了路径就用配置，否则从工作目录往上找。
     */
    private Path scriptPath() {
        if (properties.getScript() != null && !properties.getScript().isBlank()) {
            Path configured = Path.of(properties.getScript());
            if (!Files.isRegularFile(configured)) {
                throw new IllegalArgumentException("模拟器启动脚本不存在");
            }
            return configured;
        }
        Path cursor = Path.of("").toAbsolutePath();
        for (int i = 0; i < 8 && cursor != null; i++) {
            Path direct = cursor.resolve("start-microservices.sh");
            if (Files.isRegularFile(direct)) {
                return direct;
            }
            Path nested = cursor.resolve("Sinopec-SpringCloud").resolve("start-microservices.sh");
            if (Files.isRegularFile(nested)) {
                return nested;
            }
            cursor = cursor.getParent();
        }
        throw new IllegalArgumentException("这台机器上没有模拟器启动脚本");
    }

    private record Target(String role, String label, String serviceName, int port, String deviceId) {
    }
}
