package cn.cheers.x.scene.platform.service.asset.convert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleGlbWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void convertObjToGlb_shouldWriteGlbMagic() throws Exception {
        Path obj = tempDir.resolve("box.obj");
        Files.writeString(obj, """
                v 0 0 0
                v 1 0 0
                v 0 1 0
                v 0 0 1
                f 1 2 3
                f 1 3 4
                """);
        Path glb = tempDir.resolve("out.glb");
        SimpleGlbWriter.convertObjToGlb(obj, glb);
        byte[] bytes = Files.readAllBytes(glb);
        assertTrue(SimpleGlbWriter.looksLikeGlb(bytes));
        assertTrue(bytes.length > 100);
    }

    @Test
    void buildGlb_shouldStartWithGlTFMagic() {
        float[] positions = new float[]{0, 0, 0, 1, 0, 0, 0, 1, 0};
        int[] indices = new int[]{0, 1, 2};
        byte[] bytes = SimpleGlbWriter.buildGlb(positions, indices);
        assertTrue(SimpleGlbWriter.looksLikeGlb(bytes));
    }
}
