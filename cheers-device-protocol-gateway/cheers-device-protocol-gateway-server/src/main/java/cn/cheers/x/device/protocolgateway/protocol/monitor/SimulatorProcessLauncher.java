package cn.cheers.x.device.protocolgateway.protocol.monitor;

import java.nio.file.Path;

/**
 * 拉起或停掉已经写好的模拟器进程。
 * <p>不负责：决定哪一台设备可以启动。那一层在监控服务里看设备是否已经在线。
 * <p>禁止：按调用方传入的任意命令启动程序。serviceName 只能是脚本里的 uav 或 robot。
 */
public interface SimulatorProcessLauncher {

    /** 这个端口已经在听，说明对应模拟器进程开着。 */
    boolean listening(int port);

    /**
     * 后台执行启动脚本。调用后马上返回，不等进程听端口。
     */
    void launch(Path script, String serviceName);

    /**
     * 停掉这一台并等到结束。
     * Linux 走停止脚本。Windows 结束本机拉起的进程，并清掉仍占着 {@code port} 的进程。
     * 失败时抛 IllegalArgumentException，页面看到这句话。
     */
    void halt(Path script, String serviceName, int port);

    /**
     * 最近一次启动脚本已经退出且失败时的说明。
     * 还在跑，或已经成功退出，返回空字符串。
     */
    String launchError(String serviceName);
}
