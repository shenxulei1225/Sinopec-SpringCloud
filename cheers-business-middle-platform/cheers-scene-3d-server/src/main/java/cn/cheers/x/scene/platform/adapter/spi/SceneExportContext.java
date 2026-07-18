package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

@Data
public class SceneExportContext {

    private String sceneCode;

    private String engineProfile;

    private boolean includeAssets = true;

    private boolean includeSnapshots = false;
}
