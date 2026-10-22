package cn.cheers.x.device.protocolgateway.protocol.exchange;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import cn.cheers.x.inspection.task.api.dto.ReplayPoseReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptReqDTO;
import cn.cheers.x.inspection.task.api.dto.TaskExchangeTranscriptRespDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 东古地面站和大疆上云的往来原文交给巡检。
 * 报文里有任务 id 或飞行编号就记到那次。平台这次下发已经知道执行记录时，指令包和同号答卷也记到这次。
 * 回执只核对消息编号，不拿走这次对应。同号业务答卷再用一次。
 * 打开周期状态时记下这条执行。之后这台设备的定时状态记到这条，不在多条到点待执行里挑。
 * 其余对不上的报文：一条正在执行，或只有一条已经到点的待执行，才记入。
 * <p>收包线程只排队。写出发生在另一条线程，或发生在下发请求自己的线程。
 * 东古状态里的经纬度另存成回放位置，不写进过程列表。
 * <p>不负责：判断步骤成败、改执行状态。
 * <p>禁止：在收包线程里调用巡检；队列满了假装已经记上；用任务模板 id 选执行记录。
 */
@Slf4j
@Component
public class RunningTaskExchangeRecorder {

    private static final int QUEUE_CAPACITY = 256;
    private static final int LINK_CAP = 1024;
    private static final long LINK_TTL_MS = 600_000L;

    private final InspectionDeviceUplinkApi inspectionDeviceUplinkApi;
    private final BlockingQueue<Pending> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final ConcurrentMap<String, Link> replyLinks = new ConcurrentHashMap<>();
    /** 这台设备的周期状态当前记在哪条执行上。定时状态会连续上来，这里不按条数用完。 */
    private final ConcurrentMap<String, Link> timedStatusByDevice = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public RunningTaskExchangeRecorder(InspectionDeviceUplinkApi inspectionDeviceUplinkApi) {
        this.inspectionDeviceUplinkApi = inspectionDeviceUplinkApi;
    }

    @PostConstruct
    void start() {
        running.set(true);
        worker = new Thread(this::drain, "device-exchange-transcript");
        worker.setDaemon(true);
        worker.start();
    }

    @PreDestroy
    void stop() {
        running.set(false);
        if (worker != null) {
            worker.interrupt();
        }
    }

    /**
     * 收包线程调用。只入队，不在这条线程上访问巡检。
     */
    public void enqueue(String deviceId, String direction, String payloadJson) {
        enqueue(deviceId, direction, payloadJson, null);
    }

    /**
     * 收包线程调用。executionRecordId 仅在发送侧已经对上这次执行时才传。
     */
    public void enqueue(String deviceId, String direction, String payloadJson, Long executionRecordId) {
        offer(new Pending(
                deviceId, direction, payloadJson, System.currentTimeMillis(), executionRecordId, null));
    }

    /**
     * 收包线程调用。只把位置点送出这条线程。不在这里访问巡检。
     */
    public void enqueuePose(ReplayPoseReqDTO pose) {
        if (pose == null) {
            return;
        }
        long at = pose.getT() == null ? System.currentTimeMillis() : pose.getT();
        offer(new Pending(pose.getDeviceId(), null, null, at, pose.getExecutionRecordId(), pose));
    }

    /**
     * 写出之前记下消息编号。同号答卷收回时用这个编号归到这次执行。
     */
    public void remember(String deviceId, String msgId, Long executionRecordId) {
        if (blank(deviceId) || blank(msgId) || executionRecordId == null || executionRecordId <= 0L) {
            return;
        }
        replyLinks.put(key(deviceId, msgId), new Link(executionRecordId, System.currentTimeMillis()));
        if (replyLinks.size() <= LINK_CAP) {
            return;
        }
        long cutoff = System.currentTimeMillis() - LINK_TTL_MS;
        replyLinks.entrySet().removeIf(entry -> entry.getValue().at() < cutoff);
    }

    /** 没写出时撤掉刚才记下的消息编号，避免后来的同号包挂错。 */
    public void forget(String deviceId, String msgId) {
        if (blank(deviceId) || blank(msgId)) {
            return;
        }
        replyLinks.remove(key(deviceId, msgId));
    }

    /**
     * 回执先到时只查看，不拿走。同号业务答卷还要用这个编号。
     */
    public Long peek(String deviceId, String msgId) {
        return readLink(deviceId, msgId, false);
    }

    /**
     * 收回的同号业务答卷用一次就拿掉。超过十分钟的编号不再挂。
     */
    public Long linked(String deviceId, String msgId) {
        return readLink(deviceId, msgId, true);
    }

    /**
     * 这条执行打开了这台设备的周期状态。之后的定时状态记到这条执行。
     */
    public void bindTimedStatus(String deviceId, Long executionRecordId) {
        if (blank(deviceId) || executionRecordId == null || executionRecordId <= 0L) {
            return;
        }
        timedStatusByDevice.put(deviceId.trim(), new Link(executionRecordId, System.currentTimeMillis()));
    }

    /** 这条执行关掉周期状态后，后面的定时状态不再记到它。 */
    public void clearTimedStatus(String deviceId) {
        if (blank(deviceId)) {
            return;
        }
        timedStatusByDevice.remove(deviceId.trim());
    }

    /** 这台设备的定时状态当前记在哪条执行上。连续上报不拿掉。 */
    public Long timedStatusRecord(String deviceId) {
        if (blank(deviceId)) {
            return null;
        }
        Link link = timedStatusByDevice.get(deviceId.trim());
        if (link == null || link.recordId() == null || link.recordId() <= 0L) {
            return null;
        }
        return link.recordId();
    }

    private Long readLink(String deviceId, String msgId, boolean consume) {
        if (blank(deviceId) || blank(msgId)) {
            return null;
        }
        String linkKey = key(deviceId, msgId);
        Link link = consume ? replyLinks.remove(linkKey) : replyLinks.get(linkKey);
        if (link == null || link.recordId() == null || link.recordId() <= 0L
                || System.currentTimeMillis() - link.at() > LINK_TTL_MS) {
            return null;
        }
        return link.recordId();
    }

    /**
     * 下发请求线程调用。记入之后返回没记上的原因。记上了返回空串。
     */
    public String recordNow(String deviceId, String direction, String payloadJson) {
        return recordNow(deviceId, direction, payloadJson, null);
    }

    public String recordNow(String deviceId, String direction, String payloadJson, Long executionRecordId) {
        return record(deviceId, direction, payloadJson, System.currentTimeMillis(), executionRecordId);
    }

    /**
     * 下发说明后面接上没记入任务的原因。没有原因时说明保持原样。
     */
    public static String joinNote(String note, String reason) {
        if (reason == null || reason.isBlank()) {
            return note;
        }
        if (note == null || note.isBlank()) {
            return reason;
        }
        return note + "。" + reason;
    }

    private void drain() {
        while (running.get()) {
            try {
                Pending pending = queue.poll(1, TimeUnit.SECONDS);
                if (pending == null) {
                    continue;
                }
                if (pending.payloadJson() != null) {
                    String reason = record(
                            pending.deviceId(),
                            pending.direction(),
                            pending.payloadJson(),
                            pending.receivedAtEpochMs(),
                            pending.executionRecordId());
                    if (reason != null && !reason.isBlank()) {
                        if (reason.contains("多条")) {
                            log.warn("[device-protocol] {} deviceId={}", reason, pending.deviceId());
                        } else {
                            log.info("[device-protocol] {} deviceId={}", reason, pending.deviceId());
                        }
                    }
                }
                if (pending.pose() != null) {
                    writePose(pending.pose());
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private String record(
            String deviceId,
            String direction,
            String payloadJson,
            long receivedAtEpochMs,
            Long executionRecordId) {
        if (deviceId == null || deviceId.isBlank() || payloadJson == null || payloadJson.isBlank()) {
            return "缺少设备编号或报文，未记入任务";
        }
        TaskExchangeTranscriptReqDTO req = new TaskExchangeTranscriptReqDTO();
        req.setDeviceId(deviceId.trim());
        req.setDirection(direction);
        req.setPayloadJson(payloadJson);
        req.setReceivedAtEpochMs(receivedAtEpochMs);
        req.setExecutionRecordId(executionRecordId);
        try {
            CommonResult<TaskExchangeTranscriptRespDTO> result = inspectionDeviceUplinkApi.recordExchange(req);
            if (result == null || !result.isSuccess() || result.getData() == null) {
                String message = result == null ? null : result.getMsg();
                return message == null || message.isBlank() ? "记入任务失败" : message;
            }
            if (result.getData().isRecorded()) {
                return "";
            }
            String reason = result.getData().getReason();
            return reason == null || reason.isBlank() ? "未记入任务" : reason;
        } catch (RuntimeException ex) {
            log.warn("[device-protocol] 记入任务失败 deviceId={}", deviceId, ex);
            return "记入任务失败";
        }
    }

    private void writePose(ReplayPoseReqDTO pose) {
        try {
            CommonResult<Boolean> result = inspectionDeviceUplinkApi.recordReplayPose(pose);
            if (result != null && result.isSuccess() && Boolean.TRUE.equals(result.getData())) {
                return;
            }
            log.info("[device-protocol] 位置点未记入任务 deviceId={}", pose.getDeviceId());
        } catch (RuntimeException ex) {
            log.warn("[device-protocol] 位置点记入任务失败 deviceId={}", pose.getDeviceId(), ex);
        }
    }

    private void offer(Pending pending) {
        if (queue.offer(pending)) {
            return;
        }
        log.error("[device-protocol] 往来记录队列已满，未记入任务 deviceId={}", pending.deviceId());
    }

    private static boolean blank(String text) {
        return text == null || text.isBlank();
    }

    private static String key(String deviceId, String msgId) {
        return deviceId.trim() + "\0" + msgId.trim();
    }

    private record Link(Long recordId, long at) {
    }

    private record Pending(
            String deviceId,
            String direction,
            String payloadJson,
            long receivedAtEpochMs,
            Long executionRecordId,
            ReplayPoseReqDTO pose) {
    }
}
