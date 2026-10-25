package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.dto.ProtocolMonitorSimulatorDTO;
import cn.cheers.x.device.protocolgateway.config.ProtocolMonitorSimulatorProperties;
import cn.cheers.x.device.protocolgateway.transport.DeviceTransport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 监控页只允许启动没被真实设备占着的模拟器。
 */
class ProtocolMonitorSimulatorServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void startCommand_usesTheServiceName() {
        List<String> start = BashSimulatorProcessLauncher.command(Path.of("start-microservices.sh"), false, "robot");
        List<String> stop = BashSimulatorProcessLauncher.command(Path.of("start-microservices.sh"), true, "robot");

        assertEquals(List.of("bash", "start-microservices.sh", "robot"), start);
        assertEquals(List.of("bash", "start-microservices.sh", "stop", "robot"), stop);
    }

    @Test
    void windowsStart_usesGitBashAndTheSameScript() {
        List<String> start = BashSimulatorProcessLauncher.windowsScriptCommand(
                Path.of("C:/Program Files/Git/bin/bash.exe"),
                Path.of("E:/Sinopec/Sinopec-SpringCloud/start-microservices.sh"),
                false,
                "robot");

        assertEquals("C:\\Program Files\\Git\\bin\\bash.exe", start.get(0));
        assertEquals("E:\\Sinopec\\Sinopec-SpringCloud\\start-microservices.sh", start.get(1));
        assertEquals("robot", start.get(2));
    }

    @Test
    void windowsStop_killsTheListeningPort() {
        List<String> stop = BashSimulatorProcessLauncher.windowsStopCommand(8302);

        assertEquals("cmd.exe", stop.get(0));
        assertTrue(stop.get(2).contains("taskkill"));
        assertTrue(stop.get(2).contains(":8302"));
    }

    @Test
    void startRobot_whenDeviceAlreadyOnline_isRejected() throws Exception {
        DeviceTransport transport = mock(DeviceTransport.class);
        when(transport.isOnline("c9fb0e4891064f759509ed72c92fb71c")).thenReturn(true);
        RecordingLauncher launcher = new RecordingLauncher(false);
        ProtocolMonitorSimulatorService service = service(transport, launcher);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> service.start("robot"));

        assertTrue(error.getMessage().contains("已经在线"));
        assertTrue(launcher.launched.isEmpty());
    }

    @Test
    void startUav_launchesSimulatorProcess() throws Exception {
        DeviceTransport transport = mock(DeviceTransport.class);
        when(transport.isOnline("de6e497903c847d0b2a6803aeaa1136c")).thenReturn(false);
        RecordingLauncher launcher = new RecordingLauncher(false);
        ProtocolMonitorSimulatorService service = service(transport, launcher);

        ProtocolMonitorSimulatorDTO row = service.start("uav");

        assertEquals("uav", launcher.launched.get(0));
        assertEquals(ProtocolMonitorSimulatorService.STARTING, row.phase());
        assertEquals("无人机模拟器", row.label());
    }

    @Test
    void stop_whenProcessIsDown_doesNotCallHalt() throws Exception {
        RecordingLauncher launcher = new RecordingLauncher(false);
        ProtocolMonitorSimulatorService service = service(mock(DeviceTransport.class), launcher);

        ProtocolMonitorSimulatorDTO row = service.stop("robot");

        assertEquals(ProtocolMonitorSimulatorService.STOPPED, row.phase());
        assertTrue(launcher.halted.isEmpty());
    }

    private ProtocolMonitorSimulatorService service(DeviceTransport transport, RecordingLauncher launcher)
            throws Exception {
        Path script = tempDir.resolve("start-microservices.sh");
        Files.writeString(script, "#!/bin/bash\nexit 0\n");
        ProtocolMonitorSimulatorProperties properties = new ProtocolMonitorSimulatorProperties();
        properties.setScript(script.toString());
        return new ProtocolMonitorSimulatorService(transport, properties, launcher);
    }

    private static final class RecordingLauncher implements SimulatorProcessLauncher {
        private final boolean listening;
        private final List<String> launched = new ArrayList<>();
        private final List<String> halted = new ArrayList<>();

        private RecordingLauncher(boolean listening) {
            this.listening = listening;
        }

        @Override
        public boolean listening(int port) {
            return listening;
        }

        @Override
        public void launch(Path script, String serviceName) {
            launched.add(serviceName);
        }

        @Override
        public void halt(Path script, String serviceName, int port) {
            halted.add(serviceName);
        }

        @Override
        public String launchError(String serviceName) {
            return "";
        }
    }
}
