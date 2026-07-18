package cn.cheers.x.scene.platform.service.asset.convert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 将三角网格写成最小合法 GLB（单 mesh / 无材质）。
 */
public final class SimpleGlbWriter {

    private SimpleGlbWriter() {
    }

    public static void writeTriangleMesh(Path targetGlb, float[] positions, int[] indices) throws IOException {
        Files.write(targetGlb, buildGlb(positions, indices));
    }

    static byte[] buildGlb(float[] positions, int[] indices) {
        if (positions == null || positions.length < 9 || positions.length % 3 != 0) {
            throw new IllegalArgumentException("positions 至少需要一个三角形（9 个 float）");
        }
        if (indices == null || indices.length < 3 || indices.length % 3 != 0) {
            throw new IllegalArgumentException("indices 长度须为 3 的倍数");
        }

        ByteBuffer bin = ByteBuffer.allocate(positions.length * 4 + indices.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : positions) {
            bin.putFloat(v);
        }
        int indexByteOffset = positions.length * 4;
        for (int idx : indices) {
            bin.putInt(idx);
        }
        byte[] binBytes = bin.array();

        float[] min = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY};
        float[] max = new float[]{Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
        for (int i = 0; i < positions.length; i += 3) {
            min[0] = Math.min(min[0], positions[i]);
            min[1] = Math.min(min[1], positions[i + 1]);
            min[2] = Math.min(min[2], positions[i + 2]);
            max[0] = Math.max(max[0], positions[i]);
            max[1] = Math.max(max[1], positions[i + 1]);
            max[2] = Math.max(max[2], positions[i + 2]);
        }

        int vertexCount = positions.length / 3;
        int indexCount = indices.length;
        String json = String.format(Locale.ROOT,
                "{\"asset\":{\"version\":\"2.0\"},\"buffers\":[{\"byteLength\":%d}],"
                        + "\"bufferViews\":["
                        + "{\"buffer\":0,\"byteOffset\":0,\"byteLength\":%d,\"target\":34962},"
                        + "{\"buffer\":0,\"byteOffset\":%d,\"byteLength\":%d,\"target\":34963}"
                        + "],\"accessors\":["
                        + "{\"bufferView\":0,\"componentType\":5126,\"count\":%d,\"type\":\"VEC3\","
                        + "\"max\":[%s,%s,%s],\"min\":[%s,%s,%s]},"
                        + "{\"bufferView\":1,\"componentType\":5125,\"count\":%d,\"type\":\"SCALAR\"}"
                        + "],\"meshes\":[{\"primitives\":[{\"attributes\":{\"POSITION\":0},\"indices\":1}]}],"
                        + "\"nodes\":[{\"mesh\":0}],\"scenes\":[{\"nodes\":[0]}],\"scene\":0}",
                binBytes.length,
                indexByteOffset,
                indexByteOffset,
                indices.length * 4,
                vertexCount,
                fmt(max[0]), fmt(max[1]), fmt(max[2]),
                fmt(min[0]), fmt(min[1]), fmt(min[2]),
                indexCount
        );

        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        int jsonPadding = (4 - (jsonBytes.length % 4)) % 4;
        int binPadding = (4 - (binBytes.length % 4)) % 4;
        int totalLength = 12 + 8 + jsonBytes.length + jsonPadding + 8 + binBytes.length + binPadding;

        ByteBuffer out = ByteBuffer.allocate(totalLength).order(ByteOrder.LITTLE_ENDIAN);
        out.putInt(0x46546C67);
        out.putInt(2);
        out.putInt(totalLength);
        out.putInt(jsonBytes.length + jsonPadding);
        out.putInt(0x4E4F534A);
        out.put(jsonBytes);
        for (int i = 0; i < jsonPadding; i++) {
            out.put((byte) 0x20);
        }
        out.putInt(binBytes.length + binPadding);
        out.putInt(0x004E4942);
        out.put(binBytes);
        for (int i = 0; i < binPadding; i++) {
            out.put((byte) 0);
        }
        return out.array();
    }

    private static String fmt(float v) {
        return String.format(Locale.ROOT, "%.6f", v);
    }

    public static void convertObjToGlb(Path objFile, Path targetGlb) throws IOException {
        List<float[]> verts = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        for (String line : Files.readAllLines(objFile)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            if ("v".equals(parts[0]) && parts.length >= 4) {
                verts.add(new float[]{
                        Float.parseFloat(parts[1]),
                        Float.parseFloat(parts[2]),
                        Float.parseFloat(parts[3])
                });
            } else if ("f".equals(parts[0]) && parts.length >= 4) {
                int[] face = new int[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    String token = parts[i].split("/")[0];
                    int idx = Integer.parseInt(token);
                    if (idx < 0) {
                        idx = verts.size() + idx + 1;
                    }
                    face[i - 1] = idx - 1;
                }
                for (int i = 1; i + 1 < face.length; i++) {
                    indices.add(face[0]);
                    indices.add(face[i]);
                    indices.add(face[i + 1]);
                }
            }
        }
        if (verts.isEmpty() || indices.size() < 3) {
            throw new IOException("OBJ 无可三角化几何: " + objFile);
        }
        float[] positions = new float[verts.size() * 3];
        for (int i = 0; i < verts.size(); i++) {
            float[] v = verts.get(i);
            positions[i * 3] = v[0];
            positions[i * 3 + 1] = v[1];
            positions[i * 3 + 2] = v[2];
        }
        int[] idx = indices.stream().mapToInt(Integer::intValue).toArray();
        writeTriangleMesh(targetGlb, positions, idx);
    }

    public static boolean looksLikeGlb(byte[] bytes) {
        return bytes != null && bytes.length >= 12
                && bytes[0] == 'g' && bytes[1] == 'l' && bytes[2] == 'T' && bytes[3] == 'F';
    }
}
