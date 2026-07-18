package cn.iocoder.yudao.module.scene.platform.service.asset.impl;

import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.asset.AssetResourceMapper;
import cn.iocoder.yudao.module.scene.platform.service.asset.convert.AssetConvertProperties;
import cn.iocoder.yudao.module.scene.platform.service.asset.convert.AssetFormatConverter;
import cn.iocoder.yudao.module.scene.platform.service.asset.convert.ObjToGlbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssetConvertServiceImplTest {

    @InjectMocks
    private AssetConvertServiceImpl service;

    @Mock
    private AssetResourceMapper assetResourceMapper;

    private final AssetConvertProperties convertProperties = new AssetConvertProperties();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        convertProperties.setEnabled(true);
        ReflectionTestUtils.setField(service, "convertProperties", convertProperties);
        ReflectionTestUtils.setField(service, "converters", List.<AssetFormatConverter>of());
        ReflectionTestUtils.setField(service, "localBasePath", "");
        ReflectionTestUtils.setField(service, "localBaseUrl", "");
    }

    @Test
    void convertToGlb_shouldPassThrough_whenSourceIsGlb() {
        AssetResourceDO asset = new AssetResourceDO();
        asset.setId(1L);
        asset.setAssetUrl("https://cdn.example.com/scene-assets/pump.glb");
        asset.setFormat("glb");
        asset.setMetadataJson("{\"convertStatus\":\"pending\"}");
        when(assetResourceMapper.selectById(1L)).thenReturn(asset);

        service.convertToGlb(1L);

        ArgumentCaptor<AssetResourceDO> captor = ArgumentCaptor.forClass(AssetResourceDO.class);
        verify(assetResourceMapper).updateById(captor.capture());
        Map<String, Object> metadata = JsonUtils.parseObject(captor.getValue().getMetadataJson(), Map.class);
        assertEquals("ready", metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS));
        assertEquals(asset.getAssetUrl(), metadata.get(AssetConvertServiceImpl.METADATA_RUNTIME_URL));
        assertFalse(metadata.containsKey(AssetConvertServiceImpl.METADATA_CONVERT_MESSAGE));
    }

    @Test
    void convertToGlb_shouldPassThrough_whenSourceIsGltf() {
        AssetResourceDO asset = new AssetResourceDO();
        asset.setId(2L);
        asset.setAssetUrl("https://cdn.example.com/scene-assets/building.gltf");
        asset.setFormat("gltf");
        when(assetResourceMapper.selectById(2L)).thenReturn(asset);

        service.convertToGlb(2L);

        ArgumentCaptor<AssetResourceDO> captor = ArgumentCaptor.forClass(AssetResourceDO.class);
        verify(assetResourceMapper).updateById(captor.capture());
        Map<String, Object> metadata = JsonUtils.parseObject(captor.getValue().getMetadataJson(), Map.class);
        assertEquals("ready", metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS));
        assertEquals(asset.getAssetUrl(), metadata.get(AssetConvertServiceImpl.METADATA_RUNTIME_URL));
    }

    @Test
    void convertToGlb_shouldMarkPending_whenSourceIsFbx() {
        AssetResourceDO asset = new AssetResourceDO();
        asset.setId(3L);
        asset.setAssetUrl("https://cdn.example.com/scene-assets/robot.fbx");
        asset.setFormat("fbx");
        when(assetResourceMapper.selectById(3L)).thenReturn(asset);

        service.convertToGlb(3L);

        ArgumentCaptor<AssetResourceDO> captor = ArgumentCaptor.forClass(AssetResourceDO.class);
        verify(assetResourceMapper).updateById(captor.capture());
        Map<String, Object> metadata = JsonUtils.parseObject(captor.getValue().getMetadataJson(), Map.class);
        assertEquals("pending", metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS));
        assertTrue(metadata.containsKey(AssetConvertServiceImpl.METADATA_CONVERT_MESSAGE));
        assertFalse(metadata.containsKey(AssetConvertServiceImpl.METADATA_RUNTIME_URL));
    }

    @Test
    void convertToGlb_shouldMarkFailed_whenObjRemoteWithoutLocalFile() {
        ReflectionTestUtils.setField(service, "converters", List.of(new ObjToGlbConverter()));
        AssetResourceDO asset = new AssetResourceDO();
        asset.setId(4L);
        asset.setAssetUrl("https://cdn.example.com/scene-assets/model.obj");
        asset.setFormat("obj");
        when(assetResourceMapper.selectById(4L)).thenReturn(asset);

        service.convertToGlb(4L);

        ArgumentCaptor<AssetResourceDO> captor = ArgumentCaptor.forClass(AssetResourceDO.class);
        verify(assetResourceMapper).updateById(captor.capture());
        Map<String, Object> metadata = JsonUtils.parseObject(captor.getValue().getMetadataJson(), Map.class);
        assertEquals("failed", metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS));
        assertTrue(String.valueOf(metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_MESSAGE)).contains("本地源文件"));
    }

    @Test
    void convertToGlb_shouldConvertObj_whenLocalFileReadable(@TempDir Path tempDir) throws Exception {
        Path obj = tempDir.resolve("tri.obj");
        Files.writeString(obj, """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                f 1 2 3
                """);
        Path work = tempDir.resolve("work");
        Files.createDirectories(work);
        convertProperties.setWorkDir(work.toString());
        ReflectionTestUtils.setField(service, "converters", List.of(new ObjToGlbConverter()));
        ReflectionTestUtils.setField(service, "localBasePath", tempDir.resolve("uploads").toString());

        AssetResourceDO asset = new AssetResourceDO();
        asset.setId(5L);
        asset.setAssetUrl(obj.toUri().toString());
        asset.setFormat("obj");
        when(assetResourceMapper.selectById(5L)).thenReturn(asset);

        service.convertToGlb(5L);

        ArgumentCaptor<AssetResourceDO> captor = ArgumentCaptor.forClass(AssetResourceDO.class);
        verify(assetResourceMapper).updateById(captor.capture());
        Map<String, Object> metadata = JsonUtils.parseObject(captor.getValue().getMetadataJson(), Map.class);
        assertEquals("ready", metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS));
        assertTrue(String.valueOf(metadata.get(AssetConvertServiceImpl.METADATA_RUNTIME_URL)).contains(".glb"));
        assertEquals("builtin-obj", metadata.get(AssetConvertServiceImpl.METADATA_CONVERTER_NAME));
    }
}
