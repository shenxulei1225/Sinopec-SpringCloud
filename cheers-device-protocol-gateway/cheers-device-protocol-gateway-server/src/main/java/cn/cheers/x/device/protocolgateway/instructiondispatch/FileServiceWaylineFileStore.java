package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.infra.api.file.FileApi;
import cn.cheers.x.infra.api.file.dto.FileCreateReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 生成任务时，把航线文件交给已经启用的文件存储服务保存。
 * <p>目录固定是 wayline。文件服务返回的访问地址给机场下载。
 * <p>不负责：自己再写一个本地目录。文件服务没返回地址时失败，不另编一个地址。
 */
@Component
@RequiredArgsConstructor
public class FileServiceWaylineFileStore implements WaylineFileStore {

    /** 文件服务里的目录名。航线文件都放在这一层。 */
    static final String WAYLINE_DIRECTORY = "wayline";

    private static final String KMZ_MIME = "application/vnd.google-earth.kmz";

    private final FileApi fileApi;

    /**
     * 正式生成任务时写入航线文件。试译不调用这里。
     *
     * @param executionId 这次任务或这次执行的编号，用来给文件起名
     * @param kmz         航线文件正文
     * @param fingerprint 文件内容签名
     * @return 文件编号、机场能够下载的地址、内容签名
     */
    @Override
    public Stored store(String executionId, byte[] kmz, String fingerprint) {
        if (kmz == null || kmz.length == 0) {
            throw new IllegalArgumentException("航线文件是空的");
        }
        if (fingerprint == null || fingerprint.isBlank()) {
            throw new IllegalArgumentException("航线文件没有内容签名");
        }
        String fileId = fileId(executionId, fingerprint.trim());
        CommonResult<String> saved = fileApi.createFile(new FileCreateReqDTO()
                .setName(fileId + ".kmz")
                .setDirectory(WAYLINE_DIRECTORY)
                .setType(KMZ_MIME)
                .setContent(kmz));
        if (saved == null || !saved.isSuccess() || saved.getData() == null || saved.getData().isBlank()) {
            String reason = saved == null ? "" : saved.getMsg();
            throw new IllegalStateException(
                    reason == null || reason.isBlank() ? "航线文件没有写入文件存储" : reason);
        }
        return new Stored(fileId, saved.getData().trim(), fingerprint.trim());
    }

    private static String fileId(String executionId, String fingerprint) {
        String prefix = executionId == null || executionId.isBlank() ? "wayline" : executionId.trim();
        return prefix + "-" + fingerprint;
    }
}
