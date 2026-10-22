package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成任务或更新任务时，把收流名字登记到 go2rtc。
 * <p>负责：流媒体首页出现这次要留下的空名字。设备还没推时，这一行的在线数是 0。
 * 更新任务时，这条任务以前登记、这次不再使用的名字从配置里拿掉。已经开始的计划点用的名字由调用方放进留下名单。
 * 空名字写进配置后，正在运行的 go2rtc 不会立刻放进首页名单。本类让它重新载入配置。
 * 重新载入时，已经在看的画面会断一下。一次生成只载入一次。
 * <p>不负责：拼收流地址；判断哪一次计划点已经开跑；开跑后向网关要画面。
 * 网关地址没配、连不上或载入后名单里仍然没有要留下的名字时，生成任务失败。
 */
@Component
public class Go2RtcStreamRegistrar {

    private static final Duration RELOAD_WAIT = Duration.ofSeconds(15);
    private static final Duration RELOAD_POLL = Duration.ofMillis(200);

    private final String gatewayApiBaseUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public Go2RtcStreamRegistrar(PatrolMediaAddress media, ObjectMapper objectMapper) {
        this(media.gatewayApiBaseUrl(), objectMapper, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build());
    }

    /** 测试传入登记接口。运行时从 {@link PatrolMediaAddress} 取主机。 */
    Go2RtcStreamRegistrar(String gatewayApiBaseUrl, ObjectMapper objectMapper, HttpClient httpClient) {
        String base = gatewayApiBaseUrl == null ? "" : gatewayApiBaseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        this.gatewayApiBaseUrl = base;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    /**
     * 首页还没有这些名字时，一次写入配置并让 go2rtc 重新载入。
     * 载入后名单里必须能看到每一个名字。已经都在名单里则不再载入。
     */
    public void registerReceiveSlots(List<String> streamNames) {
        if (gatewayApiBaseUrl.isBlank()) {
            throw ServiceExceptionUtil.invalidParamException("未配置流媒体网关地址，不能登记收流名字");
        }
        if (streamNames == null || streamNames.isEmpty()) {
            return;
        }
        synchronized (this) {
            List<String> missing = missingNames(streamNames);
            if (missing.isEmpty()) {
                return;
            }
            postConfig(configWithNames(missing));
            if (missingNames(streamNames).isEmpty()) {
                return;
            }
            requestReload();
            waitUntilListed(missing);
        }
    }

    /**
     * 只登记一个收流名字。多个计划点要一次登记，避免每个名字都让流媒体重新载入。
     */
    public void registerReceiveSlot(String streamName) {
        registerReceiveSlots(List.of(streamName));
    }

    /**
     * 更新任务时，把这条任务的收流名字收成这次要留下的一批。
     * 首页上属于这条任务、又不在留下名单里的名字删掉。留下名单里还没有的补上。
     * 已经开始或已经执行的计划点，调用方把它们的名字放进留下名单。
     * 一次写入。名单还没跟上时才重新载入一次。
     */
    public void replaceTaskReceiveSlots(long taskId, List<String> keepNames) {
        if (gatewayApiBaseUrl.isBlank()) {
            throw ServiceExceptionUtil.invalidParamException("未配置流媒体网关地址，不能登记收流名字");
        }
        if (taskId <= 0) {
            throw ServiceExceptionUtil.invalidParamException("清理收流名字缺少任务 id");
        }
        List<String> keep = distinctNames(keepNames);
        synchronized (this) {
            String config = get("/api/config");
            List<String> removed = new ArrayList<>();
            String updated = dropUnusedTaskStreams(config, taskId, keep, removed);
            List<String> added = new ArrayList<>();
            updated = ensureKeptStreams(updated, keep, added);
            if (!removed.isEmpty() || !added.isEmpty()) {
                postConfig(updated);
            }
            if (listOutOfDate(keep, removed)) {
                requestReload();
                waitUntilSynced(keep, removed);
            }
        }
    }

    private static List<String> distinctNames(List<String> names) {
        List<String> distinct = new ArrayList<>();
        if (names == null) {
            return distinct;
        }
        for (String name : names) {
            if (!StringUtils.hasText(name)) {
                continue;
            }
            String trimmed = name.trim();
            if (!distinct.contains(trimmed)) {
                distinct.add(trimmed);
            }
        }
        return distinct;
    }

    private static String dropUnusedTaskStreams(
            String config, long taskId, List<String> keep, List<String> removed) {
        String updated = config;
        for (String name : Go2RtcEmptyStreamConfig.streamNames(config)) {
            if (!PatrolTaskRecordIngestAllocator.belongsToTask(name, taskId) || keep.contains(name)) {
                continue;
            }
            updated = Go2RtcEmptyStreamConfig.removeStream(updated, name);
            removed.add(name);
        }
        return updated;
    }

    private static String ensureKeptStreams(String config, List<String> keep, List<String> added) {
        String updated = config;
        for (String name : keep) {
            String next = Go2RtcEmptyStreamConfig.ensureEmptyStream(updated, name);
            if (!next.equals(updated)) {
                added.add(name);
                updated = next;
            }
        }
        return updated;
    }

    private boolean listOutOfDate(List<String> keep, List<String> removed) {
        try {
            String json = get("/api/streams");
            return !listedAll(json, keep) || !absentAll(json, removed);
        } catch (RuntimeException ex) {
            return true;
        }
    }

    private boolean listedAll(String json, List<String> names) {
        for (String name : names) {
            if (!listed(json, name)) {
                return false;
            }
        }
        return true;
    }

    private boolean absentAll(String json, List<String> names) {
        for (String name : names) {
            if (listed(json, name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * go2rtc 重新载入时会先断开。连不上先继续等。
     * 等到要留下的名字都在名单里，并且这次拿掉的名字都不在名单里。
     */
    private void waitUntilSynced(List<String> keep, List<String> removed) {
        long deadline = System.nanoTime() + RELOAD_WAIT.toNanos();
        while (System.nanoTime() <= deadline) {
            try {
                String json = get("/api/streams");
                if (listedAll(json, keep) && absentAll(json, removed)) {
                    return;
                }
            } catch (RuntimeException ex) {
                // 重新载入的间隙接口会拒绝连接
            }
            sleepPoll();
        }
        throw ServiceExceptionUtil.invalidParamException(
                "流媒体没有收下收流名字「" + String.join("、", keep) + "」");
    }

    private String configWithNames(List<String> names) {
        String updated = get("/api/config");
        try {
            for (String name : names) {
                updated = Go2RtcEmptyStreamConfig.ensureEmptyStream(updated, name);
            }
        } catch (IllegalArgumentException ex) {
            throw ServiceExceptionUtil.invalidParamException(ex.getMessage());
        }
        return updated;
    }

    private List<String> missingNames(List<String> streamNames) {
        String json = get("/api/streams");
        List<String> missing = new ArrayList<>();
        for (String name : streamNames) {
            if (!listed(json, name)) {
                missing.add(name);
            }
        }
        return missing;
    }

    /**
     * go2rtc 重新载入时会先断开。连不上先继续等，等到名单里出现这些名字为止。
     */
    private void waitUntilListed(List<String> names) {
        long deadline = System.nanoTime() + RELOAD_WAIT.toNanos();
        while (System.nanoTime() <= deadline) {
            try {
                if (missingNames(names).isEmpty()) {
                    return;
                }
            } catch (RuntimeException ex) {
                // 重新载入的间隙接口会拒绝连接
            }
            sleepPoll();
        }
        throw ServiceExceptionUtil.invalidParamException(
                "流媒体没有收下收流名字「" + String.join("、", names) + "」");
    }

    private void sleepPoll() {
        try {
            Thread.sleep(RELOAD_POLL.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        }
    }

    private void requestReload() {
        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(gatewayApiBaseUrl + "/api/restart"))
                            .timeout(Duration.ofSeconds(3))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw ServiceExceptionUtil.invalidParamException(
                        "流媒体没有重新载入收流名字（HTTP " + response.statusCode() + "）");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            // go2rtc 开始重新载入时会断开这次连接
        }
    }

    private boolean listed(String json, String streamName) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node != null && node.isObject() && node.has(streamName);
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("流媒体名单无法识别，不能登记收流名字");
        }
    }

    private String get(String path) {
        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(gatewayApiBaseUrl + path))
                            .timeout(Duration.ofSeconds(3))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw ServiceExceptionUtil.invalidParamException(
                        "流媒体没有返回配置，不能登记收流名字（HTTP " + response.statusCode() + "）");
            }
            return response.body() == null ? "" : response.body();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        }
    }

    private void postConfig(String configYaml) {
        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(gatewayApiBaseUrl + "/api/config"))
                            .timeout(Duration.ofSeconds(3))
                            .header("Content-Type", "text/plain; charset=utf-8")
                            .POST(HttpRequest.BodyPublishers.ofString(configYaml, StandardCharsets.UTF_8))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw ServiceExceptionUtil.invalidParamException(
                        "流媒体没有写下收流名字（HTTP " + response.statusCode() + "）");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("流媒体网关连不上，不能登记收流名字");
        }
    }
}
