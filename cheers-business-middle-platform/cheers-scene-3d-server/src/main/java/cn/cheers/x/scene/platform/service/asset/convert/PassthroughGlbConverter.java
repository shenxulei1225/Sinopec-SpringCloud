package cn.cheers.x.scene.platform.service.asset.convert;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * glb/gltf 直通：复制为 .glb（gltf 仍复制，运行时 URL 由编排层决定是否直用源 URL）。
 */
@Component
public class PassthroughGlbConverter implements AssetFormatConverter {

    @Override
    public String name() {
        return "builtin-passthrough";
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public boolean supports(String format) {
        String f = format == null ? "" : format.trim().toLowerCase();
        return "glb".equals(f) || "gltf".equals(f);
    }

    @Override
    public void convert(Path sourceFile, Path targetGlb) throws Exception {
        Files.copy(sourceFile, targetGlb, StandardCopyOption.REPLACE_EXISTING);
    }
}
