package cn.iocoder.yudao.module.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class EngineAssetRef {

    private String engineProfile;

    private String assetType;

    private String assetUri;

    private String payloadJson;
}
