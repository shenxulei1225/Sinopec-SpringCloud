package cn.cheers.x.device.protocolgateway.instructiondispatch;

/**
 * 执行无人机上唯一一台云台相机的挂载快照，供对照 EXECUTOR_MOUNT 解析。
 */
record ExecutorGimbalMount(
        long modelId,
        int mountPoint,
        String cameraType,
        String payloadIndex
) {
}
