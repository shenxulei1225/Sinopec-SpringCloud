package cn.cheers.x.scene.platform.service.asset.convert;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * 内置 OBJ → GLB（几何优先）。
 */
@Component
public class ObjToGlbConverter implements AssetFormatConverter {

    @Override
    public String name() {
        return "builtin-obj";
    }

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public boolean supports(String format) {
        return format != null && "obj".equalsIgnoreCase(format.trim());
    }

    @Override
    public void convert(Path sourceFile, Path targetGlb) throws Exception {
        SimpleGlbWriter.convertObjToGlb(sourceFile, targetGlb);
    }
}
