package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class AssetImportResult {

    private boolean success;

    private String assetCode;

    private String issue;
}
