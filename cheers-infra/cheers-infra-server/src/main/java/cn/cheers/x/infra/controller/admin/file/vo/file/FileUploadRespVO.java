package cn.cheers.x.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 上传文件 Response VO")
@Data
public class FileUploadRespVO {

    @Schema(description = "访问地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/file/abc.pdf")
    private String url;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "sop-standard.pdf")
    private String name;

    @Schema(description = "MIME 类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "application/pdf")
    private String mime;

    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long size;

    @Schema(description = "Magika 检测类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "pdf")
    private String detectedType;
}
