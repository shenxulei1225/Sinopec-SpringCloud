package cn.cheers.x.device.protocolgateway.protocol.monitor;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 用现有启动脚本拉起无人机或机器人模拟器。
 * <p>负责：看端口、按脚本正名拉起进程、同步 stop。
 * 启动脚本的第一个词是服务名。无人机写 uav，机器人写 robot。停止才在前面加 stop。
 * <p>不负责：判断真实设备是否已经占着连接。
 * <p>禁止：serviceName 不是 uav 或 robot 时仍去执行脚本。
 */
@Component
public class BashSimulatorProcessLauncher implements SimulatorProcessLauncher {

    private static final int CONNECT_TIMEOUT_MS = 300;
    private static final int STOP_WAIT_SECONDS = 20;
    private final ConcurrentMap<String, Process> launches = new ConcurrentHashMap<>();

    @Override
    public boolean listening(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), CONNECT_TIMEOUT_MS);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void launch(Path script, String serviceName) {
        launches.put(serviceName, run(script, serviceName, false, command(script, false, serviceName)));
    }

    @Override
    public void halt(Path script, String serviceName) {
        launches.remove(serviceName);
        run(script, serviceName, true, command(script, true, serviceName));
    }

    @Override
    public String launchError(String serviceName) {
        Process process = launches.get(serviceName);
        if (process == null || process.isAlive() || process.exitValue() == 0) {
            return "";
        }
        return "模拟器没有启动起来";
    }

    /**
     * 启动命令是脚本名加服务正名。停止才在前面加 stop。
     */
    static List<String> command(Path script, boolean stop, String serviceName) {
        if (!"uav".equals(serviceName) && !"robot".equals(serviceName)) {
            throw new IllegalArgumentException("没有这个模拟器");
        }
        List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(script.toString());
        if (stop) {
            command.add("stop");
        }
        command.add(serviceName);
        return command;
    }

    private Process run(Path script, String serviceName, boolean wait, List<String> command) {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(script.getParent().toFile());
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.appendTo(controlLog(script, serviceName).toFile()));
        try {
            Process process = builder.start();
            if (!wait) {
                return process;
            }
            if (!process.waitFor(STOP_WAIT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalArgumentException("停止模拟器超时");
            }
            if (process.exitValue() != 0) {
                throw new IllegalArgumentException("停止模拟器失败");
            }
            return process;
        } catch (IOException ex) {
            throw new IllegalArgumentException("模拟器脚本没有跑起来", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("停止模拟器被打断", ex);
        }
    }

    private static Path controlLog(Path script, String serviceName) {
        Path log = script.getParent().resolve("logs").resolve(serviceName + "-control.log");
        try {
            Files.createDirectories(log.getParent());
        } catch (IOException ex) {
            throw new IllegalArgumentException("写不了模拟器日志", ex);
        }
        return log;
    }
}
