package cn.cheers.x.inspection.task.service.execution.analysis;

/**
 * 从正在接收的流里取当前这一帧的图片地址。
 * <p>没有画面时返回 null。不调用跑冒滴漏，也不写执行步骤。
 */
public interface StreamFrameSource {

    String currentImageUrl(String streamName);
}
