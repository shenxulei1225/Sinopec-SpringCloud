package cn.cheers.x.scene.platform.service.asset.convert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliAssetFormatConverterTest {

    @TempDir
    Path tempDir;

    @Test
    void supports_shouldBeFalse_whenPluginDisabled() {
        AssetConvertProperties props = new AssetConvertProperties();
        AssetConvertProperties.PluginConfig cfg = new AssetConvertProperties.PluginConfig();
        cfg.setEnabled(false);
        cfg.setCommand("assimp export {input} {output}");
        props.setPlugins(Map.of("fbx", cfg));

        CliAssetFormatConverter converter = new CliAssetFormatConverter();
        ReflectionTestUtils.setField(converter, "properties", props);

        assertFalse(converter.supports("fbx"));
    }

    @Test
    void convert_shouldFail_whenCommandMissing() throws Exception {
        AssetConvertProperties props = new AssetConvertProperties();
        AssetConvertProperties.PluginConfig cfg = new AssetConvertProperties.PluginConfig();
        cfg.setEnabled(true);
        cfg.setCommand("this-binary-does-not-exist-xyz {input} {output}");
        cfg.setTimeoutSeconds(5);
        props.setPlugins(Map.of("fbx", cfg));

        CliAssetFormatConverter converter = new CliAssetFormatConverter();
        ReflectionTestUtils.setField(converter, "properties", props);

        Path src = tempDir.resolve("a.fbx");
        Files.writeString(src, "fake");
        Path out = tempDir.resolve("a.glb");

        assertTrue(converter.supports("fbx"));
        assertThrows(Exception.class, () -> converter.convert(src, out));
    }
}
