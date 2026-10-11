package cn.cheers.x.device.protocolgateway.instructiondispatch;

/**
 * 航线文件交给文件存储服务保存。调用方只拿编号、下载地址和内容签名。
 * <p>不负责：改任务步骤树、给东古或机器人打任务指令包、自己再开一个存放目录。
 */
public interface WaylineFileStore {

    Stored store(String executionId, byte[] kmz, String fingerprint);

    /**
     * @param fileId      这一次执行用的文件编号
     * @param url         机场能够下载的地址
     * @param fingerprint 文件内容 MD5
     */
    record Stored(String fileId, String url, String fingerprint) {
    }
}
