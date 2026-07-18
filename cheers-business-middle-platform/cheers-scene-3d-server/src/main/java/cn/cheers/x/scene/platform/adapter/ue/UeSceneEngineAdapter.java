package cn.cheers.x.scene.platform.adapter.ue;

import cn.cheers.x.scene.platform.adapter.spi.ActorPreviewContext;
import cn.cheers.x.scene.platform.adapter.spi.ActorPreviewResult;
import cn.cheers.x.scene.platform.adapter.spi.EngineScenePackage;
import cn.cheers.x.scene.platform.adapter.spi.SceneEngineAdapter;
import cn.cheers.x.scene.platform.adapter.spi.SceneExportContext;
import cn.cheers.x.scene.platform.adapter.spi.SceneImportResult;
import cn.cheers.x.scene.platform.adapter.spi.SceneValidationContext;
import cn.cheers.x.scene.platform.adapter.spi.SceneValidationResult;
import org.springframework.stereotype.Component;

@Component
public class UeSceneEngineAdapter implements SceneEngineAdapter {

    @Override
    public String engineProfile() {
        return "UE";
    }

    @Override
    public EngineScenePackage exportScene(SceneExportContext context) {
        return new EngineScenePackage();
    }

    @Override
    public SceneImportResult importScene(EngineScenePackage scenePackage) {
        return new SceneImportResult();
    }

    @Override
    public SceneValidationResult validateScene(SceneValidationContext context) {
        return new SceneValidationResult();
    }

    @Override
    public ActorPreviewResult buildActorPreview(ActorPreviewContext context) {
        return new ActorPreviewResult();
    }
}
