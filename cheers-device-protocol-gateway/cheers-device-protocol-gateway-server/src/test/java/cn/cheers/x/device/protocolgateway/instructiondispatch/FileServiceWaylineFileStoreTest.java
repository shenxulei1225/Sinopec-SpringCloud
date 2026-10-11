package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.infra.api.file.FileApi;
import cn.cheers.x.infra.api.file.dto.FileCreateReqDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileServiceWaylineFileStoreTest {

    @Test
    void store_usesFileServiceUrl() {
        FileApi fileApi = mock(FileApi.class);
        when(fileApi.createFile(org.mockito.ArgumentMatchers.any(FileCreateReqDTO.class)))
                .thenReturn(CommonResult.success("http://127.0.0.1:15080/wayline/task.kmz"));
        FileServiceWaylineFileStore store = new FileServiceWaylineFileStore(fileApi);

        WaylineFileStore.Stored stored = store.store("patrol-task-1", new byte[] {1, 2}, "abc");

        ArgumentCaptor<FileCreateReqDTO> captor = ArgumentCaptor.forClass(FileCreateReqDTO.class);
        verify(fileApi).createFile(captor.capture());
        FileCreateReqDTO request = captor.getValue();
        assertEquals("wayline", request.getDirectory());
        assertEquals("patrol-task-1-abc.kmz", request.getName());
        assertArrayEquals(new byte[] {1, 2}, request.getContent());
        assertEquals("patrol-task-1-abc", stored.fileId());
        assertEquals("http://127.0.0.1:15080/wayline/task.kmz", stored.url());
        assertEquals("abc", stored.fingerprint());
    }

    @Test
    void store_whenFileServiceRejects_throws() {
        FileApi fileApi = mock(FileApi.class);
        when(fileApi.createFile(org.mockito.ArgumentMatchers.any(FileCreateReqDTO.class)))
                .thenReturn(CommonResult.error(400, "主存储器不可用"));
        FileServiceWaylineFileStore store = new FileServiceWaylineFileStore(fileApi);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> store.store("patrol-task-1", new byte[] {1}, "abc"));
        assertEquals("主存储器不可用", ex.getMessage());
    }

    @Test
    void store_emptyFile_doesNotCallFileService() {
        FileApi fileApi = mock(FileApi.class);
        FileServiceWaylineFileStore store = new FileServiceWaylineFileStore(fileApi);

        assertThrows(IllegalArgumentException.class, () -> store.store("patrol-task-1", new byte[0], "abc"));
        verify(fileApi, never()).createFile(org.mockito.ArgumentMatchers.any(FileCreateReqDTO.class));
    }
}
