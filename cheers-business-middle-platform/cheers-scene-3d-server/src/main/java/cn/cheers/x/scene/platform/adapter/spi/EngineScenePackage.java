package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class EngineScenePackage {

    private String engineProfile;

    private String sceneCode;

    private String packageFormat;

    private String packageVersion;

    private String payloadJson;
}
