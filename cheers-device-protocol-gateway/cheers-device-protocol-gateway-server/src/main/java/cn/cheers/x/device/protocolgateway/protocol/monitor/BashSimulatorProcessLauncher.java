package cn.cheers.x.device.protocolgateway.protocol.monitor;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 按当前操作系统拉起无人机或机器人模拟器。
 * <p>负责：看端口、启动、停止。Linux 和 Mac 用 {@code start-microservices.sh}。
 * Windows 用 Git Bash 跑同一份脚本。本机 PATH 里的 bash 是 WSL，会把反斜杠吃掉，脚本找不到。
 * <p>不负责：判断真实设备是否已经占着连接。
 * <p>禁止：在 Windows 上调用 PATH 里的 bash。禁止：serviceName 不是 uav 或 robot 时仍去启动。
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
        List<String> chosen = windowsHost()
                ? windowsScriptCommand(requireGitBash(), script, false, serviceName)
                : command(script, false, serviceName);
        launches.put(serviceName, run(script, serviceName, false, chosen));
    }

    @Override
    public void halt(Path script, String serviceName, int port) {
        if (windowsHost()) {
            haltOnWindows(script, serviceName, port);
            return;
        }
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
     * Linux 和 Mac 的启动命令。启动是脚本名加服务正名。停止才在前面加 stop。
     */
    static List<String> command(Path script, boolean stop, String serviceName) {
        requireService(serviceName);
        List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(script.toString());
        if (stop) {
            command.add("stop");
        }
        command.add(serviceName);
        return command;
    }

    /**
     * Windows 启动。用 Git Bash 执行脚本，参数与 Linux 相同。
     * 停止不走这段：Git Bash 里没有 lsof，脚本判断不了端口，停止改按端口结束进程。
     */
    static List<String> windowsScriptCommand(Path bash, Path script, boolean stop, String serviceName) {
        requireService(serviceName);
        if (bash == null) {
            throw new IllegalArgumentException("这台 Windows 上没有 Git Bash，模拟器脚本无法启动");
        }
        List<String> command = new ArrayList<>();
        command.add(bash.toString());
        command.add(script.toString());
        if (stop) {
            command.add("stop");
        }
        command.add(serviceName);
        return command;
    }

    /** Git Bash。找不到时返回 null。 */
    static Path gitBash() {
        List<Path> candidates = List.of(
                Path.of("C:/Program Files/Git/bin/bash.exe"),
                Path.of("C:/Program Files (x86)/Git/bin/bash.exe"));
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static Path requireGitBash() {
        Path bash = gitBash();
        if (bash == null) {
            throw new IllegalArgumentException("这台 Windows 上没有 Git Bash，模拟器脚本无法启动");
        }
        return bash;
    }

    /**
     * 结束仍占用该端口的进程。端口号由监控页传入，和这台模拟器正在听的口一致。
     */
    static List<String> windowsStopCommand(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("没有这个模拟器端口");
        }
        String inner = "for /f \"tokens=5\" %p in ('netstat -ano ^| findstr LISTENING ^| findstr /C:\":"
                + port + " \"') do taskkill /F /T /PID %p";
        return List.of("cmd.exe", "/c", inner);
    }

    static boolean windowsHost() {
        String name = System.getProperty("os.name", "");
        return name.toLowerCase(Locale.ROOT).contains("win");
    }

    private void haltOnWindows(Path script, String serviceName, int port) {
        requireService(serviceName);
        destroyTree(launches.remove(serviceName));
        Process killer = run(script, serviceName, false, windowsStopCommand(port));
        try {
            if (!killer.waitFor(STOP_WAIT_SECONDS, TimeUnit.SECONDS)) {
                killer.destroyForcibly();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("停止模拟器被打断", ex);
        }
        if (listening(port)) {
            throw new IllegalArgumentException("停止模拟器失败");
        }
    }

    private static void destroyTree(Process process) {
        if (process == null || !process.isAlive()) {
            return;
        }
        process.descendants().forEach(handle -> handle.destroyForcibly());
        process.destroyForcibly();
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

    private static void requireService(String serviceName) {
        if (!"uav".equals(serviceName) && !"robot".equals(serviceName)) {
            throw new IllegalArgumentException("没有这个模拟器");
        }
    }

}
