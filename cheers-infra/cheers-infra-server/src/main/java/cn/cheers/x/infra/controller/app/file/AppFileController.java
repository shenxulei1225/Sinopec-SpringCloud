package cn.cheers.x.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.infra.controller.admin.file.vo.file.FileUploadRespVO;
import cn.cheers.x.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.cheers.x.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.cheers.x.infra.controller.app.file.vo.AppFileUploadReqVO;
import cn.cheers.x.infra.service.file.FileService;
import cn.cheers.x.infra.service.file.FileUploadSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    @Resource
    private FileService fileService;
    @Resource
    private FileUploadSecurityService fileUploadSecurityService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    @PermitAll
    public CommonResult<FileUploadRespVO> uploadFile(@Valid AppFileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        FileUploadSecurityService.FileDetectionResult detect = fileUploadSecurityService
                .detectAndValidate(content, file.getOriginalFilename(), file.getContentType());
        String url = fileService.createFile(content, file.getOriginalFilename(),
                uploadReqVO.getDirectory(), detect.mime());
        FileUploadRespVO respVO = new FileUploadRespVO();
        respVO.setUrl(url);
        respVO.setName(file.getOriginalFilename());
        respVO.setMime(detect.mime());
        respVO.setSize((long) content.length);
        respVO.setDetectedType(detect.detectedType());
        return success(respVO);
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

}
