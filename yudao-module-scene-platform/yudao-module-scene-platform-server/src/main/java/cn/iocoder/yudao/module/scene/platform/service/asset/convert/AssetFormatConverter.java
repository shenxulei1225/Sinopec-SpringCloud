package cn.iocoder.yudao.module.scene.platform.service.asset.convert;

import java.nio.file.Path;

/**
 * 资产格式转换器扩展点：某一源格式 → GLB。
 */
public interface AssetFormatConverter {

    /** 转换器名称，写入 metadata.converterName */
    String name();

    /** 越大越优先（外部命令插件应高于内置） */
    int priority();

    boolean supports(String format);

    /**
     * 将源文件转为 targetGlb（父目录已存在）。
     *
     * @throws Exception 转换失败
     */
    void convert(Path sourceFile, Path targetGlb) throws Exception;
}
