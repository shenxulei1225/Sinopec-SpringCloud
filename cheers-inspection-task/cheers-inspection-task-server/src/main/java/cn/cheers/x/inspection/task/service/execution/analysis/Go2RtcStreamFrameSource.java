package cn.cheers.x.inspection.task.service.execution.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 向流媒体网关要当前这一帧。网关已经在收这路流。
 * <p>只确认这一刻有图片。不把视频送给算法，也不写执行步骤。
 */
@Component
public class Go2RtcStreamFrameSource implements StreamFrameSource {

    private final String frameUrlTemplate;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public Go2RtcStreamFrameSource(
            @Value("${patrol.media.frame-url-template:http://127.0.0.1:1984/api/frame.jpeg?src={streamName}}")
            String frameUrlTemplate) {
        this.frameUrlTemplate = frameUrlTemplate;
    }

    @Override
    public String currentImageUrl(String streamName) {
        String url = StreamFrameAddress.jpegUrl(frameUrlTemplate, streamName);
        if (url == null) {
            return null;
        }
        try {
            HttpResponse<byte[]> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(2)).GET().build(),
                    HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200 || response.body() == null || response.body().length == 0) {
                return null;
            }
            String type = response.headers().firstValue("content-type").orElse("");
            return type.startsWith("image/") ? url : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
