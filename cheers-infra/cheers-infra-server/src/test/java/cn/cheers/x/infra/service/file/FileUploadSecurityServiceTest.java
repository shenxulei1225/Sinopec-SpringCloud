package cn.cheers.x.infra.service.file;

import cn.cheers.x.infra.controller.admin.file.FileController;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static cn.cheers.x.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.cheers.x.infra.enums.ErrorCodeConstants.FILE_TYPE_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUploadSecurityServiceTest {

    @Test
    void emptyContentIsAllowedWithoutGuessingExtension() {
        FileUploadSecurityService service = newService();
        FileUploadSecurityService.FileDetectionResult result =
                service.detectAndValidate(new byte[0], "notes.txt", "text/plain");
        assertEquals("empty", result.detectedType());
        assertEquals("text/plain", result.mime());
    }

    @Test
    void magikaEmptyLabelIsAllowed() {
        FileUploadSecurityService service = newService();
        FileUploadSecurityService.FileDetectionResult result = service.acceptOrReject(
                new FileUploadSecurityService.FileDetectionResult("empty", "application/octet-stream"),
                "blank.txt");
        assertEquals("empty", result.detectedType());
    }

    @Test
    void archiveAliasesNormalizeToMagikaLabels() {
        assertEquals("sevenzip", FileUploadSecurityService.normalizeType("7z"));
        assertEquals("sevenzip", FileUploadSecurityService.normalizeType("7zip"));
        assertEquals("gzip", FileUploadSecurityService.normalizeType("gz"));
        assertTrue(FileUploadSecurityService.parseAllowedTypes(FileUploadSecurityService.DEFAULT_ALLOWED_TYPES)
                .contains("zip"));
        assertTrue(FileUploadSecurityService.parseAllowedTypes(FileUploadSecurityService.DEFAULT_ALLOWED_TYPES)
                .contains("sevenzip"));
    }

    @Test
    void rejectMessageUsesChineseTypeAndAllowedList() {
        FileUploadSecurityService service = newService();
        assertServiceException(
                () -> service.acceptOrReject(
                        new FileUploadSecurityService.FileDetectionResult("png", "image/png"),
                        "shot.png"),
                FILE_TYPE_NOT_ALLOWED,
                "图片（PNG）",
                FileUploadSecurityService.ALLOWED_TYPES_USER_HINT);
        assertEquals("ZIP 压缩包", FileUploadSecurityService.describeDetectedType("zip"));
        assertEquals("无法识别的类型", FileUploadSecurityService.describeDetectedType("unknown"));
    }

    @Test
    void missingUploadIsOnlyWhenUserPickedNothing() {
        assertTrue(FileController.isMissingUpload(null));
        assertTrue(FileController.isMissingUpload(new MockMultipartFile("file", new byte[0])));
        assertFalse(FileController.isMissingUpload(
                new MockMultipartFile("file", "notes.txt", "text/plain", new byte[0])));
        assertFalse(FileController.isMissingUpload(
                new MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes())));
    }

    private static FileUploadSecurityService newService() {
        FileUploadSecurityService service = new FileUploadSecurityService();
        ReflectionTestUtils.setField(service, "allowedTypesConfig", FileUploadSecurityService.DEFAULT_ALLOWED_TYPES);
        return service;
    }
}
