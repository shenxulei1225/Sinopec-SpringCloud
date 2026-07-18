package cn.cheers.x.scene.platform.adapter.spi;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneImportResult {

    private boolean success;

    private String sceneCode;

    private Integer actorCount;

    private Integer assetCount;

    private List<String> issues = new ArrayList<>();
}
