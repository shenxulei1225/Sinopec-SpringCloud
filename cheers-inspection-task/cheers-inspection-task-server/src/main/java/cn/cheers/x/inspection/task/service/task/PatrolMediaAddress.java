package cn.cheers.x.inspection.task.service.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 巡检用的这一台流媒体服务器。
 * <p>负责：从配置读主机和收流协议，拼出推流地址、登记接口、回放和抽帧地址。
 * 收流协议只认 rtsp 或 rtmp。rtsp 用 8554，rtmp 用 1935。页面和登记都走 1984。
 * <p>不负责：向媒体服务器登记流名，不改已经写在任务账上的地址。
 */
@Component
public class PatrolMediaAddress {

    /** go2rtc 的 RTSP 收流口。 */
    static final int RTSP_INGEST_PORT = 8554;
    /** go2rtc 的 RTMP 收流口。 */
    static final int RTMP_INGEST_PORT = 1935;
    /** go2rtc 的 HTTP 接口，登记流名、回放和抽帧都走这里。 */
    static final int HTTP_API_PORT = 1984;

    private final String host;
    private final String ingestProtocol;

    public PatrolMediaAddress(
            @Value("${patrol.media.host:}") String host,
            @Value("${patrol.media.ingest-protocol:}") String ingestProtocol) {
        if (!StringUtils.hasText(host)) {
            throw new IllegalStateException("未配置流媒体服务器主机 patrol.media.host");
        }
        String trimmedHost = host.trim();
        if (trimmedHost.contains("://") || trimmedHost.contains("/") || trimmedHost.contains(":")) {
            throw new IllegalStateException(
                    "流媒体服务器主机只写主机名或 IP。当前是「" + trimmedHost + "」");
        }
        if (!StringUtils.hasText(ingestProtocol)) {
            throw new IllegalStateException("未配置收流协议 patrol.media.ingest-protocol");
        }
        String protocol = ingestProtocol.trim().toLowerCase();
        if (!"rtsp".equals(protocol) && !"rtmp".equals(protocol)) {
            throw new IllegalStateException(
                    "收流协议只认 rtsp 或 rtmp。当前是「" + ingestProtocol.trim() + "」");
        }
        this.host = trimmedHost;
        this.ingestProtocol = protocol;
    }

    /** 生成任务时拼在流名前面的收流前缀。 */
    public String ingestBaseUrl() {
        int port = "rtmp".equals(ingestProtocol) ? RTMP_INGEST_PORT : RTSP_INGEST_PORT;
        return ingestProtocol + "://" + host + ":" + port;
    }

    /** 登记流名用的 HTTP 接口。 */
    public String gatewayApiBaseUrl() {
        return "http://" + host + ":" + HTTP_API_PORT;
    }

    /** 任务结束后执行记录页播放的 MP4 地址模板。 */
    public String recordFileUrlTemplate() {
        return gatewayApiBaseUrl() + "/api/stream.mp4?src={streamName}";
    }

    /** 执行中向媒体服务器要当前画面的地址模板。 */
    public String frameUrlTemplate() {
        return gatewayApiBaseUrl() + "/api/frame.jpeg?src={streamName}";
    }
}
